# dayloop

**Unofficial Android companion/checklist app for calendar-based ATLUS-style JRPGs** — launching with Persona 5 Royal, Persona 3 Reload, and Metaphor: ReFantazio. The engine stays pack-generic; further ATLUS titles can drop in later.

> *This is a fan-made tool. It is not affiliated with or endorsed by ATLUS/SEGA. No game assets are bundled.*

## What it is

An advanceable in-game calendar tracker. You move your game's clock forward in the app ("End Day") and it always answers:

1. **What should I do right now?**
2. **What am I about to miss?**

Checklists, bond/confidant roadmaps, palace/deadline tracking, exam windows — all hanging off that clock.

## Design pillars

- **Engine + data packs.** The app is a generic calendar-engine; every game is a swappable JSON pack (`/content/packs/<slug>/`). No Kotlin/UI code knows what a "Palace", "Confidant", or "Royal Virtue" is — those words live in pack data.
- **Anti-deviation UX.** Every item supports Done / Skip / Later — never a red failure state. Going off-guide just changes future suggestions.
- **Spoiler-safe by default.** Deadlines visible, story commentary hidden behind taps.
- **Offline-first.** Content ships bundled; progress saves locally (Room); no account required.
- **In-game-time honesty.** Always-visible deadline bars + home-screen widget beat push notifications nobody plays at the right pace for.

## Tech stack

Kotlin · Jetpack Compose · Material 3 · MVVM (ViewModel + StateFlow) · Hilt · kotlinx.serialization · Room · DataStore · minSdk 26

## Roadmap

| Phase | Deliverable |
|---|---|
| 0 | Repo skeleton, Gradle version catalog, CI (build + test + packlint) |
| 1 | Pack schema + `packlint` validator + cross-game fit checks (mini-packs: Metaphor `dayCounter` model, P3R `weekdayGrid` model) |
| 2 | Read-only app: renders seeded P5R April–May days, calendar, confidant screens |
| 3 | Progress layer: checkboxes, End-Day advancement, profiles/reset |
| 4 | Full P5R pack authoring (template + override days) |
| 5 | Polish: routes/profiles, Glance widget, exam answers, search, icon |
| 6 | Second complete pack (P3R or Metaphor) proving drop-in claim, then the third to complete the first-release trio |
| 7 | First-run onboarding carousel & pack selection relocated to Settings ([docs/ROADMAP-v2.md](docs/ROADMAP-v2.md)) |

See [docs/PLAN.md](docs/PLAN.md) for the full architecture plan.

## Status

🚧 **Phase 0–7 done — v0.2.0 released** ([Releases](https://github.com/shdwmnrchbks/dayloop/releases)). Phase 0–4 delivered the pack schema + `packlint` + `packgen`, the **complete P5R pack** — every calendar day 2016-04-09 → 2017-02-03 authored and packlint-validated (301 walkthrough days, all 23 confidant arcs with full rank ladders, 23 deadlines covering palaces 1–8 + exam windows + missable gates, 73 activities) — the read-only app rendering all three packs from bundled assets with a pack switcher, and the **progress layer**: per-pack profiles in Room, persisted Done/Skip/Later checkboxes, the End-Day in-game clock (with reroll/reset), a carried-over queue for deferred steps, and orphaned-mark review when pack content changes (docs/PLAN.md §3.6).

Phase 5 added:
- **Routes** — packs can declare multiple walkthrough routes (`pack.json` `routes` + `walkthrough/<routeId>/`); profiles pin a route (Room v2 migration, default `standard`), and the Metaphor fit-check pack ships a second "Casual" route proving multi-route rendering.
- **Exam answers** — structured answer sheets (`answers.json`) for all 12 P5R exam days and 53 class questions, surfaced in a new Answers tab and on day pages.
- **Search** — top-bar search across steps, bonds, activities, deadlines, and answers for the selected pack/route.
- **Home-screen widget (Glance)** — in-game date, today's done-count, and the next deadline, always visible; refreshes with app state and re-reads progress on its own.
- **Launcher icon** — original adaptive artwork (sun + day-loop arrow), no game assets.

Phase 6:
- **Default-pack selection is now pack-agnostic** — the last engine hardcode (`PackStore` defaulting to P5R) is gone: the app opens on the most complete installed pack and persists the user's choice in DataStore.
- **Complete P3R pack** — every playable calendar day 2009-04-08 → 2010-03-05 authored and packlint-clean (0 errors, 0 warnings; story-skipped February 2010 and March 1–3 are non-playable), with 301 walkthrough days across 11 months, all 22 Social Links full-ladder (203 rank entries), Link Episodes woven into the walkthrough days, 15 deadlines (full-moon operations + all 4 exam windows + one-day sales), and 53 answer sheets (17 exam days, 36 class questions).
- **`dayCounter` time model** — the engine now supports pack-declared game calendars (`monthLengths`, `weekdayCycle`, `weekdayAnchor`): `GameCalendar` in core/pack, lint cross-checks for cycle weekdays and game-month coverage, progress-clock stepping, and cycle-aware date formatting, day arithmetic, and month grids throughout the app (today/deadlines/bonds/answers/search/settings/widget/month views).

- **Complete Metaphor: ReFantazio pack** — the first-release trio is complete: every playable in-game day 2100-06-02 → 2100-10-26 authored and packlint-clean (0 errors; 140 walkthrough days across five 30-day game months; June 3–9's story-only dates non-playable), all 14 Follower bonds full-ladder (109 dated rank entries), 11 deadlines (nine story missions including the Charadrius window and the story-locked 10/26 final battle, plus missable inn-cooking and book windows), and 16 activities (7 books, ranked league + gauntlet challenge, inn/runner cooking, fishing, Gold Beetles, Akademeia study, podium debates, bounty requests). Facts curated from the HayateButler Metaphor guide (docs/sources.md).

Phase 7 (v0.2.0) — [docs/ROADMAP-v2.md](docs/ROADMAP-v2.md):
- **First-run onboarding** — a fresh install (or a legacy install that never picked a game) opens on a swipeable carousel of game cards with per-pack cover art (`art/card.png|jpg`, monogram fallback until more art lands); tapping a card starts tracking that game.
- **Pack switching moved to Settings** — the top-bar hot-swap dropdown is gone; the title is now a static label, and the new "Game" section lists installed packs with saved-profile counts.
- Cold-start loading shell so returning users never see the picker flash before their saved game reopens.

Next: capability-driven tabs (Phase 8), the data-completeness audit (Phase 9), and per-pack theming & art (Phase 10) — all in [docs/ROADMAP-v2.md](docs/ROADMAP-v2.md).

Pack focus: **P5R → P3R → Metaphor: ReFantazio**. Persona 4 Golden is deferred — the schema keeps any future pack drop-in.

## License

Code under [MIT](LICENSE). Game-data packs currently authored from scratch — see docs/PLAN.md §Content sources for sourcing policy.
