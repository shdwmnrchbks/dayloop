# P5R trophy availability audit

Scope: first-class Royal trophy `availableFrom` metadata that had inherited a completion-route month/date instead of the game's actual fixed story or facility availability.

This pass does **not** try to force every trophy into the authored 100% Completion Route. `availableFrom` answers when a trophy can first become obtainable under the independently checked game rules; the walkthrough is still free to earn it later.

## Sources checked

- PlayStationTrophies Royal trophy guide and trophy-specific pages — trophy requirements, fixed story timing, fusion availability, Museum Treasure Demon behavior, Kichijoji facility behavior, the non-trophy Jun 5 darts introduction, Jose's first encounter, and Akihabara's crane-game unlock.
- TrueAchievements Royal achievement pages — independent checks for the May 5 Phantom Thieves formation, mandatory May 7 first Mementos request, and May 18 Strength-rank-1 / Group Guillotine availability.
- GameFAQs and Samurai Gamers Royal walkthroughs — independent May 5 LeBlanc cleaning/trophy checks, May 18 Museum first-free-exploration checks, and route-date comparisons.
- Samurai Gamers Royal April walkthrough — independent Apr 11 Arsène awakening/story check.
- Samurai Gamers Old Temple guide — explicit Jun 6 temple unlock.
- Samurai Gamers June walkthrough — independent early Kichijoji darts/billiards usage examples.

## Corrected anchors

### Spirit of Rebellion — 2016-04-11

Royal's first Persona awakening is a fixed story event in the Castle Palace on Apr 11. The previous Apr 9 value was the pack calendar start, not the trophy event.

### A Deadly Debut — 2016-04-18

Normal Guillotine fusion becomes available in the Velvet Room by Apr 18, and Royal's fusion tutorial can award the trophy that day. The old Apr 9 value again represented the pack calendar start rather than the mechanic being available.

### Phantom Thieves: Assemble! — 2016-05-05

This trophy is fixed story progression: the group formally names the Phantom Thieves during the May 5 hotel sequence. The old May 1 month anchor was several days early.

### Leblanc Buffer — 2016-05-05

Royal allows the protagonist to clean Café LeBlanc on the evening of May 5, and independent completion walkthroughs explicitly award `Leblanc Buffer` for that visit. The old July 1 value reflected a much later route cleanup rather than the first confirmed trophy opportunity.

### One Step at a Time — 2016-05-07

The mandatory first Mementos request, `Beware the Clingy Ex-boyfriend!`, is completed during the May 7 introduction. It is the first deterministic trophy point. The previous May 1 value predated Mementos request completion.

### Talent Thief — 2016-05-07

The trophy requires a Palace Will Seed crystal to be refined into its usable accessory. Jose first appears during the tutorial Mementos visit on May 7, so June was unnecessarily late as an availability anchor.

### The Purpose of a Thief — 2016-05-18

The first Treasure Demon is Regent in the Museum Palace's scripted golden-pot encounter. May 18 is the first free-exploration day for the Museum and independent Royal walkthroughs explicitly warn the player to keep a free Persona stock slot so recruiting Regent awards the trophy. The old May 1 value predated the Museum entirely.

### Efficient Executioner — 2016-05-18

Group Guillotine unlocks at Strength rank 1. The first opportunity to start/complete that rank is May 18 by presenting Jack Frost with Mabufu, after which a three-Persona Group Guillotine can award the trophy. The old Jun 1 value was only a month-level route approximation.

### Trash Into Treasure — 2016-06-05

Kichijoji unlocks on Jun 5 and Furugi no Neuchi can accept Sooty clothing from that point. Dayloop's own route already sells Sooty clothes in Kichijoji on Jun 5; the old Jun 1 month anchor predated the facility.

### Dartslinger — 2016-06-06

The Jun 5 Penguin Sniper sequence is an introduction/cutscene and does **not** unlock the trophy. Playable free-time darts begin after Jun 5, so Jun 6 is the earliest calendar anchor. The route may still use the Jun 5 introduction for Baton Pass progression without implying the trophy is obtainable in that scene.

### A Hustler's Journey — 2016-06-06

Billiards is available at Penguin Sniper once Kichijoji's introduction has occurred. The completion route waits until Aug 17 to play billiards, but that route-selected date is not the facility unlock.

### A Serene Experience — 2016-06-06

The Kichijoji temple becomes usable on Jun 6. Dayloop's completion route intentionally postpones its temple visit until Jan 30; the old Jan 1 metadata incorrectly turned that late route choice into trophy availability.

### Going Against the Crane — 2016-08-31

Akihabara and its crane game unlock on Aug 31. The route starts its crane-prize chain in September, but the trophy is already obtainable on Aug 31.

## Deliberately excluded from this pass

### Jose's Favorite Customer / The Phantom Philatelist

Jose is introduced on May 7, but independent guides distinguish that mandatory tutorial encounter from the later freely explorable Mementos session where flowers/stamps can be collected and traded. Optimized completion routes commonly place the first shop/stamp trophy run on May 31, while other references only state `after meeting Jose`. Until the exact free-exploration gate is independently pinned, the audit does not replace the existing broad anchors with false precision.

### A Grand Experiment

Electric Chair itemization unlocks after securing the Museum Treasure route, but flexible Royal routes can secure that route on different days. The exact earliest legal calendar date should be pinned separately from one guide's chosen infiltration day before replacing the current coarse metadata.

### Punch That Clock!

Part-time jobs open in late April, but route references differ on the first schedulable workday because the player can spend those early after-school slots on Palace progress or other activities. The current month-level anchor should be replaced only after the feature-availability rule is separated cleanly from a route-selected first shift.

### A Night in Kichijoji

The jazz club is gated by Justice/Akechi rank 4, not merely the Kichijoji district unlock. Independent references disagree on the exact earliest calendar date achievable for rank 4 (and optimized routes can move it), so this pass does not replace the current metadata with a falsely precise fixed day. The condition should be modeled or separately audited before changing it.

### Angler's Debut

Royal references conflict on the exact Ryuji invitation date that first unlocks Ichigaya (the source completion route and several Royal guides use Jul 3, while other trophy references use Jul 6). The initial visit also has trophy-specific semantics. This remains a separate conflict-resolution item rather than being silently normalized here.

### Professional Modification

Gun customization depends on Guts rank 4 plus Hanged Man/Iwai rank 1 and returning to the shop afterwards. That is player-state dependent, so a single fixed calendar day requires a separate gate-model audit.

## Regression coverage

`P5RTrophyAvailabilityAuditTest` pins the thirteen corrected fixed/first-opportunity availability anchors, confirms they remain manual/no-deadline trophies, checks the mandatory May 7 Mementos introduction, and explicitly verifies that the route's later billiards, temple and crane choices do not overwrite real facility availability.
