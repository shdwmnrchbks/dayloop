# P5R trophy availability audit

Scope: first-class Royal trophy `availableFrom` metadata that had inherited a completion-route month/date instead of the game's actual fixed story or facility availability.

This pass does **not** try to force every trophy into the authored 100% Completion Route. `availableFrom` answers when a trophy can first become obtainable under the independently checked game rules; the walkthrough is still free to earn it later.

## Sources checked

- PlayStationTrophies Royal trophy guide and trophy-specific pages — trophy requirements, fixed Palace-resolution timing, Castle combat tutorials, grappling-hook behavior, third-semester awakening timing, lottery result semantics, part-time-job behavior, Showtime/Fusion Alarm behavior, Velvet Room execution mechanics, Jose flower/stamp trading, cognition-max costs, Mementos deviation behavior, Museum Treasure Demon behavior, Kichijoji facility behavior, the non-trophy Jun 5 darts introduction, and Akihabara's crane-game unlock.
- TrueAchievements Royal achievement pages — independent checks for the May 5 Phantom Thieves formation, mandatory May 7 first Mementos request, May 9 Aiyatsbus/stamp availability, May 18 Strength-rank-1 / Group Guillotine availability, exact Palace/Mementos story-trophy dates, and player reports of a deviation spawning in Aiyatsbus before Madarame's Palace is cleared.
- GameFAQs Royal walkthroughs — Apr 15 negotiation availability, the Apr 18 Baton Pass / Grappling Hook / Disaster Shadow / Technical tutorial run, early part-time-job availability, May 5 LeBlanc cleaning, May 9 Aiyatsbus unlock/free Mementos exploration, Jose mechanics, May 18 Museum first-free-exploration checks, Jul 11/12 Kaitul story timing, earliest Palace-route security windows, Palace-resolution dates, and route-date comparisons.
- Persona 5 Royal Japanese strategy references — Triple Seven job availability from Apr 18, ordinary lottery's Apr 18 sale start / seven-day result delay, May 9 Aiyatsbus availability, Jul 12 Royal Kaitul infiltration, path stamp counts, the 30-stamp item-cognition maximum, earliest Madarame route security on May 20, and Fusion Alarm gating after Kaneshiro route security.
- Neoseeker Royal walkthrough — independent Jose trophy semantics and Jul 26 earliest Futaba Palace Treasure-route security check for Gallows availability.
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

### Punch That Clock! — 2016-04-18

Part-time-job listings become available on Apr 18. Triple Seven requires no social-stat prerequisite and can be worked during daytime, so this is the first possible trophy date. The previous May 1 value was a coarse route-month placeholder rather than feature availability.

### Easy Money — 2016-04-25

The ordinary weekly lottery begins selling tickets on Apr 18 and posts results seven in-game days later. Therefore Apr 25 is the earliest possible trophy result **if** the first ticket wins. This remains RNG-dependent: `availableFrom` means first possible result, while `expectedBy` stays absent because no completion date is guaranteed.

### Phantom Thieves: Assemble! — 2016-05-05

This trophy is fixed story progression: the group formally names the Phantom Thieves during the May 5 hotel sequence. The old May 1 month anchor was several days early.

### Leblanc Buffer — 2016-05-05

Royal allows the protagonist to clean Café LeBlanc on the evening of May 5, and independent completion walkthroughs explicitly award `Leblanc Buffer` for that visit. The old July 1 value reflected a much later route cleanup rather than the first confirmed trophy opportunity.

### One Step at a Time — 2016-05-07

The mandatory first Mementos request, `Beware the Clingy Ex-boyfriend!`, is completed during the May 7 introduction. It is the first deterministic trophy point. The previous May 1 value predated Mementos request completion.

### Talent Thief — 2016-05-07

The trophy requires a Palace Will Seed crystal to be refined into its usable accessory. Jose first appears during the tutorial Mementos visit on May 7, so June was unnecessarily late as an availability anchor.

### Jose's Favorite Customer — 2016-05-09

Jose is introduced during the forced May 7 Mementos tutorial. Once Mementos becomes freely explorable in Aiyatsbus, flowers appear on regular floors and Jose can randomly set up his shop. Aiyatsbus opens on May 9, making that the first possible day to collect at least 15 flowers, encounter Jose and buy an item. The encounter is not guaranteed on the route, so this remains manual with no `expectedBy`.

### The Phantom Philatelist — 2016-05-09

Royal sources explicitly tie official stamp collection to the Path of Aiyatsbus / the second Jose encounter. Aiyatsbus becomes freely explorable on May 9; stamps can be collected and traded to unlock one cognition-change stage on that visit. The old Jun 1 value reflected a later route Mementos trip rather than the mechanic's first availability.

### The Deviated Cognition — 2016-05-09

Aiyatsbus unlocks on May 9, after the forced May 7 Qimranut tutorial. Deviated floors are random Mementos floor-generation events, and independent player reports confirm they can already occur in Aiyatsbus Area 2 before Madarame's Palace is cleared. Therefore May 9 is the first independently supported **possible** trophy date. This remains manual with no `expectedBy`: the route does not promise a deviation will spawn on May 9 or on any particular Mementos trip.

### The Purpose of a Thief — 2016-05-18

The first Treasure Demon is Regent in the Museum Palace's scripted golden-pot encounter. May 18 is the first free-exploration day for the Museum and independent Royal walkthroughs explicitly warn the player to keep a free Persona stock slot so recruiting Regent awards the trophy. The old May 1 value predated the Museum entirely.

### Efficient Executioner — 2016-05-18

Group Guillotine unlocks at Strength rank 1. The first opportunity to start/complete that rank is May 18 by presenting Jack Frost with Mabufu, after which a three-Persona Group Guillotine can award the trophy. The old Jun 1 value was only a month-level route approximation.

### A Grand Experiment — 2016-05-20

Electric Chair itemization unlocks after securing the Museum Palace Treasure route. Royal references place the earliest legal Madarame route-security day on May 20, making that the first possible Electric Chair trophy date. This replaces the previous May 1 placeholder, which predated the mechanic entirely.

### Trash Into Treasure — 2016-06-05

Kichijoji unlocks on Jun 5 and Furugi no Neuchi can accept Sooty clothing from that point. Dayloop's own route already sells Sooty clothes in Kichijoji on Jun 5; the old Jun 1 month anchor predated the facility.

### Dartslinger — 2016-06-06

The Jun 5 Penguin Sniper sequence is an introduction/cutscene and does **not** unlock the trophy. Playable free-time darts begin after Jun 5, so Jun 6 is the earliest calendar anchor. The route may still use the Jun 5 introduction for Baton Pass progression without implying the trophy is obtainable in that scene.

### A Hustler's Journey — 2016-06-06

Billiards is available at Penguin Sniper once Kichijoji's introduction has occurred. The completion route waits until Aug 17 to play billiards, but that route-selected date is not the facility unlock.

### A Serene Experience — 2016-06-06

The Kichijoji temple becomes usable on Jun 6. Dayloop's completion route intentionally postpones its temple visit until Jan 30; the old Jan 1 metadata incorrectly turned that late route choice into trophy availability.

### It's Showtime! — 2016-06-21

Showtime is introduced during the Bank Palace arc. The forced tutorial itself does not award the trophy, but subsequent Showtime activations are eligible from Jun 21 onward. The prior Jun 25 date reflected a particular route's progress rather than first trophy eligibility.

### Accident-Prone — 2016-06-21

Fusion Alarms become available only after securing Kaneshiro's Treasure route. Jun 21 is the earliest legal route-security date, and an execution performed during an alarm can award `Accident-Prone` from that point. This replaces the previous Jun 25 route-specific anchor.

### The Search for Power — 2016-07-12

This trophy requires maxing one Jose cognition-change category. The cheapest line is Item Drops at 30 stamp points in the Royal Japanese strategy table and the PlayStation trophy guide. Before Kaitul, Qimranut + Aiyatsbus + Chemdah contain 20 total stamps. Royal's Kaitul path becomes freely explorable on Jul 12 and contains 10 fixed podiums plus additional random podiums, so the player can reach the required 30 points during the first Kaitul visit even without relying on a later Palace-era route. The old Aug 1 date was a coarse route approximation.

Some secondary tables list the Item Drops total as 34 rather than 30. That conflict does not change the first-possible date: Kaitul contains 20 total stamps, so the additional four points can still be farmed from the path's random podiums during the same Jul 12 visit. Dayloop therefore pins the date without encoding a disputed total in user-visible achievement copy.

### Success Built on Sacrifice — 2016-07-26

Gallows execution becomes available after securing the Futaba Palace Treasure route. Jul 26 is the earliest legal day to secure that route; Jul 25 is still investigation/story setup. The old Jul 1 value was a month placeholder before Gallows exists.

### Going Against the Crane — 2016-08-31

Akihabara and its crane game unlock on Aug 31. The route starts its crane-prize chain in September, but the trophy is already obtainable on Aug 31.

### Awakening the Phantom Thieves — 2017-01-10

On the Royal third-semester path, Morgana's third awakening is a mandatory Jan 10 story scene and immediately awards the trophy. Optional party-member third awakenings happen later, so the old Jan 1 month anchor was early while waiting for an optional teammate is unnecessarily late. Dayloop's Jan 10 route already records Morgana's awakening.

## Deliberately excluded from this pass

### The Path Chosen

Unlike the fixed Palace-resolution trophies, this trophy is awarded only for endings that qualify under Royal's trophy rules. Several early bad endings do not count, while the original ending and third-semester endings occur on different branches and the trophy unlocks after the credits. A single in-calendar date would be misleading without explicitly modeling the chosen ending route.

### Unsurpassed Rebel

The Reaper begins spawning from Aiyatsbus onward, so the encounter mechanic itself is accessible from May 9. The trophy, however, requires defeating a level-85 superboss. The current metadata model cannot distinguish `encounter available` from `practically trophy-obtainable on a fresh file`, so this audit does not silently replace the late broad anchor with the spawn date.

### A Night in Kichijoji

The jazz club is gated by Justice/Akechi rank 4, not merely the Kichijoji district unlock. Independent references disagree on the exact earliest calendar date achievable for rank 4 (and optimized routes can move it), so this pass does not replace the current metadata with a falsely precise fixed day. The condition should be modeled or separately audited before changing it.

### Angler's Debut

Royal references conflict on the exact Ryuji invitation date that first unlocks Ichigaya (the source completion route and several Royal guides use Jul 3, while other trophy references use Jul 6). The initial visit also has trophy-specific semantics. This remains a separate conflict-resolution item rather than being silently normalized here.

### Professional Modification

Gun customization depends on Guts rank 4 plus Hanged Man/Iwai rank 1 and returning to the shop afterwards. That is player-state dependent, so a single fixed calendar day requires a separate gate-model audit.

## Regression coverage

`P5RTrophyAvailabilityAuditTest` now pins **38** corrected fixed/first-opportunity availability anchors. It includes all nine Palace/Mementos story-resolution trophies, Apr 15 negotiation, Apr 18 Castle combat/mechanic/job trophies, the first possible Apr 25 lottery result, May 9 free-Mementos Jose flower/stamp/deviation trophies, Jul 12 cognition-max availability, earliest Electric Chair/Fusion Alarm/Showtime/Gallows trophy availability, and the mandatory Jan 10 Morgana third-awakening trophy. The test confirms these remain manual/no-deadline trophies and explicitly checks that later completion-route choices do not overwrite earlier real game availability.
