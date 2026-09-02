# P5R February 2017 route-order audit

Scope: finish issue #12's month-by-month Persona 5 Royal route reproduction through the final authored walkthrough day on February 3. The existing third-semester audit already covers February stat points and deadline metadata; this pass focuses on Feb 1-3 calendar state, the fixed final Calling Card and final-boss chronology.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — primary Dayloop route source for the Feb 1 movie cleanup and Feb 2-3 ending sequence.
- Kamigame, **P5R February story schedule** — independent calendar check: Feb 1 has after-school and night free time; Feb 2 has an after-school slot, ending branch, fixed nighttime Calling Card and Akechi event; Feb 3 is the final Palace battle.
- Kamigame, **Maruki Palace guide** — independently states that the Treasure route must be secured by Feb 2 and that Maruki receives the Calling Card at LeBlanc on the night of Feb 2.
- GameFAQs Royal February walkthrough (`faq/78212`) — independent check for Feb 1 free time, Feb 2 Sumire third awakening followed by the Maruki/Akechi story sequence, and the fixed final confrontation afterward.
- GameFAQs Royal board discussion on final-Palace timing — secondary confirmation that Feb 2's Calling Card is automatic/fixed once the Treasure route is secured.

## Corrections made

### Feb 1 remains a school/free-time route day

Feb 1 stays `school`. Dayloop keeps its route-selected `The Goodfather` viewing and optional ultimate-Persona cleanup. No additional time-consuming action is inserted.

One monthly Royal schedule lists a Feb 1 crossword, but dedicated Royal crossword catalogs end on Jan 27. Because those sources conflict, this audit does **not** invent a new February crossword entry.

### Feb 2 is school plus after-school freedom, then fixed nighttime story

The pack previously marked Feb 2 as an all-day `story` entry and omitted the fixed Calling Card. It is now `school`, matching Royal's schedule with a usable after-school slot followed by a locked nighttime sequence.

The route now explicitly records:

1. **After school:** answer Sumire's message and see her third awakening, assuming Faith was completed on the route.
2. The Laboratory Treasure route must already be secured by this date; Dayloop's route does so on Jan 26.
3. **Night:** refuse Maruki's ending offer when he visits LeBlanc.
4. **Night:** send Maruki the fixed final Calling Card.
5. Answer Akechi `We're stopping Maruki` and receive his third awakening on the supported Royal route.

This separates the route deadline from the Calling Card event rather than implying the player can freely send the card earlier.

### Feb 3 remains the final story battle

Feb 3 remains `story` and keeps the three authored boss phases:

- Maruki / Azathoth
- Adam Kadmon
- scripted survival / ending sequence

No post-Feb-3 calendar filler is added because Dayloop's authored completion route ends at the final confrontation.

## Regression coverage

`P5RFebruaryRouteOrderAuditTest` pins:

1. Feb 1 school/free-time route state and final movie cleanup;
2. Feb 2 `school` state and Sumire after-school event;
3. the Feb 2 route-secured reminder, Maruki rejection, fixed nighttime Calling Card and Akechi awakening;
4. Feb 3 final-battle story state and all three boss phases;
5. Jan 26's already-secured Laboratory Treasure route; and
6. the existing Feb 2 Treasure-route deadline / Feb 3 final-confrontation deadline split.

With this pass, every authored P5R walkthrough month from **April 2016 through February 3, 2017** has dedicated route-order/state reproduction coverage. Remaining issue #12 work should no longer describe full route reproduction as outstanding; any residual work is limited to player-state/RNG/branch-dependent conditions, documented source conflicts and isolated one-off facts that cannot honestly be represented as universal calendar dates.
