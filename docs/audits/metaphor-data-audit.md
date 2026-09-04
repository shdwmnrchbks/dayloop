# Metaphor: ReFantazio data audit

Status: **in progress**. This ledger records evidence-first corrections to the bundled `metaphor` pack. A finding is only marked fixed when the pack data and a regression test encode the conclusion.

## Source roles

- **Primary completion route:** HayateButler, *Comprehensive Walkthrough Schedule & General Resource* (Steam Community guide 3346632862). This is the authored 100% route Dayloop follows. Dates chosen by this schedule are route facts, not universal game availability.
- **Follower mechanics / prerequisites:** HayateButler, *All Followers Guide* (Steam Community guide 3346632882), cross-checked against independent follower/quest references where a date is represented as fixed game timing.
- **Mission deadlines:** Game8 main-operation references and Neoseeker operation walkthroughs are used to distinguish actual quest windows from the primary guide's selected clear dates.
- **Existing source policy:** `docs/sources.md` remains authoritative: `scheduledFor` is for the selected completion route; `availableFrom` / `availableUntil` are reserved for independently supported game windows or fixed story timing.

## Findings

### MET-AUD-001 — Metaphor did not identify its authored route — FIXED

`pack.json` previously declared no route even though the walkthrough is a source-specific 100% schedule. The pack now declares the `standard` **100% Completion Route** and explicitly states that its route dates are not universal availability or deadlines. `contentVersion` is bumped to 3.

Regression: `MetaphorFollowerAuditTest` pins the route declaration.

### MET-AUD-002 — Follower route dates were stored as universal availability — FIXED

The 14-Follower / 112-rank catalog was largely authored with `availableFrom` using the day chosen by the completion guide. That made a route choice appear to be a game mechanic.

The catalog now uses:

- `availableFrom` only for fixed automatic story dates that have independent support.
- `scheduledFor` for player-selected rank events, request/relic/task completions, and automatic ranks whose calendar day depends on when the route clears the triggering objective.

Regression: `MetaphorFollowerAuditTest` pins the fixed-story rank set and requires every other rank to carry a route date instead of `availableFrom`.

### MET-AUD-003 — Gallica's automatic ranks were flattened into one date type — FIXED

Gallica progresses automatically, but not every rank is tied to a fixed calendar day. Ranks 1, 2, 5, 7, and 8 are fixed story timing and remain `availableFrom`. Ranks 3, 4, and 6 trigger when the corresponding main operation is cleared, so Dayloop stores the primary route's clear day in `scheduledFor` with an explicit trigger note.

This prevents a selected dungeon-clear date from being rendered as a universal unlock date.

### MET-AUD-004 — Catherina mixes quest, fixed-story, travel-triggered, and manual ranks — FIXED

Catherina's ladder now distinguishes the mechanics:

- Rank 1: route date for completing `A Friend in Need`.
- Ranks 2 and 3: fixed automatic story encounters on 07/02 and 07/19, conditional on having started her bond.
- Rank 4: route date for the third Gauntlet Runner encounter; it depends on travel timing.
- Ranks 5-8: completion-route bond dates.

### MET-AUD-005 — More rank 2 had no route date — FIXED

More rank 2 is task-driven: complete `More's Task: Foreword and Prologue` after raising a Healer Archetype to rank 10. The primary route turns it in on 06/12, so rank 2 is now `scheduledFor: 2100-06-12` with the requirement documented.

### MET-AUD-006 — Route clear dates and story beats were mislabeled as mission deadlines — FIXED

The deadline catalog previously treated several selected clear dates or mandatory story dates as if they were deadlines. The operation entries now represent real game windows:

| Operation | Start | Deadline |
|---|---:|---:|
| Necromancer Takedown | 06/12 | 06/21 |
| Apprehend the Real Kidnapper | 07/05 | 07/16 |
| Infiltrate the Charadrius | 07/23 | 08/12 |
| Obtain Drakodios | 08/19 | 09/05 |
| Prepare for the Final Battle | 09/13 | 09/22 |
| Skybound Avatar Conquest | 09/26 | 10/25 |

Northern Border Fort, Nord Mines, Montario Opera House, the 09/23 Royal Capital duel, and the 10/26 final battle remain dated entries but are explicitly `other` mandatory story events, not `palace` deadline windows.

Regression: `MetaphorDeadlineAuditTest` pins every operation window and the mandatory-story classification.

### MET-AUD-007 — Stable deadline IDs needed additions for audited concepts — FIXED

Added stable IDs for `Prepare for the Final Battle` and the 10/26 `Tyrant's Star` story boundary, and updated the Metaphor pack ID baseline. Existing IDs were retained where their concept still matched the corrected entry.

## Remaining audit queue

These are intentionally **not** marked complete yet:

1. **Royal Virtue math:** verify every inline `statGains` value against the guide's coin-to-point conversion and rank thresholds; verify every rank-up marker against cumulative totals.
2. **Activity catalog:** verify book names, per-session vs completion bonuses, coliseum values, fishing/cooking notes, Gold Beetle counts, and whether reusable activity-level stat gains are valid or misleading.
3. **Podium debates:** verify all eight opponents, availability windows / weekday schedules, answer text, and achievement placement; do not model a weekly schedule as one fixed calendar date.
4. **Requests / missables:** audit side-request starts, actual deadlines, turn-in dates, Gold Beetles, cooking cutoff, and the Julian book missable.
5. **Achievements:** verify every achievement/trophy trigger and place automatic achievements on the actual completion day rather than a month-end placeholder or arbitrary route date.
6. **Walkthrough route:** line-by-line compare June 2 through October 26 against the primary schedule, including time-of-day slot, story/free classification, follower ranks, dungeon clears, collectibles, and missable warnings.
7. **Endgame / postgame:** verify October 26 free-time semantics, final-battle sequencing, and any postgame/NG+ facts that belong in the app rather than the completion route.

The Metaphor audit should only be declared complete after these queues are closed and regression-protected.
