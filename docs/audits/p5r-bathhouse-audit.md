# P5R bathhouse / Getting the Vapors audit

Scope: Royal's `Getting the Vapors` trophy and the distinction between trophy availability and the authored completion route.

## Sources checked

- PlayStationTrophies, **Getting the Vapors** — independent Royal trophy reference: use the Yongen-Jaya bathhouse during rainy weather and get the overheated/Guts result rather than the extra-Charm result; saving before entering is recommended because the outcome is conditional.
- TrueAchievements, **Getting the Vapors** — independent Royal achievement reference: rainy nights and snowy weather qualify; May 19 is a confirmed early opportunity and is specifically reported as an official-guide date, with later confirmed opportunities also documented.
- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — source completion route. The route does not need to spend May 19 at the bathhouse, so a confirmed trophy opportunity must not be converted into a mandatory route action.

## Decision

Dayloop previously used `availableFrom: 2016-06-01`, which was only a coarse month anchor. Independent Royal references support **2016-05-19** as the first confirmed opportunity.

The trophy metadata now uses:

- `availableFrom: 2016-05-19`,
- a description that calls out the rainy/snowy bathhouse condition,
- manual tracking, and
- no `expectedBy` date.

This intentionally does **not** insert a May 19 bathhouse step into the 100% Completion Route. `availableFrom` means the trophy can be attempted from a confirmed opportunity; it does not mean the authored route must spend that time slot there.

## Conditional outcome

The important gameplay distinction is preserved: the player needs the overheated result during a qualifying rainy/snowy visit. Royal trophy guides recommend saving before the bathhouse and retrying if the alternate Charm result occurs.

Because the result is conditional, Dayloop must not model this trophy as a guaranteed deadline or deterministic completion-route checkpoint unless a separately audited deterministic rule is added later.

## Regression coverage

`P5RBathhouseAuditTest` pins:

1. May 19 as the first confirmed availability anchor,
2. rainy/snowy wording in the requirement,
3. manual tracking,
4. no guaranteed `expectedBy` date, and
5. no fabricated `Getting the Vapors` deadline.
