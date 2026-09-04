# Content sources policy

dayloop packs are authored from **facts** (dates, thresholds, requirements,
answers — not copyrightable expression). Guide prose is never copied; every
label, note, and hint in `/content` is written in our own words. Graphics from
the guide archives are curated into `content/packs/<slug>/images/` for the
private build (rule 2 below); the source archives themselves are never
committed (docs/PLAN.md §4/§10, ROADMAP-v2 Phase 10).

## Sources used

| Source | What we take | License / terms |
|---|---|---|
| Alyookid, "Persona 5 The Royal 100% Achievements + Perfect Schedule" (Steam Community, id 2877808380) | P5R completion-route **facts**: day sequences, stat gains, route-selected confidant rank dates, exam answers and route targets. **Graphics**: every guide image, curated into `content/packs/p5r/images/` + `media.json` (rule 2) | All rights reserved by the author; personal-study use only. Raw archive stays OUT of version control (`P5R_100p_Guide_AI_Package/` is gitignored). Facts extracted via `tools/packgen` into our own schema. Route-selected dates must not be presented as universal availability/deadline facts without a second source. |
| sdarkpaladin, "Persona 5 Royal 100% Completion Walkthrough" (GameFAQs) | Independent P5R route/date spot-checking and Palace/Confidant progression cross-checks | Public guide; facts only. Used to distinguish route timing from fixed game timing. |
| Raidramon0, "Persona 5 Royal Confidant Guide" (GameFAQs, updated 2026-07-27) | Current Royal Confidant names, stat gates, availability/time gates, request gates and automatic progression facts | Public guide; facts only, no guide prose copied. |
| RPG Site Persona 5 Royal Confidant guides | Royal-specific Confidant unlocks, recurring availability, stat requirements, weather restrictions and request gates | Public web guide; facts only. Conflicting community claims are resolved against multiple sources before authoring. |
| Sentovibes/persona-companion-app | Structured P5/P5R social-link data used as an additional independent check for automatic ranks, unlock requirements and recurring schedules | MIT. Facts are re-expressed in dayloop's schema; no prose is copied. |
| Push Square, "Persona 5 Royal: Exam Answers — All School and Test Questions Answered" | P5R school/class and exam answer cross-check; used by the 2026-09 P5R data audit to fill omitted questions and correct date/answer transcription errors | Public web guide; facts only, no guide prose copied. |
| Samurai Gamers, "Persona 5 Royal — Mementos Request List and Guide"; Aqiu/megaten-database P5R Metaverse reference | Royal's 33 request identities, targets, locations, weaknesses and rewards; cross-check for special and Third Semester request handling. Alyookid remains the source for this app's route completion dates. | Public web guides; facts only, with all Dayloop task/tip wording independently written. |
| Megami Tensei Wiki, "Palace" / Persona 5 calendar pages | P5R palace availability/story-deadline cross-check | Public community reference; facts only. |
| HayateButler, "Persona 3 Reload 100% Perfect Schedule Guide" (Steam Community, id 3152126765) | P3R schedule **facts**: day sequences, social link/link-episode rank dates, social stat gains, deadlines, request answers. **Graphics**: every guide image (incl. full-moon marker, character portraits), curated into `content/packs/p3r/images/` + `media.json` (rule 2) | All rights reserved by the author; personal-study use only. Raw archive stays OUT of version control (`P3R_100p_Guide_AI_Package/` is gitignored). Facts to be extracted via `tools/packgen` into our own schema when P3R authoring starts. |
| HayateButler, "Metaphor: ReFantazio 100% Perfect Schedule Guide" (Steam Community, id 3346632862) | Metaphor schedule **facts**: day sequences, follower rank dates, Royal Virtue gains and thresholds, deadlines, request/achievement facts. **Graphics**: every guide image (incl. achievement icons), curated into `content/packs/metaphor/images/` + `media.json` (rule 2) | All rights reserved by the author; personal-study use only. Raw archive stays OUT of version control (`Metaphor_ReFantazio_Guide_AI_Package/` is gitignored). Facts extracted via the scratch extractor in `build/packgen/metaphor/` (gitignored build output) into the ledger and our own schema. |
| rana-shahroz P5R/P3R/Metaphor guides (planned) | Additional fact cross-checking | Community compendium |

## Rules

1. **Facts vs prose.** Schedules, thresholds, dates, exam answers = facts (safe
   to structure). Guide sentences = rewritten in our own words.
2. **Route facts vs game facts.** A completion guide's selected day is stored as
   a route date (`scheduledFor` for bond ranks, or a walkthrough step date), not
   as universal availability/deadline data. `availableFrom` / `availableUntil`
   are reserved for independently supported game windows.
3. **Graphics, curated and declared (updated 2026-08-31 / ROADMAP-v2 Phase 10,
   served end to end in ROADMAP-v3 Phase 11).** Every graphic from
   `*_Guide_AI_Package/images/` is **copied into `content/packs/<slug>/images/`**
   and declared in the pack's `media.json` — this private, non-commercial build
   ships them (the original `*_Guide_AI_Package/` archives themselves still
   never enter git). All graphics are attributed below, and the strip-art
   pipeline (ROADMAP-v3 Phase 18) removes them before any public flip
   (docs/PLAN.md §9).
4. **Attribution.** Every pack's `docs/sources.md` entry lists its sources, and
   the pack carries a content-licensing header before any public flip
   (docs/PLAN.md §9).
5. **The archive never enters git.** `.gitignore` guards
   `P5R_100p_Guide_AI_Package/` (and its siblings); extraction reads it from
   disk by path only. The curated copies under `content/packs/<slug>/images/`
   are the only graphics that ship.
