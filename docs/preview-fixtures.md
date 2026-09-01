# Preview fixtures — Phase 17d / Phase 18 handoff

Phase 17d defines deterministic inputs for the widget and cold-start rows of the UI parity matrix. It intentionally does **not** create golden screenshot files yet; Phase 18 owns the Roborazzi (or equivalent) capture/baseline system and intentional-diff workflow.

The canonical fixture source is:

`app/src/debug/java/com/shadowmonarchbooks/dayloop/preview/Phase17PreviewFixtures.kt`

It is debug-only so fixture scaffolding is never part of the release APK. The fixture vocabulary uses the generic skin DSL families (`engine`, `masks`, `moon`, `crown`) rather than game ids or game-name branches.

## Widget matrix

Widget fixtures keep one identical semantic payload (pack title/date, 3-of-5 completion, route/profile, next deadline) and vary only skin chrome + Glance size. Stable ids:

- `widget.engine.compact`
- `widget.engine.standard`
- `widget.engine.expanded`
- `widget.masks.compact`
- `widget.masks.standard`
- `widget.masks.expanded`
- `widget.moon.compact`
- `widget.moon.standard`
- `widget.moon.expanded`
- `widget.crown.compact`
- `widget.crown.standard`
- `widget.crown.expanded`

Sizes are the Phase 17b responsive sizes: 180×75 dp, 250×110 dp, 320×160 dp. `DayloopWidgetContent(...)` is the exact Glance content function used by production and is `internal` so the Phase 18 renderer can consume these snapshots without duplicating widget UI code.

Android/Glance rendering is still platform-backed; Phase 17d therefore pins the data + exact render entry point, while Phase 18 supplies the deterministic capture harness and golden files.

## Cold-start matrix

Cold-start fixtures cover all four skin families in both light and dark mode. Stable ids:

- `cold-start.engine.light` / `cold-start.engine.dark`
- `cold-start.masks.light` / `cold-start.masks.dark`
- `cold-start.moon.light` / `cold-start.moon.dark`
- `cold-start.crown.light` / `cold-start.crown.dark`

`StartupShell()` is the exact production Compose surface. Debug `@Preview` functions render the four families under light/dark Android Studio preview modes, so visual tuning can happen before Phase 18 pins screenshots.

The platform-owned Android splash screen itself is not a JVM-renderable Compose surface. Phase 18 should screenshot-pin the in-app `StartupShell` and keep the existing startup state tests for the pre-first-frame selection contract; device-level smoke verification remains appropriate for the system splash handoff.

## Phase 18 rule

Screenshot tests must consume these fixture ids/objects rather than defining a second matrix. If a fixture id or semantic payload changes, treat it like a screenshot-baseline API change: update this document, the fixture regression test, and the intentional screenshot rebaseline in the same change.
