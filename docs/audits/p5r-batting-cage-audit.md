# P5R batting-cage / Batter Up audit

Scope: Royal's `Batter Up!` trophy, the completion route's August batting-cage visit, `Batting Science`, and the separate Thieves' Den `Home Run King` grind.

## Sources checked

- PlayStationTrophies, **Batter Up!** — independent Royal trophy reference: the trophy requires hitting a single baseball at the Yongen-Jaya batting cages; it does not require a home run. The cages are an early-game Yongen-Jaya activity.
- GameFAQs Royal April walkthrough and early-game route references — independent check that the Yongen-Jaya bathhouse/batting-cage locations can be exposed in the April free-time period beginning after the introductory 4/9–4/17 block.
- Speedrun.com P5R batting-cage discussion — secondary exact-date check identifying April 18 as the first playable batting-cage day.
- Megami Tensei Wiki, **Batting Science** and **Batting Cages** — independent check that `Batting Science` appears after using the cages once and unlocks Third Eye while batting.
- GameFAQs, **Thieves' Den Awards FAQ** — independent check that `Home Run King` is a separate Thieves' Den award requiring **30 home runs**, and that `Batting Science` is recommended for that grind.
- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — source completion route for the Aug 18 save/reload-until-home-run step and subsequent `Batting Science` purchase.

## Confirmed distinctions

### Batter Up! trophy

- Requirement: score **one hit** at the batting cages.
- A home run is not required.
- Dayloop previously used `availableFrom: 2016-08-01`, which reflected the broad month of the source route's eventual batting visit rather than Royal's real early-game availability.
- The corrected availability anchor is **2016-04-18**.
- The trophy remains manual and has no `expectedBy` date; the completion route's August choice is not a universal deadline.

### Completion-route August visit

On **2016-08-18**, the source completion route deliberately asks the player to save, use the Yongen-Jaya batting cages and reload until a **home run** is achieved. Dayloop stores the activity's +2 hidden Proficiency reward.

That stronger home-run objective is a route choice. It safely satisfies `Batter Up!`, but it must not redefine the trophy requirement.

The route then buys `Batting Science` in Shinjuku. The reusable activity metadata correctly says the book appears after using the batting cages and enables Third Eye to slow pitches.

### Thieves' Den Home Run King

Royal's separate `Home Run King` Thieves' Den award requires **30 home runs**. It is not a PlayStation/Steam/Xbox trophy and is not represented as a Dayloop deadline.

The source perfect-schedule guide itself treats the 30-home-run Thieves' Den grind separately from the ordinary `Batter Up!` trophy. Dayloop therefore does not inflate the first-class trophy description or add a fabricated 30-home-run calendar target.

## Regression coverage

`P5RBattingCageAuditTest` pins:

1. `Batter Up!` as a one-hit requirement,
2. April 18 as the corrected Royal availability anchor,
3. manual tracking with no guaranteed completion date,
4. the route's Aug 18 save/reload home-run session and +2 hidden Proficiency reward,
5. the Shinjuku / post-batting / Third Eye semantics of `Batting Science`, and
6. the absence of any false 30-home-run deadline in the first-class route/trophy data.
