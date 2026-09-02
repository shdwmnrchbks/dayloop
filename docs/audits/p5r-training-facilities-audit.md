# P5R training facilities audit

Scope: the completion route's late Kichijoji temple visit and optional Protein Lovers Gym cleanup, with route timing kept separate from each facility's actual Royal unlock.

## Sources checked

- Samurai Gamers Royal Old Temple guide — Kichijoji temple unlocks on Jun 6, is daytime-only, costs no money, and the first three meditation visits give +3 maximum SP each.
- GameFAQs Royal Battle Boosts guide — independent temple progression (+3 / +5 / +8 / +12 max SP by visit tier) and Protein Lovers Gym progression.
- GameFAQs Royal Battle Boosts / Confidant references — Protein Lovers Gym is unlocked through Chariot rank 5; Lovers rank 8 is an alternate unlock path.

## Verified Dayloop route

### Kichijoji temple

`A Serene Experience` correctly uses **2016-06-06** as first facility/trophy availability. Dayloop deliberately waits until **2017-01-30** to meditate there as a cleanup activity.

The Jan 30 step says the temple raises maximum SP and does not encode that increase as a social-stat `statGains` value. That is correct: max-SP growth is a combat-stat increase, not Knowledge/Guts/Proficiency/Kindness/Charm progress.

The current completion route contains only this one temple meditation. Under Royal's visit table that is a first-visit **+3 maximum SP** result, although the route text does not need to expose the numeric combat-stat delta to remain correct.

### Protein Lovers Gym

Royal unlocks Protein Lovers once either Ryuji reaches Chariot rank 5 or Ann reaches Lovers rank 8. Dayloop's route already reaches **Chariot rank 5 on Jun 4**, so the facility is available long before the route's **Jan 31** optional cleanup suggestion.

The route therefore does not confuse the Jan 31 optional training date with the gym's unlock.

## Regression coverage

`P5RTrainingFacilityAuditTest` pins:

1. the route's single Jan 30 Kichijoji temple visit,
2. max-SP wording remaining separate from social-stat gains,
3. `A Serene Experience` first availability on Jun 6,
4. Chariot rank 5 scheduled on Jun 4 in this completion route, and
5. the Jan 31 Protein Lovers Gym step remaining explicitly optional cleanup.

No route correction was required in this pass; the existing late temple/gym choices are legal and already separated from global availability metadata.
