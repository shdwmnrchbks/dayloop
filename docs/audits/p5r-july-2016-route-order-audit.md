# P5R July 2016 route-order audit

Scope: continue issue #12's month-by-month reproduction of the authored Persona 5 Royal completion route after April-June. The pass preserves Dayloop's chosen flexible Confidant/activity order while correcting fixed school/story semantics, housebound-slot context and one imported activity-location error.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — primary source-route facts for July 1-31, including the July 10 home DVD slot, July 17 Aojiru/movie route, July 18 Star Forneus route, July 20 lockpick batch, July 22 `Flowerpedia`, July 25 first Pyramid infiltration and July 26-27 Pyramid progression.
- GameFAQs Royal July walkthroughs (`faq/79923`, `faq/78212`, `faq/78256`) — independent checks for July 2/9 school slots, the July 10 LeBlanc lock, July 17 summer-festival story, July 18 fireworks story, July 20-23 Alibaba/Futaba investigation block and July 25 first Pyramid investigation.
- Neoseeker Royal July walkthrough — secondary check for the July 16 no-free-time story transition, July 17 festival, July 18 fireworks and July 25 first Palace investigation.
- Samurai Gamers Royal July walkthrough — secondary fixed-calendar check for the July 17-18 story block and the mid-July transition.
- Megami Tensei Wiki / GameFAQs Palace references — Futaba's Palace is an exception to the usual Calling Card timing: the Calling Card and Treasure heist occur on the same day after the Treasure route is secured.

## Corrections made

### School days

Two Saturdays were incorrectly rendered as ordinary free days even though the route itself contains classroom actions:

- **July 2:** now `school`; `Speed Reader` is read during class before the route's after-school Priestess/Twins actions.
- **July 9:** now `school`; the `A Triangle` class question precedes the route's Justice/Judgement progression.

### July 10 housebound story

July 10 is not a normal free Sunday. Royal's daytime story has Joker taking care of LeBlanc and the evening is confined to the café/home area. The route's request pickup and second `Jail Break` viewing remain valid home actions, but the day is now `story` and explicitly says the player cannot leave LeBlanc that evening.

### Mid-July story block

Dayloop already had July 13-15 as the three answer-bearing exam days and July 16 as a no-free-time story day. That split is preserved. Some calendar guides call July 16 the final exam/result-calculation day, while the primary completion schedule calls it story-only; there are no player-answer choices on July 16, so this audit does not introduce a synthetic fourth answer-bearing `exam` day.

The following fixed story days are now explicit:

- **July 17:** summer/meat festival and Ryuji-Yusuke Showtime story occupy the daytime; evening free time then resumes for the route's Aojiru/Luck Reading/movie actions.
- **July 18:** fireworks and follow-up story occupy the day; afterward the player is confined to LeBlanc, where the route plays `Star Forneus`.
- **July 20:** first Alibaba contact; confined LeBlanc evening for the lockpick batch.
- **July 21:** second Alibaba contact; confined LeBlanc evening for `Learn Pro Darts`.
- **July 22:** Futaba investigation around Yongen-Jaya/Backstreets and the supermarket; confined LeBlanc evening for `Flowerpedia`.
- **July 23:** third Alibaba contact; confined LeBlanc evening for bathroom cleaning.
- **July 24:** already correctly `story`/no free time.
- **July 25:** fixed first Pyramid investigation and automatic Magician rank 6; the route's later plant/bathroom actions remain home-only choices.

### `Flowerpedia` location

The imported July 22 step said `Read 'Flowerpedia' at the supermarket`. That conflated the daytime story investigation location with the evening time-slot action. The supermarket is part of the Futaba investigation; the route's book reading is performed later while confined to **LeBlanc**. The label now says `Read 'Flowerpedia' at LeBlanc`.

### July exam result / route state

July 19 remains a school/result day. The route continues to record:

- exam result: **Charm +5**;
- Sojiro's **Dandy Mirror** exam reward;
- `Finals` crossword: **Knowledge +2**;
- the route's Rafflesia and Star-rank activity choices.

These are preserved rather than replaced with another guide's legal but different Confidant route.

### July 20 lockpick state

The primary schedule's housebound July 20 evening is retained:

- craft the full route batch;
- finish with **10 lockpicks total** including the route's prior inventory/Morgana bonus;
- **Proficiency +5**;
- Proficiency reaches rank 4.

### Futaba Palace exception

The route chronology is intentionally different from the normal Palace Calling Card pattern:

1. **July 25** — mandatory first Pyramid investigation;
2. **July 26** — second infiltration / reach the Treasure route;
3. **July 27** — send the Calling Card and steal the Treasure on the **same day**.

Futaba's Palace is one of Royal's explicit exceptions to the usual next-day heist rule. The July 27 card/heist pairing must therefore not be "fixed" by inserting an extra calendar day.

## Regression coverage

`P5RJulyRouteOrderAuditTest` pins:

1. July 2/9 school day kinds;
2. July 13-15 answer-bearing exam block;
3. July 10, 16-18 and July 20-25 story-constrained day kinds;
4. July 10/17/18 housebound/free-evening context;
5. July 19 Charm +5, Dandy Mirror and `Finals` +2 Knowledge;
6. July 20 10-lockpick / +5 Proficiency route state;
7. July 22 `Flowerpedia` at LeBlanc rather than the supermarket; and
8. July 25 -> 26 -> July 27 same-day Calling Card/heist Pyramid chronology.

April through July now have dedicated route-order/state reproduction passes. August onward remains under issue #12, along with player-state/RNG/branch-dependent cases that should not be represented with false fixed-date precision.
