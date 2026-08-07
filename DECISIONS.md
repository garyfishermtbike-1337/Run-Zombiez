# Decision Log

Lightweight record of settled architectural/product decisions. Don't reopen these without a concrete new reason — see spec section 46.

| Decision | Date | Notes |
|---|---|---|
| Native Android, Kotlin + Jetpack Compose | 2026-08-07 | Per spec section 27. AGP 8.5.2, Kotlin 1.9.24, compileSdk/targetSdk 34, minSdk 26. |
| Timeline-driven MVP mission engine | 2026-08-07 | GPS deferred (spec section 7). Missions fire events at fixed `atSeconds` offsets from mission start. |
| GPS tracking deferred | 2026-08-07 | Not abandoned — future milestone per spec section 25. Mission/event model designed so GPS-triggered events can hook into the same `MissionEngine.dispatch` path later. |
| GPS zombie chases deferred | 2026-08-07 | Zombie encounters exist now as timeline-triggered audio events; chase mechanics (speed/distance-based) are future work (spec section 8). |
| Offline-first, no accounts | 2026-08-07 | All missions bundled as JSON under `assets/missions/`; audio bundled under `assets/audio/` (spec sections 13-14). |
| Runner identity = Runner 007 | 2026-08-07 | Used directly in dialogue/UI copy. |
| Survivor settlement = Haven Base | 2026-08-07 | Used directly in dialogue/UI copy. |
| Imperial units | 2026-08-07 | No workout-tracking UI yet to apply this to, but locked in for when it lands (spec section 16). |
| Dark apocalyptic noir visual direction | 2026-08-07 | Implemented as a Compose `darkColorScheme` only — no light theme (spec section 21). Palette: Haven Black `#0A0A0B`, Ash Gray `#2B2B2E`, Bone White `#E8E4DC`, Warning Red `#B0231C`, Emergency Amber `#D98324`, Static Green `#3A5A40`. |
| One-tap Demo required | 2026-08-07 | `DemoController` plays the bundled `demo_mission.json` through the real `MissionEngine` — the demo is not a separate code path, so it's an honest preview. |
| Audio engine: Media3/ExoPlayer, 4 independent channels | 2026-08-07 | One `ExoPlayer` each for MUSIC/VOICE/SFX/AMBIENCE so they can layer and cross-fade independently (spec sections 9, 28, 31). |
| Spatial pan via custom `AudioProcessor` on the SFX channel only | 2026-08-07 | Chose a lower-level Media3 audio API (spec section 28 allows this "if it provides a material advantage") because `Player.setVolume()` alone can't achieve true stereo pan. This is the least-verified piece of the codebase — flagged in code comments and `STATUS.md`. |
| Mission JSON schema | 2026-08-07 | `Mission { id, name, description, durationSeconds, storyArtwork?, timeline: [MissionEvent] }`; `MissionEvent { atSeconds, type, audioAsset?, text?, speaker?, artwork?, pan, channel }` (spec section 30). |
| Package structure: single `:app` module, package-by-feature | 2026-08-07 | `ui/`, `mission/`, `audio/`, `demo/` as packages within one module — not split into Gradle modules (spec section 31: don't over-modularize for MVP). |
| No custom fonts yet | 2026-08-07 | Typography uses platform `SansSerif`/`Monospace` families with heavy weights/letter-spacing to approximate the cinematic look. Custom display faces deferred to asset production (`docs/ASSETS.md`). |
| **Pivot: primary platform is now a web app (PWA), not native Android** | 2026-08-07 | User's explicit call after being told this environment can't build/run/APK the Android project (no SDK, no Studio, JDK 8 only): *"let's just make it a website that I can access on my phone."* This overrides spec section 27's native-Android direction for the active build. The `app/` Android module is kept as reference/paused, not deleted — see README "Android app (paused)". |
| Web app: vanilla JS, no framework, no build step | 2026-08-07 | Matches spec section 32's "don't over-engineer" for an MVP; static HTML/CSS/JS + a service worker is buildable and testable entirely inside this environment (no npm/webpack toolchain required), unlike the Android path. |
| Web audio engine: native Web Audio API (`AudioContext`/`GainNode`/`StereoPannerNode`) | 2026-08-07 | Ported from the Android `AudioEngine`/`PanAudioProcessor` design (4 independent channels: MUSIC/VOICE/SFX/AMBIENCE). `StereoPannerNode` gives real stereo pan natively — no custom DSP processor needed, unlike the Media3 side, which is a meaningful simplification. |
| Web mission JSON schema kept identical to the Android schema | 2026-08-07 | `Mission`/`MissionEvent` fields (`atSeconds`, `type`, `audioAsset`, `text`, `speaker`, `artwork`, `pan`, `channel`) are unchanged from `mission/Mission.kt`, so mission content is portable between platforms if native Android is ever resumed. |
| Speaker label only shown for actual dialogue events | 2026-08-07 | Found via in-browser testing of the demo mission: `currentSpeaker` was persisting across non-dialogue events (e.g. showing "HAVEN BASE" during a zombie-encounter narration line that has no speaker). Fixed in both `web/js/mission-engine.js` and the still-unbuilt `MissionEngine.kt` to clear the speaker for `ZOMBIE_ENCOUNTER`/`STORY_BEAT` events. |
| Story-panel art: original hand-authored SVG, not raster/AI-generated images | 2026-08-07 | No image-generation tool was available in this session; SVG lets original noir-silhouette illustrations be authored directly as code, fits the flat high-contrast graphic-novel style well, and is tiny/fast for an offline-first PWA. Marked as placeholder per spec section 42 — real key art is still future work. |
