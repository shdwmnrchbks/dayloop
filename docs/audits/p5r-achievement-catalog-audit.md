# P5R Royal trophy catalog audit

This pass promotes Persona 5 Royal trophies from the legacy `media.json`
fallback to a first-class `achievements.json` catalog and independently checks
catalog completeness plus the three trophies that were absent from the imported
guide artwork.

## Sources

- TrueTrophies, **Persona 5 Royal Trophies** — independent complete list of 53
  Royal trophies and their unlock requirements.
- GameFAQs, marendarade, **Persona 5 Royal — Trophies** — second Royal-specific
  trophy requirement list.
- PlayStationTrophies Royal trophy pages — independent requirement and mechanic
  checks for Showtime, Fusion Alarm, Akihabara and later availability audits.
- Royal walkthrough/mechanic references under `docs/audits/p5r-trophy-availability-audit.md`
  — exact first-opportunity/fixed-story checks for trophy `availableFrom` metadata.

## Findings

### Catalog completeness

`media.json` contained only 50 trophy-image entries, and because P5R did not yet
ship `achievements.json`, the app's legacy Achievements screen treated those 50
images as the complete trophy catalog.

Royal has 53 trophies. The missing entries were:

- `It's Showtime!` — perform a Showtime attack.
- `Accident-Prone` — perform an execution during a Fusion Alarm.
- `Master of Akihabara` — order from the Maid Cafe special menu.

The new first-class catalog contains all 53.

### Progress compatibility

The 50 trophies that already existed as achievement media deliberately keep
their existing `p5r.media.achievement.*` ids as their first-class achievement
ids. Dayloop's earned-achievement persistence is keyed by achievement id, so
this preserves existing users' checked trophy state when the pack moves from
legacy-media tracking to `achievements.json`.

The three newly added trophies use new `p5r.achievement.*` ids. They have no
matching imported guide artwork, so `iconMediaRef` is intentionally absent and
the app renders its built-in fallback icon rather than inventing or duplicating
art.

### Availability semantics

`availableFrom` is now treated as the first independently supported Royal date
on which a trophy can become obtainable, **not** the completion route's chosen
day to earn it. Route-selected actions and cleanup targets remain in the
walkthrough or `expectedBy`/`routeTarget` metadata instead of overwriting game
availability.

For the three trophies restored by this catalog pass:

- `It's Showtime!` — **2016-06-21**. Showtime is introduced during the Bank arc;
  the mandatory tutorial itself does not award the trophy, but later Showtime
  activations are trophy-eligible from June 21 onward.
- `Accident-Prone` — **2016-06-21**. Fusion Alarms become available after the
  Bank Treasure route is secured; June 21 is the earliest legal route-security
  date in Royal.
- `Master of Akihabara` — **2016-08-31**. Akihabara and the Maid Cafe unlock on
  August 31. Its `expectedBy` date remains a completion-route target for the
  later stamp cleanup, not a universal deadline.

The earlier June 25 values were the authored route's chosen Bank-Palace day and
are retained only where useful in walkthrough guidance. They are not trophy
availability dates.

The broader first-opportunity/fixed-story audit now lives in
`p5r-trophy-availability-audit.md`, with focused regression coverage in
`P5RTrophyAvailabilityAuditTest`.

## Tracking boundary

All 53 P5R trophies intentionally use manual tracking in this pass. The catalog
is complete and the requirements are visible, but a trophy is not promoted to
story/event/counter/checklist automation until its deterministic rule is
separately audited. This avoids false auto-awards while preserving the option to
add rich tracking incrementally, as was done for P3R.

`P5RAchievementCatalogAuditTest` pins:

- exactly 53 unique Royal trophy ids,
- preservation of all 50 legacy media ids,
- the three no-art fallback entries,
- June 21 / June 21 / August 31 for the three restored trophies,
- manual tracking for the current P5R catalog,
- valid calendar dates and resolvable icon references.

`P5RTrophyAvailabilityAuditTest` separately pins the expanded set of exact
first-opportunity/fixed-story dates and ensures route-selected dates do not leak
back into `availableFrom`.

## Remaining boundary for issue #12

This closes the **trophy catalog identity/completeness** gap and has now been
followed by a much broader trophy-availability audit. It still does not turn
player-state, RNG or branch-dependent trophies into falsely precise calendar
facts. Those unresolved gates, along with flexible route-order and remaining
one-off gameplay facts, stay under the fact-by-fact verification definition of
done in #12.
