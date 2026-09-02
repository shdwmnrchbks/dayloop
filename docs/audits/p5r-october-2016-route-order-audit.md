# P5R October 2016 route-order audit

Scope: continue issue #12's month-by-month Persona 5 Royal route reproduction after April-September. October mixes ordinary school days, the Okumura aftermath, a four-day midterm block, festival preparation, school-festival story, the forced first Casino infiltration, and a deliberate return to normal free time on October 30.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — primary Dayloop route source for the October activity choices, including the route-selected October 4-6 Spaceport finish and October 22 `Duhvengers` viewing.
- Persona 5 Royal Comprehensive Strategy Wiki (WIKIWIKI), October monthly schedule — independent calendar/free-time check for October 1-31, including October 11-13 story lock, October 17-20 midterms, October 21-29 story/festival/Casino restrictions, and October 30 free-time return.
- GameFAQs Royal walkthroughs (`faq/78256`, `faq/79923`) — independent checks for school attendance on October 1/4/8/15, LeBlanc confinement on October 12/13/20/21/25/27/29, festival-committee timing on October 22, and the forced Casino introduction on October 29.
- Samurai Gamers / other Royal calendar references — secondary checks for the October 22 class question, October 25-26 festival events and October 29 Casino introduction.
- Japan 2016 national-holiday calendar — October 10 was Health and Sports Day, so it is not a normal school day.

## Corrections made

### Ordinary school and holiday state

Several dates were previously rendered as generic `free` or `school` days even though Royal's calendar state is fixed:

- **Oct 1:** now `school`; the route's Shinya/Iwai activities occur after school.
- **Oct 4:** now `school`; the route shops first, then enters the Spaceport after school.
- **Oct 8:** now `school`; the route's Shinya/Iwai choices happen after school.
- **Oct 10:** now `free`, with Health and Sports Day surfaced explicitly.
- **Oct 15:** now `school`; this is the final chalk-throw classroom day.
- **Oct 24 / Oct 31:** remain `school` and are regression-pinned.

### Okumura aftermath and LeBlanc confinement

The pack previously hid fixed story restrictions behind ordinary day kinds:

- **Oct 11:** now `story`; the class question is followed by the forced Destinyland celebration / Okumura press-conference story and there is no free time.
- **Oct 12:** now `story`; daytime is automatic and the legal route activity is confined to LeBlanc in the evening.
- **Oct 13:** now `story`; same daytime lock / LeBlanc-only evening pattern.

The route's DVD viewings remain legal and are not removed.

### Four-day October midterms

The October midterms run **Oct 17-20**, not only Oct 17-19. October 20 is now `exam`, with the pack explicitly noting that it is the final midterm day and that the evening is confined to LeBlanc. The route's `Tee` viewing remains the legal home activity.

### Festival / investigation story block

October 21-29 contains several days where the player has no normal daytime free slot even though a home/evening activity may remain:

- **Oct 21:** police questioning + team meeting; LeBlanc-only evening.
- **Oct 22:** class question, then mandatory festival-committee work; normal free time resumes in the evening. The route's `Duhvengers` viewing is therefore kept and explicitly marked as an evening movie.
- **Oct 23:** festival-planning story occupies daytime; the route's no-time Sunday errands and evening Twins outing remain legal.
- **Oct 25:** school-festival daytime; LeBlanc-only evening, where the route plays `Gambla Goemon`.
- **Oct 26:** school festival + after-party story; no normal free slot.
- **Oct 27:** story daytime; LeBlanc-only evening, where the route plays `Gambla Goemon` again.
- **Oct 28:** fixed story / automatic Judgement rank 6; no free time.
- **Oct 29:** forced first Casino infiltration; the route's book reading is retained as a legal LeBlanc evening activity.
- **Oct 30:** normal free time resumes and remains `free`.

### Aojiru reusable links

All five October Sunday-drink entries now point to `p5r.activity.drink.fruit-drink` while preserving the existing Royal rotation:

- Oct 2 — Proficiency +2
- Oct 9 — Guts +2
- Oct 16 — Kindness +2
- Oct 23 — Knowledge +2
- Oct 30 — Charm +2

### Palace chronology

The month-level regression keeps two distinct route facts separate:

1. the authored Spaceport finish remains **Oct 4 route / Oct 5 Calling Card / Oct 6 heist**; and
2. the Casino's **forced first infiltration is Oct 29**, followed by the return of normal route freedom on Oct 30 and Empress rank 1 on Oct 31.

The audit does not replace Dayloop's route choices with another guide's optimized Confidant ordering.

## Regression coverage

`P5ROctoberRouteOrderAuditTest` pins:

1. the October school-day and Oct 10 holiday classifications;
2. Oct 11-13 fixed story / LeBlanc-confinement semantics;
3. all four October midterm days;
4. Oct 21-29 festival/investigation/Casino story restrictions and legal evening actions;
5. the Oct 22 evening `Duhvengers` timing;
6. all five October Aojiru reusable links and rotating rewards;
7. Oct 4 -> Oct 5 -> Oct 6 Spaceport route / Calling Card / heist order; and
8. Oct 29 Casino introduction -> Oct 30 free time -> Oct 31 Empress unlock progression.

April through October now have dedicated route-order/state reproduction passes. November onward remains under issue #12, alongside player-state/RNG/branch-dependent cases that should not be represented with false fixed-date precision.
