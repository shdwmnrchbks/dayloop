# P3R data audit ledger — September 2026

This ledger records the factual and semantic audit of the Persona 3 Reload pack.
The P3R walkthrough is an authored completion route; its chosen dates must not be
silently promoted into universal game availability, unlock, or deadline facts.
The audit follows the same route-vs-game-fact discipline used by the P5R audit.

## Status

**Social Link, answer, exam, rescue-deadline, timed Elizabeth-request, early
social-stat/Tartarus, epilogue-calendar, and achievement-catalog baselines are
corrected and regression-protected; the broader month-by-month factual audit
remains in progress.**

The current stable catalog contains **22 Social Links, 37 deadlines, 53 answer
sheets, 48 achievements, and zero reusable activities**. The route now explicitly
satisfies all eight missing-person rescue cutoffs and all fourteen explicitly
timed Elizabeth requests without replacing the authored Social Link or
time-consuming activity choices.

## Source roles

- **HayateButler, Persona 3 Reload 100% Perfect Schedule Guide** — primary source
  for the authored route order and completion-plan choices already represented
  by the pack. Route-selected dates are route facts, not universal availability.
- **RPG Site P3R guides** — independent checks for Social Links, missing persons,
  Elizabeth requests, and activity/stat facts.
- **Game8 / Push Square / GameFAQs / PlayStationTrophies / PowerPyx** — independent
  cross-checks for class/exam answers, request prerequisites/deadlines, automatic
  Social Links, Tartarus timing, epilogue timing, and achievement mechanics.
- **Platform trophy/achievement text plus cross-checked trophy guides** — canonical
  P3R base-game achievement titles/descriptions and mechanic-specific unlock facts.
- **megaten-database P3R data** — structured cross-check for school answers,
  social-stat points, and automatic Social Link mechanics.

## Baseline findings

### P3R-AUD-001 — Missing route identity — FIXED

P3R now declares `standard` as **100% Completion Route** and explicitly states that
its authored dates are not universal availability, unlock, or deadline facts.
`contentVersion` was bumped from 1 to 2.

### P3R-AUD-002 — Social Link identities corrupted — FIXED

The original import mapped Magician to Junpei and Moon to Kenji and repeated those
swaps in route prose. Canonical identities now include Magician/Kenji Tomochika,
Moon/Nozomi Suemitsu, Hanged-Man/Maiko Oohashi, Temperance/Bebe,
Devil/President Tanaka, Tower/Mutatsu, Fortune/Keisuke Hiraga,
Star/Mamoru Hayase, and Sun/Akinari Kamiki. Regression coverage scans the full
walkthrough and keeps legitimate Junpei/Kenji Linked Episode or story references
separate from Social Link rank actions.

### P3R-AUD-003 — Route dates overloaded into `availableFrom` — FIXED

Ordinary player-selected Social Link dates now use `scheduledFor`. `availableFrom`
is reserved for independently verified fixed automatic story timing. Judgment's
post-unlock ranks are floor-driven rather than assigned synthetic calendar dates.

### P3R-AUD-004 — Answer catalog stored option numbers — FIXED

All **53** answer sheets now contain answer text instead of numeric menu positions.
Exam sheets resolve to their exam deadline through `deadlineRef`.

### P3R-AUD-005 — No Activities catalog — OPEN COVERAGE DEBT

P3R still ships no `activities.json`. This remains intentional until reusable
activity effects and point values are independently audited; embedded route values
must not be promoted into base data merely because they parse successfully.

### P3R-AUD-006 — Stable-ID baseline was a seed subset — FIXED

`pack-ids.baseline.json` now pins **22 Social Links, 37 deadlines, 53 answer
sheets**, and zero activities. The deadline baseline includes exams, route targets,
missing-person cutoffs, and the fourteen verified timed Elizabeth requests.

### P3R-AUD-007 — Ordinary Social Link rank ladders were incomplete — FIXED

Importer omissions were restored, including Devil ranks 5/10, Tower rank 4,
Lovers ranks 2/3, and the dropped July 27–31 route block. Every non-automatic
Social Link is regression-pinned to a continuous 1–10 ladder.

### P3R-AUD-008 — Automatic Social Links were modeled as estimates — FIXED

- Fool preserves the game's real ranks 1,2,3,4,5,6,7,9,10 and fixed story dates.
- Death preserves real ranks 1,3,5,6,8,10 and the game's intentional skips.
- Judgment rank 1 unlocks December 31; ranks 2–10 advance at Adamah 227F, 230F,
  236F, 241F, 246F, 247F, 253F, 254F, and 255F.

### P3R-AUD-009 — Exam windows and top-class requirements were wrong — FIXED

The audited exam windows/requirements are:

- **May 18–23** — Academics rank **3** + all player answers correct.
- **July 14–18** — Academics rank **4** + all player answers correct.
- **October 13–17** — Academics rank **5** + all player answers correct.
- **December 14–19** — Academics rank **6** + all player answers correct.

Final Saturdays are exam mornings while preserving available after-school time.

### P3R-AUD-010 — April prep targets were mislabeled as hard deadlines — FIXED

The April 20–26 first-Thebel block and April 25 discounted Muscle Drink purchase
are completion-route targets, not universal missable cutoffs. Their stable IDs are
preserved as `routeTarget` entries.

### P3R-AUD-011 — Missing-person rescue cutoffs were absent — FIXED

The pack now exposes the last actionable rescue dates:

- **July 6** — 50F, 56F, 64F.
- **August 5** — 79F, 84F.
- **September 4** — 101F, 109F, 114F.
- **October 3** — 120F, 135F, 140F; includes Bunkichi.
- **November 2** — 146F, 159F, 165F; includes Maiko.
- **December 1** — 177F, 196F.
- **December 30** — 209F, 221F.
- **January 30** — 232F, 250F.

September 4 is intentionally the UI's last actionable rescue date even though
some references phrase the story cutoff as September 5, when the mandatory
full-moon operation removes normal Tartarus access. The authored route is also
pinned to safe batch-clear Tartarus visits on June 27, August 3, September 4,
October 1, November 2, November 29, December 30, and January 15.

### P3R-AUD-012 — April social-stat point units — FIXED

The internal raw-point scale is pinned as: correct classroom answer +2 Charm,
stay-awake +2 Academics, Nurse medicine +2 Courage, social-stat movie/arcade +4,
and Mystery Burger +3 Courage. April 26 reaches the 15-point Courage Rank 2
threshold exactly. An interim +2 Mystery Burger edit was reverted after it was
shown to mix display-note/pip counts with raw points.

### P3R-AUD-013 — April Thebel/Elizabeth dependency chain was incomplete — FIXED

The April route now explicitly reaches Thebel's 22F border and takes Old Document
01 for Request #2, preserves the Odd Morsel needed for Moon, identifies the first
Thebel gatekeeper weaknesses, and keeps valid Hermit/Hanged-Man Persona fusion
prep. The April 25 pharmacy wording now reflects the recurring Saturday discount
rather than a fictional one-day-only sale.

### P3R-AUD-014 — May social-stat/request representation was incomplete — FIXED BASELINE

May's embedded software, arcade, food, group-study, exam-result, and nurse gains
were restored in the same raw-point scale. Regression coverage reconciles the
route's advertised rank checkpoints, including Academics Rank 3 before exams and
Courage Rank 4 on May 25. Request #9 now explains the twelve-unique-drink vending
route rather than merely saying to complete it.

This does not mean every May gameplay fact is fully audited; it means the point
and early-request baseline no longer contains the known importer omissions.

### P3R-AUD-015 — Timed Elizabeth requests were absent from deadlines and route — FIXED

The original pack did not expose the game's explicitly timed Elizabeth requests,
and the walkthrough omitted multiple accept-before-item and hand-in steps. The
catalog now has **14** `request` deadlines:

- #12 Pine Resin / #13 Handheld Console — **June 6**.
- #27 Fencing Epee / #28 Amateur Protein / #29 Fashionable Item — **July 5**.
- #43 Christmas Star / #44 Ocean Souvenir — **August 4**.
- #58 Straw Millionaire — **August 31**.
- #68 Fruit Knife / #69 Machine Oil — **October 2**.
- #76 Glasses Wipe — **November 1**.
- #94 Furry Friend Food / #95 Featherman R Figure — **November 30**.
- #97 Christmas Present — **December 25**.

The completion route now explicitly closes each chain before its cutoff:

- May 10 accepts and completes #12/#13 through Yukari and Junpei.
- June 14 completes #27/#28 and accepts #29; June 27 obtains Black Quartz and
  June 28 converts it into the fashionable item and reports #29.
- July 9 accepts #43/#44 and completes #43 through Fuuka; the accepted #44 is
  collected during Yakushima and reported July 23.
- August 8 records the full #58 barter chain through the Cat Ear Headband.
- September 10 completes #68 then #69 through Shinjiro and Aigis.
- October 6 accepts #76 before the October 7 Ikutsuki Glasses Wipe handoff.
- November 6 completes #94 then #95 through Koromaru and Ken.
- December 4 accepts and completes #97 through the Eccentric Man exchange.

These interactions were added around the existing authored route rather than
replacing Social Link or other time-consuming choices. Regression coverage checks
both the deadline catalog and all fourteen route handoffs, including ordering
where one request unlocks another or acceptance is required before the NPC item
becomes available.

### P3R-AUD-016 — January ending to March epilogue transition — FIXED

The pack's calendar correctly treats **February 1 through March 3** as the skipped
post-Promised-Day span while keeping **March 4** and **March 5** inside the active
calendar. March 4 remains player-controlled epilogue cleanup: the route sends the
player through the school/city and dorm before bed. March 5 is the story-only
Graduation Day ending.

Regression coverage now pins the complete 31-date non-playable span, requires the
calendar to end on March 5, rejects March 4/5 from `nonPlayableDates`, and requires
both epilogue walkthrough entries to remain present.

### P3R-AUD-017 — Achievement catalog mixed paraphrases and route dates with game facts — FIXED BASELINE

The pack ships **48** base-game Journey achievements. Their descriptions are now
normalized to the canonical platform achievement wording instead of guide-style
paraphrases. Route tracking remains separate from actual mechanic availability.

Specific semantic corrections include:

- **Top of the Class** — canonical condition is `Aced an exam.`; this completion
  route satisfies the first-exam requirements and records the unlock on the
  **May 25** results day rather than delaying it to July 24.
- **Reaper Reaped** — the Reaper becomes available with the June Tartarus
  progression, so `availableFrom` is **June 13**; January remains this route's
  dedicated preparation/expected completion period rather than a fake unlock.
- **The Horror of the Shade** — random Dark Zones relevant to the achievement are
  modeled from the Tziah tutorial point on **October 1**; the scripted tutorial
  Dark Zone itself does not count.
- **Eagle Eye** — the lingering `Twinkling Fragments` importer typo was corrected
  to **Twilight Fragments** in both the May 25 walkthrough step and semantic event
  anchor.

Achievement regression coverage still preserves the existing structural checks:
48 unique definitions, valid tracking types, resolvable event anchors, explicit
choice/checklist/confirmation semantics, the nine-member Theurgy checklist, the
shared good-ending choice state, Social Link completion anchors, gardening
confirmation, and the >¥50,000 part-time-job counter. It now also pins the audited
achievement descriptions and availability/route dates above.

## Regression rules for P3R

1. A completion-route date is not `availableFrom` unless independently fixed.
2. Route-selected Social Link dates use `scheduledFor`.
3. Social Links and Linked Episodes remain separate systems.
4. Ordinary Social Links keep complete 1–10 ladders; automatic links keep real skips.
5. Floor-driven progression is not given synthetic dates.
6. Answer sheets contain useful text, never merely menu positions.
7. Exam windows cover the full exam period and link to answer sheets.
8. Route optimization targets are not mislabeled as hard deadlines.
9. Missing-person UI deadlines use the last actionable rescue date.
10. Every explicitly timed Elizabeth request has both a catalog deadline and a
    route handoff before that deadline.
11. If a request requires acceptance before an NPC item interaction, the route
    must show that acceptance before the handoff.
12. Social-stat data uses one raw/internal point scale and does not mix pip counts.
13. Achievement descriptions use canonical platform wording; route completion
    checkpoints must not masquerade as mechanic availability dates.
14. The post-January calendar skip ends on March 3; March 4 player control and
    March 5 Graduation Day remain represented.
15. Reusable activity effects must be verified before creating `activities.json`.
16. Structural validation proves schema/reference integrity, not gameplay facts.
17. Alternate valid guide dates do not override this authored route merely for
    stylistic consistency.
18. Fixed story dates, unlock gates, deadlines, and availability windows require
    independent support beyond the primary completion schedule.

## Next passes

- Continue the June → January month-by-month gameplay/stat/activity audit beyond
  the timed-request corrections already landed.
- Re-check full-moon/story labels as their route months are audited.
- Revisit achievement availability dates only when a true mechanic boundary is
  independently established; do not substitute this route's cleanup date.
- Build P3R activities only after the underlying reusable point/effect audit is stable.
