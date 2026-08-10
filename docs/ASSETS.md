# Asset Inventory

Per spec section 41: every asset's source/status should be documented. This is the tracking sheet for Phase 5 (Polish), covering both `web/` and `app/` — see `README.md` for the current state of each.

## Status legend

- **Original** — created for this project
- **AI-generated** — generated for this project (note the tool)
- **Public domain** — verified public domain source
- **Licensed** — openly licensed, attribution/terms noted
- **Placeholder** — temporary, must be replaced before a polished build

## Visual — Web app (`web/`)

| Asset | Path | Status | Notes |
|---|---|---|---|
| App icon | `web/icons/icon.svg` | Original / Placeholder | Hand-authored SVG hazard-triangle mark. Works for Android "Add to Home Screen" (SVG icons supported); a PNG `apple-touch-icon` fallback would be needed for solid iOS support — not created, no rasterization tool available in this session. |
| Story panels | `web/art/story_panels/*.svg` | Original / Placeholder | 8 hand-authored SVG illustrations (`haven_gate`, `gate_open`, `gate_close`, `zombie_alley`, `zombie_chase`, `overpass`, `depot_interior`, `transit_depot`) — flat noir-silhouette style matching the palette, verified rendering inline in the mission screen. Simple placeholder compositions, not final key art. |

## Visual — Android app (`app/`)

| Asset | Path | Status | Notes |
|---|---|---|---|
| App launcher icon | `app/src/main/res/drawable/ic_launcher_{background,foreground}.xml` | Placeholder | Simple vector hazard-triangle mark, same concept as the web icon. |
| Story panels | *(none)* | **Missing** | Mission JSON references artwork paths but `MissionScreen.kt` never wires up image display — text only. |
| Concept art | `art/concept/` | Empty | Reserved for early visual-direction exploration. |
| UI textures/backgrounds | `art/ui/` | Empty | Reserved for distressed/noir texture overlays referenced in spec section 21. |

## Audio — sourced 2026-08-10, wired into both platforms

All audio below lives under `app/src/main/assets/audio/` (Android) and `web/audio/` (web), identical relative paths in both. 46MB total, 30 files.

### Voice (14 files, `voice/haven_base/`, `voice/runner007/`)

- **Status:** AI-generated (text-to-speech)
- **Tool:** [edge-tts](https://github.com/rany2/edge-tts) — a free, open-source CLI that uses Microsoft Edge's neural text-to-speech voices, no account or API key required. (ElevenLabs was the first choice but its free tier requires creating an account, which is not something this assistant does on a user's behalf — see `DECISIONS.md`.)
- **Voices:** Haven Base = `en-US-AriaNeural`, Runner 007 = `en-US-GuyNeural` — chosen for maximum contrast (different gender/register) so the two are easy to tell apart on a phone speaker.
- **Script:** `docs/VOICE_SCRIPT.md` is the authoritative source — every line's text is identical to the mission JSON's `text` field for that event, so subtitle and audio never drift.
- **License:** Microsoft Edge neural voice output via edge-tts has no attribution requirement for this kind of use; it's the same voice engine behind Edge's built-in "Read Aloud" feature. No usage restriction has been identified for a free personal project, but this hasn't been reviewed by a lawyer — flag for the user if this ever needs a formal license check.

### Music (3 files, `music/`)

| File | Track | Length | Feel |
|---|---|---|---|
| `main_theme.mp3` | "Shadowlands 1 - Horizon" | 3:40 | Eerie, Intense, Somber |
| `tension_theme.mp3` | "SCP-x7x (6th Floor)" | 2:41 | Dark, Mysterious |
| `escape_theme.mp3` | "Shadowlands 5 - Antechamber" | 2:58 | Eerie, Intense, Unnerving |

- **Status:** Licensed
- **Source:** [incompetech.com](https://incompetech.com) — Kevin MacLeod, direct MP3 download, no account required.
- **License: Creative Commons BY 4.0 — attribution required.** Required attribution text (per-track, from incompetech's own attribution generator):
  > "Shadowlands 1 - Horizon" / "SCP-x7x (6th Floor)" / "Shadowlands 5 - Antechamber" — Kevin MacLeod (incompetech.com)
  > Licensed under Creative Commons: By Attribution 4.0 License — http://creativecommons.org/licenses/by/4.0/
- **TODO:** this attribution is not yet displayed anywhere in either app. Add a Credits/About section (Help screen on web, a new screen or dialog on Android) before treating this as a finished, shippable build — CC BY is violated if the credit is never shown to end users, even for a private/personal app.

### Sound effects (12 files, `sfx/zombies/`, `sfx/environment/`, `sfx/ui/`)

| File | Source track | 
|---|---|
| `sfx/zombies/groan_distant.wav` | "Single zombie breath" |
| `sfx/zombies/groan_close.wav` | "Zombie monster growl" |
| `sfx/zombies/groan_fade.wav` | "Gasping zombie" |
| `sfx/zombies/chase_stinger.wav` | "Frightening zombie roar" |
| `sfx/zombies/horde_distant.wav` | "People moaning sadly" (repurposed as distant-horde ambience) |
| `sfx/environment/street_wind.wav` | "Wind blowing ambience" |
| `sfx/environment/gate_creak_open.wav` | "Creaky door open" |
| `sfx/environment/gate_creak_close.wav` | "Creaky closing wood door" |
| `sfx/environment/footsteps_running.wav` | "Crunchy road fast walking loop" |
| `sfx/ui/radio_static_in.wav` | "Radio static fx" |
| `sfx/ui/radio_static_out.wav` | "Metal button radio ping" |
| `sfx/ui/alarm.wav` | "Security facility breach alarm" |
| `sfx/ui/mission_complete.wav` | "Completion of a level" |

- **Status:** Licensed
- **Source:** [Mixkit](https://mixkit.co) free sound effects — direct WAV download from `assets.mixkit.co`, no account required.
- **License: Mixkit License — free for personal and commercial use, no attribution required.** (See https://mixkit.co/license/#sfxFree.)
- Kept as `.wav` rather than converted to `.mp3` — no `ffmpeg` or other audio-conversion tool was available in this session, and both Media3/ExoPlayer (Android) and the Web Audio API (web) play WAV natively, so there was no need to convert. Mission JSON `audioAsset` paths use the `.wav` extension for these files accordingly.

### Wired vs. sourced-but-not-wired

**Wired into the mission timelines** (both `demo_mission.json` and `outbreak_signal.json`, both platforms): all voice lines, all 3 music tracks, `groan_distant`/`groan_close`/`groan_fade`/`chase_stinger`, `street_wind`, `gate_creak_open`/`gate_creak_close` (new `STORY_BEAT` audio support), `mission_complete` (new `MISSION_COMPLETE` audio support), and `horde_distant` (layered into Mission 01's depot-interior story beat as an ambience swap).

**Sourced but not wired into any timeline yet:** `footsteps_running.wav`, `radio_static_in.wav`, `radio_static_out.wav`, `alarm.wav`. These exist under `sfx/environment/` and `sfx/ui/` and are ready to use, but adding them well means either new timeline events (radio static in/out bookending each transmission — ~14 more event pairs across both missions) or new engine capability (e.g. auto-layering a static blip whenever `AudioEngine` starts a VOICE cue). Left for a follow-up pass rather than bolted on quickly.

### Engine changes made to support this

`MissionEngine` (both `MissionEngine.kt` and `mission-engine.js`) previously ignored `audioAsset` on `STORY_BEAT` and `MISSION_COMPLETE` events. Both now play the event's `audioAsset` on its `channel` if present — this is what makes the gate-creak and mission-complete-sting cues work. `complete()` in both engines now stops MUSIC/VOICE/AMBIENCE explicitly instead of calling `stopAll()`, so a mission-complete sting on the SFX channel isn't cut off by its own completion.

## Fonts

**Web (`web/`):** system font stack (`-apple-system`, "Segoe UI", Roboto, sans-serif) plus `"Courier New"`/monospace for labels — see `web/css/style.css`. No custom webfont yet.

**Android (`app/`):** platform system fonts (`FontFamily.SansSerif` / `FontFamily.Monospace`), see `ui/theme/Type.kt`.

A custom display face for the noir/graphic-novel look is future work on either platform — must be open-licensed (e.g. SIL OFL) if added.
