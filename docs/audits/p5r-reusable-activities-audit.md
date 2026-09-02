# P5R reusable activities audit — DVDs and retro games

This pass extends the reusable-activity audit beyond movies and point units. It
independently checks the Royal DVD and retro-game catalogs, where the activities
are performed, how they are acquired/unlocked, and the reusable modifiers that
change execution without changing the stored base reward.

## Sources

- GameFAQs, marendarade, **Persona 5 Royal — Social Stats**:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78212/social-stats
- Megaten Database, **Persona 5 Royal — Overworld** (structured Royal activity
  tables; mirrors the same DVD/game mechanics in a compact catalog):
  https://aqiu384.github.io/megaten-database/p5r/overworld.html
- GameFAQs, Bkstunt_31 / Haeravon, **Week 12: July 1–10** — independently
  confirms the second Scarlet DVD inventory wave in the Royal walkthrough:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78256/week-12-july-1st-july-10th
- GameFAQs, Bkstunt_31 / Haeravon, **Week 19: August 22–31** — independent Royal
  walkthrough example using Guy McVer for +3 Proficiency per viewing:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78256/week-19-august-22nd-august-31st
- GameFAQs Royal walkthrough/catalog (FAQ 78629) — independently records the
  seven-game Royal retro catalog, level counts and Royal DVD set:
  https://gamefaqs.gamespot.com/ps4/260936-persona-5-royal/faqs/78629/walkthrough

## DVDs

Royal has 12 rental DVDs. All are watched at the attic TV, can be watched twice,
and grant the listed hidden-point reward on **each** viewing. Dayloop stores the
Royal +3 base per viewing; after reading `The Craft of Cinema`, the route adds the
separate +2 modifier for a +5 viewing total.

The Scarlet membership is a one-time subscription: one borrowed DVD at a time,
with **no set return deadline**. The reusable activity notes now state that
explicitly instead of relying on the walkthrough's source-specific return/cleanup
step.

Inventory waves encoded in the activity notes:

- Initial: `Bubbly Hills, 90210` (Charm), `Wraith` (Kindness), `Guy McVer`
  (Proficiency), `The X Folders` (Guts).
- From 6/1: `Not-so-hot Betsy` (Charm), `ICU` (Kindness), `Jail Break`
  (Proficiency), `The Running Dead` (Guts).
- Royal-only wave from 8/1: `D Housewives` (Charm), `Mouse MD` (Kindness), `31`
  (Guts), `Tee` (Proficiency).

## Retro games

All seven Royal retro games are played on the attic TV. A successful level/clear
uses the pack's +3 hidden-point reward convention. `Game Secrets` makes the
minigames easier; it does **not** increase the stat reward.

- `Star Forneus` — Guts, three stages; bundled with the Retro Game Set from the
  Yongen-Jaya recycling shop.
- `Gambla Goemon` — Charm, two stages; Yongen-Jaya recycling shop from 7/26.
- `Punch Ouch` — Charm, three stages; Akihabara retro game shop.
- `Featherman Seeker` — Knowledge, Royal-only, three stages; Akihabara retro
  game shop.
- `Train of Life` — Kindness, three stages; Akihabara retro game shop.
- `Power Intuition` — Guts, three stages; Akihabara retro game shop.
- `Golfer Sarutahiko` — Proficiency, three stages; Akihabara retro game shop.

For the Akihabara titles, the notes deliberately avoid inventing a precise date
when the independent Royal catalog only establishes the shop/location (or the
Akihabara unlock). Route-specific purchase dates remain walkthrough choices.

## Regression coverage

`P5RActivityCatalogAuditTest` now pins three independent reusable catalogs:

- all 18 Royal theater movies,
- all 12 Royal rental DVDs,
- all 7 Royal retro games.

The DVD/game assertions cover display names, affected stat, hidden-point base,
activity location, inventory/acquisition text, stage/viewing rules, and the
Craft of Cinema / Game Secrets modifier semantics.

## Verification boundary

This closes the reusable DVD/game **catalog + location + unlock/acquisition +
base-reward** portion of issue #12. It does not claim every optional route date
for watching/renting/buying those activities is universal; those remain the
100% completion route's selected schedule unless independently encoded as a
fixed game unlock.
