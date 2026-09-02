# P5R media metadata audit — source-specific guide graphics

This pass closes the high-risk **non-trophy media metadata** portion of the P5R audit by separating game facts from presentation metadata imported with the private guide-image archive.

## Source and policy

`docs/sources.md` records Alyookid's **Persona 5 The Royal 100% Achievements + Perfect Schedule** as the source for the curated P5R guide graphics. The source policy explicitly distinguishes completion-route/game facts from graphics and requires route-selected or guide-placement data not to be presented as universal game availability.

The P5R media manifest contains 53 items total:

- **50** achievement/trophy images from the imported guide archive.
- **3** non-trophy guide graphics:
  - `p5r.media.month-opener`
  - `p5r.media.marker-schedule`
  - `p5r.media.marker-deadline`

The three non-trophy records are not gameplay unlocks, deadlines, locations, or rewards. Their `months` fields mean only **where the imported guide uses that reusable graphic**.

## User-visible semantics

The manifest captions already identify all three non-trophy items as guide graphics (for example, a graphic that opens a month's schedule or a marker used by the guide on a schedule/deadline section). That source-specific wording is intentional and is now regression-pinned.

Achievement artwork has a separate safeguard: once a pack ships first-class `achievements.json`, Media UI labels trophy-art month/day anchors as **guide placement months/days** so those imported anchors cannot be mistaken for trophy unlock timing.

The ordinary month/section guide graphics keep neutral placement labels in the media gallery because their captions already state that they are guide presentation assets rather than game facts.

## Regression coverage

`P5RMediaCatalogAuditTest` pins the provenance boundary:

- exactly 53 P5R media records,
- exactly 50 trophy images,
- exactly the three known non-trophy guide graphics,
- one `month` graphic and two `section` markers,
- every non-trophy caption must continue to identify the item as guide/source-specific presentation,
- every guide month anchor must stay inside authored P5R walkthrough coverage,
- reusable guide chrome must not acquire day or Confidant anchors that could be read as gameplay facts.

`P5RAchievementCatalogAuditTest` separately pins that trophy-art month anchors render as **guide placement**, not achievement availability.

## Verification boundary

This treats the three non-trophy media records as **source-specific presentation metadata**, which satisfies issue #12's requirement that a user-visible datum either receive independent verification or be explicitly designated source-specific.

No claim is made that a particular guide graphic must appear in every gameplay month. The month lists are an imported-guide layout fact, not a Persona 5 Royal mechanic.

Remaining issue #12 work is therefore concentrated in flexible route order / conditional state and one-off gameplay facts that are still represented directly to the user rather than in the reusable media catalog.
