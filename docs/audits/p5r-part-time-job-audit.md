# P5R part-time job / Yoshida audit

Scope: the completion route's Ore no Beko Beef Bowl shifts and the distinction between global part-time-job availability and the route-selected shifts used to unlock Toranosuke Yoshida.

## Sources checked

- GameFAQs Royal **Social Stats** guide — Triple Seven, Rafflesia and Ore no Beko job locations/timing, hidden social-stat point values, special-shift bonuses and request/Confidant work-count requirements.
- GameFAQs Royal May walkthrough — independent proof that Ore no Beko can be accepted and worked on May 6 once nighttime Shibuya travel is available; this confirms Dayloop's May 18 start is a route choice, not the job's global unlock.
- GameFAQs Royal **Sun** guide — Sun rank 0.1 requires two Beef Bowl shifts before Yoshida's Confidant progression begins.

## Verified Dayloop sequence

- **May 18:** apply for the Beef Bowl Shop job and work the first evening shift. Dayloop records **+3 hidden Proficiency points**, matching the normal Ore no Beko reward.
- **May 21:** work the second shift and get every order right. Dayloop records **+5 hidden Proficiency points**: +3 base plus the +2 successful-order bonus.
- **May 26:** after the two required shifts and the route's politician interactions, Yoshida reaches **Sun rank 1**.

The associated `Punch That Clock!` trophy remains available from **Apr 18** because other part-time jobs are already usable in the first free-roam period. That trophy date must not be replaced by Dayloop's later May Beef Bowl route choice.

## Regression coverage

`P5RPartTimeJobAuditTest` pins:

1. the May 18 first Beef Bowl shift and +3 Proficiency,
2. the May 21 successful-order shift and +5 Proficiency,
3. the two-shift prerequisite recorded on Sun rank 1,
4. the route's May 26 Yoshida rank-1 checkpoint, and
5. the separation between Apr 18 general part-time-job trophy availability and this route's May Beef Bowl schedule.

No walkthrough correction was required in this pass; the existing route ordering and hidden-point values already match the independently checked Royal mechanics.
