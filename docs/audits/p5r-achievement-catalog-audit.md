# P5R Royal trophy catalog audit

This pass promotes Persona 5 Royal trophies from the legacy `media.json`
fallback to a first-class `achievements.json` catalog and independently checks
catalog completeness plus the three trophies that were absent from the imported
guide artwork.

## Sources

- TrueTrophies, **Persona 5 Royal Trophies** — independent complete list of 53
  Royal trophies and their unlock requirements:
  https://www.truetrophies.com/game/Persona-5-Royal/trophies
- GameFAQs, marendarade, **Persona 5 Royal — Trophies** — second Royal-specific
  trophy requirement list:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/trophies
- GameFAQs, Bkstunt_31 / Haeravon, **Week 11: June 20–30 — Kaneshiro's Palace**
  — independently places the Showtime tutorial/trophy during the June 25 Palace
  infiltration used as Dayloop's route checkpoint:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78256/week-11-june-20th-june-30th-kaneshiros-palace
- GameFAQs, marendarade, **Velvet Room — Fusion Alarm (Royal)** — Fusion Alarms
  unlock after securing the Bank Palace Treasure route:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/velvet-room
- GameFAQs, sdarkpaladin, **August** — Akihabara unlocks on August 31 with the
  Hermit story event:
  https://gamefaqs.gamespot.com/ps5/370928-persona-5-royal/faqs/79923/august
- GameFAQs, marendarade, **Social Stats — Akihabara / Maid Cafe** — the special
  menu becomes available after spending ¥20,000 / collecting the required stamp
  progress at the Maid Cafe:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/social-stats

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

### Route availability checkpoints

`availableFrom` is a Dayloop route checkpoint, not a claim that the trophy is
universally forced on that calendar date.

- `It's Showtime!` — **2016-06-25**. The independent Royal walkthrough places
  the Showtime tutorial during the June 25 Kaneshiro infiltration, which is also
  Dayloop's authored Bank Palace run for this mechanic.
- `Accident-Prone` — **2016-06-25**. Fusion Alarms unlock only after securing the
  Bank Palace Treasure route; Dayloop explicitly secures that route and performs
  an execution for the achievement on June 25.
- `Master of Akihabara` — **2016-08-31**. Akihabara becomes available on August
  31. Dayloop's `expectedBy` date remains a completion-route target for the
  later Maid Cafe stamp cleanup, not a universal deadline.

The first draft used June 20 / June 21 for the first two trophies; the
independent mechanic/date check rejected those guesses and the catalog plus
regression test now pin June 25 instead.

## Tracking boundary

All 53 P5R trophies intentionally use manual tracking in this pass. The catalog
is complete and the requirements are now visible, but a trophy is not promoted
to story/event/counter/checklist automation until its deterministic rule is
separately audited. This avoids false auto-awards while preserving the option to
add rich tracking incrementally, as was done for P3R.

`P5RAchievementCatalogAuditTest` pins:

- exactly 53 unique Royal trophy ids,
- preservation of all 50 legacy media ids,
- the three no-art fallback entries,
- the audited June 25 / June 25 / August 31 availability checkpoints,
- manual tracking for the current P5R catalog,
- valid calendar dates and resolvable icon references.

## Remaining boundary for issue #12

This closes the **trophy catalog identity/completeness** gap and independently
checks the three previously missing trophy requirements. It does not by itself
verify every route month anchor attached to the legacy trophy artwork, nor does
it claim that every trophy's current `availableFrom` month-start approximation
is a universal unlock date. Those broader route/metadata facts remain under the
fact-by-fact verification definition of done in #12.
