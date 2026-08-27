# dayloop

**Unofficial Android companion/checklist app for calendar-based ATLUS-style JRPGs** — Persona 3 Reload, Persona 4 Golden, Persona 5 Royal, Metaphor: ReFantazio.

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
| 1 | Pack schema + `packlint` validator + cross-game fit checks (mini-packs: Metaphor `dayCounter` model, P4G weather model) |
| 2 | Read-only app: renders seeded P5R April–May days, calendar, confidant screens |
| 3 | Progress layer: checkboxes, End-Day advancement, profiles/reset |
| 4 | Full P5R pack authoring (template + override days) |
| 5 | Polish: routes/profiles, Glance widget, exam answers, search, icon |
| 6 | Second complete pack (Metaphor or P4G) proving drop-in claim |

See [docs/PLAN.md](docs/PLAN.md) for the full architecture plan.

## Status

🚧 In planning / pre-scaffold.

## License

Code under [MIT](LICENSE). Game-data packs currently authored from scratch — see docs/PLAN.md §Content sources for sourcing policy.
