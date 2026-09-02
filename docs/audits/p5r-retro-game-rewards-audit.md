# P5R retro-game reward audit

Status: targeted Royal-specific verification complete for the seven retro-game completion cards/accessories used by the source completion route.

## Sources checked

- Megami Tensei Wiki — Royal retro-game reward table and Royal item list.
- Samurai Gamers — P5R retro-video-game accessory table.
- megaten-database — Royal retro-game unlock/activity mechanics and completion-card exchange behavior.
- Alyookid — `Persona 5 The Royal 100% Achievements + Perfect Schedule` for the completion route's chosen redemption dates and `5/7`, `6/7`, `7/7` milestones.

## Royal completion rewards

| Game | Completion reward | Royal effect |
|---|---|---|
| Star Forneus | Forneus Badge | Ice Amp |
| Gambla Goemon | Gambla Badge | Evade Elec |
| Featherman Seeker | Featherman Badge | Wind Amp |
| Punch Ouch | Punch Badge | Evade Fire |
| Train of Life | Train Badge | Elec Amp |
| Power Intuition | PI Badge | Nuke Amp |
| Golfer Sarutahiko | Golfer Badge | Psy Amp |

Completing a game awards its card/postcard; the card is exchanged at Retro Game Shop Super Baron in Akihabara for the corresponding accessory. The accessory redemption itself does not consume a time slot.

## Route-state corrections

The imported route previously used generic accessory wording late in January. One label was actively misleading:

- **2017-01-28** said `Claim the final retro-game accessory`, but the route has only completed six of seven games at that point. `Golfer Sarutahiko` is not completed until **2017-01-30**, and the source schedule explicitly collects the last accessory on **2017-01-31**.

The route now makes the state explicit:

- **2017-01-23** — redeem any still-outstanding badges for the five games completed by the `Train of Life (5/7)` milestone: Forneus, Gambla, Featherman, Punch and Train badges.
- **2017-01-28** — redeem the **PI Badge** after `Power Intuition (6/7)` is completed on 2017-01-26.
- **2017-01-31** — redeem the **Golfer Badge** after `Golfer Sarutahiko (7/7)` is completed on 2017-01-30.

The 2016-11-25 source-route instruction remains intentionally flexible: Alyookid says an accessory can be collected for any already-completed retro game. The January wording therefore says `outstanding` rather than assuming which earlier badge the player chose to redeem in November.

## Regression coverage

`P5RRetroGameRewardAuditTest` pins:

- the five possible completed-game badge names called out on January 23,
- the Power Intuition -> PI Badge relationship and its 6/7 route milestone,
- the Golfer Sarutahiko -> Golfer Badge relationship and its 7/7 route milestone,
- the Train of Life 5/7 milestone, and
- the invariant that January 28 is not described as the final retro-game accessory redemption.
