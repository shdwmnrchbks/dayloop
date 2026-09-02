# P5R trophy availability audit

Scope: first-class Royal trophy `availableFrom` metadata that had inherited a completion-route month/date instead of the game's actual fixed story or facility availability.

This pass does **not** try to force every trophy into the authored 100% Completion Route. `availableFrom` answers when a trophy can first become obtainable under the independently checked game rules; the walkthrough is still free to earn it later.

## Sources checked

- PlayStationTrophies Royal trophy guide and trophy-specific pages — trophy requirements, fixed Palace-resolution timing, Castle combat tutorials, grappling-hook behavior, third-semester awakening timing, fusion availability, Museum Treasure Demon behavior, Kichijoji facility behavior, the non-trophy Jun 5 darts introduction, Jose's first encounter, and Akihabara's crane-game unlock.
- TrueAchievements Royal achievement pages — independent checks for the May 5 Phantom Thieves formation, mandatory May 7 first Mementos request, May 18 Strength-rank-1 / Group Guillotine availability, and exact Palace/Mementos story-trophy dates.
- GameFAQs Royal walkthroughs — Apr 15 negotiation availability, the Apr 18 Baton Pass / Grappling Hook / Disaster Shadow / Technical tutorial run, May 5 LeBlanc cleaning, May 18 Museum first-free-exploration checks, Palace-resolution dates, and route-date comparisons.
- Samurai Gamers Royal April walkthrough — independent Apr 11 Arsène awakening/story and Apr 18 Castle tutorial checks.
- Samurai Gamers Old Temple guide — explicit Jun 6 temple unlock.
- Samurai Gamers June walkthrough — independent early Kichijoji darts/billiards usage examples.

## Corrected anchors

### Palace and main-story completion trophies

The heist/boss day for the first seven Palaces can move because the player chooses when to complete the dungeon. The corresponding Royal trophy does **not** pop on that flexible boss day: it appears at the fixed story resolution/deadline. Dayloop previously used broad month-start anchors for most of these trophies, which made `availableFrom` neither the route date nor the actual trophy date.

The audited fixed pop dates are:

- `Castle of Lust: Seized` — **2016-05-02**, Kamoshida's confession.
- `Museum of Vanity: Repossessed` — **2016-06-05**, Madarame-arc resolution.
- `Bank of Gluttony: Cleaned Out` — **2016-07-09**, Kaneshiro-arc resolution.
- `Pyramid of Wrath: Plundered` — **2016-08-22**, the Medjed/Futaba resolution after the 8/21 deadline.
- `Spaceport of Greed: Obliterated` — **2016-10-11**, Okumura-arc resolution.
- `Casino of Jealousy: Bankrupted` — **2016-11-20**, when the story catches up to the interrogation.
- `Cruiser of Pride: Capsized` — **2016-12-18**, Shido-arc/election resolution.
- `The Thorough Trickster` — **2016-12-24**, after completing Mementos Depths / Qliphoth and the original final boss.
- `Take Back the Future` — **2017-02-03**, after the Laboratory Palace final battle.

These values are trophy availability/pop dates. They must not replace the separate actionable Palace deadlines or the completion route's chosen infiltration/heist dates.

### Spirit of Rebellion — 2016-04-11

Royal's first Persona awakening is a fixed story event in the Castle Palace on Apr 11. The previous Apr 9 value was the pack calendar start, not the trophy event.

### I am Thou... — 2016-04-15

Shadow negotiation is introduced during the Apr 15 Castle sequence. Independent Royal walkthroughs identify Apr 15 as the first opportunity to recruit a Persona through negotiation; Dayloop's own Apr 15 route already says to capture Personas during the third infiltration. The old Apr 9 anchor predated negotiation entirely.

### A Deadly Debut — 2016-04-18

Normal Guillotine fusion becomes available in the Velvet Room by Apr 18, and Royal's fusion tutorial can award the trophy that day. The old Apr 9 value again represented the pack calendar start rather than the mechanic being available.

### Tactical Teamwork — 2016-04-18

The first free Castle infiltration includes the Baton Pass tutorial and can award the trophy immediately. Dayloop's completion route postpones its long Castle infiltration until Apr 24, but route selection does not change the mechanic's Apr 18 availability.

### Let's Blow It Up — 2016-04-18

Disaster Shadows are introduced during the Apr 18 Castle infiltration. The tutorial encounter can award the trophy when the explosion defeats another enemy. The old Apr 9 value predated Disaster Shadows.

### You'd Better Hang On! — 2016-04-18

The opening Casino grappling-hook sequence does not award this trophy. The first valid trophy use is the mandatory Castle grappling-hook tutorial during the Apr 18 infiltration. This is an actual game-availability anchor, not Dayloop's later chosen Castle-clearing date.

### Technician — 2016-04-18

Royal introduces Technical attacks during the Apr 18 Castle run and provides a tutorial battle where the trophy can be earned. The previous Apr 9 anchor was before the mechanic exists in normal play.

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

### Awakening the Phantom Thieves — 2017-01-10

On the Royal third-semester path, Morgana's third awakening is a mandatory Jan 10 story scene and immediately awards the trophy. Optional party-member third awakenings happen later, so the old Jan 1 month anchor was early while waiting for an optional teammate is unnecessarily late. Dayloop's Jan 10 route already records Morgana's awakening.

## Deliberately excluded from this pass

### The Path Chosen

Unlike the fixed Palace-resolution trophies, this trophy is awarded for reaching **an ending**. Royal contains multiple ending branches and the exact trophy date depends on the ending path. A single fixed calendar day would therefore be misleading unless the achievement is explicitly tied to Dayloop's authored true-ending route.

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

`P5RTrophyAvailabilityAuditTest` now pins **28** corrected fixed/first-opportunity availability anchors. It includes all nine Palace/Mementos story-resolution trophies, the Apr 15 negotiation trophy, the Apr 18 Castle combat/mechanic trophies, and the mandatory Jan 10 Morgana third-awakening trophy. The test confirms these remain manual/no-deadline trophies and explicitly checks that later completion-route choices do not overwrite earlier real game availability.
