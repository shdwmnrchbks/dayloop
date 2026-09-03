# P5R calendar, contrast, and completion audit — rc5

## Scope

This audit tracks the ten follow-up adjustments requested after the rc4 device review. It changes UI presentation and interaction only; the audited P5R walkthrough ordering, achievement state, and deadline facts remain unchanged.

## Acceptance matrix

| Request | rc5 contract |
|---|---|
| Perfect Day contrast | Black title and supporting text on the light inverse card |
| Primary command contrast | Tasks, End day, and Achievements this month use white display text on red |
| End day scale | Larger display label, still centered in its pinned half-width action |
| Completion duration | Both passive holds are exactly 2,000 ms |
| Day Complete motion | 400 ms cover plus 400 ms reveal, 800 ms total |
| Overlay priority | Day Complete suppresses Perfect Day whenever both states overlap |
| Calendar heading | Header images removed; equivalent invisible slots preserve title/background geometry |
| Confidants list | White display name; rank/route/date supporting line is red |
| Deadline markers | Month-opener art on deadline end/due dates replaces the generic red marker |
| Section markers | Regular marker on the first authored date of its month; deadline marker on deadline-range starts |
| Calendar navigation | Left swipe advances one month; right swipe goes back one month; arrows remain available |
| Today deadline contrast | Black deadline title with red days-left line |

## Marker semantics

The three graphics remain declared as source-specific guide presentation assets. rc5 does not rewrite their media metadata as universal game facts. Calendar placement is derived at render time:

- `month` art is reused on each `deadlineEnd` date;
- the first `section` item marks the first authored date in each month it declares;
- remaining `section` items mark `deadlineStart` dates in months they declare;
- multiple source markers can coexist in one cell without hiding the date number.

Other skins retain their existing marker behavior. Exact due dates come from `deadlines.json`; the calendar does not invent or alter deadline facts.

## Regression coverage

- `CalendarInteractionTest` pins swipe direction, threshold, edge clamping, due-date artwork, and section-start placement.
- `SkinFxTimingTest` pins the two-second holds, 800 ms Day Complete transition, and Day Complete priority.
- Existing task, navigation, achievement, media-provenance, and pack audit suites remain part of CI.
- The release workflow assembles the candidate APK, runs app/core/tool tests, validates every bundled pack, and publishes only from merged `main`.
