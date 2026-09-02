# P5R room decoration audit — 20/20 Confidant sequence

This pass independently checks the completion route's twenty **Confidant/hangout room decorations**. Desk dolls from the Akihabara crane game are a separate collection and are not part of this 20/20 counter.

## Sources

Primary route source:

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** (Steam Community): https://steamcommunity.com/sharedfiles/filedetails/?id=2877808380

Independent Royal references:

- SAMURAI GAMERS, **Room Decorations List**: https://samurai-gamers.com/persona-5/room-decorations-list/
- SAMURAI GAMERS, **Large Shelf Decoration and Desk Decoration List**: https://samurai-gamers.com/persona-5/large-shelf-decoration-and-desk-decoration-list/
- Neoseeker, **Shelf Decorations — Persona 5 Royal**: https://www.neoseeker.com/persona-5-royal/Shelf_Decorations
- GameFAQs / nineline, **Decoration List**, used as a secondary condition/location cross-check: https://gamefaqs.gamespot.com/ps4/835628-persona-5/faqs/77554/decoration-list

The Alyookid source explicitly states that its perfect schedule romances every romanceable girl; choosing otherwise is valid gameplay but can change affinity progression and later availability. That distinction is now stated in the P5R route description instead of being left implicit.

## Audited completion-route sequence

| # | Route date | Decoration | Confidant / location |
|---:|---|---|---|
| 1 | 2016-07-06 | Choco Fountain | Ann / Shibuya |
| 2 | 2016-07-08 | Idol Poster | Ann / Harajuku |
| 3 | 2016-10-15 | Swan Boat | Iwai / Inokashira Park |
| 4 | 2016-10-30 | Balloons | Shinya / Destinyland |
| 5 | 2016-11-01 | Sushi Teacup | Ohya / Ginza |
| 6 | 2016-11-06 | Kumade | Haru / special Asakusa event |
| 7 | 2016-11-12 | King Piece | Hifumi / Jinbocho |
| 8 | 2016-11-16 | Ramen Bowl | Ryuji / Ogikubo |
| 9 | 2016-11-30 | Hero Figure | Futaba / Akihabara |
| 10 | 2016-12-02 | Night Pennant | Kawakami / Seaside Park romance outing |
| 11 | 2016-12-04 | Star Stickers | Yusuke / Ikebukuro |
| 12 | 2016-12-07 | I <3 Tokyo Shirt | Futaba / Asakusa |
| 13 | 2016-12-08 | Skytree Lamp | Chihaya / Skytree romance outing |
| 14 | 2016-12-13 | Featherman Dolls | Ryuji / Nakano |
| 15 | 2016-12-14 | Sea Slug Doll | Yoshizawa / Shinagawa |
| 16 | 2016-12-15 | Gi-Nyant Doll | Makoto / Suidobashi |
| 17 | 2016-12-17 | Hamaya | Yusuke / Meiji Shrine |
| 18 | 2016-12-22 | Nude Statue | Yusuke / Ueno |
| 19 | 2017-01-24 | Giant Spatula | Haru / Tsukishima |
| 20 | 2017-01-29 | Shumai Cushion | Makoto / Chinatown |

The exact dates above are **completion-route dates**, not universal availability dates. Independent references support the item, giver, location and special conditions; the Steam schedule supplies the route-selected day.

## Corrections

### Missing Choco Fountain name

The July 6 route step counted `room decoration 1/20` but only said to give Ann the bouquet. It did not name the actual reward. The step now explicitly says the Shibuya outing awards the **Choco Fountain**.

### Missing Dec 8 Skytree Lamp block

The largest omission was between decorations 12 and 14. Dayloop previously jumped from:

- Dec 7 — I <3 Tokyo Shirt (12/20)
- Dec 13 — Featherman Dolls (14/20)

with no 13/20 entry.

Alyookid's own Dec 8 source block contains the missing evening sequence after the cruise-ship Palace run:

1. Kawakami massage,
2. buy the Glass Vase,
3. hang out with Chihaya at Skytree,
4. receive **Skytree Lamp (13/20)**,
5. give Chihaya the Glass Vase,
6. before sleep, choose **I want to keep our promise** for the optional Akechi/Royal event chain.

That block is now restored to the walkthrough. The Akechi choice was already separately represented as a Dec 8 missable reminder; restoring it to the authored day makes the walkthrough complete as well as the deadline surface.

### Route romance policy

The route description now states that the **source route chooses romance at romanceable Confidant branches**. This matters because at least Night Pennant and Skytree Lamp are tied to romance outings in Royal. The app still tells users at rank 9 that romance is a choice; it no longer implies romance is a universal game requirement, while also being clear that deviating to friendship can change the source schedule and decoration availability.

## Naming notes

Secondary references vary slightly on several English labels:

- `Sushi Teacup` vs `Sushi Mug`
- `Skytree Lamp` vs older/base-game-style `Sky Tower Lamp`
- `Featherman Doll(s)`

Dayloop retains the wording used by its Royal route/source where it is unambiguous, while the regression test pins the app's user-visible identity so future edits cannot silently rename an item without another audit.

## Regression coverage

`P5RRoomDecorationAuditTest` requires:

- exactly twenty numbered `room decoration n/20` steps,
- every sequence number 1 through 20 to resolve on its audited route date,
- the expected decoration name to appear in that step,
- the route description to keep its source-route romance policy explicit,
- the romance-dependent Night Pennant and restored Skytree Lamp entries to remain present.

## Verification boundary

This closes the **20 Confidant room-decoration** one-off collection for issue #12. It does not cover the separate eight Akihabara crane-game desk dolls; those remain a distinct audit cluster.
