# P3R data audit ledger — September 2026

This ledger records the factual and semantic audit of the Persona 3 Reload pack.
The P3R walkthrough is an authored completion route; its chosen dates must not be
silently promoted into universal game availability, unlock, or deadline facts.
The audit follows the same route-vs-game-fact discipline used by the P5R audit.

## Status

**Baseline audit in progress.** The first pass covers pack semantics, stable IDs,
Social Link identity, school answers, deadlines, and April route data before the
month-by-month route audit expands through January/March.

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

### P3R-AUD-001 — Missing route identity

`content/packs/p3r/pack.json` does not declare `routes`, even though the shipped
walkthrough is a source-authored 100% completion schedule. The app therefore
cannot label the plan with the same explicit route identity used by the audited
P5R pack. This is a semantic presentation gap, not merely missing metadata.

**Required correction:** add a `standard` route such as **100% Completion Route**
and explicitly state that walkthrough dates are the authored order, not universal
availability/deadline dates.

### P3R-AUD-002 — Social Link identities are corrupted

At least two catalog identities are factually wrong:

- `p3r.bond.magician` is labeled **Junpei**. In Persona 3 Reload, Magician is
  **Kenji Tomochika**; it starts automatically on April 22 and is available in
  Classroom 2-F on Tuesday/Thursday/Friday.
- `p3r.bond.moon` is labeled **Kenji**. Moon is **Nozomi Suemitsu**, found at
  Paulownia Mall after his unlock prerequisites.

The same Magician corruption is present in April walkthrough steps on April 22,
April 28, and April 30, which tell the player to hang out with Junpei while
claiming Magician ranks.

**Required correction:** audit every Social Link identity and every walkthrough
name/Arcana reference, then pin the canonical mapping with regression tests.
Linked Episodes must remain distinct from Social Links.

### P3R-AUD-003 — Route dates are overloaded into `availableFrom`

`confidants.json` broadly stores completion-route rank dates in `availableFrom`.
For example, ordinary player-selected Magician/Chariot/Strength/etc. rank-ups are
represented as though each route-selected date were the game's first universal
availability date. This repeats the semantic defect already removed from P5R.

**Required correction:** move completion-route choices to `scheduledFor`. Keep
`availableFrom` / `availableUntil` only for independently verified game windows,
fixed unlock dates, or automatic story timing. Automatic links require a separate
story-trigger audit rather than synthetic route windows.

### P3R-AUD-004 — Answer catalog stores option numbers instead of answer text

`answers.json` currently stores values such as `"3"`, `"2"`, and `"1"`. The
indices checked so far point at the correct choices, but the Answers UI therefore
serves choice positions rather than useful answer text. The audited P5R catalog
stores the actual answer strings.

April is independently confirmed as:

- 2009-04-08 — **Vivid Carp Streamers**
- 2009-04-18 — **Middens**
- 2009-04-27 — **A**

May class/exam indices checked in the baseline also match the correct choice
positions, so this presently looks like an import-representation problem rather
than widespread wrong-choice data.

**Required correction:** replace numeric indices with canonical answer text for
the full school/exam catalog and add `deadlineRef` to exam sheets where the exam
deadline exists.

### P3R-AUD-005 — No Activities catalog

P3R ships no `activities.json`, and the coverage matrix explicitly notes that its
walkthrough steps do not use `activityRef`. This is structurally valid today but
leaves reusable activities, locations, effects, and stat gains embedded only in
walkthrough prose.

**Audit classification:** coverage debt, not a schema failure. Build the catalog
after route/stat-gain verification so bad imported point values are not promoted
into reusable definitions.

### P3R-AUD-006 — Stable-ID baseline is only a seed subset

`pack-ids.baseline.json` currently pins only seven bond IDs, three deadline IDs,
three answer IDs, and zero activities. It does not represent the current P3R
catalog and therefore provides weak protection against accidental identifier
churn during the audit.

**Required correction:** regenerate/expand the baseline after the first catalog
corrections, then add regression coverage that prevents audited IDs from being
silently renamed or dropped.

## April route audit — opened

April is the first month under detailed review. Initial checks show that the
school-answer choice indices are correct, but the Magician character is wrong in
both the Social Link catalog and route prose. The next April pass will verify:

1. fixed story dates vs flexible route choices;
2. every Social Link rank/flag and Linked Episode distinction;
3. social-stat point units for class, arcade, movie, nurse, food, and study steps;
4. Tartarus progression and Elizabeth-request preparation claims;
5. omitted route actions, duplicate actions, and impossible ordering.

## Regression rules for P3R

1. A completion-route date is not `availableFrom` unless an independent source
   establishes that date as an actual unlock/window boundary.
2. Social Links and Linked Episodes are different systems and must not be merged
   or represented under the wrong Arcana.
3. `answers[].answers` should contain the answer the player needs, not merely its
   menu position.
4. Reusable activity stat gains must be verified before creating
   `activities.json`; do not promote unverified route annotations into base data.
5. Structural validation proves schema/reference integrity, not gameplay facts.
6. When two valid completion guides differ only on a flexible action date,
   preserve the authored route and document the difference rather than changing
   one valid route to imitate another.
7. Fixed story dates, unlock gates, deadlines, and availability windows require
   independent support beyond the primary completion schedule.

## Next passes

- Finish April route + point-unit audit and add April regression tests.
- Audit the full Social Link/Linked Episode mapping and migrate route dates to
  `scheduledFor`.
- Replace the complete answer catalog with answer text and verify exam windows.
- Audit deadlines/full-moon/Tartarus rescue windows independently.
- Continue May → January month-by-month, then verify the January-ending to March
  epilogue/non-playable calendar transition.
- Build P3R activities only after the underlying point/effect audit is stable.
