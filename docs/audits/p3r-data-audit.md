# P3R data audit ledger — September 2026

This ledger records the factual and semantic audit of the Persona 3 Reload pack.
The P3R walkthrough is an authored completion route; its chosen dates must not be
silently promoted into universal game availability, unlock, or deadline facts.
The audit follows the same route-vs-game-fact discipline used by the P5R audit.

## Status

**Social Link, answer, exam, rescue-deadline, and April social-stat baselines are corrected and regression-protected; month-by-month factual audit remains in progress.**
Route identity, answer representation, stable IDs, Social Link identities, ordinary
route-date semantics, ordinary rank-ladder completeness, automatic-link chronology,
exam windows/requirements, actionable missing-person rescue cutoffs, rescue-route
timing, and April social-stat point units now have regression coverage. April
Tartarus/Elizabeth details and the broader May → January route audit remain open.

## Source roles

- **HayateButler, Persona 3 Reload 100% Perfect Schedule Guide** — primary source
  for the authored route order and completion-plan choices already represented
  by the pack. Route-selected dates are route facts, not universal availability.
- **RPG Site P3R Social Link guides** — independent check for Social Link
  identity, unlock prerequisites, recurring availability, and rank mechanics.
- **RPG Site / Push Square / Game8 school-answer guides** — independent checks
  for class/exam dates and answer text.
- **megaten-database P3R social-events data** — structured cross-check for school
  answers, social-stat points, and automatic Social Link mechanics.
- **GameFAQs P3R walkthrough/social-link/gameplay references** — independent
  check for social-stat point units, fixed automatic ranks, story skips, and
  Judgment floor progression.
- **RPG Site P3R Missing Persons guide** — independent check for rescue batches,
  Tartarus floors, story cutoffs, and the Bunkichi/Maiko Social Link risk.
- **Game8 / GameSkinny P3R missing-person references** — cross-check for the
  last actionable rescue dates, especially September 4 and January 30.

## Baseline findings

### P3R-AUD-001 — Missing route identity — FIXED

P3R now declares the `standard` route as **100% Completion Route** and explicitly
states that its authored dates are not universal availability, unlock, or
deadline facts. `contentVersion` was bumped from 1 to 2.

### P3R-AUD-002 — Social Link identities corrupted — FIXED

The original import mapped Magician to Junpei and Moon to Kenji, then repeated
those name/Arcana swaps throughout the walkthrough.

The catalog now uses canonical identities, including:

- Magician — **Kenji Tomochika**
- Moon — **Nozomi Suemitsu**
- Hanged-Man — **Maiko Oohashi**
- Temperance — **Bebe**
- Devil — **President Tanaka**
- Tower — **Mutatsu**
- Fortune — **Keisuke Hiraga**
- Star — **Mamoru Hayase**
- Sun — **Akinari Kamiki**

The targeted route cleanup covers April, May, June, July, August, October, and
December. Regression coverage scans every walkthrough month and rejects any
Magician rank action naming Junpei or Moon rank action naming Kenji. Legitimate
Junpei/Kenji Link Episodes, invitations, festival scenes, prerequisite dialogue,
and other non-Social-Link references are preserved.

### P3R-AUD-003 — Route dates overloaded into `availableFrom` — FIXED

Ordinary player-selected Social Link rank dates imported from the completion
schedule use `scheduledFor`; they are no longer presented as universal first-
availability dates.

`availableFrom` is now limited to independently verified fixed automatic story
rank dates for Fool and Death plus Judgment's fixed rank-1 unlock. Judgment ranks
after the unlock are floor-driven, not calendar-driven.

### P3R-AUD-004 — Answer catalog stored option numbers — FIXED

All 53 answer sheets now store actual answer text rather than menu positions.
Exam sheets resolve to their corresponding exam deadline through `deadlineRef`.

April is pinned by regression coverage as:

- 2009-04-08 — **Vivid Carp Streamers**
- 2009-04-18 — **Middens**
- 2009-04-27 — **A**

### P3R-AUD-005 — No Activities catalog — OPEN COVERAGE DEBT

P3R still ships no `activities.json`, and its walkthrough steps do not use
`activityRef`. This is structurally valid, but reusable activities, locations,
effects, and social-stat gains therefore remain embedded in route prose.

Do not build this catalog until the route's point values are independently
audited; otherwise bad imported gains would be promoted into reusable base data.

### P3R-AUD-006 — Stable-ID baseline was only a seed subset — FIXED

`pack-ids.baseline.json` now pins the current catalog: **22 Social Links, 23
deadlines, 53 answer sheets**, and zero activities. The deadline floor includes
the eight missing-person rescue cutoff IDs added by the audit.

### P3R-AUD-007 — Ordinary Social Link rank ladders were incomplete — FIXED

The apparent gaps were importer omissions rather than intentional game skips.
The authored completion route and existing walkthrough data resolve them as:

- **Devil rank 5** — July 28.
- **Devil rank 10** — September 1.
- **Tower rank 4** — July 31.
- **Lovers rank 2** — September 7.
- **Lovers rank 3** — September 10.

The July importer had also dropped most of the July 27–31 route block. That block
has been restored, including track practice, the Devil/Tower ranks, fridge/Taiyaki
steps, arcade time, and Fuuka's dorm hangout. Every non-automatic Social Link is
now regression-pinned to a continuous 1–10 rank ladder.

### P3R-AUD-008 — Automatic Social Links were modeled as estimates — FIXED

The original data used end-of-month status estimates for Fool and Death and left
Judgment almost empty. Independent references establish the actual mechanics:

- **Fool**: ranks 1, 2, 3, 4, 5, 6, 7, 9, 10 occur automatically on fixed story
  dates; Persona 3 Reload has no separate Fool rank-8 event.
- **Death**: actual automatic events reach ranks 1, 3, 5, 6, 8, 10; ranks
  2, 4, 7, and 9 are skipped by the game.
- **Judgment**: rank 1 unlocks automatically on December 31 on the good-ending
  path; ranks 2–10 advance automatically at Adamah 227F, 230F, 236F, 241F,
  246F, 247F, 253F, 254F, and 255F respectively.

The catalog now records real fixed dates only where dates exist and uses explicit
floor guidance for Judgment instead of inventing calendar dates.

### P3R-AUD-009 — Exam windows and top-class requirements were wrong — FIXED

The imported deadline labels overstated the Academics requirement for the first
three exam periods and truncated every exam window before its final Saturday.
The audited requirements/windows are now:

- **May 18–23** — Academics rank **3** + all player answers correct.
- **July 14–18** — Academics rank **4** + all player answers correct.
- **October 13–17** — Academics rank **5** + all player answers correct.
- **December 14–19** — Academics rank **6** + all player answers correct.

The final Saturday is represented as an exam morning while preserving the route's
after-school/evening actions once the exam session ends.

### P3R-AUD-010 — April prep targets were mislabeled as hard deadlines — FIXED

Two April entries were route conveniences, not missable game cutoffs:

- The April 20–26 Thebel block is the authored route's first exploration cycle;
  Tartarus does not close after April 26.
- The April 25 Muscle Drink purchase is discounted Saturday prep for Elizabeth
  Request #1; the request unlocks May 10 and has no deadline, and Muscle Drink is
  not a one-day-only item.

Both stable IDs are preserved but are now `routeTarget` entries rather than hard
missable deadlines.

### P3R-AUD-011 — Missing-person rescue cutoffs were absent — FIXED

Missing People begin appearing after the mechanic unlocks on June 18. The pack
now surfaces the eight actionable rescue cutoffs as `missable` deadlines:

- **July 6** — 50F, 56F, 64F.
- **August 5** — 79F, 84F.
- **September 4** — 101F, 109F, 114F.
- **October 3** — 120F, 135F, 140F; includes **Bunkichi**.
- **November 2** — 146F, 159F, 165F; includes **Maiko**.
- **December 1** — 177F, 196F.
- **December 30** — 209F, 221F.
- **January 30** — 232F, 250F.

For September, some references describe the narrative/story deadline as September
5, but the September 5 full-moon operation removes normal Tartarus access. Those
same references direct the player to rescue the batch on September 3 or 4, while
other Reload-specific guides list September 4 directly as the due date. Dayloop
therefore stores **September 4 as the last actionable rescue date**. This is a UI
and gameplay-safety semantic choice, not a claim that every source prints 9/4.

The authored completion route also has regression coverage for its corresponding
batch-clear Tartarus visits: **June 27, August 3, September 4, October 1,
November 2, November 29, December 30, and January 15**. Each visit occurs after
the batch has appeared and no later than its actionable rescue cutoff.

### P3R-AUD-012 — April social-stat point units — FIXED

April route `statGains` are now independently pinned in the pack's internal
numeric point scale:

- correct classroom answer — **+2 Charm**;
- stay awake in class — **+2 Academics**;
- Nurse's Office medicine — **+2 Courage**;
- Screen Shot movie — **+4** to the advertised social stat;
- Game Parade social-stat games — **+4**;
- Mystery Burger — **+3 Courage**.

An interim audit edit incorrectly changed Mystery Burger from +3 to +2 after
mixing display-note/pip representations with raw point values. Point-based Reload
references agree on **+3**, so the route has been restored to +3 and the old +2
regression assertion removed. The April Courage route now sums to exactly **15
points** on April 26 (2 + 4 + 2 + 4 + 3), matching the Courage Rank 2 threshold.

## April route audit

April now has regression coverage for:

- Magician identity and the April 22/28/30 Kenji rank events;
- classroom answer text;
- social-stat point units for class, nurse, movie, arcade, and Mystery Burger;
- the April 26 Courage Rank 2 point threshold;
- first-Tartarus and Muscle Drink entries as route targets rather than fake hard
  deadlines.

Still open for April: Tartarus progression/gatekeeper details, Elizabeth request
preparation, omitted/duplicate steps, and fixed-story-vs-flexible-route
classification.

## Regression rules for P3R

1. A completion-route date is not `availableFrom` unless an independent source
   establishes it as an actual fixed game boundary.
2. Route-selected Social Link dates use `scheduledFor`.
3. Social Links and Linked Episodes are different systems and must not be merged
   or represented under the wrong Arcana.
4. Every ordinary Social Link maintains a complete 1–10 rank ladder.
5. Automatic links preserve real skipped ranks; they are not padded with fake
   events merely to create a numeric sequence.
6. Floor-driven progression such as Judgment is not assigned synthetic dates.
7. `answers[].answers` contains answer text, never merely a menu position.
8. Exam answer sheets resolve to their exam deadline when that deadline exists.
9. Exam deadline windows include the complete exam period, including the final
   Saturday morning even when after-school time becomes available that day.
10. Route prep/optimization targets are not labeled as hard missable deadlines.
11. Missing-person cutoffs use the final **actionable rescue date** in the UI; if
    a narrative cutoff falls on a mandatory story night, document the distinction.
12. Social-stat data uses raw/internal point units consistently; do not mix them
    with UI note/pip counts from guides that use a different representation.
13. Reusable activity stat gains must be verified before creating
    `activities.json`.
14. Structural validation proves schema/reference integrity, not gameplay facts.
15. When two valid completion guides differ only on a flexible action date,
    preserve the authored route and document the difference rather than changing
    one valid route to imitate another.
16. Fixed story dates, unlock gates, deadlines, and availability windows require
    independent support beyond the primary completion schedule.
17. Walkthrough identity cleanup is targeted to Social Link rank actions; do not
    globally replace legitimate Junpei/Kenji story references.

## Next passes

- Finish April Tartarus progression/gatekeeper and Elizabeth-prep audit.
- Continue May → January month-by-month, then verify the January-ending to March
  epilogue/non-playable calendar transition.
- Re-check full-moon/story labels while auditing the corresponding route months.
- Audit achievement descriptions/triggers against canonical trophy data.
- Build P3R activities only after the underlying point/effect audit is stable.
