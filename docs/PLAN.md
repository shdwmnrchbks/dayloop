# dayloop — Architecture & Build Plan

Working plan v1 (updated after multi-game amendment). Decisions locked during planning:

- **Stack:** Kotlin + Jetpack Compose (native Android first)
- **First complete pack:** Persona 5 Royal
- **Content:** authored + bundled JSON, validated by tooling; never scraped prose
- **Distribution:** private GitHub repo now, personal use first, designed so going public later is cheap
- **Long-term:** upgradeable engine supporting other ATLUS calendar games via data packs

---

## 1. Core product concept

A **game-calendar tracker**, not a wiki reader. The user advances their *in-game* clock through the app (tap "End Day"), and the app always answers two questions:

- *What should I do right now?*
- *What am I about to miss?*

All checklist items, confidant roadmaps, and palace/exam deadlines hang off that in-game clock.

### MVP scope
- Advanceable in-game calendar
- Per-day/slot checklist (afternoon/evening slots)
- Confidant reference with rank requirements and rank deadlines
- Major deadline list (palaces, exams, missables)
- Progress saved locally

### Later scope
- Multiple routes per pack (100% vs casual)
- Home-screen widget (Glance)
- Exam/test answer sheets
- Requests list
- Additional packs: Metaphor: ReFantazio, P3R, P4G (drop-in via schema)

## 2. Key design decision: model entities, not paragraphs

- **Pack format** `/content/packs/p5r/`: title-agnostic JSON bundle — `pack.json` (calendar range incl. Third Semester, time slots, currencies), `confidants.json`, `activities.json`, `deadlines.json`, `walkthrough/{month}.json`.
- **Template + override days:** most school days share a weekly template; special days (exams, plot events, confidant opportunities) are diffs/overrides. Cuts authoring effort roughly in half while keeping every day renderable.
- **Confidants as objects:** each rank step carries gates (availability window, stat requirement, location, prerequisites) so the UI can show *why* something isn't available today, and validators can catch contradictions.
- **Pack validation script** (`tools/packlint`): every deadline exists on the real calendar, every rank step precedes its date, no double-booked evening slot. Content edits become tested changes instead of silent breakage.

## 3. Multi-game engine architecture

**Guiding principle:** the app is a generic ATLUS-calendar engine; every game is a data pack. Screens/logic/strings must never know a specific game. If something says "Palace"/"Confidant"/"Royal Virtue" in Kotlin, that's an architecture bug — those words live in pack data.

### 3.1 Generic vocabulary, pack-supplied labels
Engine terms: `CalendarDate`, `TimeSlot`, `Activity`, `Stat`, `Bond`, `Deadline`, `MissableWindow`.
Packs map display names: Bond → Confidant (P5R) / Social Link (P3/P4) / Follower (Metaphor); Stat → Charm/Kindness/…/Royal Virtues. UI layer hardcodes no English game terms.

### 3.2 Capability manifest (in `pack.json`)
- `timeModel`: `"weekdayGrid"` (P3R/P4G/P5R) vs `"dayCounter"` (Metaphor travel calendar)
- `slots`: e.g. P5R `[Afternoon, Evening]`; count/names come from data
- Optional subsystem flags: exams, weather (true for P4G), stat system flavor, fusion extras (parked)

### 3.3 Typed condition DSL for availability
Structured predicates instead of free text, combinable via `allOf`/`anyOf`:

```json
{ "type": "weekdays", "value": ["tue","thu"] }
{ "type": "weather", "equals": "rain" }
{ "type": "statGte", "stat": "kindness", "rank": 3 }
{ "type": "storyFlag", "id": "p5r.flag.madarame.dead" }
```

V1 ships a small closed set of predicate types; new mechanics are additive. Powers the cross-game UX feature: explaining *why* something is locked today.

### 3.4 Templates keyed off declared dimensions
Weekly templates where `timeModel=weekdayGrid`; travel-segment templates for `dayCounter`. Overrides stay day-indexed either way.

### 3.5 Theming rides in the pack
Accent colors, original iconography, typographic accents per title. Switching games switches the skin — zero code change.

### 3.6 ID & version discipline
- IDs like `p5r.deadline.palace3.calling-card` are immutable forever; never reuse/recycle
- Saves stamp `packId @ contentVersion`
- Packlint rule: deleting/renaming an existing ID fails CI (additions fine)
- Migration surfaces orphaned saved checkboxes in a review dialog rather than dropping them

### 3.7 Profiles belong to packs
Game-switcher lists installed packs (bundled now, imported later); separate profiles/saves per pack.

## 4. Process guardrails

- **Design for three, build one.** Before mass-authoring P5R, throwaway mini-packs exercise the schema: one Metaphor month (`dayCounter`, virtue gates, remaining-day counters) and one P4G month (weather gating, fog deadlines). Both must render through the same engine unmodified before Phase 4 authoring begins.
- **Facts vs prose.** Schedules, thresholds, dates, exam answers = facts (safe to structure). Guide sentences = rewritten in our own words.
- **No game assets ever bundled.** Original typography/shapes only; unofficial-fan-tool naming ready for public flip.

## 5. Architecture table

| Layer | Choice |
|---|---|
| Language/UI | Kotlin, Jetpack Compose, Material 3 (dark-first), Navigation Compose |
| State | MVVM / unidirectional flow (ViewModel + StateFlow) |
| DI | Hilt |
| Static content | Bundled JSON via kotlinx.serialization (read-only assets) |
| Mutable state | Room (progress, checkmarks, profiles); DataStore (settings) |
| Min SDK | 26 (Android 8) |

### Screens
Today (hero: current slot, next action, deadline bar) → Day detail (slot-by-slot) → Month calendar (markers) → Bonds list/detail → Deadlines → Settings (advance/reroll/reset, profiles).

## 6. UX principles

1. **Anti-deviation friction:** Done / Skip / Later everywhere; deviating updates suggestions, never punishes.
2. **Progressive disclosure:** future deadlines visible; spoilers behind taps.
3. **In-game-time honesty:** deadline bar + widget > push notifications.

## 7. Milestones

| Phase | Deliverable | Size |
|---|---|---|
| 0 | Repo skeleton, Gradle version catalog, CI (build/test/packlint) | Small |
| 1 | Pack schema + packlint + seed P5R Apr–May + two cross-game mini-packs | Medium |
| 2 | Read-only app rendering seed data | Medium |
| 3 | Progress layer (checkboxes, End-Day, profiles/reset) | Medium |
| 4 | Full P5R pack authoring (**long pole**) | Large |
| 5 | Routes/profiles polish, widget, exam answers, search, icon | Flexible |
| 6 | Second complete pack (Metaphor or P4G) end-to-end | Large |

## 8. Repo layout

```
/app                      # Kotlin + Compose application
/content/packs/p5r/       # authored JSON packs (validated by packlint)
/tools                    # packlint + generators (JVM Gradle tasks)
/docs                     # schema docs, authoring guide, this plan
```

## 9. Public-later readiness checklist

- [x] Code license separable (MIT) from content licensing (CC BY when public)
- [x] Unofficial fan-tool positioning baked into identity
- [ ] Content licensing headers added to packs before any public flip
- [ ] Icon/name trademark sweep before Play Store submission

## 10. Content sources policy

- Mine MIT-licensed community datasets for fact cross-checking with attribution notes in `/docs/sources.md` (e.g. Sentovibes/persona-companion-app JSONs)
- Verify deadlines/ranks against community compendium sites (rana-shahroz P5R/P3R/Metaphor guides)
- Never copy guide prose; never bundle datamined assets

## 11. Known research findings (planning inputs)

- Competition thin: best multi-game app ~5K downloads, uneven coverage; nothing exists for Metaphor on mobile; no iOS companion apps for P3R/P4G/Metaphor at all
- Two OSS datasets worth mining for facts: Sentovibes/persona-companion-app (MIT), rana-shahroz/metaphor-guide (+ sister P5R/P3R sites)
- Engine + swappable packs is both hygiene and the differentiator for any future public release
