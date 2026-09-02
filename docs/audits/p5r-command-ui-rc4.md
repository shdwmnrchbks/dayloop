# P5R command UI and completion audit — rc4

## Scope

This audit tracks the eleven follow-up adjustments requested after the rc3 device review. It is a presentation and task-progress pass; the source-backed Day/Night ordering and month-end achievement mapping audited for rc3 remain unchanged.

## Acceptance matrix

| Request | rc4 contract |
|---|---|
| End day sizing | Fills its half of the pinned footer; enlarged label is centered |
| Shared typography | Tasks and all skin-aware command buttons use the date display face |
| Command colors | Tasks/End day: black on red; Check all/Undo day: red on black with white keyline/offset |
| Check all size | Compact command plate matching the Tasks header scale |
| Deadline title | Red title; days-left urgency color unchanged |
| Deadline navigation | Removed from bottom tabs; Search detail route retained |
| Confidants list | P5R name uses thick red display type; supporting rank/route/date text unchanged |
| Calendar achievement heading | Thick black display type |
| Day Complete | Three-second readable hold, still tap-to-dismiss |
| Task actions | Done and Skip only; no Later action is rendered |
| End day behavior | Unchecked tasks are persisted as Skipped before the clock advances |
| Perfect Day | Three-second hold with red title and supporting copy |

## Compatibility decisions

- `StepMark.LATER` remains in the persistence model so upgrading cannot corrupt or discard an older profile. The task UI no longer offers a way to create new Later marks.
- Deadlines remain a registered navigation destination so Search can open them. They are intentionally absent from `TopLevelRoutes`, so the bottom bar treats the screen as a detail page with Back navigation.
- End day preserves every explicit Done, Skip, or legacy Later mark and fills only missing task marks with Skip.

## Regression coverage

- `EndDayTaskMarkingTest` pins the unchecked-only auto-skip selection.
- `TaskActionContractTest` pins the visible action set to Done and Skip.
- `NavigationContractTest` pins Deadlines outside both the top-level route set and generated bottom tabs.
- `SkinFxTimingTest` pins both requested holds to exactly 3,000 ms.
- The release workflow assembles the candidate APK, runs app/core/tool unit tests, and validates every bundled pack before publishing rc4.
