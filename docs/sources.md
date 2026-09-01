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
| Push Square, "Persona 5 Royal: Exam Answers — All School and Test Questions Answered" | P5R school/class and exam answer cross-check; used by the 2026-09 P5R data audit to fill omitted questions and correct date/answer transcription errors | Public web guide; facts only, no guide prose copied. |
| Megami Tensei Wiki, "Palace" / Persona 5 calendar pages | P5R palace availability/story-deadline cross-check | Public community reference; facts only. |
| RPG Site P5R confidant guides + GameFAQs P5R confidant guides/Q&A | Royal-specific Confidant gates and missable cross-checks: Councilor rank 9 unlock requirement, Justice rank 8/Promise optional content, Faith rank 5 continuation deadline | Public web guides/community reference; facts only. Conflicting community claims are resolved against multiple sources before authoring. |
| HayateButler, "Persona 3 Reload 100% Perfect Schedule Guide" (Steam Community, id 3152126765) | P3R schedule **facts**: day sequences, social link/link-episode rank dates, social stat gains, deadlines, request answers. **Graphics**: every guide image (incl. full-moon marker, character portraits), curated into `content/packs/p3r/images/` + `media.json` (rule 2) | All rights reserved by the author; personal-study use only. Raw archive stays OUT of version control (`P3R_100p_Guide_AI_Package/` is gitignored). Facts to be extracted via `tools/packgen` into our own schema when P3R authoring starts. |
| HayateButler, "Metaphor: ReFantazio 100% Perfect Schedule Guide" (Steam Community, id 3346632862) | Metaphor schedule **facts**: day sequences, follower rank dates, Royal Virtue gains and thresholds, deadlines, request/achievement facts. **Graphics**: every guide image (incl. achievement icons), curated into `content/packs/metaphor/images/` + `media.json` (rule 2) | All rights reserved by the author; personal-study use only. Raw archive stays OUT of version control (`Metaphor_ReFantazio_Guide_AI_Package/` is gitignored). Facts extracted via the scratch extractor in `build/packgen/metaphor/` (gitignored build output) into the ledger and our own schema. |
| Sentovibes/persona-companion-app (planned) | Additional fact cross-checking | MIT |
| rana-shahroz P5R/P3R/Metaphor guides (planned) | Additional fact cross-checking | Community compendium |

## Rules

1. **Facts vs prose.** Schedules, thresholds, dates, exam answers = facts (safe
   to structure). Guide sentences = rewritten in our own words.
2. **Graphics, curated and declared (updated 2026-08-31 / ROADMAP-v2 Phase 10,
   served end to end in ROADMAP-v3 Phase 11).** Every graphic from
   `*_Guide_AI_Package/images/` is **copied into `content/packs/<slug>/images/`**
   and declared in the pack's `media.json` — this private, non-commercial build
   ships them (the original `*_Guide_AI_Package/` archives themselves still
   never enter git). All graphics are attributed below, and the strip-art
   pipeline (ROADMAP-v3 Phase 18) removes them before any public flip
   (docs/PLAN.md §9).
3. **Attribution.** Every pack's `docs/sources.md` entry lists its sources, and
   the pack carries a content-licensing header before any public flip
   (docs/PLAN.md §9).
4. **The archive never enters git.** `.gitignore` guards
   `P5R_100p_Guide_AI_Package/` (and its siblings); extraction reads it from
   disk by path only. The curated copies under `content/packs/<slug>/images/`
   are the only graphics that ship.
