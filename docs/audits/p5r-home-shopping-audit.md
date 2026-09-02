# P5R Home Shopping Program audit

Scope: the Home/TV Shopping reminders that the P5R 100% Completion Route already presents. This is an audit of existing route claims, not an instruction to add every optional Royal broadcast to the route.

## Sources checked

- GameFAQs, **Persona 5 Royal Walkthrough & Guide — Battle Boosts** — Royal-specific broadcast dates, set names, item bundles and prices.
- Megami Tensei Wiki, **Home Shopping Program** — independent Royal broadcast table and item effects.
- Samurai Gamers, **Persona 5 Royal — Home TV Shopping Item List and Prices** — independent date/set/item table.
- Pro Game Guides, **Everything for sale on the Home Shopping Program in Persona 5 Royal** — secondary full-season table, retained with the source conflicts below.
- Neoseeker Royal month walkthroughs — date-specific checks for the June 26 and November 27 broadcasts.

## Royal schedule vs Dayloop route reminders

Royal has additional valid broadcasts that this completion route does not surface as reminders. In particular, **May 1** and **January 29** are real broadcasts but are optional for this route. Their omission must not be interpreted as a claim that the program is unavailable on those dates.

The route currently surfaces 18 Home/TV Shopping reminders, all on verified Royal Sundays:

- Apr 24 — generic reminder only; neither optional opening set is presented as required.
- May 8 — Bio Nutrients Set.
- May 15 — Allergy Relief Pack (the route calls out the 20 Wide Eye Drops it cares about).
- May 22 — Muscle Plus Set / Outdoors Kit.
- May 29 — Folding Screen Set.
- Jun 19 — Supportive Gift Set / Busy Revival Set.
- Jun 26 — Dark Power Set / Cursed Tools Set.
- Jul 3 — Calm Mind Set.
- Aug 7 — Phantom Thief Set / Heroic Set.
- Aug 14 — Drink Set / Floral Gift Set.
- Sep 25 — the two Phantom-Thief-themed bundles; the route calls out Lockpicks, Phantom Wafers and Calling Postcards rather than reproducing every item in both sets.
- Oct 2 — Pumpkin Ghost / Haunted Repel sets.
- Nov 6 — Sturdy Ointment / Inner Muscle sets.
- Nov 13 — Instant Spray / Meditative sets.
- Nov 27 — Yaki-Imo / sweets bundles.
- Dec 11 — Super Detox / Fancy Magatama sets.
- Jan 15 — Lucky Worker / Lucky Muscle bags.
- Jan 22 — Talisman / Sweet Delight sets.

This is intentionally a **route reminder list**, not a complete Home Shopping catalog.

## Source conflicts / localization variants

### June 26 — Cursed Tools Set quantity

GameFAQs Battle Boosts, Samurai Gamers, Neoseeker and an in-game transcript independently support **Five-inch Nail x3 + Straw Doll x10**. Pro Game Guides lists only two Five-inch Nails. Dayloop's existing route wording says `10 Straw Dolls + 3 Curse items`; the count therefore agrees with the stronger multi-source evidence while avoiding an unsupported claim about a different quantity.

### September 25 — set naming

Royal references vary between `Phantom Thief Set 2`, `Phantom Thief Set`, `Thief Fun Set` and `Phantom Thief Fun Set`. The route's plural `Phantom Thief sets` wording is treated as deliberate shorthand; the item examples it names (Lockpicks, Phantom Wafers, Calling Postcards) are all present in the independently checked bundles.

### October 2 — second set naming

References use both `Haunted Repel Set` and `Ghost Repellent Set`. Dayloop retains `Haunted Repel`, which is supported by the Royal Home Shopping table and does not change the underlying Baptismal Water / Exorcism Water bundle.

### November 27 — roasted-potato / sweets naming

Royal guides variously render the first bundle as `Yaki-Imo Set`, `Ishiyaki Potato Set` or even `Taki-Imo Set`; the second appears as `Special Sweets Set` or `Limited Sweets Set`. The underlying items are consistently Legendary Yaki-Imo x3 + Beni-Azuma x10 and Melon Pan x3 + Moon Dango x5. Dayloop keeps the source completion route's `Yaki-Imo and Limited Sweets sets` wording and records the naming variance here rather than churning route text between equivalent localized labels.

## Route shorthand vs catalog identity

Several Dayloop rows intentionally mention only the part of a set that matters to this completion route. Examples:

- May 15 calls out the 20 Wide Eye Drops but not the two Calming Masks in the Allergy Relief Pack.
- Jun 19 summarizes the Busy Revival Set as three revivals plus ten single-target heals rather than reproducing the exact consumable names.
- Sep 25 names the route-relevant Lockpick / Phantom Wafer / Calling Postcard items but not every consumable in the two offered bundles.

Those are route-specific summaries, not false statements that the omitted bundle items do not exist.

## Regression coverage

`P5RHomeShoppingAuditTest` pins:

1. exactly the 18 broadcast dates that the current completion route chooses to remind the player about,
2. that every such reminder falls on a real Sunday,
3. the audited set-name/item fragments Dayloop already exposes, and
4. the explicit distinction between a route reminder list and Royal's fuller optional broadcast schedule.

The test deliberately does **not** require May 1 or Jan 29 to appear in the completion route, because adding optional broadcasts would be route-design expansion rather than correcting an existing factual claim.
