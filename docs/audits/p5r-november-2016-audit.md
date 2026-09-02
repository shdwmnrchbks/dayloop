# P5R November 2016 point-unit and Royal-gate audit

This is a focused continuation of the P5R data audit. It preserves the existing
100% completion-route order while verifying November mechanics that are
independent of that route choice.

## Scope

This pass covers hidden social-stat points, reusable book/movie/game rewards,
Craft of Cinema modifiers, Aojiru, Mega Fertilizer, Tower/Hanged stat rewards,
class/crossword/TV rewards, and the wording of the Royal third-semester gate.
It does not claim that every flexible November action has been independently
replayed on the same date as the authored route.

## Independent references

- GameFAQs, marendarade, **Persona 5 Royal — November**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/november
- GameFAQs, sdarkpaladin, **Persona 5 Royal — November**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/79923/november
- GameFAQs, marendarade, **Persona 5 Royal — Social Stats**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/social-stats
- GameFAQs, marendarade, **Hanged — Munehisa Iwai**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/hanged
- GameFAQs, Raidramon0, **Hanged Man — Munehisa Iwai** (updated 2026-07-27):
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/82334/hanged-man-munehisa-iwai
- GameFAQs Royal Q&A, **Third semester unlock requirements**:
  https://gamefaqs.gamespot.com/ps4/370658-persona-5-royal/answers/546454-what-are-3rd-semester-unlock-requirements

## Corrections

- November class questions, crosswords, TV quizzes and Aojiru now use their
  actual +2 hidden-point rewards rather than one-note shorthand.
- `The Hero with a Bow` and `Dressed in Ashes` completions use their +5 rewards;
  `Heroic Revelations`, `Call Me Chief` and `Reckless Casanova` use +7.
- `Admission Impossible`, `Pach-Saw` and `Over the Pigeon's Nest` use their
  +5 movie base plus the already-active +2 Craft of Cinema modifier = +7.
- `Featherman Seeker` and `Punch Ouch` use the reusable retro-game +3 reward.
- Mega Fertilizer is +5 Kindness.
- Tower ranks 8 and 9 use +5 Kindness in the audited November events.
- Hanged Man rank 8 uses +5 Proficiency; rank 9 does not invent a stat reward.
- Akechi darts use +3 Proficiency.
- Moon rank 7 no longer invents a Kindness social-stat reward.
- The duplicated Strength rank-9 ritual on November 5 was removed.

Most importantly, the November walkthrough no longer says Justice/Akechi ranks
7 or 8 are required to unlock the third semester. Councilor/Maruki rank 9 by
November 17 is the unlock requirement. Justice rank 8 and its dialogue choices
add optional Akechi/Royal content but do not unlock the semester itself.

## Regression coverage

`P5RNovemberAuditTest` pins the corrected high-risk point values, confirms the
November 5 Strength event is present once, and rejects a regression that labels
Justice rank 7/8 as a third-semester requirement.

## Remaining audit work

December 2016 onward still needs the same point-unit and conditional-modifier
pass. Full issue completion still requires either independent verification or
an explicit route/source-specific designation for every user-visible P5R fact.
