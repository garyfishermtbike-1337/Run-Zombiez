# Status — Handoff State

Last updated: 2026-08-11 (first real-device test — found and fixed an actual crash). The app has now genuinely run on a physical phone for the first time in this project's history. It crashed. Now it doesn't. See "First on-device test" below — this is a bigger deal than it sounds, since nothing before this point had ever verified more than "the build compiles."

## First on-device test — 2026-08-11

Connected the user's Samsung Galaxy S24 Ultra over USB (developer mode + USB debugging enabled), installed the current `app-debug.apk` via `adb install`, and drove it with `adb shell input`/`screencap`/`logcat` while the user kept the phone unlocked.

**What worked immediately:** app installs, launches, Home screen renders exactly as designed (hero image, letterboxing genuinely invisible against the black background, all 4 captioned buttons correct), app icon renders cleanly in the launcher/app drawer (the safe-zone crop technique worked as intended — see `DECISIONS.md`).

**What crashed:** tapping DEMO (or Start Mission — same code path) crashed the app almost immediately. `logcat` showed:
```
FATAL EXCEPTION: DefaultDispatcher-worker-3
java.lang.IllegalStateException: Player is accessed on the wrong thread.
Current thread: 'DefaultDispatcher-worker-3'
Expected thread: 'main'
	at androidx.media3.exoplayer.ExoPlayerImpl.getVolume(ExoPlayerImpl.java:1550)
	at com.rangerdie.runzombiez.audio.AudioEngine$fadeTo$1.invokeSuspend(AudioEngine.kt:103)
```
**Root cause:** `AudioEngine`'s `CoroutineScope(SupervisorJob())` had no dispatcher specified, so it defaulted to `Dispatchers.Default` (a background thread pool). ExoPlayer requires all `Player` access on the main thread. Every `play()` call triggers a fade-in via `fadeTo()`, which reads/writes `player.volume` — so this crashed on essentially the first thing a user would ever do. **Fixed** by adding `Dispatchers.Main` to the scope (`app/src/main/java/com/rangerdie/runzombiez/audio/AudioEngine.kt`), matching the pattern `MissionEngine` already used correctly. Rebuilt, reinstalled, reran the exact same steps on the same device: Demo now runs past the crash point cleanly, confirmed via `logcat` showing zero `FATAL EXCEPTION`s and a screenshot of the mission screen mid-playback (Runner 007's line displaying on schedule).

**Why this was never caught before:** no amount of `assembleDebug`/`lintDebug` passing catches a runtime threading violation — only actually running the app does. This bug had been latent since `AudioEngine.kt` was first written, many sessions ago. It's the clearest possible argument for why "builds clean" and "verified" are not the same claim, and why this was the right next priority.

**Not yet verified:** whether the audio is actually audible (the phone's ringer showed a mute icon in several screenshots — unclear if that affects media volume; the user was mid-testing when this session's transcript captured stopped). Story-panel art still isn't wired on Android. No automated tests exist yet to catch a regression of this exact bug class in the future.

## Branding — integrated 2026-08-11

App icon and home screen now use the user-provided key art on both platforms — no more placeholder hazard-triangle icon or plain text title.

## Branding — integrated 2026-08-11

The user provided finished key art (`Run! Zombiez Logo.png`, now `art/branding/logo_master.png`) plus 3 UI-direction mockups. Adopted "Option 1: The Classic Look" only (already the app's existing red/black palette) — no multi-theme switcher, no new nav tabs, per explicit user direction.

- **App icon** (both platforms): a Pillow-cropped detail (runner silhouette + red sun, no title text) used for masked contexts — Android's 5-density adaptive-icon foreground, web's maskable manifest icons — since the OS mask crops both adaptive-icon layers to a ~66% safe zone, not just one. Full-bleed resizes of the raw art used where there's no mask (Android's background layer stays a solid `HavenBlack` vector, web's "any"-purpose icons, `apple-touch-icon.png`). This also fixes a real pre-existing bug: `apple-touch-icon` used to point at an SVG, which iOS Safari silently ignores.
- **Home screen** (both platforms): hero image replaces the plain text title, rendered with `contain`/`ContentScale.Fit` (not cropped) — the art's background is nearly black already, so letterboxing is invisible. The 4 existing buttons (same function, same IDs) gained two-line label+caption styling; captions use each mission's real duration (verified from the JSON, not the mockup's placeholder numbers).
- Source mockup PNGs relocated from repo root into `art/branding/` and `art/concept/`.

**Verified:** Android `assembleDebug`+`lintDebug` clean (hit the same OneDrive file-lock issue as before — see below — resolved the same way). Web verified in-browser: hero loads at correct size with no clipping, all 4 buttons render two clean lines with no overflow, no console errors, no horizontal scroll, service worker correctly rebuilt cache under `runzombiez-v4` with new assets precached. Live GitHub Pages site re-verified with the new branding after a normal Pages-build wait (not the CDN-propagation-lag issue seen earlier — this time the build itself just hadn't finished yet when first checked).

**New recurring build gotcha, now documented twice:** this project lives in a OneDrive-synced folder. OneDrive occasionally locks files under `app/build/` mid-build, causing `AccessDeniedException` on `mergeDebugResources`. Fix: `gradle --stop`, and if that doesn't fully release the daemon (check `tasklist | grep java`), kill the lingering `java.exe` PID directly, delete `app/build/`, retry. Not a code issue — don't waste time debugging the build itself when this happens.

**Not done:** the other 2 mockup color directions and the theme-switcher concept were explicitly deferred (see `DECISIONS.md`), not forgotten.

## Audio content — sourced and FULLY wired 2026-08-10

30 files, 46MB, covering both missions on both platforms. All 30 are now used in at least one mission:

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

1. **Get the Android APK onto a device or emulator and actually see/hear it.** Still hasn't happened at all — everything verified so far is "loads/plays/renders the right thing," not "works on real hardware."
2. Wire up story-panel image display in `MissionScreen.kt` (Android) — web already does this with inline SVG. App icon/hero art is done; the mission-timeline artwork gap is still open.
3. Write automated tests; nothing exists yet on either platform.
4. Consider a second mission — both existing ones now have a complete, wired audio pipeline and finished branding as a template to follow.
5. If desired later: the deferred multi-theme switcher and the other 2 mockup color directions (see `DECISIONS.md`).

## Key files to read first if resuming

- `docs/ORIGINAL_SPEC.md` — the full original product spec, preserved verbatim.
- `DECISIONS.md` — settled decisions, including this session's audio-sourcing choices; don't relitigate without new information.
- `docs/VOICE_SCRIPT.md` — authoritative voice line text, speaker/voice mapping.
- `docs/ASSETS.md` — exactly what's sourced, what's missing, and every license/attribution obligation.
- `README.md` — architecture map for both `web/` and `app/`.
