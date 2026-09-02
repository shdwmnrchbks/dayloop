# P5R June 2016 route-order audit

Scope: continue issue #12's month-by-month reproduction of the authored Persona 5 Royal completion route after the April and May passes. The goal is to preserve Dayloop's chosen flexible route while correcting fixed story/school semantics, missing chronology and state claims that can be independently checked.

## Sources checked

- Alyookid, **Persona 5 The Royal 100% Achievements + Perfect Schedule** — primary source-route facts for June 1-30, including the Kichijoji tutorial sequence, Moon/Temperance route choices, June Knowledge progression, June 18-19 Kawakami lockpick counts and the Kaneshiro Palace order.
- GameFAQs Royal June walkthroughs (`faq/78256`, `faq/79923`, `faq/78212`) — independent checks for the June 5 Kichijoji/darts introduction, June 9 TV-station trip, June 10 automatic Justice start, June 11 full-day story, June 12 morning story/free afternoon split and the Kaneshiro story progression.
- Samurai Gamers Royal June walkthrough — secondary fixed-calendar check for the June 9-14 mandatory story block and the June 25 school/class slot.
- Royal school-answer/crossword references — June 4 class question plus the June 3/7/16/22/30 crossword dates and actual +2 hidden Knowledge values.

## Corrections made

### Fixed story and school day kinds

The route had several days whose actions were legal but whose top-level day kind hid mandatory Royal story/school time.

Corrected to `school`:

- **June 4** — Shujin is in session and the route answers the Halo Effect class question.
- **June 18** — Saturday school precedes the after-school request/Emperor route.
- **June 25** — the route explicitly reads `Game Secrets` during class before entering Kaneshiro's Palace.

Corrected to `story` and given explicit story context where useful:

- **June 5** — the Kichijoji introduction includes the mandatory first darts tutorial with Ryuji; the authored booth/sooty-clothes/Aojiru/Yoshida route is preserved around that fixed sequence.
- **June 9** — forced social-studies trip to the TV station; Suidobashi/Dome Town unlocks, then the route uses the evening for Moon rank 3.
- **June 10** — TV-station story continues and Justice rank 1 starts automatically; the evening Temperance visit remains usable.
- **June 11** — hot-pot/story events occupy the full day and Fool reaches rank 5 automatically.
- **June 12** — mandatory morning story occurs before the route's normal afternoon/evening actions.
- **June 14** — Makoto's mandatory story occupies the daytime; the route keeps Moon rank 4 in the evening.

Mixed weekdays such as June 13/15/16/17 remain `school` when the authored route contains normal school/class state plus later story/free actions. This audit does not flatten every mixed school day into `story`.

### Continuous Knowledge state

April and May now end with **110 authored Knowledge points**. June contributes exactly 16 more through the June 13 class answer:

- Jun 2 TV game show: +2
- Jun 3 `Master` crossword: +2
- Jun 4 class question: +2
- Jun 5 Kichijoji information booth: +2
- Jun 7 class question: +2
- Jun 7 `Conference` crossword: +2
- Jun 8 class question: +2
- Jun 13 class question: +2

That puts the route at exactly **126 Knowledge points on June 13**, Royal's rank-4 threshold. The existing `Knowledge reaches rank 4` label is therefore retained and is now backed by the continuous April -> May -> June state reconstruction.

### June 18-19 lockpick chain

The primary route's Kawakami crafting state is now explicit instead of using ambiguous `2 total` wording:

- **June 18:** select 4 lockpicks for Kawakami; she crafts 2, leaving the route with **3 total**.
- **June 19:** select 4 again; she crafts 2 more, leaving **5 total**.

The audit preserves this as route inventory state, not a universal crafting rule beyond Kawakami's half-quantity behavior.

### June 19 chronology

Dayloop previously listed the Sunday Home Shopping and Kawakami crafting before the first Kaneshiro bank infiltration. Royal's story order is the reverse: the daytime Palace scouting happens first, then the route returns to LeBlanc for evening actions.

June 19 is now ordered as:

1. first bank infiltration / scout Kaneshiro's Palace;
2. Home Shopping Program;
3. Kawakami crafts 2 more lockpicks, reaching 5 total;
4. second viewing of `The Running Dead`.

### Kaneshiro Palace route order

The authored flexible Palace route remains legal and is regression-pinned:

1. June 19 — first story scouting infiltration;
2. June 20 — second infiltration / Makoto joins;
3. June 25 — third infiltration, collect Will Seeds and secure the Treasure route;
4. June 27 — send the Calling Card;
5. June 28 — steal Kaneshiro's Treasure.

This is the route's chosen timing, not a claim that June 25/27/28 are global Palace deadlines.

## Regression coverage

`P5RJuneRouteOrderAuditTest` pins:

1. corrected June 4/18/25 school day kinds;
2. June 5/9/10/11/12/14 mandatory-story day kinds and context;
3. the exact **126 Knowledge** state through the June 13 class answer;
4. June 18 -> 19 lockpick totals of 3 -> 5;
5. June 19 daytime Palace scouting before all evening actions; and
6. the June 25 Treasure-route -> June 27 Calling Card -> June 28 heist chronology.

April, May and June now have dedicated route-order/state reproduction passes. July onward remains under issue #12, along with player-state/RNG/branch-dependent cases that should not be represented as false fixed dates.
