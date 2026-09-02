# P5R Twins / Lavenza outing audit

Status: targeted Royal-specific verification complete for the completion route's numbered `1/13` through `13/13` warden/Lavenza outing chain.

## Scope

This pass separates two different facts that should not be conflated:

1. **Royal availability** — when each outing can actually be initiated, including prerequisite/location and limited-time constraints.
2. **Completion-route scheduling** — the particular date on which the imported Alyookid route chooses to perform that outing.

The route date is not a universal availability date. The regression test only requires the source-specific route date to fall inside the independently supported Royal availability window.

## Sources checked

- GameFAQs — Persona 5 Royal Strength / Caroline & Justine guide (Royal outing locations, skill-card rewards and requirements).
- Samurai Gamers — Persona 5 Royal Strength Confidant guide (Royal availability windows, conditions and rewards).
- aqiu384 P5R walkthrough / Megaten Database material (Royal outing sequence and additional comparison point).
- Alyookid — `Persona 5 The Royal 100% Achievements + Perfect Schedule` (the imported completion route's chosen dates and `n/13` numbering).

## Audited route chain

| # | Source-route date | Royal outing | Reward | Royal availability used for validation |
|---|---|---|---|---|
| 1 | 2016-06-06 | Big Bang Burger | Maragi, Mabufu | 6/6–12/19 |
| 2 | 2016-06-07 | Shibuya movie theater | Frei, Psy | 6/7–12/19; theater visit prerequisite |
| 3 | 2016-06-16 | Protein Lovers gym | Apt Pupil, Counter | 6/15–12/19 |
| 4 | 2016-07-02 | Kanda church | Samarecarm | 6/25–12/19 |
| 5 | 2016-08-03 | Shinagawa aquarium | Masukukaja, Masukunda | 7/26–12/19 |
| 6 | 2016-09-05 | Asakusa Skytree | Tarukaja, Rakukaja, Sukukaja | 7/26–12/19 |
| 7 | 2016-09-25 | Akihabara Maid Cafe | Tetraja, Dekunda, Dekaja | 9/19–12/19; prior cafe visit prerequisite |
| 8 | 2016-09-27 | Miura Beach | Growth 2 | 9/2–9/29; daytime and weather constrained |
| 9 | 2016-10-16 | Destinyland | Tetrakarn | 10/1–12/19; weather constrained |
| 10 | 2016-10-23 | Ueno Museum | Regenerate 3 | 10/1–11/3 |
| 11 | 2016-11-25 | Cafe LeBlanc | High Counter | 11/25–12/19 |
| 12 | 2016-12-03 | Shibuya Underground Mall | Heat Riser | 12/1–12/9 in Samurai Gamers; the 12/3 route date is inside all checked windows |
| 13 | 2017-01-13 | Lavenza / protagonist room | Enduring Soul | 1/13 after the required prior outing chain |

## Source conflicts / wording notes

- Some community-derived tables disagree on a small number of secondary details. In particular, one aqiu384 page labels the gym's second card `Sharp Student`, while GameFAQs, Samurai Gamers and the imported route agree on `Counter`. This audit keeps `Counter` as the supported Royal route reward.
- References disagree on whether the Underground Mall outing remains available after 12/9. This does not affect Dayloop's route because it schedules the outing on 12/3, inside the narrower window.
- The imported schedule numbers all twelve Caroline/Justine events plus the January Lavenza event as `1/13` through `13/13`. The order is source-specific; it should not be interpreted as a universal mandatory gameplay order beyond the prerequisites Royal itself enforces.

## Result

No illegal Dayloop route date was found in this pass. The existing source-specific dates all fall inside the Royal windows checked above, including both limited-time outings. No walkthrough correction was required.

## Regression coverage

`P5RTwinsOutingAuditTest` pins:

- exactly thirteen numbered `n/13` route steps,
- each source-route date and sequence number,
- chronological ordering of the completion-route sequence, and
- the invariant that every route date falls inside its audited Royal availability window.
