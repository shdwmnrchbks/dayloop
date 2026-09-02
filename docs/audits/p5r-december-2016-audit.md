# P5R December 2016 point-unit and calendar audit

This is a focused continuation of the P5R data audit. It preserves the existing
100% completion-route order where the route is flexible, while independently
checking December mechanics and fixed calendar facts that should not depend on
which completion guide is followed.

## Scope

This pass covers hidden social-stat points, reusable movie/game rewards, Craft
of Cinema, Aojiru, Mega Fertilizer, fishing, Maid Cafe rewards, Tower/Hanged
stat rewards, crosswords/TV quizzes, the final-exam result/reward dates, and the
route-specific Akechi promise prompt. It does not claim that every optional
December hangout was independently replayed on the same day as the authored
route.

## Independent references

- GameFAQs, marendarade, **Persona 5 Royal — December**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/december
- GameFAQs, marendarade, **Persona 5 Royal — Justice**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/justice
- GameFAQs, sdarkpaladin, **Persona 5 Royal — December**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/79923/december
- GameFAQs, marendarade, **Persona 5 Royal — Social Stats**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/social-stats
- GameFAQs, Raidramon0, **Persona 5 Royal — Proficiency** (updated 2026-07-27):
  https://gamefaqs.gamespot.com/ps4/260936/persona-5-royal/faqs/82334/proficiency
- Megami Tensei Wiki, **Persona 5 Royal calendar**:
  https://megatenwiki.com/wiki/User:Desacredess/sandbox5
- Samurai Gamers, **Persona 5 Royal December Walkthrough and Guide**:
  https://samurai-gamers.com/persona-5/p5r-december-walkthrough/

## Corrections

- Mega Fertilizer uses +5 Kindness.
- Hanged Man rank 10 uses +3 Proficiency; Tower rank 10 uses +2 Kindness.
- December Aojiru, crosswords and TV quizzes use +2 hidden points rather than
  one-note shorthand.
- Fishing at Ichigaya uses +2 Proficiency for the route sessions represented
  here.
- `Clean Hard` and `Merry Christmess` use their +5 movie base plus the already
  active +2 Craft of Cinema modifier = +7.
- `Punch Ouch` and `Train of Life` use the reusable retro-game +3 reward.
- Maid Cafe visits carry the guaranteed +3 Charm base reward. The 20-stamp
  special-menu visit is +5 Charm total: +3 base plus the +2 special-menu bonus.
  Conditional Clara mistake rewards are not invented when the route does not
  specify which response/outcome occurred.
- The December 19 group-study reward remains +3 Knowledge.
- The December 24 top final-exam result is stored as +5 Charm in hidden-point
  units, not the displayed three-note shorthand.
- The `Sido` typo in the Shido Palace boss instruction was corrected.

## Akechi promise semantics

Justice ranks 9 and 10 are story-triggered during the Cruiser Palace after rank
8 has been completed. The optional `I want to keep our promise` choice is a
one-time prompt on the night after rank 10; it is not an open calendar window.

The old deadline entry incorrectly exposed a broad November–December window.
For this authored completion route Justice rank 10 occurs on December 8, so the
entry is now explicitly labeled a **completion-route reminder** on December 8.
The underlying Confidant remains story-triggered; this route date must not be
interpreted as universal availability.

## Fixed calendar correction

The prior route placed Sojiro's Boss Undies exam reward on December 22. Royal
calendar references place the final exams on December 20–22, Magician rank 10
and the Sojiro exam-reward interaction on December 23, and exam results on
December 24. The pack now moves the Boss Undies interaction to December 23 and
marks that date as story-heavy while preserving the route's evening Leblanc
activity.

A source conflict exists around the **exam-result date**: the sdarkpaladin
GameFAQs schedule places results on December 23, while the marendarade Royal
walkthrough and independent Royal calendar references place the results on
December 24. The pack retains December 24 because that date is supported by the
more date-specific Royal calendar evidence and matches the existing story-day
structure. This conflict is recorded here rather than treated as unanimous.

## Regression coverage

`P5RDecemberAuditTest` pins the corrected high-risk values, Craft of Cinema
labels, Maid Cafe totals, Shido spelling, Boss Undies date, December 23 day kind,
December 24 exam-result reward, and the route-specific Akechi promise reminder.

## Remaining audit work

The targeted hidden-point/conditional-modifier pass now continues through the
Royal third semester in `p5r-third-semester-2017-audit.md`. Full issue completion
still requires either independent verification or an explicit
route/source-specific designation for every user-visible P5R gameplay fact.
