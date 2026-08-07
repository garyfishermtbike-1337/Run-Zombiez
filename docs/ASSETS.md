# Asset Inventory

Per spec section 41: every asset's source/status should be documented. Nothing here is finished production art or audio yet — this is the tracking sheet for Phase 5 (Polish).

## Status legend

- **Original** — created for this project
- **AI-generated** — generated for this project (note the tool)
- **Public domain** — verified public domain source
- **Licensed** — openly licensed, attribution/terms noted
- **Placeholder** — temporary, must be replaced before a polished build

## Visual

| Asset | Path | Status | Notes |
|---|---|---|---|
| App launcher icon | `app/src/main/res/drawable/ic_launcher_{background,foreground}.xml` | Placeholder | Simple vector hazard-triangle mark. Replace with real key art. |
| Story panels | `art/story_panels/` | **Missing** | Referenced by mission JSON (`gate_open.png`, `gate_close.png`, `overpass.png`, `depot_interior.png`, `transit_depot.png`, `haven_gate.png`) but not yet created. `MissionScreen` currently only displays panel *text*, not the artwork itself — image rendering isn't wired up yet either. |
| Concept art | `art/concept/` | Empty | Reserved for early visual-direction exploration. |
| UI textures/backgrounds | `art/ui/` | Empty | Reserved for distressed/noir texture overlays referenced in spec section 21. |

## Audio

**No audio files exist yet.** The audio engine, mission JSON schema, and directory layout are ready for them; missions reference paths like `music/main_theme.mp3` that will currently fail to load. Directory layout already created:

```
app/src/main/assets/audio/
  music/
  voice/haven_base/
  voice/runner007/
  sfx/zombies/
  sfx/environment/
  sfx/ui/
```

Filenames already referenced by `demo_mission.json` and `outbreak_signal.json` (needed first):

- `music/main_theme.mp3`, `music/tension_theme.mp3`, `music/escape_theme.mp3`
- `voice/haven_base/intro.mp3`, `briefing.mp3`, `warning.mp3`, `outro.mp3`, `m1_intro.mp3`, `m1_checkin_1.mp3`, `m1_checkin_2.mp3`, `m1_warning.mp3`, `m1_outro.mp3`
- `voice/runner007/ack.mp3`, `effort.mp3`, `m1_ack.mp3`, `m1_arrival.mp3`, `m1_run.mp3`
- `sfx/zombies/groan_distant.mp3`, `groan_close.mp3`, `groan_fade.mp3`, `chase_stinger.mp3`
- `sfx/environment/street_wind.mp3`

All must be either recorded/composed originals, or sourced from public-domain/openly-licensed libraries with the license noted here before being added — per spec section 41, never bundle copyrighted commercial audio without permission.

## Fonts

Currently using platform system fonts (`FontFamily.SansSerif` / `FontFamily.Monospace`) with heavy weights and letter-spacing, see `ui/theme/Type.kt`. A custom display face for the noir/graphic-novel look is future work — must be open-licensed (e.g. SIL OFL) if added.
