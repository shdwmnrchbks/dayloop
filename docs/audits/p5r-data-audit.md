# P5R data audit ledger — September 2026

This ledger records what the P5R pack is claiming and, just as importantly,
what it is **not** claiming. The original pack was extracted primarily from a
single 100% completion schedule. That is a valid authored route, but a route's
chosen date is not automatically a universal game availability date.

## Source roles

- **Alyookid 100% schedule** — primary source for the authored completion route:
  the action order, route-selected Confidant rank-ups, stat-building sequence,
  and completion targets. Its `+1/+2/+3` social-stat annotations frequently
  describe the game's displayed music-note count, not hidden point totals.
- **sdarkpaladin GameFAQs 100% walkthrough** — independent route/date spot-check
  for the calendar plan and Palace/Confidant progression.
- **GameFAQs Royal Social Stats guides / megaten-database Royal overworld data** —
  structured checks for actual hidden social-stat point gains, rank thresholds,
  movie/DVD/game rewards and activity-enhancing book effects.
- **Raidramon0 GameFAQs Royal Confidant guide (2026)** — current structured check
  for Confidant stat gates, time gates, automatic ranks, request gates and
  recurring availability.
- **RPG Site / Samurai Gamers Royal guides** — independent checks for unlock
  conditions, recurring activity effects, school-question dates and stat gains.
- **Sentovibes/persona-companion-app** — structured independent check for Royal
  automatic-rank timing and Confidant schedules/requirements.
- **Push Square** — broad school/exam answer cross-check; where its date conflicts
  with multiple date-specific Royal walkthroughs, the date-specific sources win.
- **Megami Tensei Wiki / calendar references** — Palace story deadlines and
  secondary checks for Royal item/book effects.

## Audit decisions

### Walkthrough and activities

`walkthrough/*.json` is an authored **100% Completion Route**. Its dates describe
what this route tells the player to do, not every day on which an action is
possible. The route is now named and described that way in `pack.json`, and the
app surfaces the route identity with the active profile rather than silently
presenting the plan as universal availability.

The route has been spot-checked against an independent Royal 100% walkthrough.
When two valid routes choose different days, dayloop keeps its authored route
instead of treating the difference as an error. Universal claims (deadlines,
stat gates, unlock windows) require independent support.

`activities.json` records the activity outcomes used by that route. A route
reference means "do this activity here in this plan"; it does not claim the
activity is available only on that date.

The activity schema defines `statGains` as **actual social-stat points**, not the
music-note icons displayed by the game. The original P5R catalog and walkthrough
mixed those units. The September audit normalized the reusable definitions:

- Standard Shujin-library/Shibuya stat books use their actual 5-point rewards;
  Jinbocho stat books use 7. `Social Thought` is also a special 7-point book.
- Royal DVDs use 3 base points per viewing, require two viewings, and use the
  subscription model with no return deadline. `The Craft of Cinema` is a
  separate +2-point modifier per viewing.
- First-time movie viewings use 5 base points; `The Craft of Cinema` again adds
  +2 once it has been read.
- Retro-game clears use 3 points. `Game Secrets` enables an assist/cheat mode;
  it is not represented as a universal guaranteed win.
- Route-only DVD rental counters were not treated as "12-part rental series";
  walkthrough labels now identify a title's first/second viewing instead.
- `Knowing the Heart` now describes its real Technical-combination effect;
  `Factorization Guide`, billiards books and several movie-theater locations
  were corrected at the same time.

The game maps several different hidden point totals to the same displayed note
count, so future audits must compare actual point values rather than counting
note icons in screenshots.

#### Route point audit status

April, May and June 2016 have been normalized from note-count shorthand to
actual points and have regression coverage. Confirmed examples include class
answers, crosswords/TV quizzes, studying, plant nutrients, Death/Sun bonus stat
rewards, Aojiru, books, DVDs, movies, retro games, darts, the rainy bathhouse,
chalk dodges and the first Big Bang Burger challenge.

The month-by-month audit also found route steps lost during the original import,
not merely bad numbers. May now restores the first `Guy McVer` viewing and the
5/8, 5/22 and 5/29 Aojiru purchases. June restores the 6/25 `Game Secrets`
reading. The restored Aojiru sequence is important because the drink stand
keeps offering the current stat until it is purchased; omitting a purchase
shifts every later route-specific stat.

`The Craft of Cinema` is read on 2016-06-23 in this route. Regression tests
therefore require pre-6/23 DVD gains to equal the 3-point base and post-read DVD
viewings to include the +2 modifier (for example ICU on 6/25 and 6/27 = 5).

July onward has **not** yet received the same exhaustive point-unit pass, so the
ledger does not claim the full walkthrough is independently verified yet.

### Confidants

`RankStep.scheduledFor` is the completion route's chosen rank-up date.
`availableFrom` and `availableUntil` are reserved for actual game windows or
fixed story timing. The September audit migrated P5R away from overloading
`availableFrom` with route dates.

High-risk gates were independently checked and encoded or called out, including:

- Lovers rank 2 — Kindness 2 and after 5/6.
- Death rank 2 — Guts 2; later branch requires Charm 4 plus its Mementos request.
- Hierophant rank 5 — story-time gate after 8/22; rank 7 — Kindness 5.
- Temperance start — Operation Maidwatch plus Guts 3.
- Priestess start — Kaneshiro clear plus Knowledge 3; rank 6 — Charm 5.
- Emperor rank 6 — Proficiency 4.
- Empress rank 2 — Proficiency 5.
- Hermit rank 2 — Kindness 4.
- Hanged Man start — Guts 4; later progression requires Guts 5/request work.
- Star start — Kanda/Emperor setup plus Charm 3; later request gate uses max
  Knowledge.
- Fortune start — the multi-visit ¥100,000/Holy Stone/Mementos prerequisite.
- Tower start — Winners Don't Use Cheats setup.
- Councilor rank 9 — November cutoff and the actual third-semester unlock path.
- Justice rank 7 — Knowledge 4; rank 8/Promise affect optional Akechi Royal
  content and are **not** the third-semester unlock condition.
- Faith rank 5 — required to continue Faith ranks 6–10 in the third semester,
  not to unlock the semester itself.

Automatic Fool, Magician and Judgement ranks were also rechecked against
structured Royal references. This corrected the old Fool rank-1 4/9 value to
4/12 and separates Palace-progress-dependent automatic ranks from fixed dates.
Palace-triggered Fool/Magician ranks now keep the completion route's chosen day
only in `scheduledFor`; they deliberately do **not** carry synthetic
`availableFrom`/`availableUntil` windows because the trigger is Palace/story
progress rather than an ordinary calendar availability window.

### Deadlines

Palace entries are story deadlines, not the days the original completion route
chose to steal each Treasure. Completion-only targets such as book/game cleanup
are labeled as completion-route targets rather than universal missables.

Councilor is the third-semester requirement. Justice/Faith entries describe the
additional Royal content they gate without claiming they unlock the semester.

### School and exam answers

The answer table was cross-checked as a Royal table rather than inherited from
one route. The audit filled omitted April 25, April 30 and May 23 questions and
restored multi-part answers that had been flattened or omitted.

A source conflict was found for `Fatal woman`: Push Square places it on May 6,
but multiple date-specific Royal walkthroughs (including GameFAQs, Neoseeker
and Samurai Gamers) place the question on **May 7**, with May 6 containing a
lecture/chalk sequence. The pack now uses May 7 and keeps the stable
`p5r.answers.class.2016-05-07` id. This is recorded explicitly so a later audit
does not reintroduce the off-by-one date merely by switching source tables.

## Regression rules

1. Never put a completion-route-selected Confidant day in `availableFrom` merely
   because the route used that day. Use `scheduledFor`.
2. Story/Palace-progress automatic ranks must not invent calendar availability
   windows just to bound a route date; describe the trigger and keep the route
   day in `scheduledFor`.
3. `Activity.statGains` and walkthrough `statGains` are actual hidden social-stat
   points, not displayed music notes. Do not copy note counts into the schema.
4. Conditional bonuses such as `The Craft of Cinema` or `Factorization Guide`
   belong in effect/condition wording and route-specific totals rather than
   being silently folded into a reusable activity's base `statGains`.
5. For stateful route activities such as Aojiru, audit omitted steps as well as
   values: a missing purchase changes the later state/rotation.
6. Never label a route cleanup target as a game deadline without an independent
   game-rule source.
7. When answer/date sources conflict, prefer multiple date-specific Royal
   walkthroughs over a single undifferentiated answer table and record the
   conflict here.
8. Any new P5R universal gate/deadline should have an independent verifier in
   this ledger or `docs/sources.md`.
9. Structural validation (`packlint`) proves references and schema integrity;
   it does not by itself prove gameplay facts. Fact provenance remains a content
   responsibility.
10. When independent guides disagree only on *when a flexible action is done*,
    preserve the authored route and label it as route-specific instead of
    "correcting" it into another guide's route.
