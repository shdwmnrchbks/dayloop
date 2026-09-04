# Metaphor: ReFantazio data audit

Status: **complete**. This ledger records evidence-first corrections to the bundled `metaphor` pack. A finding is marked fixed only when the data model and regression coverage encode the conclusion.

## Source roles

- **Primary completion route:** HayateButler, *Comprehensive Walkthrough Schedule & General Resource* (Steam Community guide 3346632862). Dayloop follows this authored 100% route. Dates chosen by that schedule are route facts, not universal availability.
- **Follower mechanics / prerequisites:** HayateButler, *All Followers Guide* (Steam Community guide 3346632882), cross-checked against independent follower/quest references for fixed timing and prerequisite claims.
- **Mission / request windows:** HayateButler's quest resource plus independent operation/request references are used to distinguish actual game windows from the primary route's chosen completion date.
- **Achievements:** Steam's 44-achievement catalog supplies canonical names/descriptions; independent trophy/achievement references are used for trigger mechanics where the platform text is incomplete or misleading.
- **Existing source policy:** `docs/sources.md` remains authoritative: `scheduledFor` is the selected completion route; `availableFrom` / `availableUntil` are independently supported game windows or fixed story timing.

## Findings

### MET-AUD-001 — Metaphor did not identify its authored route — FIXED

`pack.json` previously declared no route even though the walkthrough is a source-specific 100% schedule. The pack now declares the `standard` **100% Completion Route** and explicitly states that route dates are not universal availability or deadlines. `contentVersion` is 3.

Regression: `MetaphorFollowerAuditTest`.

### MET-AUD-002 — Follower route dates were stored as universal availability — FIXED

The 14-Follower / 112-rank catalog was largely authored with `availableFrom` using the day chosen by the completion guide. The catalog now reserves `availableFrom` for independently supported fixed story timing and uses `scheduledFor` for route-selected events, task/relic/request completions, and objective-triggered automatic ranks whose day depends on route progress.

Regression: `MetaphorFollowerAuditTest` pins the fixed-story set and requires the remaining ranks to use route dates.

### MET-AUD-003 — Gallica's automatic ranks were flattened into one date type — FIXED

Gallica ranks 1, 2, 5, 7, and 8 are fixed story timing. Ranks 3, 4, and 6 trigger when main operations are cleared; those therefore use the completion route's clear day in `scheduledFor` rather than pretending the chosen day is a universal unlock.

### MET-AUD-004 — Catherina mixes quest, fixed-story, travel-triggered, and manual ranks — FIXED

Catherina now distinguishes her quest-earned rank 1, fixed story encounters for ranks 2–3, travel-triggered rank 4, and route-selected ranks 5–8.

### MET-AUD-005 — More progression was incomplete — FIXED

More rank 2 was undated and several later task completions had disappeared from the walkthrough. The route now includes the June 12 Foreword and Prologue turn-in, July ranks 3–5, August rank 7, and the late-game rank 8 task completion with their route gains and prerequisites represented.

### MET-AUD-006 — Route clear dates and story beats were mislabeled as mission deadlines — FIXED

The deadline catalog now represents real operation windows rather than the completion guide's selected clear dates:

| Operation | Start | Deadline |
|---|---:|---:|
| Necromancer Takedown | 06/12 | 06/21 |
| Apprehend the Real Kidnapper | 07/05 | 07/16 |
| Infiltrate the Charadrius | 07/23 | 08/12 |
| Obtain Drakodios | 08/19 | 09/05 |
| Prepare for the Final Battle | 09/13 | 09/22 |
| Skybound Avatar Conquest | 09/26 | 10/25 |

Mandatory one-day story beats such as Northern Border Fort, Nord Mines, Montario Opera House, the 09/23 Royal Capital duel, and the 10/26 Tyrant's Star boundary are explicitly `other`, not fake palace deadlines.

Regression: `MetaphorDeadlineAuditTest`.

### MET-AUD-007 — Stable deadline IDs needed audited concepts — FIXED

Added stable IDs for `Prepare for the Final Battle` and the 10/26 `Tyrant's Star` boundary and updated the Metaphor ID baseline while retaining existing concept-compatible IDs.

### MET-AUD-008 — June Royal Virtue progression omitted a required gain — FIXED

The June 12 capital errand block omitted the Tolerance gain associated with the Pagan's Dilemma / Breath of Fresh Air route work. The route now records the missing +10 Tolerance and keeps the other early visible point gains in the same units used by the guide.

### MET-AUD-009 — Hidden Royal Virtue thresholds were being treated as exact game facts — FIXED

An initial regression test attempted to validate cumulative point totals against community-estimated hidden thresholds. That is stronger than the evidence supports. The audit contract now pins every rank 2–5 transition to the primary completion route's explicit rank-up date and separately pins source-visible gains. It does **not** claim hidden engine thresholds are exact when the game does not expose them.

Regression: `MetaphorRoyalVirtueAuditTest`.

### MET-AUD-010 — July route had a concentrated data-loss cluster — FIXED

The July walkthrough restored Gallica rank 3, More ranks 3–5, New World Travel Diary's completion bonus, the route's Imagination rank 3 transition, corrected debate Imagination rewards, moved Jin from the impossible 07/21 date to 07/23, restored Milo on 07/27, and restored the Eloquence rank 4 transition.

### MET-AUD-011 — Podium debates were incomplete and one was impossible — FIXED

All eight opponents are now represented with their route date, winning answer, and Royal Virtue reward. The activity catalog documents each candidate's availability window / weekday schedule rather than reducing recurring availability to a single universal date.

Regression: `MetaphorDebateAuditTest`.

### MET-AUD-012 — Julian's book was modeled as a one-day missable — FIXED

`The Future of Magic` is now represented with the independently supported 07/23–08/12 acquisition window. The completion route still acquires it on 07/23, but that route choice no longer masquerades as the only valid day.

Regression: `MetaphorDeadlineAuditTest`.

### MET-AUD-013 — August progression had omitted route events and gains — FIXED

The August pass restored omitted companion/free-time gains, the full Virga Island beetle sweep, Gallica rank 6, Hulkenberg rank 7, Imagination rank 4 on 08/20, More rank 7 on 08/27, and the source route's late-August Follower / Royal Virtue sequence.

### MET-AUD-014 — September progression had omitted route events and gains — FIXED

The September pass restored Eupha's first book session, Maria/Fabienne cooking Tolerance, the corrected Eupha conversation value, Julian's final debate, Catherina ranks 5–6, Junah rank 7, Mt. Vulkano's Eloquence reward, Basilio's training Courage, additional late-game Wisdom gains, Imagination rank 5, and Wisdom rank 5.

### MET-AUD-015 — October/endgame was compressed past completion-critical milestones — FIXED

October now preserves the source route's individual late-game milestones instead of collapsing them into generic tasks. Restored items include:

- All That Glitters exchange completion on 10/01.
- Alonzo ranks 7–8 and Basilio rank 8's Tolerance gain.
- Hearts as One completion when Neuras reaches rank 8 on 10/05.
- all four dragon-trial clears (Devourers of Nations, Stars, Flames, then Elegy of the Soul).
- the individual Ranked League promotions and 30-round Gold Gauntlet.
- late Royal Virtue/free-time gains through 10/24.
- Skybound Avatar sequencing and the 10/26 normal-vs-Star-Shatterer final-boss branches.

Regression: `MetaphorOctoberAuditTest`.

### MET-AUD-016 — Achievement data was artwork-driven and incomplete — FIXED

The pack had 43 achievement images embedded in `media.json` but no canonical `achievements.json`, which meant the actual 44-achievement set could not be represented accurately. The missing achievement was **Entrusted**.

Metaphor now ships a pack-native 44-entry achievement catalog. The 43 real guide icons are linked through optional `iconMediaRef`; Entrusted remains iconless rather than reusing or fabricating unrelated artwork. Story/route achievements use supported dates or semantic walkthrough events. Cumulative, variable-grind, NG+, and optional-branch achievements stay counters, confirmations, or conditional rules rather than receiving arbitrary month-end dates.

Regression: `MetaphorAchievementAuditTest`.

### MET-AUD-017 — Entrusted's platform description does not describe its actual trigger — FIXED

Platform lists describe Entrusted as overcoming the trials to defeat Louis, while observed achievement behavior ties the unlock to defeating **Elegy of the Soul**, the final dragon trial. Dayloop keeps the canonical platform description but anchors completion to the audited 10/12 Elegy event.

### MET-AUD-018 — Gold Beetle completion semantics were ambiguous — FIXED

The reusable activity now keeps the supported `50 obtainable / 46 required for all exchanges` model. The route explicitly performs the final required exchanges on 10/01 and separately collects its final route beetle later; collecting all 50 is not incorrectly required for All That Glitters.

### MET-AUD-019 — Timed requests and permanently missable collectibles were under-modeled — FIXED

The deadline surface now covers every independently listed timed side request used by the first-playthrough route:

- Pagan's Dilemma — 06/12–06/16.
- Help the Hushed Honeybee — 06/12–06/19.
- Hatching a Plan — 06/29–07/11.
- A Haunted Heirloom — due 07/30; its start is prerequisite-gated rather than assigned a fabricated universal date.
- Skullduggery — due 07/30.
- Dental Distress — 07/23–08/09, explicitly noting its missable Gold Beetle reward.
- Efflorescent Youth — 07/23–08/10.
- A Guiding Gift — 08/19–08/30.
- Charadrius key objectives — due with the 08/12 operation; Sergeant Xanth is followed by either the Ceiba or Glechom corridor branch.

The other two one-way-area Gold Beetles are now prominent missables on 06/05 (Northern Border Fort) and 09/24 (Ancient Eldan Sanctum). Julian's book and the inn-cooking window remain separately represented.

There is no standalone generic Requests schema in the pack format. Non-timed requests therefore remain authored as dated walkthrough tasks; the deadline catalog is intentionally reserved for actual expiry / one-way warnings rather than duplicating every timeless side quest.

Regression: `MetaphorDeadlineAuditTest` and `MetaphorRouteResidualAuditTest`.

### MET-AUD-020 — Reusable book activities implied one fixed Royal Virtue gain — FIXED

The seven reading activities previously carried a generic `statGains` value even though the final reading session awards a larger completion bonus. Generic book gains were removed from `activities.json`; exact gains stay on the dated walkthrough steps where ordinary and completion sessions can differ.

Regression: `MetaphorActivityAuditTest`.

### MET-AUD-021 — June/July residual route compression still hid completion-critical tasks — FIXED

The residual line pass restored Catherina's June 12 `A Friend in Need` rank, the correct June 27 Martira beetle details, the Imp's Den beetle, Man-Eater's Grotto relic/beetle, the Practical Pidgeon Parcel handoff, the full five-beetle Brilehaven sweep, timed request turn-ins, and the July 23 cross-city errands.

A fabricated July 21 free-time entry was removed: the primary schedule has July 21–22 as story time, so July 23 errands are no longer shifted onto a nonexistent free day.

The July 25 Charadrius task now explains the Sergeant Xanth objective and the Ceiba/Glechom corridor choice instead of flattening the operation to generic boss text.

Regression: `MetaphorRouteResidualAuditTest`.

### MET-AUD-022 — August residual route data hid request and collectible dependencies — FIXED

The August line pass restored the Dental Distress Gold Beetle turn-in, August 19's Peak Curiosity / Price of Hope / Greater One-Eyed Scoundrel / A Guiding Gift / Save the Mourning Snakes setup, the three Polar Stones needed for A Guiding Gift, and the later request/Gold Beetle completion steps.

Regression: `MetaphorRouteResidualAuditTest`.

### MET-AUD-023 — September residual route data hid late request setup — FIXED

The September line pass restored the Proof of Power / Rudolf setup, Tainted Threads and Tail Bait shopping, Fiend in the Frozen Forest pickup, the Malva fifth-town milestone, Mt. Vulkano's Rusty Greatsword for The Edge of Glory, the Ligno stop, and the exact late bounty / Dragon Trial request names.

The Wisdom rank-5 label is normalized to the game's **Sagacious**, not the source guide's `Sagatious` typo.

Regression: `MetaphorRouteResidualAuditTest`.

### MET-AUD-024 — Achievement residuals and the NG+ boundary needed explicit contracts — FIXED

Globetrotter, Worldly Wisdom, Summon Mask Time, and Help Anyone in Need retain confirmation-based tracking where the route date is a supported expectation but the app cannot prove the external in-game flag from task completion alone.

`Closing the Book` remains New Game+ only with no first-playthrough date. The first-playthrough walkthrough is explicitly forbidden from containing the Book of Apocalypse / Redscale Dragon path, while The Traveller records that Closing the Book is required.

Regression: `MetaphorAchievementAuditTest` and `MetaphorNgPlusAuditTest`.

### MET-AUD-025 — Help Anyone in Need was phrased as an impossible 76/76 requirement — FIXED

Metaphor contains 76 quest entries, but the Charadrius corridor creates a mutually exclusive branch: after Sergeant Xanth, only one of Maintenance Chief Ceiba or Master Sergeant Glechom is required for quest-completion purposes. The achievement therefore uses the supported **75 of 76** completion rule and still unlocks only after `Save the Country` / story completion.

Dayloop now states this exception in the achievement prompt, deadline warning, and July 25 route task instead of telling users to complete both corridor branches.

Regression: `MetaphorAchievementAuditTest`, `MetaphorDeadlineAuditTest`, and `MetaphorRouteResidualAuditTest`.

## Completion gate

The Metaphor first-playthrough audit is complete when all of these conditions hold; they now do:

- 14 Followers / 112 ranks use fixed-story vs route-selected timing correctly.
- Main-operation windows, every timed side-request expiry used by the route, and permanent one-way missables are represented without inventing availability dates.
- All five Royal Virtues have their route rank-up dates and source-visible gains protected without claiming hidden thresholds as exact.
- All eight podium debates have valid schedules, answers, and rewards.
- June through October have residual route coverage for completion-critical requests, collectibles, books, virtues, Followers, and endgame milestones.
- The reusable activity catalog does not flatten variable book rewards.
- The canonical achievement catalog contains all 44 achievements and separates exact route events, manual/cumulative checks, conditional branches, and NG+ work.
- Closing the Book remains outside the first-playthrough calendar.
- Pack parsing, unit tests, and packlint pass.

## Validation

Data/test audit head `733edd833123c4ca9a331baa772e2b712bf10c0c` passed GitHub Actions **CI #643**, including `Build and test` and `Validate content packs (packlint)`.

The audit is therefore closed. Future corrections should be recorded as new findings rather than reopening the baseline audit without new evidence.
