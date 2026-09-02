# P5R Aojiru activity audit

Scope: the reusable Sunday Aojiru drink in Shibuya and every completion-route step that references it.

## Sources checked

- GameFAQs, **Persona 5 Royal Walkthrough & Guide — Social Stats / How to Use** — independent Royal reference for the Shibuya location, Sunday timing, ¥5,000 price, +2 hidden social-stat points, no-time-cost behavior, fixed stat rotation and carry-over-until-purchased rule.
- megaten-database, **Persona 5 Royal — Overworld** — independent structured check for the Underground Passage/Walkway location, Sunday availability, +2 hidden-point reward and repeat-until-purchased behavior.
- Megami Tensei Wiki, **Drink Stand** — secondary check that purchasing advances the offered Aojiru while skipping a purchase leaves the same drink available the next Sunday.

## Verified Royal semantics

- Location: Shibuya Underground Walkway / Underground Passage drink stand.
- Timing: Sundays only.
- Cost: ¥5,000.
- Time: buying the drink does **not** consume a time slot.
- Reward: +2 hidden social-stat points (one displayed note).
- Purchase rotation: Charm -> Proficiency -> Guts -> Kindness -> Knowledge, then repeat.
- The rotation advances only when the current drink is purchased; otherwise that same stat remains offered on the next Sunday.

## Dayloop state

The reusable `p5r.activity.drink.fruit-drink` entry already represented the important user-visible semantics correctly: Shibuya Underground Walkway, Sunday-only use, no time cost, +2 hidden points and carry-over until purchase. No content correction was necessary in this pass.

`P5RAojiruAuditTest` now independently pins those reusable facts and also walks every `activityRef` in the authored completion route. Each referenced purchase must:

1. occur on a real Sunday,
2. keep the Underground Walkway location visible,
3. grant exactly +2 hidden points, and
4. advance through the fixed Royal stat rotation only after a purchase.

This converts the Aojiru activity from an unaudited reusable one-off into an independently verified activity surface without changing already-correct pack data.
