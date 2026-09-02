# P5R Big Bang Burger Challenge audit

Scope: the three successful Big Bang Burger Challenge clears represented in the P5R 100% Completion Route.

## Sources checked

- GameFAQs, **Persona 5 Royal Walkthrough & Guide — Social Stats** — Royal-specific check for the Central Street location, sequential three-tier challenge, required Knowledge/Guts/Proficiency ranks, success stat categories and repeated-final-tier behavior.
- Samurai Gamers, **Persona 5 / Persona 5 Royal — Big Bang Burger Challenge Guide** — independent check for the three challenge names, rank gates, success rewards and badge/Big Bang Burger item rewards.
- P5R攻略 Wiki, **ビッグバン・チャレンジ** — independent Royal table for rank gates, success/failure stat-note rewards and the three success reward bundles.

## Verified Royal challenge tiers

| Tier | Challenge | Required ranks | Success stat rewards | Success items |
| --- | --- | --- | --- | --- |
| 1 | Comet Burger | Knowledge 2, Guts 2, Proficiency 2 | Knowledge/Guts/Proficiency/Charm: 1 displayed note each | 2nd Mate Badge, Big Bang Burger x3 |
| 2 | Gravity Burger | Knowledge 3, Guts 3, Proficiency 3 | Knowledge/Guts/Proficiency/Charm: 2 displayed notes each | 1st Mate Badge, Big Bang Burger x5 |
| 3 | Cosmo Tower Burger | Knowledge 4, Guts 4, Proficiency 4 | Knowledge/Guts/Proficiency/Charm: 3 displayed notes each | Captain Badge, Big Bang Burger x10 |

Kindness is not part of the challenge's success stat reward.

Dayloop stores hidden social-stat points rather than the displayed music-note count. Under the pack's existing hidden-point normalization, those three successful tiers are therefore represented as +2, +3 and +5 respectively to Knowledge, Guts, Proficiency and Charm.

## Completion-route dates

The source completion route chooses to clear the tiers on:

- 2016-05-31 — tier 1, +2 hidden points to each of the four rewarded stats.
- 2017-01-19 — tier 2, +3 hidden points to each of the four rewarded stats.
- 2017-01-23 — tier 3, +5 hidden points to each of the four rewarded stats.

Those dates are route choices, not universal game availability dates or deadlines. The challenge can be cleared whenever the corresponding stat requirements are satisfied, in order.

## Dayloop regression coverage

`P5RBigBangBurgerAuditTest` pins:

1. the route's 1/3 -> 2/3 -> 3/3 tier order,
2. the +2 -> +3 -> +5 hidden-point progression,
3. the exact four rewarded stats and explicit absence of Kindness, and
4. the absence of any fabricated Big Bang Burger calendar deadline.

The badge and consumable reward bundles are recorded here as independently verified supporting metadata. The current walkthrough does not present those item rewards as user-visible claims, so this pass does not invent a new schema field solely to surface them.
