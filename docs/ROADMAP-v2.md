# dayloop — UX Roadmap v2 (Phases 7–10)

Post-release roadmap (v0.1.0 shipped). Phases 0–6 in `docs/PLAN.md` are done; this
doc continues the numbering and focuses on how the app *feels* rather than what it
contains. Architecture rules from `docs/PLAN.md` §3 (engine neutrality, pack-supplied
labels, ID discipline) still apply everywhere below.

| # | Ask | Phase |
|---|-----|-------|
| 1 | First-run game picker; pack switcher out of the top bar, into Settings | Phase 7 ✓ |
| 2 | UI tailored to the pack (no Answers/Exam tab for Metaphor) | Phase 8 ✓ |
| 3 | Every fact in the packs actually served by the app | Phase 9 ✓ |
| 4 | Per-pack theme & visual identity, incl. graphic assets | Phase 10 ✓ |

---

## Phase 7 — First-run onboarding & pack selection relocation

**Status: shipped in v0.2.0.** As specified, plus these resolutions:
- The picker is a **swipeable carousel of cover-art cards** (owner request, refined from
  the first grid take): `HorizontalPager` with peeking neighbors, page dots, and a
  scrim-overlaid title block. Each pack's card renders its own `art/card.png|jpg`
  (owner-supplied covers, copied into the pack asset dirs); packs without art get a
  centered monogram fallback. Small tiles elsewhere (Settings rows) keep the separate
  `art/icon.png` slot with the same monogram fallback (`PackIcon`); both art slots are
  deliberately schema-free conventions — Phase 10 formalizes art slots in pack.json.
- Returning v0.1.0 users with no persisted selection see the picker once (their profiles
  and marks are untouched — profiles belong to packs, §3.7). The planned "restore
  prompt" was dropped: `ensureProfiles` bootstraps profiles for every pack, so
  "has progress" can't distinguish fresh from legacy installs; the one-time picker is
  the honest flow either way.
- Single-pack installs skip the picker (auto-selected and persisted).

**Goal:** a fresh install opens with "which game are we tracking?", not a hot-swappable
dropdown. Pack switching remains possible but lives in Settings.

Current state: `PackStore` auto-selects the most complete pack on first launch;
`AppRoot.DayloopTopBar` renders a pack DropdownMenu; `SettingsScreen` has no pack section.

Work items:
- **First-run detection:** "no persisted `selectedPack` in DataStore" = first run.
  `PackStore` must stop auto-selecting in that case (`selectedSlug = null`); only
  restore a persisted selection when one exists.
- **Onboarding route** (`"onboarding"`, outside the bottom-nav tabs):
  - Short app intro (one screen, not a wizard): what dayloop is + the fan-tool
    disclaimer (per-pack art per the Phase 10 decision rides on these cards).
  - One card per loaded pack: title, calendar range (pack-formatted), authored-day
    count, and the subsystems it includes (from Phase 8 capabilities: exams/answers,
    routes). Card tap = `selectPack(slug)` + navigate Today.
  - Edge cases: DataStore has profiles but no selection (restore prompt, don't re-greet
    as if new); only one pack installed → skip the picker, show a "you're tracking X"
    confirm.
- **Top bar de-swap:** remove the DropdownMenu from `DayloopTopBar`; title becomes the
  active pack title as plain text. Search/Settings actions stay.
- **Settings "Game" section** (top of `SettingsScreen`): pack list with the active one
  marked; tap to switch via existing `vm.selectPack`; each row shows per-pack profile
  count so it's clear saves are not lost (§3.7 profiles belong to packs).
- Update README Status line: onboarding + capability UI are Phase 7–8.

Acceptance:
- Fresh install → onboarding card list → pick → lands on Today; killing/reopening the
  app goes straight to Today for the chosen pack.
- No pack switcher in the top bar; switching works from Settings with per-pack profiles
  intact; a pack with zero profiles starts a fresh clock.

## Phase 8 — Capability-driven UI (pack-tailored tabs & screens)

**Status: shipped in v0.3.0.** As specified, plus these resolutions:
- The answers capability gates the tab, not a new UI concept: `capabilities.answers`
  ⇔ answers.json ships non-empty, enforced by packlint in both directions (declaring
  without data, or shipping without declaring, both fail lint — including an empty
  answers.json, which does not satisfy the capability).
- Bonds/Deadlines tabs key off **file presence** (`LoadedPack.hasBondsFile` /
  `hasDeadlinesFile`), not content count: a pack shipping an empty confidants.json
  still gets the tab, where the existing empty state renders. All three first-release
  packs ship both files, so the visible difference today is the Answers tab only.
- The empty-state audit found every shippable-empty list already carries a real empty
  state (bonds, deadlines, answer sheets, authored months, unauthored days, search);
  routes always resolve to ≥1 via the implicit default, and Settings only renders the
  route picker when >1 routes exist — no gaps to fill.
- `contentVersion` was deliberately **not** bumped for the pack.json capability
  backfill: it stamps saved marks against authored content (§3.6), and capability
  flags change no IDs, days, or steps — bumping would raise a spurious "content was
  updated" notice on every existing save.

**Goal:** the navigation and screens adapt to what the active pack actually contains.
Metaphor gets no Answers/Exam tab; nothing engine-side knows that fact — the pack does.

Current state: `Capabilities` has only `exams`/`weather`; `AppRoot.TopLevelRoutes`
hardcodes all five tabs; Metaphor ships no `answers.json` yet still gets an Answers tab.

Work items:
- **Extend the capability manifest** (`Pack.kt` `Capabilities`): add
  `answers: Boolean = false` (exam + class-question answer sheets). Keep the closed-set
  rule (§3.1): new booleans are additive, never per-game flags.
- **Backfill pack.json:** p5r/p3r `answers=true`; metaphor `answers=false` (omit = false).
- **packlint cross-checks:** `capabilities.answers == true` ⇔ `answers.json` exists and
  is non-empty; a pack declaring capabilities for files it doesn't ship fails lint (and
  vice versa).
- **Derived navigation:** build the tab list from the active pack (answers tab only when
  `capabilities.answers`; bonds/deadlines tabs only when the pack ships those files) —
  tab *count and order* become data-driven. Destinations stay registered so deep links
  never 404; tabs are what's filtered.
- **Guarded entry points:** DayScreen's answers affordance renders only on days that
  have an `AnswerSheet` in packs with the capability; Search results never link to
  hidden surfaces; the widget never promises a surface the pack lacks.
- Empty-state audit: any list the pack can legitimately ship empty (routes > 1, bonds)
  gets a real empty state, not a blank column.

Acceptance:
- Metaphor shows 4 tabs (Today/Calendar/Bonds/Deadlines); P5R/P3R show 5.
- No route reaches a screen whose data source the pack doesn't ship; packlint fails on
  manifest/file disagreement.

## Phase 9 — Data completeness: every pack fact served

**Status: done.** As specified, plus these resolutions:
- The audit lives in [docs/data-coverage.md](data-coverage.md) — every schema
  field mapped to its serving surface with a status, and a "keep it honest"
  checklist (schema change ⇒ matrix change, same commit).
- **Served gaps closed** (grouped by screen): slot pills on step rows (`Step.slot`,
  pack slot labels); Activities browsing (list + detail surfaces for
  `Activity.kind/statGains/location/notes/spoiler`, entered from a Today link,
  tappable step references, and search hits); bond rank gates render as a
  spoiler-safe "Requires: …" line (`describeCondition`, the §3.3 promise at
  presentation level — gates are validated data today, zero authored instances);
  `rank.availableUntil` renders as "Until <date>"; deadline kind chips;
  `AnswerSheet.deadlineRef` renders as a "Deadline: <label>" cross-link;
  search's activity/deadline hits navigate instead of dead-ending;
  `labels.stat` names the Activities gains section.
- **Intentionally unserved, documented:** `timeModel` (engine dimension),
  `day.weekday` (validated, not rendered), `calendar.nonPlayableDates` (clock
  behavior), `sheet.id` (identity), `capabilities.exams`/`weather` + the
  `Weather` condition (reserved flags), `routes[].description` (confined to
  profile creation).
- Cross-references the app resolves at render time are pinned by JVM tests
  (`PackContentTest`: deadlineRef, activityRef, slot and gate refs over the
  three bundled packs) on top of packlint's structural rules — CI-ready.

**Goal:** "served accordingly" is checkable, not vibes. Every field in the pack schema
is either rendered somewhere, reachable in ≤3 taps, or documented as intentionally
unserved with a reason.

Current state: the schema (`core/pack/.../schema/`) is richer than the UI in places.
Seed suspects for the audit (verify, then fix or document):
- `RankStep.gates` / `location` / `availableFrom` / `availableUntil` — the §3.3 promise
  of *"why is this locked today?"* is the biggest unserved feature candidate.
- `Activity.location` / `Activity.notes` — activities surface via walkthrough steps, but
  is there any way to browse the pack's activities themselves (stat gains, locations)?
- `Day.dayKind` / `Day.notes` — rendered distinctly, or flattened into steps?
- `Step.statGains` / `Step.spoiler` — shown per step? Spoiler behavior consistent with
  §6 progressive disclosure?
- `Bond.characterLabel`, `AnswerSheet.deadlineRef` (answer sheet ⇄ deadline cross-link).
- Route labels/descriptions surfaced outside Settings profile pinning.

Work items:
- **`docs/data-coverage.md`** — a matrix: schema field → serving surface (composable) →
  status (served / partially served / unserved / intentionally not). Kept honest by a
  checklist in the doc; updated with every schema change.
- Gaps become small work items inside this phase, grouped by screen, each one commit.
- Where cheap, add JVM tests (e.g., every `deadlineRef` resolves to a real deadline).

Acceptance:
- Matrix has zero "unknown" cells; every pack fact is reachable or documented.
- Packlint + unit tests still green on all three packs.

## Phase 10 — Per-pack theme & visual identity

**Status: shipped in v0.5.0.** As specified, plus these resolutions:
- `pack.json` gained an optional **`theme`** block: `accent`/`accentDark` seed
  colors (`#RRGGBB`/`#AARRGGBB`), a closed-set **`style`** token
  (`tonalSpot`/`vibrant`/`expressive`/`content` — how the seed expands into a
  scheme), a reserved `motif` slug token, and **`art`** slots
  (`{"card": "art/card.jpg", "icon": "art/icon.png"}`). `Theme.kt` maps the
  active pack's theme → full Material 3 dark *and* light schemes via the
  material color system (Apache-2.0 `material-color-utilities`); the
  hand-tuning lives in the pack data, the mapping is game-neutral Kotlin and
  identical for every pack. Packs without a theme keep the lantern engine skin.
- **Art decision (2026-08-31) implemented:** each pack ships curated guide-derived
  `card` (onboarding hero) and `icon` (Settings tiles) art, declared in
  `theme.art` — the Phase 7 conventional-path probing remains only as a
  fallback for theme-less packs. packlint validates every declared slot
  (pack-relative path, image extension, file exists). PLAN.md §9 carries the
  "strip game-derived art before any public flip" item.
- **Vocabulary extended to deadline kinds:** `labels.deadlineKinds` lets a pack
  rename the closed-set kind tokens the UI prints (Metaphor: `palace` →
  "Mission"); unlisted kinds keep the capitalized-token default.
- The Glance widget inherits the active pack's dark-scheme primary as its
  accent; a theme-less pack keeps the engine amber.

**Goal:** switching games switches the skin — PLAN.md §3.5 finally wired end to end.

Work items:
- **Theme rides in the pack:** `pack.json` gains an optional `theme` block
  (`accent`/`accentDark` seed colors, optional motif token). `Theme.kt` maps the active
  pack's theme → Material 3 `ColorScheme` (hand-tuned dark *and* light pairs per pack;
  fall back to the current lantern scheme when a pack declares none). No hardcoded game
  names in Kotlin — the pack supplies everything.
- **Vocabulary already pack-driven** (bond → Confidant/Social Link/Follower) — extend to
  any engine term the UI prints (stat groups, deadline kinds) so no game leaks through.
- **Graphic assets — DECIDED (2026-08-31): bundle curated art from the guide sources**
  (option c). Owner's call: fan-made, non-commercial, private repo. Implications:
  - Source material: the gitignored `*_Guide_AI_Package/images/` sets. Curate
    selectively (don't bulk-copy) into `content/packs/<slug>/art/` — the existing
    `assets.srcDir(content/packs)` ships it with no build changes.
  - Art gets committed to the private repo per this decision; PLAN.md §9 now carries a
    "strip game-derived art before any public flip" item — a future public flip means
    removing art and reverting to the original-motif identity (option a).
  - Art slots are named in `pack.json` `theme` (e.g. onboarding hero, header motif,
    bond-kind icon) so the engine stays data-driven and swapping art is a content
    change, never code.
- Onboarding cards (Phase 7) and the Settings game list get the per-pack treatment;
  the Glance widget inherits the active pack's accent so the home screen matches.

Acceptance:
- Switching packs in Settings recolors app + widget without restart; dark and light
  both hold contrast; zero game-title strings or game-derived binaries in git.

---

## Deferred / unchanged

- Release hardening (R8/minify, versioning discipline, CI) — still planned, unscheduled;
  Phase 9's JVM tests should land CI-ready.
- P4G and future packs stay drop-in: every phase above is engine-neutral by construction
  (capabilities, labels, theme all come from data).
- Import packs from device storage: not scheduled; Phase 7's onboarding assumes bundled
  packs only until that exists.
- `theme.motif` is declared and lint-validated but still reserved — no decorative surface
  consumes it yet.
