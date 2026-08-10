# Status — Handoff State

Last updated: 2026-08-10 (Phase 2: audio content). Real audio now exists and is wired into both platforms — this is the biggest single jump in "feels like the actual app" so far. Both `app/` and `web/` are real, working, and now have sound.

## Audio content — NEW, sourced and wired 2026-08-10

30 files, 46MB, covering both missions on both platforms:

- **14 voice lines** (`voice/haven_base/`, `voice/runner007/`) — generated with `edge-tts` (free, no account; `en-US-AriaNeural` for Haven Base, `en-US-GuyNeural` for Runner 007). Script lives in `docs/VOICE_SCRIPT.md`, text kept identical to the mission JSON's `text` fields.
- **3 music tracks** (`music/`) — Kevin MacLeod / incompetech.com, CC BY 4.0. **Attribution is required and not yet shown anywhere in either app — see `docs/ASSETS.md` TODO before calling this shippable.**
- **12 SFX** (`sfx/zombies/`, `sfx/environment/`, `sfx/ui/`) — Mixkit, no attribution required. 4 of these (`footsteps_running`, `radio_static_in/out`, `alarm`) are sourced but not yet wired into any mission timeline.

Full source/license breakdown per file: `docs/ASSETS.md`.

**Engine change to support this:** `MissionEngine`/`mission-engine.js` previously ignored `audioAsset` on `STORY_BEAT` and `MISSION_COMPLETE` events even though the JSON schema supported it. Both now play it — this is what makes the gate-creak and mission-complete-sting cues work. `complete()` now stops MUSIC/VOICE/AMBIENCE by name instead of `stopAll()`, so the completion sting isn't cut off by its own completion.

**Verified working:** web app confirmed in-browser — Demo flow now fetches real audio files (200 OK: `main_theme.mp3`, `voice/haven_base/intro.mp3`, `voice/runner007/ack.mp3`, `gate_creak_open.wav`, `street_wind.wav`, `tension_theme.mp3`, ...) instead of the previous expected 404s, zero console errors. Android: `assembleDebug` + `lintDebug` both still pass clean after wiring the same audio in (APK is now ~59MB, up from ~11MB). **Not yet verified: actually hearing it** — no speaker output was checked in either environment; verification here means "the pipeline loads and plays the right file at the right time," not "it sounds good."

**Service worker cache bumped to `runzombiez-v2`** since precached mission JSON content changed — needed so anyone with the PWA already installed doesn't get stuck serving stale JSON forever under the cache-first strategy.

## Android app (`app/`) — BUILT and VERIFIED

- **Android SDK**: cmdline-tools 15859902, `platform-tools`, `platforms;android-34`, `build-tools;34.0.0` — installed via `sdkmanager` under `%LOCALAPPDATA%\Android\Sdk`, portably (no admin rights — see `DECISIONS.md` re: an earlier winget/UAC hang in this project).
- **Gradle 8.7** (standalone, matching `gradle/wrapper/gradle-wrapper.properties`) — no `gradlew` wrapper jar is committed, build with the standalone `gradle.bat` directly.
- **JDK**: use a JDK 21 LTS as `JAVA_HOME`, not Android Studio's bundled JBR 25 — Gradle 8.7's embedded Kotlin-DSL compiler can't parse `"25.0.2"`.
- `local.properties` (gitignored, machine-specific) points `sdk.dir` at the installed SDK.

**Build result:** `assembleDebug` → `BUILD SUCCESSFUL`. `lintDebug` → clean (26 initial `@UnstableApi` errors fixed via `app/lint.xml` project-wide opt-in — see `DECISIONS.md`). APK at `app/build/outputs/apk/debug/app-debug.apk`, verified with `aapt2 dump badging`.

**Not yet done on Android:**
- **Not installed/run on a device or emulator.** No emulator system image installed, no physical device connected at last check.
- No automated tests (`app/src/test/` is still empty).
- No story-panel art wired to display — `MissionScreen.kt` shows text/speaker only, same gap the web app already closed with inline SVG.
- App icon is still the placeholder vector hazard-triangle mark.
- Music attribution not shown anywhere in the UI (CC BY 4.0 requirement — see above).

## Web app (`web/`) — VERIFIED, live at GitHub Pages

`https://garyfishermtbike-1337.github.io/Run-Zombiez/` (`gh-pages` branch, pushed as a subtree of `web/`). Home/Demo/Start Mission/Stop/Help all confirmed working, now with real audio loading correctly (see above). Two bugs found and fixed in the original 2026-08-07 pass (mission-start hang, stale-speaker-label) — both mirrored in Android's `MissionEngine.kt`.

**Not yet done on web:**
- Music attribution not shown anywhere in the UI.
- Offline/installability (manifest + service worker) implemented but not stress-tested beyond initial load — worth an explicit airplane-mode retest now that the cache is ~46MB bigger.
- The GitHub Pages deployment (`gh-pages` branch) has **not been re-pushed since the audio was added** — it still only has the pre-audio web app. Re-run `git subtree push --prefix=web origin gh-pages` (or equivalent) before telling the user the live site has sound.

## What's NOT done (either platform)

- **Music attribution missing** (CC BY 4.0 requirement) — highest-priority remaining gap, see above.
- **Art is placeholder-only** on both platforms; Android has no story-panel display at all yet.
- 4 sourced SFX not wired into any timeline (`footsteps_running`, `radio_static_in/out`, `alarm`).
- No automated tests on either platform.
- Android not run on a device/emulator yet.
- `gh-pages` branch is stale relative to `main` (missing the audio commit).

## Recommended next steps, in order

1. **Push the audio update to `gh-pages`** so the live web app actually has sound — currently only `main` has it.
2. Add a Credits/About surface on both platforms crediting Kevin MacLeod / incompetech.com per CC BY 4.0 (a line on the Help screen would satisfy this on web; Android has no Help-equivalent screen with real content yet, `HelpScreen.kt` exists though).
3. **Get the Android APK onto a device or emulator and actually listen to it** — this hasn't been done at all yet; everything verified so far is "loads/plays the right file," not "sounds right."
4. Decide whether to wire in the remaining 4 SFX (radio static in/out around each transmission, footsteps under running ambience, alarm during an escalation) or leave them for later polish.
5. Wire up story-panel image display in `MissionScreen.kt` (Android).
6. Write automated tests; nothing exists yet on either platform.

## Key files to read first if resuming

- `docs/ORIGINAL_SPEC.md` — the full original product spec, preserved verbatim.
- `DECISIONS.md` — settled decisions, including this session's audio-sourcing choices; don't relitigate without new information.
- `docs/VOICE_SCRIPT.md` — authoritative voice line text, speaker/voice mapping.
- `docs/ASSETS.md` — exactly what's sourced, what's missing, and every license/attribution obligation.
- `README.md` — architecture map for both `web/` and `app/`.
