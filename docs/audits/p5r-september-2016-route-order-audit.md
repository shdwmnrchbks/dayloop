# P5R September 2016 route-order audit

Scope: continue issue #12's month-by-month Persona 5 Royal route reproduction after April-August. September mixes ordinary school days, the Hawaii trip, the Morgana/Haru story chain, two Japanese national holidays and an authored Spaceport route that deliberately finishes in early October.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — primary Dayloop route facts for September 1-30 and the route-selected early-October Spaceport completion.
- GameFAQs Royal September walkthroughs (`faq/79923`, `faq/78212`, `faq/78256`) — independent checks for September classroom dates, Hawaii, September 12-18 mandatory story, September 18 LeBlanc confinement and legal post-story route slots.
- Samurai Gamers Royal September walkthrough / classroom-answer table — secondary check for September 3/17/24 school questions and the Okumura story transition.
- Japan 2016 holiday calendars — September 19 was Respect for the Aged Day and September 22 was Autumnal Equinox Day; neither is a normal school day.

## Corrections made

### School-day corrections

Three dates were mislabeled `free` despite containing fixed classroom actions:

- **September 3** — `Prosperity` class question before the route's later activities.
- **September 17** — class question before the fixed Mementos Morgana/Haru reunion and automatic Magician rank 8.
- **September 24** — Saturday school with the `20 white, 12 black` class question before Tower/Maid Café route actions.

All three are now `school`.

### September 12-18 story chain

The pack had some fixed story days represented as ordinary school/free days. The route now surfaces their real calendar state:

- **Sep 12:** return from Hawaii occupies the daytime; only the evening home slot remains, where the route fertilizes the plant.
- **Sep 13:** principal/Phantom Thieves story and Morgana's departure occupy the full day; Judgement rank 4 is automatic.
- **Sep 14:** remains `school` because there is a classroom question, with the Morgana/Haru search explicitly noted as the rest-of-day story.
- **Sep 15:** already `story`; first Spaceport investigation.
- **Sep 16:** remains `story`; Haru/Okumura investigation, no free time.
- **Sep 17:** now `school`; classroom question precedes the fixed Mementos reunion and automatic Magician rank 8.
- **Sep 18:** now `story`; Haru/Okumura story occupies daytime and the evening is confined to LeBlanc. The route's first `Mouse M.D.` viewing is retained as a legal home activity.

### September national holidays

Two weekday entries were incorrectly rendered as normal school days:

- **Sep 19 — Respect for the Aged Day:** now `free`.
- **Sep 22 — Autumnal Equinox Day:** now `free`.

The route's Tower/Shady/movie and Hifumi/Shady/TV/Ohya choices are preserved; the correction only prevents the app from implying school attendance on national holidays.

### Aojiru reusable links

The September 4 and September 25 Sunday-drink rows already had the correct continuing Royal rotation but had lost their reusable activity references. Both now point to `p5r.activity.drink.fruit-drink`:

- Sep 4 — Knowledge +2.
- Sep 25 — Charm +2.

### Spaceport route deliberately spans September and October

Independent walkthroughs often clear Okumura's Palace earlier, but that does not make Dayloop's chosen schedule wrong. The authored route is kept as:

1. **Sep 15** — first Spaceport infiltration / mandatory story investigation;
2. **Oct 4** — second Spaceport infiltration, capture the Treasure Demon / collect Will Seeds / secure route;
3. **Oct 5** — send Calling Card;
4. **Oct 6** — steal Okumura's Treasure.

Those dates are route-selected timing, not global Palace availability/deadline claims. This audit explicitly regression-pins the cross-month order so a later pass does not collapse valid route timing merely to mirror another guide.

## Regression coverage

`P5RSeptemberRouteOrderAuditTest` pins:

1. Sep 3/17/24 school day kinds;
2. the Sep 7-11 Hawaii story block;
3. Sep 12/13/15/16/18 story day kinds and Sep 14 mixed school/story context;
4. Sep 17 class + Mementos reunion + automatic Magician rank 8;
5. Sep 18 LeBlanc confinement and legal `Mouse M.D.` viewing;
6. Sep 19 and Sep 22 national-holiday `free` status;
7. Sep 4/25 Aojiru reusable links and rotating rewards; and
8. the Sep 15 -> Oct 4 -> Oct 5 -> Oct 6 Spaceport route / Calling Card / heist chronology.

April through September now have dedicated route-order/state reproduction passes. October onward remains under issue #12, alongside player-state/RNG/branch-dependent cases that should not be encoded with false fixed-date precision.
