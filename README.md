# Run! Zombiez

A private, hands-free, cinematic zombie-survival running experience for Android. Runner 007 leaves Haven Base, puts the phone away, and runs — the mission unfolds through radio transmissions, survivor dialogue, environmental audio, and zombie encounters.

This is a personal project. See `docs/ORIGINAL_SPEC.md` (the full v2.0 master spec) for the complete product vision, or the summary below for where things currently stand.

## Current status

**MVP Phase 1-4 scaffolded.** Buildable project structure, dark-noir Compose UI, a data-driven mission engine, a Media3-based layered audio engine with spatial pan, a bundled Demo mode, and one full-length starter mission — all in place as source. **Not yet built or run** in this environment; see [Build Requirements](#build-requirements) below.

See `STATUS.md` for the detailed handoff state (what works, what's untested, next steps).

## Architecture

```
app/src/main/java/com/rangerdie/runzombiez/
  MainActivity.kt          - Compose NavHost: Home -> Mission -> Help
  RunZombiezApp.kt          - Application class
  ui/
    theme/                   - Dark apocalyptic noir palette + type (Compose)
    screens/                 - HomeScreen, MissionScreen, HelpScreen
    MissionViewModel.kt      - Owns the AudioEngine + MissionEngine for the process lifetime
  mission/
    Mission.kt               - Data model: Mission, MissionEvent, MissionEventType, AudioChannel
    MissionRepository.kt     - Loads mission JSON from assets/missions/
    MissionEngine.kt         - Drives a mission's timeline, dispatches audio + UI state
  audio/
    AudioEngine.kt           - 4 layered ExoPlayer channels (MUSIC/VOICE/SFX/AMBIENCE), fades, ducking
    PanAudioProcessor.kt     - Custom Media3 AudioProcessor for stereo zombie panning
    PanningRenderersFactory.kt - Wires PanAudioProcessor into the SFX channel's ExoPlayer
  demo/
    DemoController.kt        - Loads and plays the bundled demo mission
```

Missions are plain JSON under `app/src/main/assets/missions/`. Audio is referenced by relative path under `app/src/main/assets/audio/` (see `docs/ASSETS.md` — **audio files themselves are not yet produced**; the engine and mission data are ready for them).

## Requirements

- **Android Studio** (current stable) — bundles a compatible JDK and can install the Android SDK for you.
- Android SDK Platform 34, Build-Tools matching AGP 8.5.2.
- A device or emulator running API 26+ (Android 8.0+).

## Build Requirements

This project was scaffolded in an environment **without** Android Studio, the Android SDK, or a JDK newer than 8. It has **not been compiled or run**. To build it:

1. Open the `RunZombiez/` folder in Android Studio.
2. Let Android Studio sync Gradle (it will resolve the wrapper and any missing SDK components automatically).
3. Build → Make Project. Fix any compile errors that surface — this codebase has been written carefully against the Media3/Compose APIs but has not been verified by an actual compiler.
4. Run on a device/emulator, or Build → Generate Signed/Unsigned APK.

The one area most likely to need a small fix on first build is `audio/PanAudioProcessor.kt` and `audio/PanningRenderersFactory.kt` — the exact `BaseAudioProcessor`/`DefaultAudioSink` method signatures can shift slightly between Media3 releases; see the comment at the top of `PanAudioProcessor.kt`.

## APK output

Once buildable: debug APK lands at `app/build/outputs/apk/debug/app-debug.apk`; release at `app/build/outputs/apk/release/`.

## Features (current)

- Home screen: Start Mission / Demo / Stop / Help
- Timeline-driven mission engine (no GPS required — spec deliberately defers GPS)
- Layered audio: music, voice, sfx, ambience each on independent channels with fades and ducking
- Spatial zombie audio via stereo panning
- One-tap Demo mode (bundled `demo_mission.json`, ~95s)
- One bundled starter mission (`outbreak_signal.json`, ~10 min) as a template for future missions
- Dark apocalyptic noir visual theme
- 100% offline, no accounts, no telemetry

## Deferred (by design, see DECISIONS.md)

- GPS tracking, distance-triggered events, GPS zombie chases
- Workout tracking / statistics
- Inventory / supply system
- External music service integration

## Known issues / gaps

- **No audio assets yet.** Missions reference audio files under `assets/audio/` that don't exist — see `docs/ASSETS.md`. The app will currently throw on missing asset files when a mission tries to play them; wrap `AudioEngine.play` more defensively once real audio is added, or add silence/placeholder tracks so the timeline still runs.
- **No app icon art** beyond a placeholder vector hazard-triangle mark.
- **Never compiled.** See Build Requirements above.
- No automated tests yet beyond the module structure (`app/src/test/`).

## Roadmap

See `docs/ORIGINAL_SPEC.md` sections 24-26 for the future GPS, workout-tracking, and inventory milestones.
