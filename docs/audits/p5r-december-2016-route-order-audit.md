# P5R December 2016 route-order audit

Scope: extend issue #12's month-by-month Persona 5 Royal route reproduction through December. This complements the existing December point-value audit by checking Joker's hidden-school state, the route-selected Shido clear, the election lock, return to school, finals, and the year-end story block.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — primary Dayloop route source for December activity choices, including the route-selected early Shido clear, fishing chain, maid-cafe cleanup and year-end media/game steps.
- Persona 5 Royal Kamigame December schedule — independent calendar check that normal route free time is available Dec 1-17 while Joker is still out of school, Dec 18 is Election Day with only a LeBlanc evening action, and Joker returns to school on Dec 19.
- GameFAQs Royal December walkthroughs (`faq/78212`, `faq/79923`, `faq/78256`) — independent checks for Dec 18 LeBlanc confinement, Dec 19 mandatory team study, Dec 20-22 finals, Dec 23 automatic story/LeBlanc evening and the Dec 24-31 finale block.
- Persona 5 Royal Comprehensive Strategy Wiki / Royal confidant schedule — secondary check for free-time availability before the election and the Shido Palace deadline window.

## Corrections made

### Dec 1-17: free time while presumed dead, not school

The pack previously labeled many weekdays in early December as `school`. Royal does not return Joker to school until **Dec 19**. While he is still officially presumed dead, normal route free time remains available from LeBlanc.

The following dates are now `free` rather than `school`:

- Dec 1, 2, 5, 6, 7, 8
- Dec 12, 13, 14, 15, 16

Dec 3, 4, 10, 11 and 17 were already `free`. Dec 9 remains `story` because Dayloop intentionally uses that day for its route-selected Shido Calling Card/heist.

The Dec 1 entry now explicitly states that Joker remains out of school until Dec 19 so the app does not imply ordinary class attendance during the hiding period.

### Shido route timing is preserved

Dayloop deliberately clears the Cruiser earlier than the universal deadline:

1. **Nov 24** — forced first Cruiser infiltration;
2. **Dec 8** — second infiltration / Akechi fight / route progression;
3. **Dec 9** — send the Calling Card and steal Shido's Heart on the route-selected heist day.

This is valid route timing and is not rewritten to match guides that delay the Palace toward Dec 16-17.

### Dec 18 election lock and Dec 19 school return

- **Dec 18:** changed `free` -> `story`. Election Day / Shido aftermath occupies daytime and the evening is confined to LeBlanc. The route's `Punch Ouch` session remains a legal home activity.
- **Dec 19:** remains `school`, but now explicitly surfaces Joker's return to school and the mandatory team-study event after classes.

This distinction is important: Dec 1-17 are route-free days without school attendance; Dec 19 is the actual return to ordinary school status.

### Finals and year-end story

- **Dec 20-22:** remain `exam`; Dec 22 now notes that year-end free time resumes after the final exam, preserving the route's Ueno/Maid Café actions.
- **Dec 23:** remains `story` and now explicitly records automatic daytime story plus LeBlanc-only evening. Boss Undies, `Train of Life` and automatic Magician rank 10 stay on the route.
- **Dec 24-31:** remain `story`; Dec 24 keeps exam results, Mementos depths, Fool rank 10 and Yaldabaoth before the automatic year-end sequence.

### Aojiru reusable links

The December 4 and December 11 Sunday-drink rows already had the correct Royal rotation but were missing their reusable activity references. Both now point to `p5r.activity.drink.fruit-drink`:

- Dec 4 — Knowledge +2
- Dec 11 — Charm +2

No Aojiru purchase is added to Dec 18 because Election Day consumes the daytime.

## Regression coverage

`P5RDecemberRouteOrderAuditTest` pins:

1. Dec 1-8 and Dec 10-17 free-time state while Joker remains out of school;
2. Dec 9 route-selected Shido Calling Card/heist;
3. Dec 18 Election Day story lock and LeBlanc-only evening;
4. Dec 19 school return and mandatory team study;
5. the Dec 20-22 finals block and post-exam free time on Dec 22;
6. Dec 23 story/LeBlanc confinement;
7. Dec 24-31 year-end story state;
8. Dec 4/11 Aojiru reusable links/rewards; and
9. Dec 8 Cruiser progression -> Dec 9 Shido clear -> Dec 18 election -> Dec 19 school return -> Dec 24 finale chronology.

The existing `P5RDecemberAuditTest` remains responsible for hidden stat-point values and specific December item/deadline assertions.

April through December now have dedicated route-order/state reproduction passes. The Royal third semester (January-February) remains to be checked at the same month-level chronology depth, alongside player-state/RNG/branch-dependent cases that should not be represented with false fixed-date precision.
