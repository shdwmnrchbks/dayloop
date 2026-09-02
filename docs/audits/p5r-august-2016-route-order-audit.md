# P5R August 2016 route-order audit

Scope: continue issue #12's month-by-month Persona 5 Royal route reproduction after April-July. August is mostly flexible summer break, so this pass focuses on the fixed late-month Futaba/Medjed story block, legal evening slots and reusable Aojiru references rather than replacing Dayloop's chosen Confidant order with another guide's route.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — primary route facts for August 1-31. The guide explicitly describes August as mostly free except for the final ten days, when route activity is restricted to night events.
- GameFAQs Royal August walkthroughs (`faq/79923`, `faq/78212`, `faq/78256`) — independent checks for the August 21 Medjed deadline/LeBlanc lock, August 22 aftermath, August 23-28 Futaba socialization sequence, August 29 beach trip, August 30 team homework and August 31 automatic Hermit/Akihabara story.
- Samurai Gamers Royal August walkthrough — secondary fixed-calendar check for the late-August story block and evening-only activity pattern.

## Corrections made

### August 1-20 remains flexible summer break

No broad rewrite was needed for the first twenty days. The primary schedule treats this portion of August as the route's flexible summer-break block, and Dayloop's `free` day kinds are retained.

This includes route-selected Confidants, jobs, Mementos, batting cages, billiards and media. Different valid Royal schedules can and do choose different legal activities here; those differences are not data errors.

### August 21-31 is a fixed story-day block

Dayloop previously rendered every day from August 21 through August 31 as `free`, even though the player only chooses the evening route activity after mandatory daytime story.

All eleven dates are now `story`:

- **Aug 21:** Medjed deadline/cleanse story; evening confined to LeBlanc.
- **Aug 22:** Medjed aftermath and Futaba story; evening free time resumes. Judgement rank 3 and Fool rank 7 remain automatic story ranks.
- **Aug 23-28:** daily Futaba socialization/reintegration story occupies the daytime; evening free time resumes for Dayloop's chosen route actions.
- **Aug 29:** beach-trip story occupies daytime; evening free time resumes. Magician rank 7 remains automatic.
- **Aug 30:** team-homework story occupies daytime; evening free time resumes.
- **Aug 31:** Futaba/Akihabara story occupies daytime; Hermit rank 1 starts automatically, evening is confined to LeBlanc, and Fool rank 8 advances automatically.

The route's evening Confidants, crosswords, jobs, billiards and media are preserved. The correction is about fixed calendar state, not substituting a different completion route.

### August Aojiru references

The August 7, 14 and 28 Sunday-drink steps already had the correct Royal stat rotation:

- Aug 7 — Proficiency +2
- Aug 14 — Guts +2
- Aug 28 — Kindness +2

However, all three had lost their `activityRef` during import. They are now linked to `p5r.activity.drink.fruit-drink`, matching the rest of the route and allowing reusable-activity navigation/audits to recognize them.

### Media during restricted evenings

The existing `D.Housewives` route remains legal:

- Aug 21 — first viewing while confined to LeBlanc;
- Aug 31 — second viewing while confined to LeBlanc.

Both retain the active Craft of Cinema-adjusted Charm reward already verified by the activity/point audit.

## Regression coverage

`P5RAugustRouteOrderAuditTest` pins:

1. Aug 1-20 as the flexible `free` summer-break block;
2. every Aug 21-31 date as `story`;
3. the Medjed/Futaba/Beach/Homework/Akihabara story context and LeBlanc confinement where applicable;
4. automatic Judgement 3 / Fool 7 / Hermit 1 / Fool 8 story ranks;
5. both route `D.Housewives` viewings during legal home evenings;
6. Aug 7/14/28 Aojiru reusable `activityRef` links and rotating stat rewards; and
7. retention of the route's Aug 16 Knowledge-rank-5 checkpoint.

April through August now have dedicated route-order/state reproduction passes. September onward remains under issue #12, alongside player-state/RNG/branch-dependent cases that should not be encoded as false fixed dates.
