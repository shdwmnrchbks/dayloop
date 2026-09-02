# P5R Akihabara crane-game audit

Status: targeted Royal-specific verification complete for the eight crane-game room decorations used by the completion route.

## Sources checked

- GameFAQs — Persona 5 Royal walkthrough, Battle Boosts / Akihabara crane-machine table.
- Megami Tensei Wiki — Crane Machine (Royal stock dates and sequential unlock rule).
- Megami Tensei Wiki — Persona 5 Royal item list / individual decoration entries for in-game English display names.

## Royal prize order and stock dates

| # | Prize | Royal stock date | Dayloop route date |
|---|---|---|---|
| 1 | Jack Frost Doll | 2016-09-01 | 2016-09-02 |
| 2 | Burger-kun Doll | 2016-09-22 | 2016-09-22 |
| 3 | Wanna-kun Doll | 2016-10-14 | 2016-10-14 |
| 4 | Lexy Doll | 2016-11-03 | 2016-11-03 |
| 5 | Sheep Man Doll | 2016-11-25 | 2016-11-25 |
| 6 | Black Frost Doll | 2016-12-15 | 2016-12-15 |
| 7 | Buchimaru Doll | 2017-01-13 | 2017-01-13 |
| 8 | Jagao Doll | 2017-01-23 | 2017-01-23 |

Royal requires the prizes to be obtained in sequence; a later prize cannot be won before the prior prize has been obtained. The completion route already follows that order and never schedules a prize before its Royal stock date.

## Corrections made

- `Bunguer-kun Doll` -> `Burger-kun Doll` on 2016-09-22.
- `Protective Lexy Doll` -> `Lexy Doll` on 2016-11-03. Protective Lexy is the Persona 3 reference; the P5/P5R decoration item's English display name is Lexy Doll.
- `Lost Sheep Man Doll` -> `Sheep Man Doll` on 2016-11-25. The expanded/literal wording is not the P5R English inventory display name.

## Regression coverage

`P5RCraneGameAuditTest` pins:

- exactly eight numbered Akihabara crane-game prize steps,
- the Royal English prize names,
- their 1/8 through 8/8 order,
- the completion-route dates, and
- the invariant that no route date precedes the Royal stock date.
