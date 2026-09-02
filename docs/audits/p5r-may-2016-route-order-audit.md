# P5R May 2016 route-order audit

Scope: reproduce the authored May completion-route state against Persona 5 Royal's fixed story/exam slots and the route's primary schedule, while allowing genuinely flexible Confidant/activity choices to remain Dayloop-specific. This continues issue #12's month-level route-order verification after April.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — primary source-route facts for May 1-31, including the May crosswords, the May 15 lockpick session, Bio Nutrients Set / Mega Fertilizer chain, first-exam top-10 state, Tough Belt, and Museum-Palace route order.
- GameFAQs Royal May schedules (`faq/78212`, `faq/79923`, `faq/78256`) — independent checks for story locks, the four-day May midterm block, the forced May 15 exhibit trip, usable LeBlanc evenings, `Gallery`, and alternate legal activity orderings.
- Samurai Gamers and RPG Site Royal crossword lists — independent date/name checks for **May 10 Malaise, May 18 Gallery, May 26 Pollenosis and May 31 Japanese**. Crosswords do not advance time and award Knowledge.
- RPG Site Royal exam guide — first May midterms require Knowledge rank 3 for top 10 when exam answers are correct.
- Royal exam/item references including Japanese strategy tables — May's high-placement Sojiro reward is **Tough Belt**, available after results; Royal's Tough Belt grants Defense Master.
- Samurai Gamers Royal Madarame Palace guide — May 20 is a scripted Palace combat/infiltration phase with intermediate enemies/mini-bosses, not the final Madarame boss.

## Corrections made

### Story and exam day kinds

The route previously rendered several fixed-story dates as ordinary school/free days. Day kinds now distinguish the constrained calendar correctly:

- **May 2-5:** `story` — daytime is story-locked; the route still records the legal confined LeBlanc evening actions.
- **May 11-14:** `exam` — May 14 is the fourth/final midterm day, not a generic free Saturday.
- **May 15:** `story` — the museum exhibit trip is forced; only the confined evening is available for the route's home actions.

May 16/17 remain `school` because their school/story sequences coexist with the authored class/evening entries; the audit does not flatten every mixed school day into `story`.

### Knowledge rank 3 before the first exams

The primary route contains the no-time **May 10 `Malaise` crossword**, which had been dropped during import. Restoring its **+2 hidden Knowledge points** makes the authored threshold transition coherent:

- corrected April ends with **47** authored Knowledge points;
- before the May 10 evening study, the route has **79**;
- Ryuji's +5 study session raises that to **84**, crossing Royal's **82-point Knowledge rank-3 threshold** during that session.

That makes top 10 on the May midterms possible with the pack's correct exam answers. The stale May 7 text claiming `Medjed Menace` reaches Knowledge rank 2 was removed; rank 2 was already reached in April.

### Remaining May no-time crosswords

Two later May puzzles were also missing from the imported route even though they consume no time:

- **May 18 — `Gallery`: Knowledge +2**
- **May 31 — `Japanese`: Knowledge +2**

Together with Golden, Malaise and Pollenosis, these restore the complete May Royal crossword sequence. The route ends May at **110 authored Knowledge points**. The existing June route then contributes exactly 16 more through Jun 13, explaining its **126-point Knowledge rank-4** marker instead of leaving that rank-up unsupported.

### May 15 lockpick session

The primary route uses the confined May 15 evening to craft lock picks. The missing action is restored:

- save first;
- craft 3 lock picks;
- reload for the bonus result;
- finish with **5 lock picks total**;
- **Proficiency +3** hidden points.

This also explains the May 10 reminder to have 3 Tin Clasps and 3 Silk Yarn ready.

### Exam result and Tough Belt

Once the missing Knowledge sources are restored, the route no longer represents an above-average result:

- May 20 is explicitly **top 10**;
- exam-result Charm is **+5 hidden points** rather than +3;
- the incorrect `Sojiro gives a coffee for SP` wording is replaced with the actual route reward: **talk to Sojiro for the Tough Belt**.

The Tough Belt is the Royal accessory reward for qualifying performance on the first exams; the route does not claim first place.

### Bio Nutrients Set -> Mega Fertilizer

May 8 now explicitly says to **buy** the Bio Nutrients Set and notes that it includes Mega Fertilizer. The primary route's missing May 21 plant action is restored:

- use that Mega Fertilizer on the attic plant;
- **Kindness +5 hidden points**.

### Museum Palace wording/order

May 20's old `boss fight` wording could be read as the final Madarame boss. It now says this is the **third museum infiltration / scripted Palace combat sequence, not the final Madarame boss**.

Dayloop's authored Museum sequence remains:

1. May 16 — first infiltration;
2. May 19 — second infiltration / blockade;
3. May 20 — third scripted combat phase;
4. May 23 — fourth infiltration, secure the Treasure route;
5. May 24 — Calling Card;
6. May 25 — steal the Treasure / final heist.

Other valid Royal guides group the Palace's forced phases differently; the audit preserves Dayloop's legal route numbering and only removes misleading final-boss wording.

## Guts / Temperance state

The April correction for pre-Kamoshida school-library Guts gains matters downstream. With those values plus Death/Aojiru gains, the authored route reaches exactly **38 Guts points by May 8**, Royal Guts rank 3. Therefore:

- May 27 Operation Maidwatch is state-legal;
- May 28 Temperance rank 1 satisfies the Confidant's Guts-rank-3 gate.

This is a route-state proof, not a claim that May 28 is Kawakami's universal first availability.

## Regression coverage

`P5RMayRouteOrderAuditTest` pins:

1. May 2-5 story-constrained day kinds;
2. the complete May 11-14 exam block and May 15 story day;
3. `Malaise` +2 and the **79 -> 84** Knowledge transition across May 10's Ryuji study, crossing rank 3 at 82;
4. removal of the stale May 7 Knowledge-rank-2 claim;
5. purchase of the May 8 Bio Nutrients Set and its Mega Fertilizer dependency;
6. May 15 lockpick crafting / +3 Proficiency / five-lockpick total;
7. **Gallery** and **Japanese** +2 no-time crosswords and the **110 Knowledge** May month-end state;
8. May 20 top-10 +5 Charm and Tough Belt;
9. May 21 Mega Fertilizer +5 Kindness;
10. exact **38 Guts by May 8** before Maidwatch/Temperance; and
11. the May 16 -> 19 -> 20 -> 23 -> 24 -> 25 Museum route / Calling Card / heist chronology.

April and May now have dedicated independent route-order/state reproduction passes. June onward remains open under issue #12.
