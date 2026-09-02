# P5R completion-target audit — full book catalog

Scope: the January 25 completion-route target currently shown in Dayloop as the point where the route finishes every Royal book.

## Sources checked

- Persona 5 Royal comprehensive Japanese strategy wiki — independently lists **46 total books** in Royal and states that the Thieves' Den `Bookworm` award requires completing **40 distinct books** from that catalog.
- GameFAQs, **Persona 5 Royal Thieves' Den Awards FAQ** — independently states `Bookworm` requires 40 books read and completed.
- GameFAQs Royal walkthrough — independently shows the `Bookworm` award being obtained at 40 completed books during November, well before this completion route's January full-catalog cleanup.

## Verified distinction

Two different completion concepts were being easy to conflate:

1. **Full Royal book catalog:** 46 books exist in Royal. The source completion route chooses to finish all of them, with its final cleanup on January 25.
2. **Thieves' Den Bookworm award:** only 40 distinct completed books are required.

January 25 is therefore not a universal game deadline and 46 is not the Bookworm threshold. It is a route-specific full-catalog goal.

## Dayloop correction

The existing `routeTarget` kind was already correct, so no deadline semantics or date changed. The user-visible label now says:

> Completion-route target — finish the full 46-book catalog (Bookworm award requires 40)

`P5RCompletionTargetAuditTest` pins all three important distinctions:

- the entry remains `routeTarget`, not `missable`;
- January 25 remains this route's selected full-catalog completion date; and
- the UI must distinguish the 46-book catalog from the 40-book Bookworm award requirement.

The January 25 walkthrough step is also pinned to the route's final `Chinese Sweets` reading and its explicit all-city-location completion wording.
