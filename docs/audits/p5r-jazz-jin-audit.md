# P5R Jazz Jin audit

Scope: Royal's first Jazz Jin / `A Night in Kichijoji` opportunity, the completion route's much later Justice progression, and the late-game Sunday Jazz Jin steps that teach unique battle skills or Futaba navigator upgrades.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — source completion route and late Sunday Jazz Jin suggestions.
- GameFAQs Royal optimization / Justice references — Justice rank 4 unlocks Jazz Jin; an optimized Royal route can reach rank 4 on **Jun 25** when Palace timing is rearranged.
- PlayStationTrophies / XboxAchievements — `A Night in Kichijoji` requires a separate Jazz visit **after** the Justice rank-4 introduction; the introductory Confidant event itself only unlocks the venue.
- Samurai Gamers, **Jazz Jin Club Guide** — independent rank-4 gate and early-Jazz timing reference plus the December/January Sunday skill schedule.
- Megami Tensei / Megaten Wiki Jazz Jin references — independent check that Futaba receives her navigator skill **instead of** that Sunday's normal party skill.

## First trophy opportunity

Jazz Jin is gated by **Justice / Goro Akechi rank 4**. Optimized Royal routing can reach that rank on **Jun 25**; the trophy is not awarded during Akechi's introductory Jazz event, so the first supported separate visit is **Jun 26**.

Dayloop's authored completion route intentionally progresses Justice more slowly and schedules rank 4 on **Aug 6**. That route date is not a universal unlock. The first-class achievement catalog therefore uses:

- `A Night in Kichijoji.availableFrom = 2016-06-26`
- no `expectedBy` deadline
- manual tracking

`P5RJazzJinAuditTest` pins both the Jun 26 trophy availability and the route's Aug 6 Justice-rank-4 choice so those semantics cannot collapse back together.

## Early-date source note

Some Jazz Jin reference tables describe Jun 26 as the earliest *club unlock*, while a newer GameFAQs optimization guide explicitly demonstrates the rank-4 unlock on Jun 25 if the Bank Palace Calling Card / boss timing is rearranged. Trophy guides consistently require a separate post-rank-4 visit. Dayloop therefore uses Jun 26 for the **trophy** even though the venue can be introduced one night earlier in an optimized route.

This also avoids relying on Jun 23–24 claims that conflict with Royal's rain-sensitive Justice rank-4 availability in that calendar window.

## Verified Sunday outcomes

For a normal party member, the relevant late Sunday skills are:

| Date | Sunday skill |
| --- | --- |
| 2016-12-04 | Heat Riser |
| 2016-12-11 | Debilitate |
| 2017-01-15 | Ali Dance |
| 2017-01-22 | Arms Master |
| 2017-01-29 | Spell Master |

Futaba is a special case. On a Sunday Jazz Jin visit she does **not** receive that date's normal party skill. Her visits advance a separate fixed sequence instead:

1. Support Plus 1
2. Support Plus 2
3. Support Plus 3
4. Support Rate Up

After those four navigator skills, a fifth Sunday visit does not grant another Futaba Jazz Jin skill.

## Dayloop route corrections

The imported route phrased the December 4, December 11, January 15 and January 22 entries as though Futaba's navigator upgrade happened alongside the normal Sunday skill in the same visit. That is mechanically misleading because Joker chooses one invitee and Futaba's navigator progression replaces the normal Sunday reward.

The route now states the choice explicitly:

- invite a **non-Futaba** party member for the date-specific Sunday skill, **or**
- alternatively invite Futaba for the next Support Plus / Support Rate Up upgrade **instead**.

January 29 also states that Futaba has no further Jazz Jin skill after Support Rate Up, so the route's Spell Master target should use a non-Futaba party member.

## Naming note

Secondary references are inconsistent between `Support Plus` / `Support Rate Up` and translations such as `Support Boost` / `Support Chance Up`. The source completion route and Royal-specific reference material used for this pass support the `Support Plus 1/2/3` and `Support Rate Up` wording retained by Dayloop.

## Regression coverage

`P5RJazzJinAuditTest` pins:

1. Jun 26 `A Night in Kichijoji` first trophy availability,
2. Dayloop's Aug 6 route-selected Justice rank 4 as a separate `scheduledFor` value,
3. the five late Sunday normal party skills,
4. Futaba's four-step navigator sequence,
5. the mutually-exclusive invite semantics, and
6. Jan 29's no-fifth-Futaba-skill rule.
