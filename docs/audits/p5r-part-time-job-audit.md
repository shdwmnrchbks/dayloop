# P5R part-time job / request-gate audit

Scope: the completion route's part-time-job sequence, including the Ore no Beko Beef Bowl shifts used to unlock Toranosuke Yoshida and the Rafflesia / Triple Seven / Crossroads work counts used to identify job-linked Mementos requests.

## Sources checked

- GameFAQs Royal **Social Stats** guide — Triple Seven, Rafflesia, Ore no Beko and Crossroads locations/timing, hidden social-stat point values, special-shift bonuses and request/Confidant work-count requirements.
- GameFAQs Royal May walkthrough — independent proof that Ore no Beko can be accepted and worked on May 6 once nighttime Shibuya travel is available; this confirms Dayloop's May 18 start is a route choice, not the job's global unlock.
- GameFAQs Royal **Sun** guide — Sun rank 0.1 requires two Beef Bowl shifts before Yoshida's Confidant progression begins.

## Beef Bowl / Yoshida sequence

- **May 18:** apply for the Beef Bowl Shop job and work the first evening shift. Dayloop records **+3 hidden Proficiency points**, matching the normal Ore no Beko reward.
- **May 21:** work the second shift and get every order right. Dayloop records **+5 hidden Proficiency points**: +3 base plus the +2 successful-order bonus.
- **May 26:** after the two required shifts and the route's politician interactions, Yoshida reaches **Sun rank 1**.

The associated `Punch That Clock!` trophy remains available from **Apr 18** because other part-time jobs are already usable in the first free-roam period. That trophy date must not be replaced by Dayloop's later May Beef Bowl route choice.

## Job-linked request gates

Royal's job mechanics require prior shifts before the protagonist can identify the targets for three Mementos requests:

- **Rafflesia / `Who's Been Assaulting People?`** — at least three total flower-shop shifts.
- **Triple Seven / `Calling for Justice for Cats`** — at least three total convenience-store shifts.
- **Crossroads / `We Aren't Just Your Slaves`** — at least two total bar shifts.

Dayloop's route satisfies those counts before its Aug 16 Mementos cleanup:

### Rafflesia

1. **Jul 19** — first shift; Luck Reading boosts the normal Kindness reward to +4 hidden points.
2. **Aug 5** — second shift; correct customer order with Luck Reading, +7 hidden Kindness.
3. **Aug 10** — third shift; correct customer order with Luck Reading, +7 hidden Kindness.

The route receives `Who's Been Assaulting People?` on Aug 3, then has enough authored shifts to identify the target before Aug 16.

### Triple Seven

1. **Jul 31** — first shift; Luck Reading boosts the normal Charm reward to +4 hidden points.
2. **Aug 7** — second shift on a date ending in 7; correct barcode handling plus Luck Reading yields +7 hidden Charm.
3. **Aug 12** — third shift; Luck Reading yields +4 hidden Charm.

The route receives `Calling for Justice for Cats` on Aug 4 and reaches the required three total shifts before Aug 16.

### Crossroads

1. **Aug 7** — first shift; base Charm plus the downcast woman's Kindness reward are both represented, with Charm Luck Reading active.
2. **Aug 8** — second shift; the downcast woman is used again, this time with Kindness Luck Reading active.

The route receives `We Aren't Just Your Slaves` on Aug 2 and completes the two required bar shifts before Aug 16.

## Regression coverage

`P5RPartTimeJobAuditTest` pins the Beef Bowl/Yoshida chain and keeps general job availability separate from the route's chosen shifts.

`P5RJobRequestGateAuditTest` pins the first three Rafflesia shifts, first three Triple Seven shifts, first two Crossroads shifts, their hidden-point values, the three request receipt dates and the Aug 16 all-requests Mementos cleanup.

No walkthrough correction was required in this pass; the existing route ordering, work counts and hidden-point values already match the independently checked Royal mechanics.
