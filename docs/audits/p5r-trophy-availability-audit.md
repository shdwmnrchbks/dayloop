# P5R trophy availability audit

Scope: Royal trophy `availableFrom` metadata that had inherited broad month anchors or completion-route dates instead of the game's first independently supported trophy opportunity / fixed story pop date.

`availableFrom` is **not** an `expectedBy` deadline. Flexible, RNG-dependent, or player-state trophies remain manual and may be earned later by the authored 100% Completion Route.

## Sources checked

- PlayStationTrophies / TrueAchievements / Xbox achievement guides — Royal trophy requirements and first-opportunity mechanics.
- GameFAQs Royal walkthroughs and mechanics guides — exact story dates, Palace mechanic gates, Mementos/Jose progression, Apr 28 Ryuji/Ogikubo trophy routing, optimized Justice/Jazz Jin timing, Royal-specific July fishing timing, Strength/Lockdown progression, and route-vs-unlock comparisons.
- Samurai Gamers Royal walkthroughs / facility guides — independent checks for Castle mechanics, Kichijoji facilities, Jazz Jin, Ryuji/Ogikubo progression, Ryuji's Jul 3 Ichigaya hangout, and other calendar gates.
- Persona 5 Royal Japanese strategy references — lottery timing, Mementos path/stamp counts, Palace route-security windows, and Royal-specific Velvet Room unlocks.
- Neoseeker Royal walkthrough — additional Jose, Kichijoji incense, and Futaba/Gallows timing checks.

Where two valid Royal schedules earn a trophy on adjacent dates, `availableFrom` uses the earliest independently demonstrated legal opportunity rather than the completion route's chosen date. Cross-version or route-specific date conflicts are recorded instead of silently flattened.

## Corrected anchors

| Trophy | `availableFrom` | Audited meaning |
| --- | --- | --- |
| Castle of Lust: Seized | 2016-05-02 | Fixed Kamoshida-arc resolution / trophy pop |
| Museum of Vanity: Repossessed | 2016-06-05 | Fixed Madarame-arc resolution |
| Bank of Gluttony: Cleaned Out | 2016-07-09 | Fixed Kaneshiro-arc resolution |
| Pyramid of Wrath: Plundered | 2016-08-22 | Fixed Futaba/Medjed resolution |
| Spaceport of Greed: Obliterated | 2016-10-11 | Fixed Okumura-arc resolution |
| Casino of Jealousy: Bankrupted | 2016-11-20 | Fixed interrogation/casino resolution |
| Cruiser of Pride: Capsized | 2016-12-18 | Fixed Shido/election resolution |
| The Thorough Trickster | 2016-12-24 | Fixed Mementos Depths / Qliphoth completion |
| Take Back the Future | 2017-02-03 | Fixed Laboratory final-battle completion |
| Spirit of Rebellion | 2016-04-11 | Arsène awakening |
| I am Thou... | 2016-04-15 | First Shadow negotiation / Persona recruitment |
| A Deadly Debut | 2016-04-18 | First normal Guillotine opportunity |
| Tactical Teamwork | 2016-04-18 | First Baton Pass trophy opportunity |
| Let's Blow It Up | 2016-04-18 | Disaster Shadow tutorial window |
| You'd Better Hang On! | 2016-04-18 | First trophy-valid Castle grappling-hook use |
| Technician | 2016-04-18 | First Technical tutorial battle |
| Punch That Clock! | 2016-04-18 | Part-time jobs / Triple Seven available |
| Easy Money | 2016-04-25 | First possible posted result from an Apr 18 ordinary lottery ticket; RNG, no deadline |
| Tokyo Tourist | 2016-04-28 | Royal schedules can reach Chariot rank 4, unlock Ogikubo and award the trophy on Apr 28 |
| Phantom Thieves: Assemble! | 2016-05-05 | Fixed team-naming story event |
| Leblanc Buffer | 2016-05-05 | First confirmed LeBlanc cleaning opportunity |
| One Step at a Time | 2016-05-07 | Mandatory first Mementos request completion |
| Talent Thief | 2016-05-07 | Jose can first refine a complete Will Seed crystal |
| Jose's Favorite Customer | 2016-05-09 | First free Aiyatsbus visit where flowers/shop can be used |
| The Phantom Philatelist | 2016-05-09 | First free Aiyatsbus stamp/cognition-change opportunity |
| The Deviated Cognition | 2016-05-09 | First possible random deviated floor in Aiyatsbus; RNG, no deadline |
| The Purpose of a Thief | 2016-05-18 | First Museum Regent recruitment opportunity |
| Efficient Executioner | 2016-05-18 | Strength rank 1 / Group Guillotine available |
| A Grand Experiment | 2016-05-20 | Earliest Royal Museum Treasure-route security / Electric Chair unlock |
| Trash Into Treasure | 2016-06-05 | Kichijoji used-clothes shop available |
| Intensive Training | 2016-06-05 | Strength rank 3 can already be reached; Kichijoji/Mantra Ganda opens with purchasable incense |
| Dartslinger | 2016-06-06 | First free-time darts after the Jun 5 introduction |
| A Hustler's Journey | 2016-06-06 | First free billiards opportunity |
| A Serene Experience | 2016-06-06 | Kichijoji temple becomes usable |
| It's Showtime! | 2016-06-21 | First trophy-eligible Showtime after the forced tutorial |
| Accident-Prone | 2016-06-21 | Earliest Kaneshiro Treasure-route security / Fusion Alarm availability |
| A Night in Kichijoji | 2016-06-26 | Optimized Justice rank 4 can unlock Jazz Jin on Jun 25; trophy requires a separate subsequent Jazz visit |
| Angler's Debut | 2016-07-04 | Jul 3 Royal Ryuji hangout unlocks Ichigaya; initial visit does not count, so next day is first possible independent catch |
| The Search for Power | 2016-07-12 | First Kaitul visit can supply enough stamps to max the cheapest cognition line |
| Success Built on Sacrifice | 2016-07-26 | Earliest Futaba Treasure-route security / Gallows availability |
| Going Against the Crane | 2016-08-31 | Akihabara crane game unlock |
| Awakening the Phantom Thieves | 2017-01-10 | Mandatory Morgana third awakening on Royal third-semester path |

## Important route/global distinctions

### Tokyo Tourist

Multiple Royal GameFAQs schedules demonstrate Chariot rank 4 / Ogikubo / `Tokyo Tourist` on **Apr 28**. Samurai Gamers reaches the same rank and trophy on Apr 29 because it spends Apr 28 on Takemi instead. That one-day difference is schedule order, not a game gate.

Dayloop's authored completion route delays Ryuji rank 4 until Jun 1. The catalog therefore uses Apr 28 as first supported availability while preserving Jun 1 as this route's chosen progression date.

### Palace trophies

The player may choose an earlier heist/boss day, but the first seven Palace trophies pop at their fixed story resolutions. These trophy dates must not replace separate Palace deadlines or Dayloop's chosen infiltration dates.

### Easy Money and The Deviated Cognition

Both dates are **first possible**, not deterministic completion points. `Easy Money` depends on a qualifying lottery win; deviated Mementos floors are random. Both remain manual with no `expectedBy`.

### Jose trophies

May 7 introduces Jose during the forced Mementos tutorial. May 9 is the first free Aiyatsbus state where normal flowers, stamps, cognition changes and random Mementos behavior are available. The route may choose to perform those actions later.

### Intensive Training

Lockdown requires Strength rank 3. Dayloop's own route reaches Strength rank 3 on **Jun 2**. Kichijoji opens on **Jun 5**, and Mantra Ganda sells incense there once the Lockdown prerequisite is met. This makes Jun 5 the first supported trophy opportunity.

The route later buys the Jul 3 Calm Mind Set for incense, but that shopping-channel purchase is not the mechanic's unlock and must not determine `availableFrom`.

### A Night in Kichijoji

Justice rank 4 introduces Jazz Jin, but the introduction itself only unlocks the venue. Trophy guides consistently require a **separate later Jazz visit** for `A Night in Kichijoji`.

Royal timing references conflict because ordinary completion schedules reach Akechi rank 4 much later, while optimized routing compresses his early ranks. A recent GameFAQs optimization guide explicitly identifies **Jun 25** as the earliest Justice rank-4 / Jazz Jin unlock if Palace timing is rearranged; Jun 23–24 are also rain-constrained for rank 4 in the relevant Royal calendar window. The first separate Jazz visit is therefore **Jun 26**.

Dayloop's own completion route schedules Justice rank 4 on **Aug 6**. That remains a route choice, not game availability, and `P5RJazzJinAuditTest` now pins the distinction alongside the late-Sunday skill semantics.

### Angler's Debut source conflict

Several cross-version trophy pages still say Ryuji unlocks Ichigaya on Jul 6. Royal-specific walkthroughs from GameFAQs, Samurai Gamers and the current P5R route consistently place the Ryuji/Kawakami fishing hangout on **Jul 3**. Separate achievement reports agree that the invitation visit itself does **not** award `Angler's Debut`; the player must return and actually catch a fish.

Dayloop therefore uses **2016-07-04** as the first possible trophy date. The completion route intentionally does not perform its first independent fishing session until Dec 5, and that later route choice must not leak into trophy availability metadata.

### The Search for Power stamp-total conflict

Secondary tables disagree on the exact total needed for the cheapest cognition category. The date remains Jul 12 either way: Kaitul adds enough fixed/random stamp podiums to cross the disputed threshold during its first freely explorable visit. Dayloop therefore pins the first-opportunity date without exposing a disputed total in user-facing trophy copy.

## Deliberately not converted to exact fixed dates

- **The Path Chosen** — multiple qualifying ending branches occur on different dates; an ending condition should be modeled rather than assigned one universal day.
- **Unsurpassed Rebel** — the Reaper can spawn from Aiyatsbus, but `encounter available` is not the same as a realistic fresh-file defeat date.
- **Professional Modification** — depends on Guts rank 4 plus Hanged Man/Iwai progression and a return visit.

These remain separate gate-model audit items instead of receiving false calendar precision.

## Regression coverage

`P5RTrophyAvailabilityAuditTest` pins **42** corrected fixed/first-opportunity anchors and keeps them manual/no-deadline unless a separately modeled route target exists. It also guards key route/global distinctions: Apr 28 Tokyo Tourist vs the route's Jun 1 Chariot rank, Jun 2 Strength rank 3 + Jun 5 incense availability, Jun 25 Jazz unlock vs Jun 26 first trophy visit, Jul 3 Ichigaya unlock vs Jul 4 first possible fishing trophy, May 9 free-Mementos behavior, flexible Palace security gates, and late route choices such as billiards/temple/crane not overwriting real facility availability.

Dedicated activity tests (`P5RJazzJinAuditTest`, `P5RFishingAuditTest`, `P5RLotteryAuditTest`, `P5RBathhouseAuditTest`, `P5RBattingCageAuditTest`, and others) pin the higher-risk mechanic semantics behind these dates.
