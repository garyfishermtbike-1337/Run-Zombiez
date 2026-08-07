# Run! Zombiez

A private, hands-free, cinematic zombie-survival running experience. Runner 007 leaves Haven Base, puts the phone away, and runs — the mission unfolds through radio transmissions, survivor dialogue, environmental audio, and zombie encounters.

This is a personal project. See `docs/ORIGINAL_SPEC.md` (the full v2.0 master spec) for the complete original product vision, and `DECISIONS.md` for how it's evolved since — most importantly, **the active build is now a mobile web app (PWA), not the native Android app.**

## Current status

**The active MVP is `web/` — a Progressive Web App, built, running, and verified in-browser.** Home → Demo/Start Mission → live mission playback → Stop/Complete → Home all work end to end, tested at a 375×812 mobile viewport. See [`web/README` section below](#web-app-active) and `STATUS.md` for the detailed handoff state.

`app/` (native Android/Kotlin/Compose) is **paused, kept as reference** — it was the original direction per the spec, but this dev environment has no Android SDK/Studio to build it, and the user opted for a website instead. See [Android app (paused)](#android-app-paused) below.

## Web app (active)

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

## Android app (paused)

The original spec called for native Android/Kotlin/Compose (`docs/ORIGINAL_SPEC.md` section 27). That code still lives under `app/`, `build.gradle.kts`, `settings.gradle.kts`, etc., and is a complete Phase 1-4 scaffold — Compose UI, a Media3-based audio engine with custom stereo panning, and the same mission-engine design as the web version. **It has never been compiled** (no Android SDK/Studio/JDK17 in this environment) and is not being actively developed right now. If you want to pick native Android back up later, start from `STATUS.md`'s "What is NOT done" section for the Android-specific risk areas.

## Deferred (by design, see DECISIONS.md)

- GPS tracking, distance-triggered events, GPS zombie chases
- Workout tracking / statistics
- Inventory / supply system
- External music service integration

## Roadmap

See `docs/ORIGINAL_SPEC.md` sections 24-26 for the future GPS, workout-tracking, and inventory milestones (these apply to whichever platform ends up primary).
