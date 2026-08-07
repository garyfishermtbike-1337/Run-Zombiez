# Status — Handoff State

Last updated: 2026-08-07, initial scaffolding session.

## What's completed

- Full Gradle/Android project structure (`settings.gradle.kts`, root and `:app` `build.gradle.kts`, manifest, `gradle-wrapper.properties`).
- Package layout: `ui/`, `mission/`, `audio/`, `demo/` under `com.rangerdie.runzombiez`.
- Dark apocalyptic noir Compose theme (`ui/theme/`) — dark-only color scheme, cinematic type scale.
- Home screen (Start Mission / Demo / Stop / Help), Mission playback screen, Help screen, wired together with Compose Navigation in `MainActivity.kt`.
- Mission data model + JSON schema (`mission/Mission.kt`) using kotlinx.serialization.
- `MissionRepository` loading mission JSON from `assets/missions/`.
- `MissionEngine` driving a mission's timeline, dispatching audio events and exposing UI state via `StateFlow`.
- `AudioEngine`: four independent Media3 ExoPlayer channels (MUSIC/VOICE/SFX/AMBIENCE) with coroutine-based fades and ducking.
- `PanAudioProcessor` + `PanningRenderersFactory`: custom stereo panning for spatial zombie audio on the SFX channel.
- `DemoController`: plays the bundled demo mission through the real `MissionEngine`.
- Two bundled mission JSON files: `demo_mission.json` (~95s showcase) and `outbreak_signal.json` (~10 min starter mission, the one "Start Mission" currently launches).
- Placeholder adaptive launcher icon (vector hazard triangle).
- `README.md`, `DECISIONS.md`, `docs/ASSETS.md`, `docs/ORIGINAL_SPEC.md` (full spec preserved verbatim).

## What is NOT done / NOT verified

- **The project has never been compiled.** This environment has JDK 8 only, no Android SDK, no Gradle, no Android Studio. Everything above was written by careful reading of the Media3/Compose/Kotlin APIs, not by an actual compiler. **The first real task for whoever picks this up with a working Android Studio install is: open the project, sync, and fix whatever the compiler finds.**
- Most likely trouble spots, ranked by risk:
  1. `audio/PanAudioProcessor.kt` + `audio/PanningRenderersFactory.kt` — `BaseAudioProcessor`/`DefaultAudioSink.Builder` method signatures have shifted across Media3 releases; verify against the actual 1.3.1 sources.
  2. `app/build.gradle.kts` dependency versions — AGP 8.5.2 / Kotlin 1.9.24 / Compose BOM 2024.06.00 / Media3 1.3.1 were chosen as a mutually-compatible stable set from training knowledge, not verified against Maven Central at write time.
  3. `gradle/wrapper/gradle-wrapper.properties` exists but the wrapper **jar and `gradlew`/`gradlew.bat` scripts were not generated** (binary file, couldn't be produced in this text-only environment). Android Studio will handle this automatically on first "Open Project," or run `gradle wrapper` once a system Gradle is available.
- **No audio files exist.** Missions reference `.mp3` paths under `assets/audio/` that aren't there yet — see `docs/ASSETS.md` for the full list. `AudioEngine` now logs a warning via a `Player.Listener.onPlayerError` callback instead of crashing when an asset can't be resolved, so a mission's timeline/UI should still run silently end to end even with zero audio files present — this is untested (see top of this doc: nothing has been compiled).
- **No image rendering yet.** `MissionEvent.artwork` / `Mission.storyArtwork` are parsed into the data model and surfaced in `MissionUiState`, but `MissionScreen.kt` only ever displays the event *text*, not an actual image. Wiring up `Image`/`AsyncImage` display of story panels is unstarted.
- No git repository yet (in progress — see next section).
- No automated tests written (`app/src/test/` directory exists but is empty).
- No GitHub remote configured (spec section 35 — needs the user to point at the private repo; not created here).
- App icon is a placeholder, not final key art.

## Recommended next steps, in order

1. Get this project open in Android Studio and let it sync/build. Fix compiler errors — expect the most work around the audio pan processor and dependency version pins.
2. Smoke-test the Home → Demo → Mission → Complete → Home flow end to end (spec section 43 checklist) with silence standing in for missing audio, to validate the timeline/state-machine logic independent of asset production.
3. Start producing/sourcing the audio listed in `docs/ASSETS.md`, beginning with the demo mission's assets (shortest list, highest visibility).
4. Wire up story-panel image display in `MissionScreen.kt` once panel art exists.
5. `git init`, first commit, then connect the private GitHub repo when the user provides/confirms it.

## Key files to read first if resuming

- `docs/ORIGINAL_SPEC.md` — the full product spec, preserved verbatim.
- `DECISIONS.md` — settled decisions, don't relitigate without new information.
- `README.md` — architecture map and build instructions.
- `docs/ASSETS.md` — exactly what audio/art is missing and where it goes.
