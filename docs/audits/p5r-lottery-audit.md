# P5R Easy Money / lottery audit

Scope: the completion-route reminder for Royal's `Easy Money` trophy and the distinction between lottery availability, random trophy completion, and trophy-qualifying wins.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — source route. Its introduction explicitly calls `Easy Money` the one achievement the schedule cannot guarantee because it is luck-based, and its July 1 note says some lottery types do not work for the trophy.
- GameFAQs Royal July walkthrough — independently states that the July `Summer Lotto` / Summer Mammoth ticket does **not** unlock `Easy Money`.
- Persona 5 Royal Japanese strategy references — ordinary lottery tickets are sold from **Apr 18** and their results are posted **7 days later**.
- PlayStationTrophies / Xbox achievement guides — independent checks that the result is fixed when a ticket is purchased, the player must return later to claim a qualifying win, and the trophy unlocks only on a winning result.

## Confirmed semantics

- Location: Shibuya Station Square lottery stand.
- The ordinary weekly lottery becomes available on **2016-04-18**.
- Its result is available seven in-game days later, so **2016-04-25** is the first possible `Easy Money` trophy date if the first ticket wins.
- Buying/checking tickets does not consume a calendar time slot.
- The trophy is luck-dependent; the completion route cannot promise a specific completion date.
- The July Summer Mammoth/Summer Lotto draw is useful as a guaranteed money payout but **does not satisfy the Easy Money trophy**.
- The result is determined at purchase, so repeatedly reloading only on result day does not reroll the same ticket.

`availableFrom = 2016-04-25` therefore means **first possible result**, not guaranteed completion. `expectedBy` intentionally remains absent.

## Scratch-ticket source conflict

Secondary trophy guides disagree on whether Royal's daily Scratch Lottery can satisfy `Easy Money`:

- some trophy/achievement guides say a daily or weekly qualifying win can count;
- other Royal trophy references say only the ordinary Mammoth lottery counts, excluding both Scratch and Summer Mammoth.

Dayloop therefore does **not** tell the player that Scratch definitely qualifies. The user-visible route uses the source-safe wording `qualifying tickets` and only names the cross-source-supported exclusion that matters on July 1: **Summer Mammoth does not count**.

## Dayloop correction

The previous July 1 route text only said:

`Start buying lottery tickets at Shibuya Station Square; check back over the following days`

Because July is precisely the Summer Mammoth window, that could lead a trophy-focused user to assume the guaranteed summer payout was sufficient for `Easy Money`.

The route now says that:

- the achievement is RNG,
- some ticket types including Summer Mammoth do not count,
- the player should keep buying qualifying tickets, and
- each result must be checked when due.

This preserves the source schedule's intended "start trying now" guidance without pretending the trophy is deterministic.

## Achievement semantics

The first-class P5R achievement catalog now records:

- description: `Win a qualifying lottery prize.`
- `availableFrom`: `2016-04-25` — first possible ordinary-ticket result
- tracking: manual
- no `expectedBy` date

The absence of `expectedBy` is intentional: a random trophy must not be represented as a guaranteed route milestone.

## Regression coverage

`P5RLotteryAuditTest` pins the July 1 warning, Station Square location, RNG/qualifying-ticket language, Summer Mammoth exclusion, the Apr 25 first-possible availability anchor, and the achievement catalog's manual/no-guaranteed-date semantics.
