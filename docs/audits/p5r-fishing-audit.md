# P5R fishing / rare-fish audit

Scope: Royal's Ichigaya unlock / `Angler's Debut` timing plus the completion-route fishing setup through the Guardian and Royal-only Kingpin catches.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — source completion route for the Jul 3 Ryuji invitation and Dec/Jan fishing cleanup.
- GameFAQs Royal walkthroughs — multiple Royal-specific schedules place the Ryuji/Kawakami Ichigaya hangout on **Jul 3** and mark Ichigaya unlocked there.
- Samurai Gamers Royal July walkthrough / hangout-spot guide — independently places the Ryuji fishing invitation and Ichigaya unlock on **Jul 3**.
- TrueAchievements / other trophy guides — independently document the important trophy semantic that the introductory invitation visit itself does **not** count; the player must return to Ichigaya and actually catch a fish. Some of these pages still print **Jul 6**, which conflicts with Royal-specific calendar sources and appears to retain older/cross-version timing.
- Megami Tensei Wiki fishing/item references — independent checks for Suspicious Boilie, Guardian/Kingpin ordering, snow/rain rare-fish behavior and Hi-Tech Rod acquisition.

## Ichigaya / Angler's Debut timing

Royal-specific calendar sources consistently place the fishing hangout on **2016-07-03**. Dayloop's route already records `Accept Ryuji's invitation (2 of 10)` on that date.

The invitation unlocks Ichigaya, but the event is not a trophy-valid independent fishing session. `Angler's Debut` requires returning to the pond and catching a fish. Therefore:

- **Jul 3:** Ichigaya unlock / introductory Ryuji hangout.
- **Jul 4:** first possible independent visit and first possible `Angler's Debut` date.
- **Dec 5:** first independent fishing session chosen by Dayloop's authored completion route.

The trophy catalog now uses `availableFrom: 2016-07-04`; it remains manual with no `expectedBy`. The December route date is a route choice, not game availability.

## Verified completion-route sequence

- **Dec 5:** first authored independent fishing session at Ichigaya; +2 hidden Proficiency.
- **Dec 6 / Dec 9:** buy and read `Essence of Fishing`; the reusable activity records the Shinjuku bookstore source, after-fishing gate, Third Eye and Prize Tag utility.
- **Dec 13:** buy the Hi-Tech Rod at the Underground Mall sports shop, fish using Third Eye for Prize Tags, and stock Suspicious Boilie.
- **Dec 16:** use Suspicious Boilie, identify the gold-glowing rare fish with Third Eye and catch the **Ichigaya Guardian**, reloading on a miss.
- **Jan 16:** during the route's snow warning, use Suspicious Boilie for the **Ichigaya Kingpin** on the first rare-fish appearance, reloading on a miss.

The Guardian-before-Kingpin ordering matters: Royal's Kingpin becomes eligible only after the Guardian has already been caught, and the completion route satisfies that prerequisite on Dec 16.

## Source caution

Community documentation is less uniform about every general Guardian/Kingpin spawn condition outside the route's weather setup. This audit pins only the facts Dayloop actually presents instead of claiming a universal spawn formula.

The Jul 3 vs Jul 6 unlock discrepancy is also preserved explicitly: Royal-specific walkthrough/calendar sources support Jul 3; several trophy pages retain Jul 6. The trophy's `availableFrom` is based on the Royal-specific Jul 3 unlock plus the independently supported requirement to return on a later visit.

## Regression coverage

`P5RFishingAuditTest` pins:

1. Jul 3 Ryuji invitation / Ichigaya unlock vs Jul 4 first possible `Angler's Debut`,
2. the completion route's later Dec 5 first independent fishing session and +2 hidden Proficiency,
3. `Essence of Fishing` source/unlock/Third Eye metadata,
4. the Underground Mall Hi-Tech Rod purchase,
5. the Dec 16 Guardian catch with Suspicious Boilie and reload semantics,
6. the Jan 16 snow-warning Kingpin catch with Suspicious Boilie and reload semantics, and
7. Guardian-before-Kingpin route ordering.
