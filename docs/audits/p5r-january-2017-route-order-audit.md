# P5R January 2017 route-order audit

Scope: extend issue #12's month-by-month Persona 5 Royal route reproduction into the Royal third semester. The existing third-semester audit already pins hidden stat points, answers and deadlines; this pass focuses on fixed story progression, school-day state and the route's Palace/Mementos cleanup order.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — primary Dayloop route source for the January Confidant awakenings, media/game cleanup, final Laboratory route and final Mementos visit.
- Persona 5 Royal Kamigame January schedule — independent calendar check for Jan 1-10 story progression, Jan 11 school return, Jan 12 forced Laboratory infiltration, Saturday class dates and Jan 13-31 after-school/free-time availability.
- GameFAQs Royal January walkthroughs (`faq/78212`, `faq/78256`, `faq/78629`, `faq/79923`) — independent checks for Jan 14/21 classroom questions, Jan 26 after-school Palace access, Jan 27 class question, and late-January Palace/Mementos cleanup flexibility.

## Corrections made

### Fixed third-semester opening

Jan 1-10 already correctly use `story` for the new-year / altered-reality sequence:

- Jan 2 — first forced Laboratory infiltration
- Jan 3-8 — fixed teammate visits
- Jan 9 — second forced Laboratory infiltration / ending branch
- Jan 10 — Morgana's automatic awakening

Jan 11 remains `school`, and Jan 12 remains `story` for the third forced Laboratory infiltration and Faith-cap unlock.

### Saturday school corrections

Two Saturdays contained classroom questions but were incorrectly labeled `free`:

- **Jan 14:** now `school`; retains the class question, Ryuji awakening and evening route actions.
- **Jan 21:** now `school`; retains the class question, Faith rank 7 and evening retro-game action.

### Late-January school-state corrections

Royal's schedule still has school before the after-school route slots on several cleanup dates:

- **Jan 26:** changed `story` -> `school`. The Laboratory route-to-Treasure visit is explicitly labeled as an **after-school** route choice rather than an all-day forced story event.
- **Jan 28:** changed `free` -> `school`. The final Mementos cleanup is explicitly an after-school route choice.
- **Jan 31:** changed `free` -> `school`. The final optional cleanup text now says `After-school/evening cleanup` rather than implying the entire day is free.

Jan 30 was already correctly `school`.

These changes preserve Dayloop's authored route dates rather than adopting a different guide's Palace/Mementos ordering.

### Sunday Aojiru state

The existing January Sunday-drink rows were already fully linked and correct:

- Jan 15 — Proficiency +2
- Jan 22 — Guts +2
- Jan 29 — Kindness +2

The month-level regression now pins those references alongside the school corrections.

## Regression coverage

`P5RJanuaryRouteOrderAuditTest` pins:

1. Jan 1-10 fixed story state and the first/second Laboratory infiltrations;
2. Jan 11 school and Jan 12 forced third Laboratory infiltration;
3. all Royal school dates used by the route from Jan 13-31, including Jan 14/21/28/31;
4. Jan 14/21 Saturday classroom questions;
5. Jan 25 in-class full-book cleanup;
6. Jan 26 after-school final Laboratory route-to-Treasure visit;
7. Jan 28 after-school final Mementos/Jose cleanup;
8. Jan 31 after-school/evening cleanup wording;
9. all three January Aojiru references/rewards; and
10. forced Laboratory 1 -> forced Laboratory 2 -> forced Laboratory 3 -> route-to-Treasure -> final Mementos chronology.

The existing `P5RThirdSemesterAuditTest` remains responsible for hidden point values, school-answer text and the Feb 2/3 Laboratory deadline metadata.

April 2016 through January 2017 now have dedicated month-level route-order/state reproduction passes. February's final three route days are the next remaining chronology slice before this full flexible-route audit can be considered reproduced end-to-end.
