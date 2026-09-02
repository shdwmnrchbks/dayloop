# P5R Sooty Scale Armor audit

Scope: the January 2017 completion-route steps that farm `Sooty Scale Armor` from the Ravenous Dragon/Fafnir enemy and launder it into Morgana equipment.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — source route for farming the red Ravenous Dragon shadow in the final Palace and later washing the armor through Kawakami.
- GameFAQs Royal equipment references — independent item mapping from `Sooty Scale Armor` to its two laundry outcomes.
- Megami Tensei Wiki Royal equipment data — independent check for the Dragon Scale Scarf's Morgana equipment type and stats.

## Verified facts

`Sooty Scale Armor` is associated with Fafnir/Ravenous Dragon and can produce two Morgana laundry outcomes:

| Laundry result | Defense | Evasion | Effect |
| --- | ---: | ---: | --- |
| Old Scale Scarf | 301 | 23 | Reduce Physical damage (low) |
| Dragon Scale Scarf | 318 | 23 | Reduce Physical damage (high) |

The stronger `Dragon Scale Scarf` is therefore **not an automatic laundry result** from one Sooty Scale Armor. The completion route's January 14 save/reload instruction is what makes its desired result deterministic for the player.

## Dayloop correction

The January 12 route step previously said the Sooty Scale Armor simply "washes into Morgana's best armor," which could be read as a guaranteed outcome. It now says laundry **can roll** the Dragon Scale Scarf. The January 14 step retains the explicit save/reload-until-Dragon-Scale-Scarf instruction.

`P5RSootyScaleArmorAuditTest` pins that distinction so the route cannot silently regress to presenting the stronger laundry result as automatic.

## Naming note

Some Royal equipment tables shorten the stronger result to `Scale Scarf`, while other Royal references and the route source use `Dragon Scale Scarf`. Dayloop retains `Dragon Scale Scarf` because that name is supported by the Royal equipment data used for this pass and matches the authored completion route.
