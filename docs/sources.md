# Content sources policy

dayloop packs are authored from **facts** (dates, thresholds, requirements,
answers — not copyrightable expression). Guide prose is never copied; every
label, note, and hint in `/content` is written in our own words. No game assets
are ever bundled (docs/PLAN.md §4/§10).

## Sources used

| Source | What we take | License / terms |
|---|---|---|
| Alyookid, "Persona 5 The Royal 100% Achievements + Perfect Schedule" (Steam Community, id 2877808380) | P5R schedule **facts**: day sequences, stat gains, confidant rank dates, exam answers, deadlines | All rights reserved by the author; personal-study use only. Raw archive stays OUT of version control (`P5R_100p_Guide_AI_Package/` is gitignored). Facts extracted via `tools/packgen` into our own schema. |
| HayateButler, "Persona 3 Reload 100% Perfect Schedule Guide" (Steam Community, id 3152126765) | P3R schedule **facts**: day sequences, social link/link-episode rank dates, social stat gains, deadlines, request answers | All rights reserved by the author; personal-study use only. Raw archive stays OUT of version control (`P3R_100p_Guide_AI_Package/` is gitignored). Facts to be extracted via `tools/packgen` into our own schema when P3R authoring starts. |
| HayateButler, "Metaphor: ReFantazio 100% Perfect Schedule Guide" (Steam Community, id 3346632862) | Metaphor schedule **facts**: day sequences, follower rank dates, Royal Virtue gains and thresholds, deadlines, request/achievement facts | All rights reserved by the author; personal-study use only. Raw archive stays OUT of version control (`Metaphor_ReFantazio_Guide_AI_Package/` is gitignored). Facts extracted via the scratch extractor in `build/packgen/metaphor/` (gitignored build output) into the ledger and our own schema. |
| Sentovibes/persona-companion-app (planned) | Fact cross-checking | MIT |
| rana-shahroz P5R/P3R/Metaphor guides (planned) | Fact cross-checking | Community compendium |

## Rules

1. **Facts vs prose.** Schedules, thresholds, dates, exam answers = facts (safe
   to structure). Guide sentences = rewritten in our own words.
2. **No assets.** Nothing from `P5R_100p_Guide_AI_Package/images/` (or any game
   asset) is committed or bundled.
3. **Attribution.** Every pack's `docs/sources.md` entry lists its sources, and
   the pack carries a content-licensing header before any public flip
   (docs/PLAN.md §9).
4. **The archive never enters git.** `.gitignore` guards
   `P5R_100p_Guide_AI_Package/`; extraction reads it from disk by path only.
