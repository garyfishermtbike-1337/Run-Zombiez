# Status — Handoff State

Last updated: 2026-08-10 (Phase 2: audio content, now fully wired). Real audio exists, every sourced file is wired into both missions, and CC BY 4.0 attribution is shown in-app. Both `app/` and `web/` are real, working, and sound-complete for the two existing missions.

## Audio content — sourced and FULLY wired 2026-08-10

30 files, 46MB, covering both missions on both platforms. All 30 are now used in at least one mission:

- **14 voice lines** (`voice/haven_base/`, `voice/runner007/`) — generated with `edge-tts` (free, no account; `en-US-AriaNeural` for Haven Base, `en-US-GuyNeural` for Runner 007). Script lives in `docs/VOICE_SCRIPT.md`, text kept identical to the mission JSON's `text` fields.
- **3 music tracks** (`music/`) — Kevin MacLeod / incompetech.com, CC BY 4.0. **Attribution is now shown in-app** on both platforms' Help screen.
- **12 SFX** (`sfx/zombies/`, `sfx/environment/`, `sfx/ui/`) — Mixkit, no attribution required. The last 4 (`footsteps_running`, `radio_static_in/out`, `alarm`) are now wired too: radio static brackets every Haven Base transmission (timed against each clip's real measured duration via `mutagen`), an alarm cue precedes each mission's warning transmission, and footsteps swap in on the ambience channel during each mission's escape beat.

Full source/license breakdown per file: `docs/ASSETS.md`.

**Engine change to support this:** `MissionEngine`/`mission-engine.js` previously ignored `audioAsset` on `STORY_BEAT` and `MISSION_COMPLETE` events even though the JSON schema supported it. Both now play it — this is what makes the gate-creak, radio-static, alarm, and mission-complete-sting cues all work through the same generic path, no further engine changes needed for this round. `complete()` stops MUSIC/VOICE/AMBIENCE by name instead of `stopAll()`, so the completion sting isn't cut off by its own completion.

**Verified working:** web app confirmed in-browser in a clean tab — Demo flow fetches all audio including the newly-wired SFX (200/206 OK), zero console warnings. (Warnings *did* appear during messier testing with overlapping demo runs across reloads/tabs — traced to test-session artifacts and the single-threaded Python dev server straining under concurrent requests, not a real bug; a fresh isolated tab run was clean.) Android: `assembleDebug` + `lintDebug` both pass clean after this round too (APK ~59MB). One transient `AccessDeniedException` build failure was hit and resolved — this project lives under a OneDrive-synced folder, and OneDrive occasionally locks files under `app/build/` mid-build; clearing the build directory and retrying fixed it. **Still not verified: actually hearing it** — no speaker output was checked in either environment; verification here means "the pipeline loads and plays the right file at the right time," not "it sounds good."

**Service worker cache bumped to `runzombiez-v3`** (was v2) since precached content (index.html, css, mission JSON) changed again this round.

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

## Web app (`web/`) — VERIFIED, live at GitHub Pages

`https://garyfishermtbike-1337.github.io/Run-Zombiez/` (`gh-pages` branch, pushed as a subtree of `web/`). Home/Demo/Start Mission/Stop/Help all confirmed working, now with real audio (including the full SFX set) loading correctly. Two bugs found and fixed in the original 2026-08-07 pass (mission-start hang, stale-speaker-label) — both mirrored in Android's `MissionEngine.kt`.

**Not yet done on web:**
- Offline/installability (manifest + service worker) implemented but not stress-tested beyond initial load — worth an explicit airplane-mode retest now that the cache is ~46MB bigger.
- `gh-pages` needs re-pushing after this round's changes (radio static/alarm/footsteps wiring, Credits line) — confirm it's live before telling the user, same as last time.

## What's NOT done (either platform)

- **Art is placeholder-only** on both platforms; Android has no story-panel display at all yet.
- No automated tests on either platform.
- Android not run on a device/emulator yet — everything verified so far is "loads/plays the right file," not "sounds right" or "runs on real hardware."
- App icon is still a placeholder on both platforms.

## Recommended next steps, in order

1. **Get the Android APK onto a device or emulator and actually listen to it.**
2. Wire up story-panel image display in `MissionScreen.kt` (Android) — web already does this with inline SVG.
3. Write automated tests; nothing exists yet on either platform.
4. Replace placeholder art (app icon, story panels) with final key art.
5. Consider a second mission — both existing ones now have a complete, wired audio pipeline as a template to follow.

## Key files to read first if resuming

- `docs/ORIGINAL_SPEC.md` — the full original product spec, preserved verbatim.
- `DECISIONS.md` — settled decisions, including this session's audio-sourcing choices; don't relitigate without new information.
- `docs/VOICE_SCRIPT.md` — authoritative voice line text, speaker/voice mapping.
- `docs/ASSETS.md` — exactly what's sourced, what's missing, and every license/attribution obligation.
- `README.md` — architecture map for both `web/` and `app/`.
