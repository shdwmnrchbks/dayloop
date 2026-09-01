# Phase 17 — Widget, icon & launch parity

This document expands `docs/ROADMAP-v3.md` Phase 17 into reviewable implementation slices. The parent roadmap remains authoritative for the overall goal and acceptance criteria; this file defines delivery order and ownership boundaries.

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

### Phase 17d — Widget/cold-start preview fixtures

**Goal:** produce stable preview inputs for the Phase 18 screenshot parity matrix.

Scope:

- Add representative widget previews/fixtures for engine, Phantom, Moonlight, and Royal.
- Add cold-start/loading-shell preview fixtures for the same skin set and light/dark modes where supported.
- Document any platform-only pieces that cannot be deterministically screenshot-tested on JVM.
- Feed these fixtures into the Phase 18 screenshot-test rollout instead of introducing a second visual baseline system.

Acceptance:

- Widget and cold-start rows have deterministic fixtures ready for Phase 18 screenshot pinning.
- Preview data contains no game-specific branching in engine code.

## Sequencing rules

- 17a lands first because every later preview depends on stable startup skin resolution.
- 17b may add only generic Glance primitives; it must not introduce launcher concerns.
- 17c owns launcher-badge schema/lint so widget work does not become coupled to launcher resources.
- 17d is verification preparation, not the full screenshot regression system; the latter remains Phase 18.
- Phase 17 is complete only when the parent roadmap’s widget, launcher, cold-start, fallback, and preview acceptance criteria are all satisfied.
