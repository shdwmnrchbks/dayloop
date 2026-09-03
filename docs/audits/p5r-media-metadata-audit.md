# P5R media metadata audit — source-specific guide graphics

This pass closes the high-risk **non-trophy media metadata** portion of the P5R audit by separating game facts from presentation metadata imported with the private guide-image archive.

## Source and policy

`docs/sources.md` records Alyookid's **Persona 5 The Royal 100% Achievements + Perfect Schedule** as the source for the curated P5R guide graphics. The source policy explicitly distinguishes completion-route/game facts from graphics and requires route-selected or guide-placement data not to be presented as universal game availability.

The P5R media manifest contains 51 guide/trophy items total:

- **50** achievement/trophy images from the imported guide archive.
- **1** non-trophy guide graphic: `p5r.media.month-opener`.

The remaining non-trophy record is not a gameplay unlock, deadline, location, or reward. Its `months` field means only **where the imported guide uses that reusable graphic**. The two former section-marker records and PNGs were removed in rc6 after their Calendar use was retired.

## User-visible semantics

The manifest caption identifies the remaining non-trophy item as a guide graphic. That source-specific wording is intentional and regression-pinned.

Achievement artwork has a separate safeguard: once a pack ships first-class `achievements.json`, Media UI labels trophy-art month/day anchors as **guide placement months/days** so those imported anchors cannot be mistaken for trophy unlock timing.

The month guide graphic keeps a neutral placement label in the media gallery because its caption already states that it is guide presentation art rather than a game fact.

## Regression coverage

`P5RMediaCatalogAuditTest` pins the provenance boundary:

- exactly 51 audited guide/trophy media records, plus 22 supplied Confidant backgrounds (73 total),
- exactly 50 trophy images,
- exactly one known non-trophy guide graphic,
- one `month` graphic and zero P5R `section` markers,
- every non-trophy caption must continue to identify the item as guide/source-specific presentation,
- every guide month anchor must stay inside authored P5R walkthrough coverage,
- reusable guide chrome must not acquire day or Confidant anchors that could be read as gameplay facts.

`P5RAchievementCatalogAuditTest` separately pins that trophy-art month anchors render as **guide placement**, not achievement availability.

## Verification boundary

This treats the remaining non-trophy media record as **source-specific presentation metadata**, which satisfies issue #12's requirement that a user-visible datum either receive independent verification or be explicitly designated source-specific.

No claim is made that a particular guide graphic must appear in every gameplay month. The month lists are an imported-guide layout fact, not a Persona 5 Royal mechanic.

The later month-by-month route reproduction, conditional-route audit, one-off activity audits and trophy-availability cleanup completed the remaining #12 verification surfaces. This media pass is therefore a closed audit category rather than a source of outstanding work.
