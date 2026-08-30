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

See [docs/PLAN.md](docs/PLAN.md) for the full architecture plan.

## Status

🚧 Phase 0–4 done: pack schema + `packlint` + `packgen`, the **complete P5R pack** — every calendar day 2016-04-09 → 2017-02-03 authored and packlint-validated (301 walkthrough days, all 23 confidant arcs with full rank ladders, 23 deadlines covering palaces 1–8 + exam windows + missable gates, 73 activities) — the read-only app rendering all three packs from bundled assets with a pack switcher, and the **progress layer**: per-pack profiles in Room, persisted Done/Skip/Later checkboxes, the End-Day in-game clock (with reroll/reset), a carried-over queue for deferred steps, and orphaned-mark review when pack content changes (docs/PLAN.md §3.6).
Next: Phase 5 — routes/profiles polish, Glance widget, search, icon.

Pack focus: **P5R → P3R → Metaphor: ReFantazio**. Persona 4 Golden is deferred — the schema keeps any future pack drop-in.

## License

Code under [MIT](LICENSE). Game-data packs currently authored from scratch — see docs/PLAN.md §Content sources for sourcing policy.
