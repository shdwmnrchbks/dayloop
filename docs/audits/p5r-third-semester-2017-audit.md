# P5R third-semester route audit — January–February 2017

This pass finishes the point-unit audit for the authored Persona 5 Royal
completion route through its last stat-bearing activity on February 1. As with
the earlier month audits, it verifies reusable mechanics and fixed calendar
facts without claiming that dayloop's flexible route order is the only valid
Royal route.

## Sources used

- **marendarade, GameFAQs Persona 5 Royal walkthrough — January**: independent
  checks for January class-question/crossword rewards, January classroom-answer
  content, and a successful darts session. The walkthrough explicitly records
  +2 for the January classroom/crossword rewards and +3 Proficiency for the
  January Faith darts session.
- **marendarade, GameFAQs Persona 5 Royal Social Stats**: reusable Royal activity
  mechanics for first-time movies, The Craft of Cinema, retro games, Sunday
  drinks, fishing and Big Bang Burger challenge tiers.
- **Neoseeker Royal January / final-Palace walkthrough** and **GameFAQs Royal
  deadline discussions**: independent checks that February 3 is the final
  Palace/story confrontation date but the Treasure route must be secured by
  February 2.
- **Samurai Gamers Royal walkthrough/social-stat pages**: secondary check for
  darts execution, movie categories, retro-game categories and Big Bang Burger
  challenge tiers.
- The existing dayloop P5R audit convention remains authoritative for schema
  units: `statGains` stores hidden social-stat points, not the displayed
  music-note shorthand. One displayed note maps to 2 hidden points, two notes
  to 3–4, and three notes to 5/7/10 depending on the activity.

## Corrections

### School questions, crosswords and Sunday Aojiru

The remaining January `+1` imports were display-note shorthand. The route now
stores +2 hidden points for:

- 1/11 Charm classroom reward.
- 1/14, 1/18, 1/21, 1/24 and 1/27 Knowledge classroom rewards.
- 1/14 `Resolution`, 1/19 `Dionysus`, 1/23 `Lachesis` and 1/27 `Orochi`
  crosswords.
- 1/15 Proficiency, 1/22 Guts and 1/29 Kindness Sunday drinks.

The structured January answer sheets were also checked against the independent
Royal walkthrough and already match it, including the two-part 1/11 response
(`How numerous they are` / `The Eight Million Gods`) and the three-part 1/24
response (`Kind-hearted` / `Negative` / `Resentful`). The regression test now
pins all six January classroom answer sheets.

The Sunday drink steps now also carry the reusable
`p5r.activity.drink.fruit-drink` reference so the stateful Aojiru mechanic stays
visible in the route data.

### Movies and The Craft of Cinema

The route read `The Craft of Cinema` on 2016-06-23, so every January/February
first-time theater viewing uses the 5-point movie base plus the active +2
modifier:

- 1/14 `March of the Lambs` — Proficiency +7.
- 1/18 `Bite Club` — Guts +7.
- 1/27 `Finding Beemo` — Charm +7.
- 2/1 `The Goodfather` — Kindness +7.

The labels explicitly call out the active Craft of Cinema bonus to keep the
route-specific total distinguishable from the reusable 5-point movie base in
`activities.json`.

### Retro games

The January game-cleanup steps had retained two-note shorthand. Royal retro-game
clears use the reusable +3 hidden-point base, with `Game Secrets` only making the
minigame easier:

- `Train of Life` — Kindness +3 per successful clear.
- `Power Intuition` — Guts +3 per successful clear.
- `Golfer Sarutahiko` — Proficiency +3 per successful clear.

### Darts, fishing and Big Bang Burger

- 1/16 fishing is +2 Proficiency under the pack's hidden-point convention.
- 1/17 darts is pinned to +3 Proficiency for the successful route session. A
  higher optimized darts execution can exist, but this route does not claim the
  optional maximum bonus.
- The remaining Big Bang challenge tiers were still stored as their displayed
  `+2/+3` shorthand. Under the hidden-point convention the route now stores
  +3 to Knowledge/Guts/Proficiency/Charm for the Gravity tier on 1/19 and +5 to
  those four stats for the Cosmic tier on 1/23.

### Final Palace deadline semantics

Royal presents **February 3** as the final Palace/story deadline, but the
Treasure route must already be secured by **February 2**. The calling-card/final
sequence is then story-controlled. A single February 3 deadline caused Dayloop's
countdown to imply that February 2 was still a spare day for route preparation.

The pack now represents the two independently checkable facts separately:

- `p5r.deadline.missable.palace8-route` — February 2, actionable deadline to
  secure the Treasure route.
- `p5r.deadline.palace8` — February 3, Palace story/final-confrontation date.

This is deliberately not a route-selected date: the February 2 route-security
requirement is a universal failure condition for the Royal final Palace.

## Verification boundary

This pass completes the targeted hidden-point/modifier audit through February 1,
2017 and corrects the final Palace's actionable deadline semantics. It does
**not** assert that the completion route's chosen Faith rank dates,
third-awakening order, cleanup activities or free-time choices are the only
possible Royal schedule. Those remain route selections unless separately
encoded as universal gates or story timing.

`P5RThirdSemesterAuditTest` pins the corrected January–February values, active
Craft of Cinema labels, January classroom answers, and the February 2/3 deadline
split so future imports cannot silently reintroduce note counts, flattened
answers or the off-by-one actionable deadline.
