# P5R conditional route audit — choices, RNG, weather and route cleanup

This pass audits route text that depends on **player choice or conditional state** rather than a fixed calendar fact. The P5R walkthrough remains an authored `100% Completion Route`; conditional wording must say what condition the route relies on instead of making the result look automatic or universal.

## Sources

- RPG Site, **Persona 5 Royal Confidant guide** and its individual Royal Confidant pages. The individual romanceable Confidant guides identify the relationship branch as a player decision at the relevant rank (normally rank 9), with explicit friendship/romance dialogue branches. Examples:
  - Ann / Lovers: https://www.rpgsite.net/feature/9381-persona-5-royal-ann-confidant-guide-lovers-choices-romance-gifts
  - Makoto / Priestess: https://www.rpgsite.net/feature/9378-persona-5-royal-makoto-confidant-guide-priestess-choices-romance-gifts
  - Haru / Empress: https://www.rpgsite.net/feature/9379-persona-5-royal-haru-confidant-guide-empress-choices-romance-gifts
  - Futaba / Hermit: https://www.rpgsite.net/feature/9382-persona-5-royal-futaba-confidant-guide-hermit-choices-romance-gifts
  - Takemi / Death: https://www.rpgsite.net/feature/9384-persona-5-royal-takemi-confidant-guide-death-choices-romance-gifts
  - Hifumi / Star: https://www.rpgsite.net/feature/9388-persona-5-royal-hifumi-confidant-guide-star-choices-romance-unlock-gifts
  - Ohya / Devil: https://www.rpgsite.net/feature/9387/persona-5-royal-ohya-confidant-guide-devil-choices-romance-gifts
  - Chihaya / Fortune: https://www.rpgsite.net/feature/5472/persona-5-royal-chihaya-confidant-guide-fortune-choices-romance
  - Kawakami / Temperance: https://www.rpgsite.net/feature/5473/persona-5-royal-kawakami-confidant-guide-temperance-choices-romance-gifts
  - Royal Confidant index (including Faith/Kasumi): https://www.rpgsite.net/feature/5479/persona-5-royal-confidant-guide-conversation-choices-answers-romance-options-gifts-skill-unlocks
- The existing P5R activity audit/source set for Royal DVD subscription semantics; see `p5r-dvd-game-audit.md` and `p5r-october-2016-audit.md`.
- Alyookid's completion schedule remains the route source. Its selected actions are preserved as route instructions, not promoted into universal availability rules.

## Romance choice semantics

Nine of the ten romanceable rank-9 route entries were already explicit about the branch, using wording such as `romance choice available`. Ann/Lovers on June 24 was the outlier: it said rank 9 simply `becomes a relationship`, which made the romance look automatic.

The June 24 step now states:

- romance is a **choice**, and
- this particular completion route chooses romance.

That preserves the authored route's intent while keeping the game mechanic truthful. The route still reaches Lovers rank 10 on July 1 regardless of whether another user chooses the friendship branch; the UI no longer implies that rank 9 itself forces a relationship.

`P5RConditionalRouteAuditTest` pins all ten decision points:

- Ann / Lovers — 2016-06-24
- Kawakami / Temperance — 2016-07-06
- Takemi / Death — 2016-08-20
- Ohya / Devil — 2016-09-23
- Hifumi / Star — 2016-09-28
- Futaba / Hermit — 2016-11-12
- Makoto / Priestess — 2016-11-15
- Chihaya / Fortune — 2016-11-17
- Haru / Empress — 2016-12-12
- Sumire / Faith — 2017-01-23

Every one must continue to say `romance choice` rather than presenting the relationship branch as an automatic rank reward.

## DVD route cleanup vs game rule

Royal's DVD shop uses a one-time membership/subscription model and does not impose the old rental-return deadline. Earlier activity/deadline work corrected the reusable DVD model, but two walkthrough labels still sounded universal:

- October 14 called `Tee` the `last DVD rental of the game`.
- October 23 said `Return your DVDs — no more rentals from here on`.

Both are now explicitly **completion-route** statements:

- October 14 is this route's final rental.
- October 23 is route cleanup after the route has finished all DVD viewing.
- Both remind the user that Royal has **no return deadline**.

This prevents a valid completion-route sequencing decision from becoming a false system restriction in user-facing text.

## RNG / weather condition examples

Several route rewards are valid only if a visible condition is met. The route already handled the highest-risk examples correctly, and the new regression test keeps that wording from being flattened later:

- June 21 rainy bathhouse explicitly says the visit is **during the rain** before assigning the rainy-day dual-stat result.
- September 24 Maid Cafe explicitly asks Clara to make/fix the mistake and tells the player to reload if she is flawless before assigning the Guts component.
- January 16 fishing explicitly references the **snow warning** and tells the player to reload on a missed rare-fish attempt.

The same principle applies to save/reload instructions for retro games, darts, batting, fishing and job-success bonuses: a route can recommend controlling RNG, but the success bonus must not be represented as an unconditional reusable base reward.

## Verification boundary

This closes the identified high-risk **choice / weather / RNG / route-cleanup wording** regressions. Flexible hangout or acquisition dates remain route-selected unless independently supported as fixed game rules.

The later April–February route-order reproduction independently checked the complete authored action sequence, and `P5RUncertainTrophyAvailabilityAuditTest` removes false exact dates from achievements whose gates remain player-state, RNG, progression, or ending dependent. Together those later passes satisfy the remaining issue #12 conditional/state boundary without pretending the app can automatically evaluate profile bond/stat state it does not yet track.
