# P5R November 2016 route-order audit

Scope: extend issue #12's month-by-month Persona 5 Royal route reproduction through November. This is separate from the existing November point-value audit: this pass focuses on calendar state, the Casino finale, the Nov 22-24 story lock, and the unusual period after Joker is presumed dead where free time resumes but school attendance does not.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — primary Dayloop route source for November activity choices and the route-selected Casino/Cruiser progression.
- GameFAQs Royal Week 31 / November walkthroughs (`faq/78256`, `faq/79923`, `faq/78212`) — independent checks for Nov 18-24 automatic story, Nov 22-24 LeBlanc-only evenings, the forced Nov 24 Shido Palace introduction, and the return of route free time from Nov 25.
- Persona 5 Royal Comprehensive Strategy Wiki (WIKIWIKI), November monthly schedule — independent calendar check for Culture Day, Saturday school dates, the Casino aftermath and the post-Nov-25 state where Joker cannot attend school while presumed dead.
- Japan 2016 national-holiday calendar — Nov 3 was Culture Day.

A legacy/vanilla calendar lists a Nov 18 Mt. Fuji classroom question, but Royal-specific schedules used for this audit do not. This pass therefore does **not** add that disputed question.

## Corrections made

### Culture Day and Saturday school

- **Nov 3:** changed `school` -> `free`; Culture Day is surfaced explicitly.
- **Nov 5:** changed `free` -> `school`; the route uses both train reading and a classroom slack-off reading slot before the Casino infiltration.
- **Nov 12:** changed `free` -> `school`; the route has a fixed classroom question before its Confidant activities.

### Casino finale and Nov 18-24 story lock

The existing Casino endgame order remains unchanged, but the fixed calendar state is now explicit:

1. **Nov 18** — Councilor rank 10 / Calling Card sequence occupies daytime; evening is confined to LeBlanc, where the route plays `Featherman Seeker`.
2. **Nov 19** — Casino heist / Sae boss, all story.
3. **Nov 20** — interrogation and ending-choice gate, all story.
4. **Nov 21** — automatic reveal/story day, no free time.
5. **Nov 22** — automatic investigation during daytime; Kawakami covers school; evening is confined to LeBlanc.
6. **Nov 23** — Shido election-speech/story sequence during daytime; evening is confined to LeBlanc.
7. **Nov 24** — Diet Building investigation leads into the forced first Cruiser infiltration; evening remains confined to LeBlanc, where the route's TV quiz and `Punch Ouch` session are legal.

This keeps the route's existing home activities without falsely presenting those dates as normal free-school days.

### Presumed-dead state from Nov 25

A distinct state begins on **Nov 25**: Joker is still officially presumed dead and cannot attend school, but normal route daytime/evening freedom resumes from LeBlanc.

The following entries are therefore `free`, not `school`:

- Nov 25
- Nov 26
- Nov 27
- Nov 28
- Nov 29
- Nov 30

Nov 25/28/29/30 explicitly surface the no-school/presumed-dead context. The route's existing Confidant, arcade, movie, crossword and shopping choices are preserved rather than replaced by another guide's optimization.

### Aojiru reusable links

The November Sunday-drink rewards were already correct but three rows had lost the reusable activity link. They now point to `p5r.activity.drink.fruit-drink`:

- Nov 6 — Proficiency +2
- Nov 13 — Guts +2
- Nov 27 — Kindness +2

## Regression coverage

`P5RNovemberRouteOrderAuditTest` pins:

1. Nov 3 Culture Day and Nov 5/12 Saturday-school state;
2. Nov 18-24 fixed story progression;
3. Nov 18, 22, 23 and 24 LeBlanc-confinement semantics;
4. Casino Calling Card -> heist -> interrogation -> reveal chronology;
5. Nov 24 first Cruiser infiltration followed by Nov 25's return to route freedom;
6. Nov 25-30 `free` state while Joker remains absent from school; and
7. all three November Aojiru reusable links/rewards.

The existing `P5RNovemberAuditTest` remains responsible for November point-value and Royal-gate assertions; this new test intentionally complements rather than replaces it.

April through November now have dedicated route-order/state reproduction passes. December and the Royal third semester remain to be checked for full month-level chronology, alongside player-state/RNG/branch-dependent cases that should not be represented with false fixed-date precision.
