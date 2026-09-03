# P5R calendar and Confidant progress audit — rc6

## Scope

This audit tracks the eleven device-review adjustments following rc5. The pass changes presentation, picker ordering, and progress projection; it does not change the authored walkthrough sequence or deadline dates.

## Acceptance matrix

| Request | rc6 contract |
|---|---|
| Calendar heading | Month/year plate is intrinsically sized and exactly centered between fixed edge arrows in every month |
| Game picker | Generic pack metadata orders P5R first, P3R second, and Metaphor third |
| Perfect Day | Passive hold is exactly 1,250 ms |
| Section markers | No P5R Calendar placement, manifest record, or bundled PNG remains |
| Deadline marker | Month-opener art is 32 dp and overhangs the top-right corner of each deadline due-date cell |
| Calendar typography | P5R weekday abbreviations and date numbers use upright condensed display type; month/year styling is unchanged |
| Today marker | Current P5R date receives a compact red TODAY plate without replacing the day-cell background |
| Today footer | Undo day is left; End day is right |
| Deadline copy | Today removes the trailing “finish the heist beforehand” instruction from heist deadline titles only |
| Answer card | A category-equivalent authored label is suppressed, preventing duplicate “Class question” copy |
| Confidant progress | Highest rank is derived from DONE walkthrough rank-milestone tasks and rendered in both list and detail views |

## Confidant progress semantics

Walkthrough tasks explicitly author milestones such as `Chariot reaches rank 5`. The UI matches the active pack's bond label and the milestone rank only on tasks marked Done. The highest completed milestone becomes the displayed current rank; reaching a later rank implies all earlier ranks are complete. Skipped, unchecked, and unrelated tasks do not advance the relationship.

This is a read-only projection of existing profile task marks. It creates no parallel mutable Confidant counter, so undoing a task immediately rolls the displayed rank back.

## Regression coverage

- `CalendarInteractionTest` pins swipe behavior and deadline-only guide-art placement.
- `GamePickerOrderTest` loads bundled manifests and pins the three-pack order.
- `SkinFxTimingTest` pins the 1,250 ms Perfect Day hold.
- `TodayPresentationTest` pins the scoped deadline-title cleanup.
- `AnswerPresentationTest` pins the category label used for duplicate suppression.
- `BondProgressTest` pins Done-only, matching-Confidant rank derivation.
- `P5RMediaCatalogAuditTest` pins the 73-item manifest and absence of P5R section media.
