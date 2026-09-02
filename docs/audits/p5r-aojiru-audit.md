# P5R Aojiru activity audit

Scope: the reusable Sunday Aojiru drink in Shibuya and every authored completion-route Aojiru purchase.

## Sources checked

- GameFAQs, **Persona 5 Royal Walkthrough & Guide — Social Stats / How to Use** — independent Royal reference for the Shibuya location, Sunday timing, ¥5,000 price, +2 hidden social-stat point base, no-time-cost behavior, fixed stat rotation and carry-over-until-purchased rule.
- megaten-database, **Persona 5 Royal — Overworld** — independent structured check for the Underground Passage/Walkway location, Sunday availability, +2 hidden-point base reward and repeat-until-purchased behavior.
- Megami Tensei Wiki, **Drink Stand** — secondary check that purchasing advances the offered Aojiru while skipping a purchase leaves the same drink available the next Sunday.

## Verified Royal semantics

- Location: Shibuya Underground Walkway / Underground Passage drink stand.
- Timing: Sundays only.
- Cost: ¥5,000.
- Time: buying the drink does **not** consume a time slot.
- Base reward: +2 hidden social-stat points (one displayed note).
- Purchase rotation: Charm -> Proficiency -> Guts -> Kindness -> Knowledge, then repeat.
- The rotation advances only when the current drink is purchased; otherwise that same stat remains offered on the next Sunday.
- An active Chihaya Luck Reading can raise the route's stored total for the selected Aojiru stat from the +2 base to +3 hidden points. That modifier belongs to the walkthrough state, not the reusable activity's base reward.

## Dayloop state

The reusable `p5r.activity.drink.fruit-drink` entry already represented the important user-visible base semantics correctly: Shibuya Underground Walkway, Sunday-only use, no time cost, +2 hidden points and carry-over until purchase.

The first regression version incorrectly asserted that every route purchase must store exactly +2. CI #234 caught the July 31 purchase, where the route explicitly has a Charm Luck Reading active and correctly stores +3. The test now separates the reusable +2 base from route-level active modifiers.

`P5RAojiruAuditTest` walks every authored route step whose label identifies the Sunday drink, whether or not the older walkthrough import supplied an `activityRef`. Each purchase must:

1. occur on a real Sunday,
2. keep the Underground Walkway location visible,
3. follow the fixed Royal Charm -> Proficiency -> Guts -> Kindness -> Knowledge purchase rotation, and
4. store +2 hidden points normally or +3 when the route explicitly marks Luck Reading active.

Any present `activityRef` is also required to point to `p5r.activity.drink.fruit-drink`.

This converts the Aojiru surface into an independently verified route/activity invariant while preserving the distinction between base activity reward and temporary route modifiers.
