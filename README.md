# Run! Zombiez

A private, hands-free, cinematic zombie-survival running experience. Runner 007 leaves Haven Base, puts the phone away, and runs — the mission unfolds through radio transmissions, survivor dialogue, environmental audio, and zombie encounters.

This is a personal project. See `docs/ORIGINAL_SPEC.md` (the full v2.0 master spec) for the complete original product vision, and `DECISIONS.md` for how it's evolved since. There are now **two working builds**: a mobile web app (PWA) and the native Android app the spec originally called for — see `DECISIONS.md` for why/when each happened.

## Current status

**Both platforms are real, working builds as of 2026-08-10.**

- `web/` — a Progressive Web App, verified in-browser: Home → Demo/Start Mission → live mission playback → Stop/Complete → Home all work end to end. Deployed live at `https://garyfishermtbike-1337.github.io/Run-Zombiez/`. See [Web app](#web-app) below.
- `app/` — native Android/Kotlin/Compose, verified with a real compiler and lint pass: `assembleDebug` succeeds and produces a valid APK, `lintDebug` passes clean. **Not yet installed/run on a device or emulator** — that's the next step. See [Android app](#android-app) below.

See `STATUS.md` for the full detailed handoff state on both.

## Web app

```
web/
  index.html            - Single-page shell: Home / Mission / Help screens (JS-toggled)
  css/style.css          - Dark apocalyptic noir styling, mobile-first
  manifest.webmanifest   - Installable PWA metadata ("Add to Home Screen")
  service-worker.js      - Offline-first app-shell cache
  js/
    app.js                - Entry point: DOM wiring, screen routing, SW registration
    audio-engine.js        - 4 layered Web Audio channels (MUSIC/VOICE/SFX/AMBIENCE),
                              native StereoPannerNode for spatial zombie audio, fades/ducking
    mission-engine.js      - Drives a mission's JSON timeline, dispatches audio + UI state
    mission-repository.js  - Fetches mission JSON from missions/
    demo-controller.js     - Plays the bundled demo mission through the real MissionEngine
  missions/
    demo_mission.json      - ~95s one-tap demo
    outbreak_signal.json   - ~10 min starter mission ("Start Mission")
  art/story_panels/*.svg   - Original hand-authored noir SVG illustrations (placeholder art)
  icons/icon.svg           - App icon (placeholder)
  audio/                   - Directory structure ready; **no audio files exist yet**
```

### Run it locally

```
cd web
python -m http.server 8087
```

Then open `http://localhost:8087` — or, on your phone, `http://<your-computer's-LAN-IP>:8087` while on the same Wi-Fi, and use the browser's "Add to Home Screen" to install it. A `runzombiez-web` entry is also registered in the workspace's `.claude/launch.json` for the Claude Code browser preview tooling.

### Why a PWA instead of native Android

This dev environment has no Android SDK, no Gradle, and only JDK 8 — none of which can be installed/verified here. A static web app needs none of that: it can be written, served, and tested completely inside this environment (no Android Studio round-trip), and still installs to an Android home screen and runs offline via a service worker. See `DECISIONS.md`.

### Features (current, verified working)

- Home screen: Start Mission / Demo / Stop / Help
- Timeline-driven mission engine (no GPS — deliberately deferred, see spec section 7)
- Layered audio: music/voice/sfx/ambience on independent Web Audio channels with fades + ducking
- Spatial zombie audio via native `StereoPannerNode` panning
- One-tap Demo mode (~95s), plus one ~10-minute starter mission
- Inline SVG story-panel artwork tied to mission timeline events
- Installable (manifest + service worker), works offline once cached
- Dark apocalyptic noir visual theme
- 100% offline-capable, no accounts, no telemetry

### Known issues / gaps

- **No audio assets exist yet.** Missions reference `.mp3` paths under `web/audio/` that aren't there — see `docs/ASSETS.md` for the full list. `AudioEngine` logs a warning and keeps the timeline running silently when an asset 404s (verified in-browser — this is expected right now, not a bug).
- **Placeholder art only.** `icons/icon.svg` and the story panels in `art/story_panels/` are original but simple hand-authored SVGs, not final key art (spec section 41/42).
- No automated tests yet.
- No GitHub remote configured — connect the private repo when you have it (spec section 35).

## Android app

Native Android/Kotlin/Compose, per the original spec (`docs/ORIGINAL_SPEC.md` section 27) — `app/`, `build.gradle.kts`, `settings.gradle.kts` at the repo root. Same architecture as the web version: Compose UI (Home/Mission/Help + navigation), a data-driven `MissionEngine` on the same JSON schema, and a Media3-based `AudioEngine` with a custom `PanAudioProcessor`/`PanningRenderersFactory` for spatial stereo panning on zombie encounters.

**Builds clean: `assembleDebug` succeeds, `lintDebug` passes.** Verified with a real Gradle build, not just written and assumed correct.

### Build requirements

- **Android Studio** (for the SDK/JDK it bundles, and for day-to-day development) — already installed if you're reading this on the dev laptop.
- **A JDK 21 LTS to actually run Gradle with.** Android Studio's bundled JBR is JDK 25 as of this writing, which Gradle 8.7's embedded Kotlin-DSL compiler can't parse (`IllegalArgumentException: 25.0.2`). Point `JAVA_HOME` at a JDK 21 instead — a portable Temurin 21 was downloaded to `%LOCALAPPDATA%\jdk21-portable` for this project; reuse it or install your own.
- **Gradle 8.7**, matching `gradle/wrapper/gradle-wrapper.properties`. No `gradlew`/`gradlew.bat` wrapper is committed (the wrapper jar is a binary file that couldn't be produced in this project's original text-only scaffolding session) — use a standalone Gradle 8.7 install instead of `./gradlew`. A portable copy was placed at `%LOCALAPPDATA%\gradle-portable\gradle-8.7`.
- **Android SDK**: `platform-tools`, `platforms;android-34`, `build-tools;34.0.0`, installed via `sdkmanager` — a portable copy lives at `%LOCALAPPDATA%\Android\Sdk` if not already present from Android Studio's own setup.
- `local.properties` (gitignored, create locally) pointing `sdk.dir` at your SDK install.

### Build it

```
set JAVA_HOME=<path to a JDK 21>
<path to gradle 8.7>\bin\gradle.bat assembleDebug
```

Or just open the project root in Android Studio and let it sync — Studio manages its own Gradle/JDK selection and doesn't hit the JDK 25 issue the CLI path does.

### APK output

`app/build/outputs/apk/debug/app-debug.apk` after a debug build (~11MB). Install to a connected device/emulator with `adb install app/build/outputs/apk/debug/app-debug.apk`.

### Known issues / gaps

- **Not yet installed/run on a device or emulator.** No emulator system image is installed and no physical device was connected during this build session — see `STATUS.md` for next steps.
- No story-panel image display wired up in `MissionScreen.kt` — same gap the web app already closed with inline SVG rendering.
- No automated tests (`app/src/test/` is empty).
- No audio files, placeholder art only — see `docs/ASSETS.md` (shared gap with the web app).

## Deferred (by design, see DECISIONS.md)

- GPS tracking, distance-triggered events, GPS zombie chases
- Workout tracking / statistics
- Inventory / supply system
- External music service integration

## Roadmap

See `docs/ORIGINAL_SPEC.md` sections 24-26 for the future GPS, workout-tracking, and inventory milestones (these apply to whichever platform ends up primary).
