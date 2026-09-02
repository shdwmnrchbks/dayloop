# P5R darts / billiards audit

Scope: the completion-route darts Baton Pass sessions and the billiards Technical-rank progression represented by Dayloop.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — source route for the June darts sessions and the August–October billiards sequence.
- GameFAQs Royal **Battle Boosts** reference — independent check for Penguin Sniper darts, direct rank-3 partner finishes, the partner encouragement answers, and the `Expert Billiards` / Jump Cue technique progression.
- megaten-database, **Persona 5 Royal — Overworld** — independent structured check for darts Baton Pass effects and billiards Technical ranks 1–4.
- Megami Tensei Wiki, **Billiards** / **Expert Billiards** — independent check for the Proficiency gates, Jump Cue, `Billiards Magician`, Massé Shot and Technical-rank effects.

## Darts

Royal darts at Kichijoji's Penguin Sniper can raise party members' Baton Pass rank. If a partner can finish on round 4 and receives the correct encouragement, that partner can jump directly to rank 3.

The route keeps the independently supported encouragement answers it actually uses:

- Ann: `Just play like normal.`
- Yusuke: `Pretend like you are painting.`
- Makoto: `Take a deep breath.`

Dayloop's authored route uses:

- **2016-06-05:** tutorial/early Ryuji session; Ryuji reaches Baton Pass rank 2.
- **2016-06-07:** Ann/Yusuke route; two teammates reach rank 3, with the route's successful scoring worth +3 hidden Proficiency points.
- **2016-06-26:** Makoto plus another teammate; two more teammates reach rank 3, again +3 hidden Proficiency points.

The generic darts references show that Proficiency payout depends on scoring performance. This audit therefore does **not** invent a fixed reusable darts stat reward or add one to the June 5 tutorial step merely because other Royal schedules optimize that session differently.

## Billiards / Technical rank

The supported Royal progression is:

1. Play billiards at Penguin Sniper.
2. Use `Expert Billiards` / Back Hand progression for Technical Rank 2.
3. With Proficiency 3 and the Jump Cue, perform the Jump Shot for Technical Rank 3 and receive `Billiards Magician`.
4. With Proficiency 5 and `Billiards Magician` read, perform the Massé Shot for Technical Rank 4.

Dayloop's completion route preserves the relevant authored sequence:

- **Aug 17:** first authored billiards session.
- **Aug 18:** buy `Expert Billiards`, Dart Set and Jump Cue at the Shibuya Underground Mall sports shop.
- **Aug 19:** read `Expert Billiards`.
- **Aug 26:** return to billiards for Technical progression.
- **Sep 5:** reach Technical Rank 3 and receive `Billiards Magician`.
- **Sep 23:** read `Billiards Magician`.
- **Oct 14:** play billiards for Technical Rank 4.

## Expert Billiards unlock-source conflict

Secondary Royal references are not perfectly consistent about the shop unlock wording for `Expert Billiards`:

- the source completion route buys it after its Aug 17 authored billiards session;
- some Royal item tables describe the shop unlock as requiring two billiards plays;
- other structured activity descriptions phrase the early Technical progression more generally around playing billiards and then buying/reading the technique book.

Dayloop therefore keeps the reusable activity note intentionally broad (`Appears after playing billiards`) and treats the **Aug 18 purchase as this completion route's authored timing**, not as a universal claim that every Royal save exposes the book after exactly one play. This is an explicit source-conflict designation rather than silently choosing one secondary table over another.

## Regression coverage

`P5RDartsBilliardsAuditTest` pins the route-visible facts without flattening the source conflict:

- June darts location, partner answers, rank-2/rank-3 Baton Pass outcomes and stored Proficiency totals;
- generic `Learn Pro Darts`, `Expert Billiards` and `Billiards Magician` metadata;
- the Aug 17 → Oct 14 authored billiards/Technical sequence;
- the Jump Cue, Technical Rank 3, `Billiards Magician`, Massé and Rank 4 relationships.
