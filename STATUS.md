# Status — Handoff State

Last updated: 2026-08-10. Native Android (`app/`) resumed and got its first real compiler/lint pass today — it is no longer "paused/unverified." Both platforms are now real, working builds; see each section below for what "working" means on that platform.

## Android app (`app/`) — now BUILT and VERIFIED

Resumed once the user was at a laptop with Android Studio installed. This session's environment had no Android SDK and only JDK 8, so all tooling was set up from scratch, portably (no admin rights used):

- **Android SDK**: cmdline-tools 15859902, `platform-tools`, `platforms;android-34`, `build-tools;34.0.0` — installed via `sdkmanager` under `%LOCALAPPDATA%\Android\Sdk`. (Note: `winget install GitHub.cli`-style machine-wide installs hit a UAC prompt with nobody at the keyboard to approve it earlier in this project's history — same risk applies to any future machine-wide Android tooling install. Prefer portable/user-scope installs.)
- **Gradle 8.7** (standalone, matching `gradle/wrapper/gradle-wrapper.properties`) — no `gradlew` wrapper jar is committed (binary file, couldn't be produced in the original text-only scaffolding session), so build with the standalone `gradle.bat` directly, not `./gradlew`.
- **JDK**: Android Studio's bundled JBR is JDK 25, which is too new for the Kotlin compiler embedded in Gradle 8.7's kotlin-dsl support (`JavaVersion.parse` chokes on `"25.0.2"`, throws `IllegalArgumentException`). **Use a JDK 21 LTS as `JAVA_HOME` for Gradle instead** — Temurin 21 was downloaded portably for this. Don't try to build with the Studio JBR directly.
- `local.properties` (gitignored, machine-specific) points `sdk.dir` at the installed SDK.

**Build result: `./gradlew assembleDebug` (via standalone Gradle + JDK 21) → `BUILD SUCCESSFUL`, first attempt, zero compile errors.** APK produced at `app/build/outputs/apk/debug/app-debug.apk` (~11MB), verified with `aapt2 dump badging`: correct package (`com.rangerdie.runzombiez`), correct min/target SDK (26/34), correct permissions, correct app label.

**Lint**: `lintDebug` initially found 26 real errors — Media3's low-level audio-processor APIs (`BaseAudioProcessor`, `DefaultAudioSink.Builder`, the 2-arg `ExoPlayer.Builder`) are `@UnstableApi`-gated and every one of them was being used without the required opt-in. Fixed by annotating the audio-layer classes (`AudioEngine`, `PanAudioProcessor`, `PanningRenderersFactory`, `MissionViewModel`) with `@UnstableApi`, then adding `app/lint.xml` + `lint { lintConfig = file("lint.xml") }` in `app/build.gradle.kts` for project-wide opt-in rather than chasing the annotation requirement through every caller up to `MainActivity` — this matches Media3's own recommended fix for this exact situation, and matches the already-documented decision to knowingly use this unstable API surface (`DECISIONS.md`). `lintDebug` now passes clean (only informational warnings remain: outdated dependency-version notices, the intentional portrait-lock, a few unused XML color resources duplicated from `ui/theme/Color.kt`, a missing monochrome launcher-icon variant).

**Not yet done on Android:**
- **Not installed/run on a device or emulator.** No emulator system image was installed (only SDK platform/build-tools/platform-tools), and no physical device was connected at last check. This is the next concrete step — see below.
- No automated tests (`app/src/test/` is still empty).
- No audio files, no story-panel art wired to display (see `docs/ASSETS.md` — same gap as the web app).
- App icon is still the placeholder vector hazard-triangle mark.

## Web app (`web/`) — separately built and VERIFIED (2026-08-07)

Still a fully working, independent build — see git history around 2026-08-07 for the original verification pass (Home/Demo/Start Mission/Stop/Help all confirmed working in-browser at a mobile viewport, plus two bugs found and fixed: a mission-start hang if `AudioContext` stays suspended, and a stale-speaker-label bug during non-dialogue events — both fixes are also mirrored in the Android `MissionEngine.kt`). Deployed live at `https://garyfishermtbike-1337.github.io/Run-Zombiez/` via GitHub Pages (`gh-pages` branch, pushed as a subtree of `web/`).

Both platforms share the same mission JSON schema and the same `docs/ASSETS.md` audio/art gap — nothing produced yet on either side.

## What's NOT done (either platform)

- **No audio files exist.** Both `app/src/main/assets/audio/` and `web/audio/` have the right folder structure but zero actual `.mp3`s. See `docs/ASSETS.md`.
- **Art is placeholder-only** on both platforms.
- No automated tests on either platform.
- Web's offline/installability (manifest + service worker) implemented but not stress-tested beyond initial load.

## Recommended next steps, in order

1. **Get the Android APK onto a device or emulator and actually run it.** Two options depending on what's available right now: (a) plug in a physical Android phone with USB debugging enabled and `adb install app/build/outputs/apk/debug/app-debug.apk`, or (b) install an emulator system image (`sdkmanager "system-images;android-34;google_apis;x86_64"` + `avdmanager create avd` + the `emulator` package, none of which are installed yet — this is a meaningfully bigger download, worth confirming before kicking off).
2. Smoke-test the Home → Demo/Start Mission → Stop/Complete → Home flow on-device (spec section 43 checklist), same as was done for the web app.
3. Start producing/sourcing real audio (`docs/ASSETS.md`), beginning with the demo mission's shorter asset list.
4. Wire up story-panel image display in `MissionScreen.kt` (Android) — currently only text/speaker are shown, same gap the web app already closed with inline SVG rendering.
5. Write `app/lint.xml`-aware automated tests; nothing exists yet on either platform.
6. Commit this session's Android build fixes (`@UnstableApi` annotations, `app/lint.xml`, `local.properties` is gitignored so the portable-toolchain paths themselves aren't committed — only source fixes are).

## Key files to read first if resuming

- `docs/ORIGINAL_SPEC.md` — the full original product spec, preserved verbatim (still mostly applicable — GPS deferral, Runner 007/Haven Base identity, dark noir direction, etc.).
- `DECISIONS.md` — settled decisions, including the Media3 `@UnstableApi` project-wide opt-in choice; don't relitigate without new information.
- `README.md` — architecture map for both `web/` and `app/`.
- `docs/ASSETS.md` — exactly what audio/art is missing and where it goes, for either platform.
