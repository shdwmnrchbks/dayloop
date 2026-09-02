# P5R reusable activities audit — book sources and unlocks

This pass audits the user-visible **source/unlock metadata** for the reusable
book activities in Persona 5 Royal. Earlier route passes had already normalized
many book stat rewards, but `activities.json.location` mixed where the authored
route read a book with where the player actually obtains it. The Activities UI
now labels that field `Location / source`, and this audit makes the high-risk
Royal sources explicit instead of leaving route reading locations to imply an
incorrect acquisition source.

## Sources

- GameFAQs, Raidramon0, **Persona 5 Royal — Knowledge / Guts / Proficiency /
  Kindness / Charm** (updated July 2026):
  - https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/82334/knowledge
  - https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/82334/guts
  - https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/82334/proficiency
  - https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/82334/kindness
  - https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/82334/charm
- GameFAQs, marendarade, **Persona 5 Royal — Social Stats**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/social-stats
- GameFAQs, marendarade, **Persona 5 Royal — June / July / September**:
  - https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/june
  - https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/july
  - https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/september
- GameFAQs, marendarade, **Persona 5 Royal — Mementos**:
  https://gamefaqs.gamespot.com/pc/370658-persona-5-royal/faqs/78212/mementos
- GameFAQs, marendarade, **Persona 5 Royal — Battle Boosts**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/battle-boosts
- GameFAQs Royal Q&A, **Speed Reader** — confirms that Royal moved Speed Reader
  to the Shujin library and makes it available July 1 instead of requiring the
  old Jinbocho chain:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/answers/576820-do-i-have-to-read-the-great-thief
- Pro Game Guides, **Persona 5 Royal — All Books and Where to Find Them** — used
  as a secondary check for the Trader Sakai `Factorization Guide` exchange and
  special-source books:
  https://progameguides.com/persona/persona-5-royal-all-books-and-where-to-find-them/

## Corrections

### Royal Speed Reader

Royal no longer puts the speed-reading book at the end of Jinbocho's vanilla
book chain. `Speed Reader` is borrowed from the **Shujin school library from
July 1**. The activity source and note now say that explicitly.

### Taiheido / Central Street

The route often reads these books on the train, in class or at home, but those
are reading opportunities, not acquisition sources. The following reusable
entries now retain **Central Street bookstore / Taiheido** in their
`Location / source` metadata:

- `The Art of Charm` — initial stock.
- `Buchiko's Story` — initial stock.
- `Tidying the Heart` — initial stock.
- `Medjed Menace` — stock from May 6.
- `Wise Men's Words` — stock from June 1.
- `Ghost Encounters` — stock from July 1.
- `Social Thought` — found while cleaning Joker's room and also sold by
  Taiheido once Knowledge reaches rank 2.

### Shinjuku activity manuals

Royal's Shinjuku bookstore exposes these one-chapter manuals only after the
relevant activity has been performed. Their notes now record the trigger rather
than presenting them as unconditional stock:

- `Flowerpedia` — after working at the flower shop.
- `The Craft of Cinema` — after watching a movie or DVD; +2 points to later
  movie/DVD viewings.
- `Game Secrets` — after playing a retro game; makes the minigame easier but
  does not raise the stat reward.
- `Learn Pro Darts` — after playing darts.
- `ABCs of Crafting` — after crafting an infiltration tool.
- `Batting Science` — after using the batting cages.
- `Essence of Fishing` — after fishing at Ichigaya.

`Expert Billiards` remains a separate sports-store manual tied to billiards,
and `Billiards Magician` is obtained through the Technical Rank progression.

### Jinbocho / Nagiuri

This was the highest-risk source regression. Before this pass,
`Heroic Revelations` incorrectly claimed `School library / metro`, while
`Call Me Chief` and `Reckless Casanova` omitted their bookstore source.

Royal's Jinbocho progression is now represented as:

- `Master Swordsman` — first stat book, Guts +7 hidden points in Dayloop's
  convention.
- Finishing it exposes the four Royal stat books used by the pack:
  - `Call Me Chief` — Kindness +7.
  - `Reckless Casanova` — Charm +7.
  - `Heroic Revelations` — Knowledge +7.
  - `The Art of Automata` — Proficiency +7.
- `Knowing the Heart` appears after reading all five Jinbocho stat books and
  unlocks additional Technical combinations.

The route may read those books on trains or during class, so those reading
contexts remain alongside the **Jinbocho bookstore** source rather than
replacing it.

### Request / trader books

- `Chinese Sweets` is a reward for clearing the Mementos request
  `Part-time Job, Full-time Hell`; it is no longer mislabeled as a Jinbocho
  bookstore source. It unlocks Chinatown.
- `Factorization Guide` remains the Kichijoji Trader Sakai exchange. The note
  records the route-relevant July 26–30 exchange window and its +2 Knowledge
  study modifier.

## UI clarification

`ActivityDetailScreen` now prints the field as **`Location / source:`**. This is
intentional: the current engine schema has one string that may carry an
acquisition source, a place where the activity is performed, and/or a route
reading context. This wording is more truthful than presenting every value as a
single physical activity location, without introducing a P5R-specific schema.

The Activities list, kind tag and spoiler reveal surface were also migrated to
the generic skin shapes during this audit, and a slash/ink Activities preview
was added for visual review.

## Regression coverage

`P5RActivityCatalogAuditTest` now pins the high-risk book metadata alongside the
existing movie, DVD and retro-game catalogs. It checks:

- Royal Speed Reader source/date and non-Jinbocho semantics,
- Taiheido source retention,
- all seven Shinjuku activity-manual triggers,
- the five Jinbocho stat-book sources and hidden-point rewards,
- `Knowing the Heart`'s all-five prerequisite,
- Mementos/trader special sources,
- billiards manual progression.

## Verification boundary

This closes the high-risk reusable **book source/unlock** portion of issue #12.
The pack still does not claim that every authored day chosen to read a flexible
book is a universal schedule date. Remaining #12 work is concentrated in
flexible route order, conditional/item outcomes, non-reusable one-off activity
facts, and the remaining non-trophy media metadata/anchors.
