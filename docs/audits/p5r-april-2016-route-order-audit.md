# P5R April 2016 route-order audit

Scope: independently reproduce the authored April 2016 completion-route slots closely enough to prove that each mandatory/free transition, prerequisite, stat gate and Castle-Palace sequence is legal in Persona 5 Royal. This is the first month-level pass aimed at issue #12's remaining flexible route-order work rather than only point-value spot checks.

## Sources checked

- GameFAQs Royal April schedule (`faq/78212`) — independent calendar/slot reference for the mandatory opening, Apr 18 `Semesters` crossword, rainy Apr 20 evening study, class questions and early Confidant/stat actions.
- GameFAQs optimized Royal schedule discussion — independent route-level reproduction of the Apr 18 onward stat plan, Bio Nutrient prerequisite and Apr 24 route-to-Treasure -> Apr 25 Calling Card -> Apr 26 boss ordering.
- megaten-database / All Confidant Events Royal walkthrough — independent check for Apr 19 Bio Nutrient sourcing, plant reward and Apr 20 Royal free-slot/weather behavior.
- Megami Tensei Wiki / Royal library references — secondary checks for the fixed April class/crossword dates and the pre-Kamoshida library's Knowledge + Guts behavior.

## Corrections made

### Opening story locks

Royal does not give normal player-choice free time before Apr 18. Dayloop had imported several mandatory opening dates as `free` days. The following are now `story`:

- Apr 13
- Apr 14
- Apr 16
- Apr 17

Apr 15 was already correctly marked `story`. Apr 12 remains `school` because it carries the authored class-question entry even though the rest of that day is mandatory story progression.

### Apr 18 crossword

The route had lost the no-extra-slot `Semesters` crossword on Apr 18. It is restored at **+2 hidden Knowledge points** before the room-cleaning step.

### Apr 19 Bio Nutrient prerequisite

The route fed the attic plant without explicitly sourcing a Bio Nutrient. Royal guides commonly rely on free DLC/bundled items, but that is platform/package dependent. The route now says to buy one at the Underground Mall flower shop **if the storage/DLC items did not provide one**. The existing plant use remains **+3 hidden Kindness points**.

### Apr 20 rainy evening and early-library Guts

The route had no authored evening action on Apr 20 even though Royal allows LeBlanc study and the independently reproduced stat plan uses the rain bonus. The missing step is restored:

- study at LeBlanc in the evening — **+5 hidden Knowledge points**.

The pre-Kamoshida school-library penalty is also now represented correctly: studying there raises **Guts +2** as well as Knowledge. The rainy Apr 20 and Apr 21 library rows therefore carry **Knowledge +5 / Guts +2** instead of Knowledge alone.

## Resulting stat/gate invariants

With the restored no-time crossword, rainy evening slot and library Guts values:

- the Apr 27 class answer brings authored Knowledge gains to **34 points**, reaching Royal Knowledge rank 2; the same day's `Blossom` crossword then brings the end-of-day total to **36**;
- authored Guts gains total **16 points through Apr 22**, comfortably clearing the **11-point rank-2 threshold** before the Apr 23 Death rank-2 visit;
- those restored early-library Guts points are also part of the route state that reaches Guts rank 3 before the late-May Temperance setup;
- the route's Castle sequence remains internally ordered as **Apr 24 secure Treasure route -> Apr 25 send Calling Card -> Apr 26 steal the Treasure**.

This does not require Dayloop to copy another guide's flexible Confidant/book choices. Where two valid schedules choose different legal activities, Dayloop keeps its authored completion route. The audit only corrects missing/illegal slot semantics or prerequisites and pins the route's resulting state.

## Regression coverage

`P5RAprilRouteOrderAuditTest` pins:

1. Apr 13/14/15/16/17 mandatory-story day kinds;
2. the Apr 18 `Semesters` +2 Knowledge crossword;
3. Bio Nutrient sourcing before the Apr 19 plant use;
4. the Apr 20 rainy LeBlanc +5 Knowledge study action;
5. the Apr 20/21 rainy school-library **Knowledge +5 / Guts +2** rewards;
6. Knowledge at **34 before** and **36 after** the Apr 27 `Blossom` crossword;
7. **16 Guts through Apr 22** before Death rank 2; and
8. the Apr 24/25/26 Castle route / Calling Card / boss order.

April is now the first month whose flexible route ordering has a dedicated independent reproduction pass. May onward remains tracked under issue #12.
