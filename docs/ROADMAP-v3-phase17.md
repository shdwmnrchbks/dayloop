# Phase 17 — Widget, icon & launch parity ✅

This document expands `docs/ROADMAP-v3.md` Phase 17 into reviewable implementation slices. Phase 17 is complete on `main`; Phase 18 is now the active roadmap phase.

## Delivery order

### Phase 17a — Cold-start skin resolution ✅

**Goal:** a returning user must never see an engine-look or onboarding flash while the persisted active pack is still resolving.

Scope:

- Hold the platform splash until the persisted pack selection has been resolved.
- Theme the first Compose frame from the resolved `PackStore` selection rather than from a slower UI projection.
- Gate first content until the richer UI projection agrees with the resolved pack, so startup cannot accidentally route to onboarding.
- Keep the in-app loading shell skin-aware for the small handoff window between store resolution and the full screen state.
- Preserve clean fallback behavior for fresh installs, invalid persisted slugs, and single-pack installs.
- Add regression coverage for startup-ready state contracts where practical.

Acceptance:

- Returning users do not see the engine theme before their saved skin.
- Returning users do not see the onboarding picker before Today.
- Fresh installs still land on onboarding after selection resolution.
- Single-pack installs still auto-select their only pack.
- Pack switching during an active session still re-skins in place.

### Phase 17b — Glance skin primitives and widget parity ✅

**Goal:** extend the active skin’s visual language to the home-screen widget without changing widget semantics.

Scope:

- Add a Glance-safe closed set of skin primitives derived from generic theme tokens.
- Approximate jagged/ribbon, glass-tile, and framed/plaque treatments within Glance constraints.
- Preserve title/date, completion count, next-deadline semantics and existing tap targets.
- Keep widget audio and app-only animation unreachable.
- Verify engine/theme-less fallback and supported widget sizes.

Acceptance:

- Engine, Phantom, Moonlight, and Royal render correctly across supported widget sizes.
- Content and navigation behavior are identical across skins.

### Phase 17c — Launcher badge schema, serving, and lint ✅

**Goal:** allow packs to decorate Dayloop-owned launcher/shortcut identity without replacing it with game-owned launcher art.

Scope:

- Add generic `theme.art["launcherBadge"]` support.
- Resolve and serve the badge through pack data only.
- Composite the small motif badge into Dayloop-owned launcher/shortcut treatment where Android permits.
- Add packlint validation for missing/invalid badge references and dimensions as appropriate.
- Keep absence of a badge as a clean engine fallback.

Implementation note:

- Android's primary installed-app icon remains the compiled Dayloop adaptive icon; arbitrary pack assets cannot safely replace it at runtime.
- The supported runtime surface is one dynamic launcher shortcut. Its base icon is always Dayloop-owned, with the optional pack badge composited into the lower-right corner.
- Bundled masks/moon/crown skins ship original abstract 96×96 PNG badge motifs; these are not official game launcher icons.
- Badge authoring and lint rules live in `docs/launcher-badges.md`.

Acceptance:

- Invalid badge references fail packlint.
- Packs without a badge retain current launcher treatment.
- No game-specific launcher logic or literals enter engine Kotlin.

### Phase 17d — Widget/cold-start preview fixtures ✅

**Goal:** produce stable preview inputs for the Phase 18 screenshot parity matrix.

Landed:

- A debug-only canonical fixture matrix in `Phase17PreviewFixtures`: four generic skin families (`engine`, `masks`, `moon`, `crown`) × the three responsive widget sizes.
- One identical widget semantic payload across every skin, so screenshot diffs isolate visual chrome rather than content changes.
- Four skin families × light/dark cold-start fixtures plus Android Studio Compose previews of the exact production `StartupShell()`.
- The exact production `DayloopWidgetContent(...)` and `StartupShell()` surfaces are internally reusable by the future screenshot harness instead of being copied into preview-only UI.
- Stable fixture ids are unit-test pinned so Phase 18 can attach golden screenshots to a durable naming contract.
- Platform boundaries and handoff rules are documented in `docs/preview-fixtures.md`: the Android system splash and Glance host are platform-backed; Phase 18 adds capture/goldens rather than a second fixture matrix.

Acceptance:

- Widget and cold-start rows have deterministic fixtures ready for Phase 18 screenshot pinning.
- Preview data uses only generic skin DSL tokens; no game-specific branching entered engine code.

## Phase 17 closeout

All four slices are complete. The parent Phase 17 acceptance criteria are now represented by shipped runtime behavior, lint/tests, and deterministic Phase 18 inputs:

- startup resolves the persisted skin before first visible app content;
- widget chrome is responsive and skin-driven while semantics remain invariant;
- launcher badge decoration is optional, validated, Dayloop-owned at the base, and fail-closed;
- engine/theme-less fallbacks remain intact;
- widget/cold-start fixture rows are ready for screenshot pinning.

Phase 18 owns screenshot goldens, budgets, animated WebP conversion, `strip-art`, macrobenchmark smoke, and final release hardening.
