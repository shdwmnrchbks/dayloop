# P5R October 2016 point-unit audit

This is a focused continuation of `p5r-data-audit.md`. It verifies October's
social-stat point units and active modifiers without claiming that the authored
100% completion route was independently replayed day-for-day.

## Scope

The route order remains sourced from the existing Alyookid completion schedule.
This pass independently checks mechanics that can be verified without replacing
that route with another guide's preferred order: hidden social-stat points,
Craft of Cinema totals, reusable activity rewards, Confidant stat rewards,
Aojiru, plant fertilizer, exam-result rewards, the classroom blackboard bonus,
darts, and Royal DVD-return semantics.

## Independent references

- GameFAQs, marendarade, **Persona 5 Royal — Social Stats**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/social-stats
- GameFAQs, marendarade, **Persona 5 Royal — October**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/october
- GameFAQs, marendarade, **Hanged** (Munehisa Iwai):
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/hanged
- GameFAQs, Raidramon0, **Tower — Shinya Oda**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/82334/tower-shinya-oda
- GameFAQs, marendarade, **ACE October**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/ace-october
- Samurai Gamers, **Persona 5 Royal October Walkthrough and Guide**:
  https://samurai-gamers.com/persona-5/p5r-october-walkthrough/

## Corrections

The schema stores actual hidden points rather than music-note icons. October had
regressed to note-count-like values in several places. This pass corrects:

- Class questions and crosswords to the route's audited +2 Knowledge convention.
- Sunday Aojiru purchases to +2 for the currently offered stat.
- Tower rank rewards to +3 Kindness for the October rank-ups represented here.
- Hanged Man ranks 2–5 to +3 Proficiency; rank 6 correctly has no stat reward.
- `Back to the Ninja` to 5 base Knowledge +2 Craft of Cinema = +7.
- `Mouse M.D.`, `31`, and `Tee` viewings to 3 base +2 Craft of Cinema = +5.
- `Duh-vengers` to 5 base Kindness +2 Craft of Cinema = +7.
- `The Art of Automata` completion to +7 Proficiency.
- `Woman in the Dark` completion to +5 Proficiency.
- `Gambla Goemon` clears to +3 Charm.
- Mega Fertilizer to +5 Kindness.
- October exam results to +5 Charm for the route's top-result state.
- The 10/31 classroom blackboard action to +2 Guts in hidden-point units.
- Akechi darts to the +3 Proficiency base used by the audited October guides.

The 10/30 Shinya Destinyland/Balloons hangout was also corrected semantically:
it grants the room decoration/affinity event but does **not** invent a Kindness
reward. Mega Fertilizer on that date now carries the missing +5 Kindness instead.

Royal's Scarlet DVD shop uses a one-time membership and has **no set return
date**. The old deadline entry incorrectly described June–October as a rental
return window. October 23 is now explicitly a **completion-route target** for
finishing the planned DVD viewings, with no claim that Royal requires the disc
to be returned by that date.

## Regression coverage

`P5ROctoberAuditTest` pins the corrected high-risk values, explicitly checks
that the Craft of Cinema labels remain active for October media, and prevents
the Royal DVD target from regressing into a fake return window. The broader
`PackContentTest` continues to pin the reusable activity catalog and the current
P5R content version.

## Remaining audit work

The targeted point-unit/conditional-modifier pass has since continued through
February 1, 2017. The broader issue remains open until every user-visible P5R
gameplay fact has an independent verifier or an explicit
completion-route/source-specific designation.
