# P5R Maid Café stamp / special-menu audit

Scope: the completion-route Maid Café visits that build Royal's Stamp Card and culminate in the `Master of Akihabara` special-menu requirement.

## Sources checked

- Megami Tensei Wiki, **Maid Café** and **Stamp Card** — independent Royal rules for menu prices, stamp values, Saturday bonus, mistake outcomes, special-menu threshold and Photo of Clara reward.
- P5R攻略 Wiki, **秋葉原：メイド喫茶** — independent Royal check for the +4 Saturday stamp bonus, menu stamp values and the 20-stamp special menu.
- Samurai Gamers, **Persona 5 / Persona 5 Royal — Maid Cafe Guide** — independent check for the Sincere Omelette's high mistake chance and mistake-choice stat outcomes.
- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — source completion route; independently confirms the route's Dec 22 `Photo of Clara` / 20-stamp payoff.

## Verified Royal rules

- The Maid Café is in Akihabara.
- Royal gives a Stamp Card on the first visit.
- Normal stamp gain is 1 point per ¥1,000 spent.
- Every Saturday is Maid Day and adds **4 extra stamps** to the order.
- `Sincere Omelette` costs ¥5,000, so an authored Saturday omelette visit earns **9 stamps** (5 + 4).
- A normal Maid Café visit gives two displayed Charm notes; Dayloop's hidden-point normalization stores that as **Charm +3**.
- If Clara makes a mistake and the player tells her to fix it, the extra Guts reward is one displayed note; Dayloop stores that as **Guts +2**.
- Once at least 20 stamps have been accumulated, the special menu becomes available on a subsequent visit.
- The special menu gives three displayed Charm notes, represented by Dayloop as **Charm +5**, and awards the **Photo of Clara**.
- Ordering that special menu is the requirement for Royal's `Master of Akihabara` trophy.

## Completion-route stamp math

The route deliberately uses three Saturday Sincere Omelette visits:

| Date | Day | Route result | Stamp total |
| --- | --- | --- | ---: |
| 2016-09-24 | Saturday | Sincere Omelette; force Clara mistake -> Charm +3 / Guts +2 hidden points | 9 |
| 2016-12-10 | Saturday | Sincere Omelette -> Charm +3 | 18 |
| 2016-12-17 | Saturday | Sincere Omelette -> Charm +3 | 27 |

That puts the route safely above the 20-stamp threshold before **2016-12-22**, when Dayloop tells the player to order the special menu and receive the Photo of Clara.

The route does not need a separate invisible stamp counter in pack schema to make this factual: the three visible Saturday orders are sufficient to prove that its Dec 22 special-menu step is reachable.

## Mistake semantics

The Sep 24 route deliberately asks Clara to fix a mistaken Sincere Omelette and tells the player to reload if she is flawless. That is a route strategy for obtaining the Guts bonus; it is not a claim that Clara always makes a mistake.

Later visits say mistakes are fine or do not require a reload because the completion route no longer depends on that conditional extra stat reward.

## Achievement linkage

The first-class P5R achievement catalog already contains:

- `Master of Akihabara`
- description: order the Maid Café special menu after earning 20 stamps
- `expectedBy`: `2016-12-22`

The audited route now has regression coverage showing that its stamp-building visits really do make that expected date feasible.

## Regression coverage

`P5RMaidCafeAuditTest` pins:

1. all three authored Sincere Omelette visits as Saturdays,
2. their 9-stamp-per-visit arithmetic,
3. normal hidden Charm totals and the Sep 24 conditional Guts bonus,
4. the Dec 22 20-stamp / Photo of Clara special-menu step, and
5. its alignment with the `Master of Akihabara` achievement's description and `expectedBy` date.
