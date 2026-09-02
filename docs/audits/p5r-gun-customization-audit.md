# P5R gun customization / Iwai audit

Scope: the completion route's Hanged Man rank-1 gate and the subsequent `Professional Modification` gun-customization step.

## Sources checked

- PlayStationTrophies Royal `Professional Modification` guide — gun customization requires Iwai/Hanged rank 1, which requires Guts rank 4; starting rank 1 spends the time slot, so the player returns on a later visit to use the customization menu.
- GameFAQs Royal Hanged Man guide — rank 1 requires Guts rank 4 and unlocks `Starter Customization`.
- Samurai Gamers Royal Iwai guide — independent check that rank 1 is gated by Guts rank 4 and grants Starter Customization.

## Verified Dayloop sequence

- **Jul 18:** Dayloop's route reaches **Guts rank 4**.
- **Aug 10:** the route talks to Iwai and reaches **Hanged Man rank 1**. The Confidant definition retains the Guts gate rather than representing Aug 10 as universal game availability.
- **Aug 11:** the route returns to Iwai and **customizes a gun**, satisfying the trophy mechanic after Starter Customization has been unlocked.

This ordering is mechanically correct. The route intentionally delays Iwai even though an optimized player can reach Guts rank 4 and begin Hanged earlier.

## Trophy-date caution

`Professional Modification` is player-state gated: its earliest possible calendar date depends on how aggressively Guts and Iwai are routed. This audit therefore does **not** replace `availableFrom` with one optimized schedule's date. The achievement remains manual with no `expectedBy` route deadline while its condition modeling is still coarse.

## Regression coverage

`P5RGunCustomizationAuditTest` pins:

1. the Jul 18 Guts-rank-4 route milestone,
2. Hanged rank 1 scheduled for Aug 10 with its authored gate intact,
3. the Aug 10 walkthrough rank-up,
4. the Aug 11 later customization visit, and
5. manual/no-deadline trophy semantics.

No route correction was required in this pass; the existing Dayloop sequence already respects Royal's Guts → Hanged rank 1 → later customization ordering.
