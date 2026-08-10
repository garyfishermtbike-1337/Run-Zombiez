# Voice Script — Demo & Mission 01

Authoritative script for all recorded voice lines. Text here is **exactly** what's in
`assets/missions/*.json` `text` fields for each event — audio and on-screen subtitle
must never drift apart, so if a line is ever rewritten, update both places together.

## Voices (edge-tts, Microsoft Edge neural voices — free, no account)

| Character | Voice | Why |
|---|---|---|
| Haven Base | `en-US-AriaNeural` (female) | Composed, confident command-center operator — steady under pressure, the voice a runner trusts. |
| Runner 007 | `en-US-GuyNeural` (male) | Urgent, driven — reads well breathless and moving. Distinct gender/register from Haven Base so the two are instantly distinguishable on a phone speaker or one earbud. |

## Delivery notes

Radio dialogue is clipped and efficient — nobody over-explains on an open channel with a
threat nearby. Haven Base stays calm even when the news is bad; Runner 007 stays terse
and focused, more clipped as tension rises (compare `ack.mp3` to `effort.mp3`/`m1_run.mp3`).

## DEMO: First Contact (`demo_mission.json`)

| # | File | Speaker | Line |
|---|---|---|---|
| 1 | `voice/haven_base/intro.mp3` | Haven Base | "This is Haven Base. Runner 007, do you copy?" |
| 2 | `voice/runner007/ack.mp3` | Runner 007 | "Copy, Haven Base. Ready to move." |
| 3 | `voice/haven_base/briefing.mp3` | Haven Base | "Supply run to the old transit depot. Stay sharp — we've had movement on the east perimeter." |
| 4 | `voice/haven_base/warning.mp3` | Haven Base | "Runner 007, we're reading multiple signatures nearby. Keep moving, don't stop." |
| 5 | `voice/runner007/effort.mp3` | Runner 007 | "Not today." |
| 6 | `voice/haven_base/outro.mp3` | Haven Base | "We have you on approach, Runner 007. Gates opening. Welcome home." |

## MISSION 01: Transit Depot Run (`outbreak_signal.json`)

| # | File | Speaker | Line |
|---|---|---|---|
| 1 | `voice/haven_base/m1_intro.mp3` | Haven Base | "Runner 007, Haven Base. We've lost contact with the transit depot cache. Your job: get in, confirm status, get out." |
| 2 | `voice/runner007/m1_ack.mp3` | Runner 007 | "Copy. Heading out now." |
| 3 | `voice/haven_base/m1_checkin_1.mp3` | Haven Base | "You're clear for the next half mile. Nothing on our scanners yet." |
| 4 | `voice/haven_base/m1_checkin_2.mp3` | Haven Base | "You're at the halfway point. Depot's two blocks ahead." |
| 5 | `voice/runner007/m1_arrival.mp3` | Runner 007 | "I'm at the depot. Door's already open." |
| 6 | `voice/haven_base/m1_warning.mp3` | Haven Base | "We see it on the scanner. Don't slow down, Runner 007." |
| 7 | `voice/runner007/m1_run.mp3` | Runner 007 | "Cache secured. Moving, now!" |
| 8 | `voice/haven_base/m1_outro.mp3` | Haven Base | "Gates are open, Runner 007. Bring it home." |

14 lines total: 9 Haven Base, 5 Runner 007.
