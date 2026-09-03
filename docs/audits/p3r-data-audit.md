# P3R data audit ledger — September 2026

This ledger records the factual and semantic audit of the Persona 3 Reload pack.
The P3R walkthrough is an authored completion route; its chosen dates must not be
silently promoted into universal game availability, unlock, or deadline facts.
The audit follows the same route-vs-game-fact discipline used by the P5R audit.

## Status

**Baseline correction pass landed on the audit branch; month-by-month factual audit remains in progress.**
Route identity, answer representation, stable IDs, ordinary Social Link route-date
semantics, canonical Social Link identities, and the first April corrections now
have regression coverage. Rank-ladder completeness, automatic-link chronology,
full April point provenance, deadlines, and May onward remain open.

## Source roles

- **HayateButler, Persona 3 Reload 100% Perfect Schedule Guide** — primary source
  for the authored route order and completion-plan choices already represented
  by the pack. Route-selected dates are route facts, not universal availability.
- **RPG Site P3R Social Link guides** — independent check for Social Link
  identity, unlock prerequisites, recurring availability, and rank mechanics.
- **RPG Site / Push Square / Game8 school-answer guides** — independent checks
  for class/exam dates and answer text.
- **megaten-database P3R social-events data** — structured cross-check for school
  answers, Social Link point mechanics, and fixed social events.
- **GameFAQs P3R walkthroughs** — independent calendar/route spot-checking where
  route order or fixed story timing needs a second reference.

## Baseline findings

### P3R-AUD-001 — Missing route identity — FIXED

P3R now declares the `standard` route as **100% Completion Route** and explicitly
states that its authored dates are not universal availability, unlock, or
deadline facts. `contentVersion` was bumped from 1 to 2.

### P3R-AUD-002 — Social Link identities corrupted — BASELINE FIXED

The catalog incorrectly mapped Magician to Junpei and Moon to Kenji. The April
walkthrough repeated the Magician error on April 22, 28, and 30.

The baseline pass now uses canonical identities, including:

- Magician — **Kenji Tomochika**
- Moon — **Nozomi Suemitsu**
- Hanged-Man — **Maiko Oohashi**
- Temperance — **Bebe**
- Devil — **President Tanaka**
- Tower — **Mutatsu**
- Fortune — **Keisuke Hiraga**
- Star — **Mamoru Hayase**
- Sun — **Akinari Kamiki**

April route prose now uses Kenji for Magician. Linked Episodes remain outside the
Social Link catalog and must not be collapsed into an Arcana relationship.

### P3R-AUD-003 — Route dates overloaded into `availableFrom` — BASELINE FIXED

Ordinary player-selected Social Link rank dates imported from the completion
schedule have been migrated to `scheduledFor`. They are no longer presented as
universal first-availability dates.

The only links retaining any `availableFrom` values are the automatic story links
**Fool, Death, and Judgment**. Their later imported/status dates are now stored as
`scheduledFor` when the primary guide only supports a route/status estimate.
Their exact automatic-rank chronology still requires an independent story pass.

### P3R-AUD-004 — Answer catalog stored option numbers — FIXED

All 53 answer sheets now store the actual answer text rather than menu positions.
Exam sheets also resolve back to their corresponding exam deadline through
`deadlineRef`.

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

`pack-ids.baseline.json` now pins the current catalog: **22 Social Links, 15
deadlines, 53 answer sheets**, and zero activities. This gives the audit a stable
identifier floor while factual corrections continue.

### P3R-AUD-007 — Several Social Link rank ladders are incomplete — OPEN

The baseline Social Link pass exposed missing ranks that the original import had
hidden behind later rank numbers. Confirmed structural gaps include:

- **Devil** — rank 5 and rank 10 absent.
- **Tower** — rank 4 absent.
- **Lovers** — ranks 2 and 3 absent.
- **Fool / Death / Judgment** — automatic ladders are incomplete and/or use
  end-of-month status estimates rather than a fully verified rank chronology.

These must be reconstructed against the authored walkthrough and independent P3R
references. The audit deliberately does not fabricate a date just to make each
ladder numerically complete.

## April route audit — first correction pass

The first April pass corrected the Magician identity and the Wilduck **Mystery
Burger** Courage gain from `+3` to the audited `+2`. Regression coverage now pins:

- April 22 Magician start to Kenji Tomochika, with no Junpei reference;
- April 28 and April 30 Magician rank-ups to Kenji;
- April 26 Mystery Burger to Courage `+2`;
- the three April school-answer strings.

Still open for April: complete independent provenance for every class/stay-awake,
nurse, movie, arcade, food and study gain; Tartarus progression; Elizabeth
request preparation; omitted/duplicate steps; and fixed-story-vs-flexible-route
classification.

## Regression rules for P3R

1. A completion-route date is not `availableFrom` unless an independent source
   establishes it as an actual game boundary.
2. Route-selected Social Link dates use `scheduledFor`.
3. Social Links and Linked Episodes are different systems and must not be merged
   or represented under the wrong Arcana.
4. `answers[].answers` contains answer text, never merely a menu position.
5. Exam answer sheets resolve to their exam deadline when that deadline exists.
6. Reusable activity stat gains must be verified before creating
   `activities.json`.
7. Missing Social Link ranks must not be filled with synthetic dates merely to
   make a ladder look complete.
8. Structural validation proves schema/reference integrity, not gameplay facts.
9. When two valid completion guides differ only on a flexible action date,
   preserve the authored route and document the difference rather than changing
   one valid route to imitate another.
10. Fixed story dates, unlock gates, deadlines, and availability windows require
    independent support beyond the primary completion schedule.

## Next passes

- Reconstruct the missing Social Link ranks and independently verify automatic
  Fool/Death/Judgment chronology.
- Finish April route + point-unit audit.
- Audit deadlines/full-moon/Tartarus rescue windows independently.
- Continue May → January month-by-month, then verify the January-ending to March
  epilogue/non-playable calendar transition.
- Build P3R activities only after the underlying point/effect audit is stable.
