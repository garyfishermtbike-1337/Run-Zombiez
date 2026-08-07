# Asset Inventory

Per spec section 41: every asset's source/status should be documented. This is the tracking sheet for Phase 5 (Polish), covering both the active web app (`web/`) and the paused Android app (`app/`) — see `README.md` for which is which.

## Status legend

- **Original** — created for this project
- **AI-generated** — generated for this project (note the tool)
- **Public domain** — verified public domain source
- **Licensed** — openly licensed, attribution/terms noted
- **Placeholder** — temporary, must be replaced before a polished build

## Visual — Web app (`web/`, active)

| Asset | Path | Status | Notes |
|---|---|---|---|
| App icon | `web/icons/icon.svg` | Original / Placeholder | Hand-authored SVG hazard-triangle mark. Works for Android "Add to Home Screen" (SVG icons supported); a PNG `apple-touch-icon` fallback would be needed for solid iOS support — not created, no rasterization tool available in this session. |
| Story panels | `web/art/story_panels/*.svg` | Original / Placeholder | 8 hand-authored SVG illustrations (`haven_gate`, `gate_open`, `gate_close`, `zombie_alley`, `zombie_chase`, `overpass`, `depot_interior`, `transit_depot`) — flat noir-silhouette style matching the palette, verified rendering inline in the mission screen. Simple placeholder compositions, not final key art. |

## Visual — Android app (`app/`, paused)

| Asset | Path | Status | Notes |
|---|---|---|---|
| App launcher icon | `app/src/main/res/drawable/ic_launcher_{background,foreground}.xml` | Placeholder | Simple vector hazard-triangle mark, same concept as the web icon. |
| Story panels | *(none)* | **Missing** | Mission JSON references artwork paths but `MissionScreen.kt` never wires up image display — text only. Not a priority while this platform is paused. |
| Concept art | `art/concept/` | Empty | Reserved for early visual-direction exploration. |
| UI textures/backgrounds | `art/ui/` | Empty | Reserved for distressed/noir texture overlays referenced in spec section 21. |

## Audio (both platforms — same file list, different base directories)

**No audio files exist yet on either platform.** Both audio engines and both missions' JSON are ready for them; playing a mission today logs a console warning per missing file and continues silently (verified in the web app). Directory layout already created in both places:

```
web/audio/                          app/src/main/assets/audio/
  music/                              music/
  voice/haven_base/                   voice/haven_base/
  voice/runner007/                    voice/runner007/
  sfx/zombies/                        sfx/zombies/
  sfx/environment/                    sfx/environment/
                                       sfx/ui/   (web has no ui/ subfolder yet — add if needed)
```

Filenames already referenced by `demo_mission.json` and `outbreak_signal.json` (needed first — identical list for both platforms):

- `music/main_theme.mp3`, `music/tension_theme.mp3`, `music/escape_theme.mp3`
- `voice/haven_base/intro.mp3`, `briefing.mp3`, `warning.mp3`, `outro.mp3`, `m1_intro.mp3`, `m1_checkin_1.mp3`, `m1_checkin_2.mp3`, `m1_warning.mp3`, `m1_outro.mp3`
- `voice/runner007/ack.mp3`, `effort.mp3`, `m1_ack.mp3`, `m1_arrival.mp3`, `m1_run.mp3`
- `sfx/zombies/groan_distant.mp3`, `groan_close.mp3`, `groan_fade.mp3`, `chase_stinger.mp3`
- `sfx/environment/street_wind.mp3`

All must be either recorded/composed originals, or sourced from public-domain/openly-licensed libraries with the license noted here before being added — per spec section 41, never bundle copyrighted commercial audio without permission. Once added under `web/audio/`, also add the same filenames to `PRECACHE_URLS` in `web/service-worker.js` if they should be available offline immediately (otherwise they'll cache opportunistically on first play).

## Fonts

**Web (`web/`):** system font stack (`-apple-system`, "Segoe UI", Roboto, sans-serif) plus `"Courier New"`/monospace for labels — see `web/css/style.css`. No custom webfont yet.

**Android (`app/`, paused):** platform system fonts (`FontFamily.SansSerif` / `FontFamily.Monospace`), see `ui/theme/Type.kt`.

A custom display face for the noir/graphic-novel look is future work on either platform — must be open-licensed (e.g. SIL OFL) if added.
