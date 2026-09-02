# P5R activity catalog audit — Royal movies

This pass verifies the reusable Persona 5 Royal movie catalog independently of
the completion route's chosen viewing dates. It is intentionally narrower than
the month-by-month walkthrough audit: movie identity, theater, first-viewing
base reward and known Confidant invitation metadata are universal activity
facts; the date on which this route chooses to watch one is route-specific.

## Primary structured reference

- megaten-database, **Persona 5 Royal — Overworld**:
  https://aqiu384.github.io/megaten-database/p5r/overworld

The structured Royal data groups the catalog into three theaters and records a
+5 hidden-point first-viewing base for every film. `The Craft of Cinema` adds a
separate +2 when active, which remains a route-specific total in walkthrough
steps rather than being folded into `activities.json`.

## Verified catalog

### Shibuya / Central Street

- `Tanktop Millionaire` — Guts +5
- `The Cake Knight Rises` — Kindness +5; 5/29 Ryuji invitation
- `Love Possibly` — Charm +5; 7/17 Ann invitation
- `Le Miserable` — Kindness +5; 8/5 Yusuke invitation
- `Admission Impossible` — Proficiency +5
- `Clean Hard` — Kindness +5
- `Finding Beemo` — Charm +5

### Shinjuku

- `Like a Dragon` — Guts +5; 7/28 Makoto invitation
- `Saraemon` — Knowledge +5
- `Duh-vengers` — Kindness +5
- `Pach-Saw` — Guts +5; 11/13 Haru invitation
- `Bite Club` — Guts +5

### Yongen-Jaya

The Yongen-Jaya theater itself becomes available after completing `Who's
Muscling in Yongen-Jaya?`.

- `Showtime Redemption` — Charm +5
- `Back to the Ninja` — Knowledge +5; 10/2 Futaba invitation
- `Over the Pigeon's Nest` — Kindness +5
- `Merry Christmess` — Guts +5
- `March of the Lambs` — Proficiency +5
- `The Goodfather` — Kindness +5

## Source-name conflict

Some older secondary tables use alternate English labels such as `Admission
Possible`, `Soraemon`, `March of the Sheeple`, `Fighting Friends`, or `The Good
Father`. The structured Royal reference above uses the labels currently shipped
by Dayloop (`Admission Impossible`, `Saraemon`, `March of the Lambs`, `Finding
Beemo`, `The Goodfather`). Because the current catalog is internally consistent
with that higher-structure reference, this audit does **not** rename immutable
activity ids or user-visible labels merely to follow a conflicting table.

If a later primary-source extraction proves a localization string differs, the
user-visible label can be corrected while retaining the stable activity id.

## Regression coverage

`P5RActivityCatalogAuditTest` pins all 18 movie ids, labels, theater locations,
+5 first-viewing bases and the four invitation notes currently represented in
the activity catalog.

This verifies the reusable movie catalog. It does not claim the completion
route's chosen viewing dates are the only valid schedule, and it does not turn
theater unlock requirements into `Activity.location`; that field describes the
place where the activity is performed.
