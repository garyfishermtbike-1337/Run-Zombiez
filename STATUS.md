# Status — Handoff State

Last updated: 2026-08-11. Four things happened today: (1) first real-device test — found and fixed an actual crash (see below, this is a bigger deal than it sounds), (2) the Home screen buttons were rebuilt a second time to match the "Classic Look" mockup precisely after the user pointed back at it, (3) both were re-verified on the physical device later the same day once the ADB connection (which had dropped) was restored, and (4) the user said the app *still* didn't look like the mockup after all that — turned out to be the hero image, not the buttons, and got fixed (full-bleed rebuild, see "Hero image full-bleed rebuild" below).

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

**Not yet verified at the time:** whether the audio is actually audible (the phone's ringer showed a mute icon in several screenshots — unclear if that affects media volume; the user was mid-testing when this session's transcript captured stopped). Story-panel art still isn't wired on Android. No automated tests exist yet to catch a regression of this exact bug class in the future.

## Second on-device test — 2026-08-11 (same day, after phone reconnected)

The phone dropped its USB connection between sessions — Windows saw the phone's other USB interfaces as `OK` but the "ADB Interface" specifically showed `Unknown` status (a driver/enumeration issue, not a phone/cable/port problem). `Disable-PnpDevice`/`Enable-PnpDevice` failed with "Generic failure" (PowerShell wasn't elevated). Fixed from the phone side instead: toggled Developer Options → USB debugging off/on and replugged the cable, which forced Windows to re-enumerate the ADB interface — `adb devices` saw the phone again immediately after.

Reinstalled the current `app-debug.apk` (build timestamp confirmed newer than the last `HomeScreen.kt` edit, so it already contained the Classic Look rebuild), launched it, no crash.

**Classic Look buttons — confirmed correct on hardware:** screenshot shows hero image, then Demo / Start Mission / (Stop, Help) in that order, single-line icon+label+trailing-icon styling, thin borders, red border+text on Stop only, no filled buttons — matches Option 1 exactly.

**Audio — confirmed actually playing, not just wired:** the phone's ringer was in vibrate/silent mode (status bar icon), and the media (`STREAM_MUSIC`) volume was at **3/15** on the speaker — not muted, but low enough to easily seem silent. Raised to 10/15 via `adb shell input keyevent KEYCODE_VOLUME_UP`. Tapped DEMO; `dumpsys media.audio_flinger` showed **2 active AudioTrack entries for the app's PID, non-zero gain (-5.2dB / -9.1dB), buffers full (FrmRdy = FrmCnt)** — real playback, not just a registered-but-idle media session. Screenshot confirmed the mission screen ("DEMO: First Contact") was showing narrative text in sync. This closes out the one thing that had been unconfirmed since audio was first wired on 2026-08-10: **the audio pipeline does produce real, audible sound on hardware** — the earlier uncertainty was a low media-volume setting on this specific phone, not a bug in the app.

Task #37 ("re-verify Classic Look buttons on physical device") is now complete.

## Hero image full-bleed rebuild — 2026-08-11 (same day, after the second on-device test)

After the button rebuild was confirmed correct on-device, the user pasted fresh crops of the Option 1 mockup and said the app still didn't look like the picture. Comparing carefully: the buttons were actually fine — the real gap was the **hero image**. It was rendered small and letterboxed (`heightIn(max = 220.dp)` / `max-height: 220px; object-fit: contain`) specifically to avoid cropping the baked-in title text, but that made it read as tiny and boxed-in versus the mockup's large, full-bleed treatment (~55% of screen height, edge-to-edge width).

Fixed by re-cropping `art/branding/logo_master.png` and changing both platforms' layout to full-bleed:
- **New crop**: the source file has a rounded-square border baked into its pixels (confirmed by direct pixel sampling — not visible at a glance, only under zoom). A naive edge-to-edge crop showed that border as visible curved lines in the screen's real rectangular corners. Final crop box `(140, 110, 1114, 1164)` on the 1254×1254 source clears the border on all 4 corners (verified by rendering each corner at 3-4x zoom) while keeping the title, runner, and Haven Base flag fully intact. Trade-off: trims some outer background zombies and the small heartbeat-squiggle decoration at the very bottom — both deliberate, discussed with the user beforehand in plan mode.
- **Android** (`HomeScreen.kt`): hero is now `fillMaxWidth()` + `ContentScale.FillWidth`, no height cap, zero top/side padding — flush against the top of the screen (below the status bar; no edge-to-edge/insets work needed since the app has no existing insets code, so this is already the default). Button padding moved to its own inner `Column` so buttons stay inset while the hero doesn't.
- **Web** (`style.css`): `#screen-home` gets its own padding override (drops the shared `.screen` padding, keeps `env(safe-area-inset-top)`), `.hero` drops its `max-height`/`object-fit` cap, `.button-stack` gets its own horizontal padding to compensate.
- **Cache-busted**: service worker `CACHE_NAME` bumped `v5` → `v6` (hero filename is unchanged, only its bytes changed, so a version bump was required to evict the old cached image).

**Verified:** Android `assembleDebug` + `lintDebug` clean (hit the OneDrive file-lock issue a 5th time — same fix as always: kill the lingering `java.exe` daemon, delete `app/build/`, retry). Web verified via computed layout metrics in the Browser preview (screenshot tooling was down again this session) — confirmed hero renders at full viewport width with zero left offset and height scaling proportionally to the new crop's aspect ratio, buttons retain their 24px inset.

**Not yet verified:** on-device visual confirmation on the physical phone — it disconnected again right as Android build finished (all 3 USB interfaces dropped to "Unknown" in Windows this time, not just the ADB one). User is reconnecting it now.

## Branding — integrated 2026-08-11, refined same day to match "Classic Look" exactly

The user provided finished key art (`Run! Zombiez Logo.png`, now `art/branding/logo_master.png`) plus 3 UI-direction mockups (`art/concept/`). Adopted "Option 1: The Classic Look" only (already the app's existing red/black palette) — no multi-theme switcher, no new nav tabs, per explicit user direction.

- **App icon** (both platforms): a Pillow-cropped detail (runner silhouette + red sun, no title text) used for masked contexts — Android's 5-density adaptive-icon foreground, web's maskable manifest icons — since the OS mask crops both adaptive-icon layers to a ~66% safe zone, not just one. Full-bleed resizes of the raw art used where there's no mask (Android's background layer stays a solid `HavenBlack` vector, web's "any"-purpose icons, `apple-touch-icon.png`). This also fixes a real pre-existing bug: `apple-touch-icon` used to point at an SVG, which iOS Safari silently ignores.
- **Home screen hero**: image replaces the plain text title, rendered with `contain`/`ContentScale.Fit` (not cropped) — the art's background is nearly black already, so letterboxing is invisible.
- **Home screen buttons — rebuilt a second time same day** after the user pointed back at the Option 1 mockup specifically: single-line icon + label + optional trailing icon, no caption/subtitle text (the first pass had added two-line captions, closer to Option 2/3's style — removed). Order is Demo → Start Mission → (Stop, Help side-by-side row), matching the mockup; previously Start Mission was first and solid-red-filled as a "primary" button, which Option 1 doesn't actually show — no button in Option 1 is filled, all are dark with a thin border (red only for Stop). Icons are plain Unicode/emoji glyphs (💀 👣 ⏹ ❓ ▶ ›), not a new icon-library dependency. See `DECISIONS.md` for full reasoning.
- Source mockup PNGs relocated from repo root into `art/branding/` and `art/concept/`.

**Verified:** Android `assembleDebug`+`lintDebug` clean on both passes (hit the OneDrive file-lock issue twice more — see below — plus two real compile errors this round: a nonexistent `OutlinedButtonDefaults` API used instead of `ButtonDefaults.outlinedButtonColors()`, and an incorrectly-imported internal `weight` function that should resolve implicitly via `RowScope` — both fixed). Web verified in-browser both passes: hero loads at correct size with no clipping, buttons render correctly with no overflow, no console errors, no horizontal scroll, service worker correctly rebuilt cache each time (`v4` then `v5`) with new assets precached, Demo flow still dispatches correctly after the full button markup rewrite. `gh-pages` re-pushed after the Classic Look pass and verified live via `curl` (the `btn-bordered` class confirmed present in the served HTML). Android buttons re-verified on the physical device same day — see "Second on-device test" above.

**Recurring build gotcha, now hit 4 times:** this project lives in a OneDrive-synced folder. OneDrive occasionally locks files under `app/build/` mid-build (`mergeDebugResources`, or this round also `compileDebugKotlin`'s cache directory), causing `AccessDeniedException`/`IOException: Unable to delete directory`. Fix is always the same: `gradle --stop`, check `tasklist | grep java` for a lingering daemon and kill it directly if `--stop` didn't fully release it, delete `app/build/`, retry. Not a code issue — don't waste time debugging the build itself when this happens; just redo the routine.

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

## What's NOT done (either platform)

- **Story-panel art is placeholder-only** on both platforms; Android has no story-panel display at all yet (web already shows inline SVG panels).
- No automated tests on either platform.

## Recommended next steps, in order

1. **Re-verify the full-bleed hero on the physical device** once it reconnects — build-verified and web-verified, not yet visually confirmed on hardware.
2. **Push `gh-pages`** with the hero fix — web's local files are updated, live site is not yet re-pushed.
3. Wire up story-panel image display in `MissionScreen.kt` (Android) — web already does this with inline SVG.
4. Write automated tests; nothing exists yet on either platform, including nothing that would catch a regression of the `Dispatchers.Main` crash class.
5. Consider a second mission — both existing ones now have a complete, wired audio pipeline and finished branding as a template to follow.
6. If desired later: the deferred multi-theme switcher and the other 2 mockup color directions (see `DECISIONS.md`).

## Key files to read first if resuming

- `docs/ORIGINAL_SPEC.md` — the full original product spec, preserved verbatim.
- `DECISIONS.md` — settled decisions, including this session's audio-sourcing choices; don't relitigate without new information.
- `docs/VOICE_SCRIPT.md` — authoritative voice line text, speaker/voice mapping.
- `docs/ASSETS.md` — exactly what's sourced, what's missing, and every license/attribution obligation.
- `README.md` — architecture map for both `web/` and `app/`.
