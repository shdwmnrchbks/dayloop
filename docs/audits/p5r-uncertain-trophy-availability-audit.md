# P5R player-state trophy availability audit

Scope: finish issue #12's achievement-metadata cleanup after the fixed-date Royal trophy audit and the full April-February route reproduction.

## Rule

`availableFrom` is only authored when one independently supportable Royal calendar date exists for the trophy becoming actionable. A date from one optimized completion schedule is not enough when the actual gate depends on social-stat routing, Confidant routing, ending branch, combat readiness, or completion of other trophies.

The achievement schema already permits `availableFrom = null`. In the rule-based achievement tracker an omitted date leaves a manual trophy actionable instead of presenting a false `Upcoming · YYYY-MM-DD` claim. Exact dates remain on deterministic story/facility/mechanic trophies.

## Removed placeholder dates

The following eight trophies previously carried coarse first-of-month values that were not universal Royal facts and now intentionally omit `availableFrom`:

- **The Phenomenal Phantom Thief** — depends on earning every other trophy.
- **The Path Chosen** — the ending date depends on which ending branch is taken.
- **A Most Studious Disguise** — requires first place in an exam and therefore depends on Knowledge/stat routing and exam performance.
- **Pure Perfection** — depends on the player's five social-stat progression.
- **My Closest Partner** — depends on which romanceable Confidant is advanced and when the romance choice is taken.
- **True Confidence** — depends on which Confidant is first advanced to rank 10.
- **Unsurpassed Rebel** — the Reaper becomes accessible well before most fresh-file routes can reasonably defeat it; encoding a later route month as game availability was misleading.
- **Professional Modification** — requires Guts 4, Hanged Man rank 1 and a later Iwai visit; the route can demonstrate a date, but the gate is player-state dependent rather than calendar-fixed.

All eight remain `manual` and have no `expectedBy` route deadline.

## Dates deliberately retained

This change does not remove independently audited exact/actionable anchors. Nearby examples include Tokyo Tourist (Apr 28), Getting the Vapors (May 19), Angler's Debut (Jul 4), and Awakening the Phantom Thieves (Jan 10). Facility-opening dates such as the Maid Cafe/Akihabara availability remain useful where they describe when the activity can begin even if a cumulative trophy completes later.

## Regression coverage

`P5RUncertainTrophyAvailabilityAuditTest` requires the eight entries above to be exactly the P5R trophies with no `availableFrom`, keeps them manual/without `expectedBy`, and spot-checks deterministic dates that must remain.

This deliberately stops short of adding bond/stat predicates to achievement evaluation. The generic condition DSL can describe some of those gates, but the achievement tracker does not yet consume profile bond/social-stat state; an incomplete condition engine would be less accurate than explicit manual tracking.
