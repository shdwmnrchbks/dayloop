# P5R Jazz Jin Sunday audit

Scope: the completion route's late-game Sunday Jazz Jin steps that teach unique battle skills, plus Futaba's separate navigator-skill progression.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — source completion route and the late Sunday Jazz Jin suggestions.
- GameFAQs, **Battle Boosts** / Royal Jazz Club references — independent Sunday party-skill schedule and Futaba progression.
- Megami Tensei / Megaten Wiki Jazz Jin references — independent check that Futaba receives her navigator skill **instead of** that Sunday's normal party skill.
- Samurai Gamers, **Jazz Jin Club Guide** — secondary cross-check for the December/January Sunday skill dates and Futaba progression.

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

## Dayloop corrections

The imported route phrased the December 4, December 11, January 15 and January 22 entries as though Futaba's navigator upgrade happened alongside the normal Sunday skill in the same visit. That is mechanically misleading because Joker chooses one invitee and Futaba's navigator progression replaces the normal Sunday reward.

The route now states the choice explicitly:

- invite a **non-Futaba** party member for the date-specific Sunday skill, **or**
- alternatively invite Futaba for the next Support Plus / Support Rate Up upgrade **instead**.

January 29 also now states that Futaba has no further Jazz Jin skill after Support Rate Up, so the route's Spell Master target should use a non-Futaba party member.

`P5RJazzJinAuditTest` regression-pins these dates, skill names and mutually-exclusive semantics.

## Naming note

Secondary references are inconsistent between `Support Plus` / `Support Rate Up` and translations such as `Support Boost` / `Support Chance Up`. The source completion route and Royal-specific reference material used for this pass support the `Support Plus 1/2/3` and `Support Rate Up` wording retained by Dayloop.
