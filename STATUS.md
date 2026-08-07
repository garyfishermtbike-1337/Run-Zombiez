# Status — Handoff State

Last updated: 2026-08-07, same session as initial scaffolding — pivoted from native Android to a web app (PWA) partway through, per user direction ("let's just make it a website that I can access on my phone").

## What's completed and VERIFIED (web app, `web/`)

This is the active build. Verified in-browser at a 375×812 mobile viewport via the Claude Code browser preview tooling (server: `python -m http.server` in `web/`, also registered as `runzombiez-web` in the workspace's `.claude/launch.json`):

- Home screen renders with all four buttons (Start Mission / Demo / Stop / Help).
- **Demo flow**: tapping Demo navigates to the mission screen, the timeline dispatches on schedule (Haven Base intro → Runner 007 ack → briefing → story beat → zombie encounters, etc.), speaker/text update correctly, inline SVG story-panel artwork renders at the right timeline offset.
- **Start Mission flow**: loads and starts `outbreak_signal.json` (the ~10 min starter mission) correctly.
- **Stop flow**: stops the mission engine and returns cleanly to Home from the mission screen.
- **Help flow**: navigates to Help and back.
- Missing audio assets (expected — none exist yet) log a console warning via `Player`-style error handling and the mission timeline keeps running silently, exactly as designed. No crashes, no hangs.
- No console errors during any of the above beyond the expected 404s for missing `.mp3` files.
- Service worker registers without error (offline caching not separately stress-tested — e.g. no explicit airplane-mode/reload test performed yet).

### Bugs found and fixed during this verification pass

1. **`audioEngine.resume()` could hang navigation forever.** `btn-start`/`btn-demo` click handlers originally did `await audioEngine.resume()` before switching screens. If the browser's autoplay policy keeps the `AudioContext` suspended, that promise can stay pending indefinitely, silently freezing the whole app on the Home screen. Fixed: `resume()` is now fire-and-forget; screen navigation and mission start never wait on it. (`web/js/app.js`)
2. **Stale speaker label.** `currentSpeaker` was carried forward from the last dialogue line even during non-dialogue events (zombie encounters, story beats), so e.g. a zombie-encounter narration line could display under a "HAVEN BASE" label left over from an earlier line. Fixed in `web/js/mission-engine.js` — and, since the same bug exists in the parallel Kotlin implementation, also fixed in `app/.../mission/MissionEngine.kt` even though that code hasn't been compiled.

## What's completed but NOT verified (Android app, `app/` — paused)

Written for the original native-Android direction before the user pivoted to a website. Kept as reference, not deleted. **Never compiled** — this environment has JDK 8 only, no Android SDK, no Gradle, no Android Studio.

- Full Gradle/Android project structure, dark-noir Compose theme, Home/Mission/Help screens + navigation.
- `Mission`/`MissionEngine`/`MissionRepository` (same JSON schema as the web version).
- `AudioEngine` (Media3/ExoPlayer, 4 channels) + a custom `PanAudioProcessor`/`PanningRenderersFactory` for stereo pan — this pairing is the least-verified code in the whole project; flagged in `README.md`.
- `DemoController`, two bundled mission JSON files, placeholder adaptive launcher icon.
- If ever resumed: open in Android Studio, sync, fix whatever the compiler finds — expect the most friction around the pan-processor Media3 API surface and the pinned dependency versions.

## What's NOT done (either platform)

- **No audio files exist.** Both `app/src/main/assets/audio/` and `web/audio/` have the right folder structure but zero actual `.mp3`s. See `docs/ASSETS.md` for the exact file list (same list serves both platforms since paths were kept aligned).
- **Art is placeholder-only.** Web has original hand-authored SVG story panels + an SVG app icon (see `docs/ASSETS.md`); Android has only the SVG launcher icon, no story panels wired to display at all.
- No automated tests on either platform.
- No git remote / GitHub connection yet (spec section 35) — needs the user to provide/confirm the private repo.
- Offline/installability (manifest + service worker) implemented but not stress-tested (e.g. verifying the app truly works with no network after first load, or that "Add to Home Screen" produces a good icon on a real phone).

## Recommended next steps, in order

1. Get the web app on an actual phone: serve it on the LAN (or deploy it somewhere reachable), open in mobile Chrome, "Add to Home Screen," and confirm it looks/feels right outside a simulated viewport.
2. Start producing/sourcing real audio (`docs/ASSETS.md`), beginning with the demo mission's shorter asset list — highest visibility per file produced.
3. Consider whether static hosting (GitHub Pages, Netlify, etc. — all free-tier, per spec section 40) is wanted so the phone doesn't depend on this dev machine being on and serving locally. Not done yet; ask before deploying anywhere external.
4. `git add`/commit the `web/` addition (already done as of this handoff — check `git log`), then connect a GitHub remote once the user provides one.
5. If native Android is ever picked back up: get it into Android Studio and start from the "What's completed but NOT verified" section above.

## Key files to read first if resuming

- `docs/ORIGINAL_SPEC.md` — the full original product spec, preserved verbatim (still mostly applicable — GPS deferral, Runner 007/Haven Base identity, dark noir direction, etc. — except section 27's native-Android mandate, superseded per `DECISIONS.md`).
- `DECISIONS.md` — settled decisions including the Android→web pivot; don't relitigate without new information.
- `README.md` — current architecture map for both `web/` (active) and `app/` (paused).
- `docs/ASSETS.md` — exactly what audio/art is missing and where it goes, for either platform.
