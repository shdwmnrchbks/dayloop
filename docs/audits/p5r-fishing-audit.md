# P5R fishing / rare-fish audit

Scope: the completion-route fishing setup from December 2016 through the Royal-only Ichigaya Kingpin catch on January 16, 2017.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — source completion route for the Dec 5/13/16 fishing preparation, Guardian catch and Jan 16 snow-warning Kingpin catch.
- GameFAQs Royal fishing references — independent check for Ichigaya fishing, Proficiency growth, fishing-point rewards, Suspicious Boilie and the Hi-Tech Rod prize-shop inventory.
- Megami Tensei Wiki, **Fishing**, **Suspicious Boile** and **Hi-Tech Rod** — independent item/location check: Suspicious Boilie targets the Guardian/Kingpin; the Kingpin requires the Guardian to have been caught first; snow behaves like rain for immediate rare-fish appearance; and the Royal Hi-Tech Rod is sold at the Shibuya Underground Mall sports shop after fishing once as well as exchangeable at Ichigaya.
- GameFAQs community fishing reports — secondary check that snowy third-semester sessions can surface the Guardian/Kingpin immediately and that Suspicious Boilie is the required rare-fish bait.

## Verified route sequence

- **Dec 5:** first authored fishing session at Ichigaya; the route gains +2 hidden Proficiency points.
- **Dec 6 / Dec 9:** buy and read `Essence of Fishing`; the reusable activity correctly records the Shinjuku bookstore source, unlock-after-fishing gate and Third Eye / Prize Tag utility.
- **Dec 13:** buy the Hi-Tech Rod at the Underground Mall sports shop, fish using Third Eye for Prize Tags, and stock a Suspicious Boilie for the rare-fish attempts.
- **Dec 16:** lead with Suspicious Boilie, identify the gold-glowing rare fish with Third Eye and catch the **Ichigaya Guardian**, reloading on a miss.
- **Jan 16:** during the route's snow warning, use Suspicious Boilie for the **Ichigaya Kingpin** on the first rare-fish appearance, reloading on a miss.

The Guardian-before-Kingpin ordering matters: Royal's Kingpin is not merely a second name for the Guardian. The Kingpin becomes eligible only after the Guardian has already been caught, and the completion route satisfies that prerequisite on Dec 16.

## Source caution

Community documentation is less uniform about every general Guardian/Kingpin spawn condition outside the route's specific weather setup. This audit therefore pins the facts Dayloop actually presents — the Dec 16 Guardian prerequisite, Suspicious Boilie, the Jan 16 snow-warning first appearance and the reload-based completion-route strategy — rather than claiming a universal spawn formula for every weather/time combination.

## Regression coverage

`P5RFishingAuditTest` pins:

1. the Ichigaya location and +2 hidden Proficiency route reward,
2. the `Essence of Fishing` source/unlock/Third Eye metadata,
3. the Underground Mall Hi-Tech Rod purchase,
4. the Dec 16 Guardian catch with Suspicious Boilie and reload semantics,
5. the Jan 16 snow-warning Kingpin catch with Suspicious Boilie and reload semantics, and
6. Guardian-before-Kingpin route ordering.
