# dayloop — UI/UX Imitation Roadmap v3 (Phases 11–18)

Roadmap v2 (Phases 7–10) made the app *feel* like a neutral engine with per-pack
accent colors. Roadmap v3 goes further: **when you pick P5R, the app should look
like Persona 5 Royal's own UI — the graphic language, not just the palette** —
while staying the same pack-generic engine for P3R, Metaphor, and future drops.

| # | Ask | Phase |
|---|-----|-------|
| 1 | One game picker: Settings redirects to the onboarding carousel | Phase 11 ✓ |
| 2 | Every guide-package graphic bundled, declared, and served | Phase 11 ✓ |
| 3 | Full per-game UI/UX imitation (graphic style, shapes, type, motion) | Phases 12–17 |

Architecture rules from `docs/PLAN.md` §3 still bind everything below: the engine
never knows a game's name; every look is pack data (`pack.json` `theme` + bundled
art); packs without a skin keep the engine look; anything that says "Palace" or
"Phantom" in Kotlin is an architecture bug.

---

## The core idea: skins, not repaints

A **skin** is the pack's complete visual language. Today (Phase 10) a pack
declares seeds + a scheme style, which recolors Material 3. That's a repaint.
A skin adds the four layers the actual games use to make their UI instantly
recognizable:

| Layer | What it carries | Pack data home |
|---|---|---|
| **Shape** | Container silhouettes — P5R's jagged slashed panels, P3R's glass rectangles with diamond caps, Metaphor's gold filigree frames | `theme.shapes` tokens |
| **Type** | Display font, casing, italics, spacing — P5R's angled heavy italic, P3R's clean thin sans, Metaphor's engraved serif | `theme.typography` + bundled font files |
| **Decoration** | Header motifs, halftone/filigree/grain fills, corner ornaments, arcana/medallion frames | `theme.decor` art slots + the Phase 11 media system |
| **Motion** | Transition grammar — P5R's slash wipes, P3R's soft cross-fades, Metaphor's page turns | `theme.motion` token |

The engine implements a small closed set of primitives per layer (shapes, type
roles, decorations, transitions) and the pack *composes* them. New games don't
add Kotlin; they add data + art. Everything stays swappable, lint-validated,
and strippable before any public flip (PLAN.md §9).

### Non-negotiables (apply to every phase below)

1. **Engine neutrality.** No game titles, character names, or per-game colors in
   Kotlin. CI greps `app/` + `core/` for pack titles as a regression gate.
2. **Spoiler safety & anti-deviation UX survive the skin.** A P5R-skinned step
   row still has Done/Skip/Later; spoilers still collapse behind taps. Skin is
   presentation, never information.
3. **Both dark and light mode** for every skin (P5R's white-on-red comic mode
   and inverted dark mode; P3R's moonlit and dawn palettes; Metaphor's parchment
   and night).
4. **Performance budget.** 60fps scrolling on a mid-tier device; decorative
   effects degrade gracefully (AGSL shaders only on API 33+, canvas fallback
   below; static art where animation is not load-bearing).
5. **Contrast is lint-enforced.** packlint gains a contrast rule: every skin's
   declared text/decoration color pairs must pass WCAG AA before lint passes.
6. **Strippable.** Every skin asset lives under `content/packs/<slug>/` and is
   removable with zero code change (PLAN.md §9 public-flip checklist).

---

## Phase 11 — One game picker + all graphics bundled & served ✅

**Status: shipped in v0.6.0.**

- **Settings' Game section redirects to the carousel.** The inline radio list is
  gone; Settings shows the active game (icon, title, saved-profile count) and a
  "Pack media" row, both navigating to surfaces elsewhere. The onboarding
  carousel is now the *only* game picker in the app: re-entering it from
  Settings shows a back arrow, preselects the current game's card, switches the
  intro copy to "switching keeps every save", and cards show saved-profile
  counts. Picking a card (even the current game) selects and returns to Today.
- **All 116 guide-package graphics bundled and declared.** `content/packs/<slug>/images/`
  now carries every graphic from the gitignored `*_Guide_AI_Package/images/`
  sets (P5R 53, P3R 16, Metaphor 47 — ~17 MB), and a new **`media.json`**
  manifest per pack maps each file to engine-neutral metadata: an id
  (`<pack>.media.<token>`), a closed-set **kind** (`achievement`, `month`,
  `section`, `day`, `portrait`, `banner`, `guide`), a pack-supplied title,
  optional caption, and optional anchors (months / dates / bond ids).
- **packlint enforces "all art ships and serves"**: every file under `images/`
  must be declared exactly once (orphans fail), every declaration must resolve
  to an existing decodable file (gif allowed, decoded as a still today), ids
  must be unique + prefixed, and months/dates/bond anchors must exist in the
  pack's calendar and bonds. JVM tests pin the same contract
  (`PackContentTest`).
- **Serving surfaces:** day-anchored art renders on the matching Day page
  (P3R's full-moon marker on every full-moon date, the Promised Day icon on
  2010-01-31); month-anchored art + section markers decorate the Calendar
  header and month achievements strip (all 50 P5R achievement icons, 44
  Metaphor achievement icons, by month); bond-anchored portraits render on the
  matching Bond detail (Yukari/Lovers, Junpei/Magician, Mitsuru/Empress,
  Fuuka/Priestess, Aigis/Aeon); everything is browsable in the new **Media
  gallery** (Settings → "Pack media"), grouped by kind, with captions and
  anchor text.
- P3R character portraits for members whose bonds the pack doesn't ship
  (Protagonist, Akihiko, Koromaru, Ken, Shinjiro) are gallery-only until a pack
  change adds those anchors — lint leaves nothing undecidable.

**Deliberately deferred to later phases:** GIF animation (today first-frame
only), per-skin shapes/type/motion (Phases 12–16), image budget optimization
(Phase 18).

---

## Phase 12 — The Skin DSL (engine foundation)

**Goal:** the four skin layers (shape/type/decoration/motion) become pack data,
and the engine grows the primitives to render them. No pack ships a skin yet —
this phase is the socket, Phases 13–15 are the plugs.

**Schema (all optional; absent = engine look):**

```jsonc
// pack.json "theme" extensions (v3)
"theme": {
  "accent": "#A61E22", "accentDark": "#D9433C", "style": "vibrant",  // existing
  "motif": "masks",                       // promoted: reserved → decorative selector
  "shapes": {                             // closed-set silhouette tokens per slot
    "card": "jagged", "chip": "slash", "header": "ribbon", "frame": "cut"
  },
  "typography": {                         // pack-bundled fonts + role tuning
    "display": { "file": "art/fonts/display.ttf", "case": "upper", "italic": true, "tracking": 0.04 },
    "title":  { "file": "art/fonts/title.ttf" },
    "body":   null                            // null = engine default
  },
  "decor": {                              // named decoration art slots (like theme.art)
    "header": "art/header.png", "panel": "art/panel.9.png", "divider": "art/divider.png"
  },
  "motion": "slash"                       // closed set: slash | fade | flip | none
}
```

**Engine work:**

- `Skin.kt` (new, `ui/skin/`): resolves a `PackTheme` into a `SkinSpec` object —
  shape providers (`GenericShape` for jagged/slash/cut; `RoundedCornerShape`
  passthrough), typography resolvers (asset fonts via `FontFamily`, role styles
  for display/title/body), decoration painters (`Modifier.drawBehind` painters
  for halftone, grain, filigree, gradient-glass), and transition specifiers for
  `NavHost`/`AnimatedContent`.
- **Jagged/cut shapes** via `GenericShape` with parameterized spike counts and
  angles; **diagonal slash** containers via clipped `Box` + rotated accent
  strips; **filigree frames** as 9-slice-drawable-like `decor.panel` art.
- **Type**: bundle fonts per pack (`content/packs/<slug>/art/fonts/`, linted:
  exists, ttf/otf, ≤2 MB each); `MaterialTheme.typography` is overridden through
  the `SkinSpec` so every screen inherits it without per-screen work.
- **Motif promotion**: `theme.motif` graduates from reserved token to the
  decorative selector — engine maps `masks` → jagged family, `moon` → glass
  family, `crown` → filigree family (closed set, lint-validated; unknown tokens
  fail lint, theme-less packs are untouched).
- **Motion tokens** on `NavHost` and on step-list reveal: `slash` = asymmetric
  wipe + slight skew-in; `fade` = 180 ms cross-fade; `flip` = axis rotation;
  `none` = engine default. All honor the system "remove animations" setting.
- packlint: validate `theme.shapes`/`motion` closed sets, `typography` file
  rules, `decor` art slots (reuse the Phase 10 `theme.art` checker), and the new
  **contrast rule** (see guardrail 5).
- JVM tests: `SkinSpecTest` (token → primitive mapping), plus a CI grep test
  asserting no pack title appears in `app/src/main` or `core/`.

**Acceptance:** a fixture pack declaring every token renders identically to the
engine on screens that don't consume skins, and visibly different on screens
that do; lint passes on all three real packs unchanged (they declare none yet).

**Size:** L. Risk: scope creep into bespoke per-screen code — mitigated by the
closed-set rule (new look = new token + data, never a game-named composable).

---

## Phase 13 — P5R "Phantom" skin

**Goal:** with P5R selected, the app reads as Persona 5 Royal's UI — the jagged
red/black/white comic language — on every surface the engine drives.

**Pack data to author (all in p5r pack.json + art/):** `motif: "masks"` skin
tokens; display font (heavy italic condensed); halftone + slash decoration art;
red/black-on-white and inverted dark palettes tuned by hand over the vibrant
scheme.

**Per-surface spec:**

| Surface | Phantom treatment |
|---|---|
| Onboarding carousel | Cards get torn-edge frames; the selected card's border becomes a red slash ribbon; "What are we tracking?" in display type |
| Today | Dossier card: jagged black panel on red accent, date as skewed ribbon header, End-Day as a big slanted button ("TAKE YOUR TIME" analog on advance animation comes with Phase 16) |
| Day | Step rows on white jagged panels; spoiler reveal wipes in like a cut-in; done-steps get a slash strike instead of plain strikethrough |
| Calendar | Month header on a slash ribbon (uses the Phase 11 month-opener art); today cell is a red jagged star; day-kind chips become angled tabs |
| Bonds | Each confidant row is an arcana card (rank burst in the corner); detail uses the arcana-card frame with the rank ladder as stacked cards |
| Deadlines | Palace deadlines render as calling-card banners (black card, white slash type, red seal) — the warning color logic stays engine logic |
| Answers | Exam sheets as exam-paper cards with a red wax-seal chip |
| Widget | Glance layout mirrors the dossier card (jagged title ribbon, red accent from the dark scheme — already inherited) |

**Engine needs from Phase 12:** jagged/slash/cut shapes, ribbon headers, display
typography, halftone `decor` painter, `slash` motion.

**Acceptance:**
- Side-by-side review against in-game calendar/confidant screenshots for Today,
  Day, Calendar, Bonds: silhouette + type + decoration read as P5R without any
  game asset that must be stripped (all art is guide-derived or original).
- Dark and light both pass contrast lint; no plain Material cards remain on the
  five core surfaces.
- P3R/Metaphor and theme-less fixture packs render byte-identical to the engine
  look (skin isolation test).

**Size:** L (pack data + tuning; engine work lands in Phase 12).

---

## Phase 14 — P3R "Moonlight" skin

**Goal:** P3R's UI: deep-blue glass panels under moonlight, thin elegant type,
diamond caps, calm motion — with the bundled full-moon art as a first-class
motif.

**Pack data:** `motif: "moon"` tokens; display font (clean geometric sans);
glass-gradient panel art; moonlit dark + dawn light palettes over the tonalSpot
scheme.

**Per-surface spec:**

| Surface | Moonlight treatment |
|---|---|
| Today | Translucent navy glass card with a diamond-capped header; the date's moon phase renders beside it (pack media + phase math is engine-generic once `moon` motif is active) |
| Day | Slot pills become small diamond tags; full-moon dates wear the bundled full-moon marker (Phase 11 art) at the header |
| Calendar | Grid cells as soft glass tiles; full-moon-operation days get the moon icon instead of the generic deadline dot; Tartarus-block days (dayKind `forced`) invert to a darker glass |
| Bonds | Social Link rows as tarot-card slips (145×205 portrait ratio frames from the Phase 11 portraits); rank ladder as a link-episode timeline |
| Deadlines | Full-moon operations get a red-moon chip; exam windows keep the engine chip vocabulary, recolored |
| Motion | `fade` everywhere; reveal animations are soft opacity rises, no wipes |

**Acceptance:** same protocol as Phase 13 (side-by-side vs in-game UI, contrast
lint, isolation tests). The moon icon must appear on exactly the nine
full-moon dates + 2010-01-31 already anchored in media.json (lint/test-pinned).

**Size:** M (reuses Phase 12 primitives; mostly pack authoring + tuning).

---

## Phase 15 — Metaphor "Royal" skin

**Goal:** Metaphor's ornate courtly UI: parchment and gold, filigree frames,
engraved serif type, stamp-like chips — matching its fantasy travel-calendar
feel.

**Pack data:** `motif: "crown"` tokens; display font (engraved serif); filigree
frame + parchment panel art (gold-on-dark and gold-on-parchment variants);
royal palettes over the expressive scheme.

**Per-surface spec:**

| Surface | Royal treatment |
|---|---|
| Today | Parchment sheet on a gold double-border frame; the day counter (Metaphor's game-month model) rendered as an ornate plaque |
| Day | Slot rows separated by filigree dividers; follower-step gains framed as small laurel medallions |
| Calendar | Travel-itinerary grid: game-month strip across the top (the dayCounter engine already drives this), day cells as gold-ruled tiles; mission deadlines get a wax-stamp chip ("Mission", pack vocabulary) |
| Bonds | Follower ranks as embossed medallions with the bond's rank number in the crest |
| Deadlines | Mission stamps with a countdown ribbon; the post-game banner art (Phase 11) may decorate the end-of-calendar state |
| Motion | `flip` (page-turn) on day browsing; fades elsewhere |

**Acceptance:** as Phases 13–14. The Royal skin must not break the
`dayCounter` month grid (30-day months, cycle weekdays) — explicitly screenshotted.

**Size:** M.

---

## Phase 16 — Motion, feedback & sound

**Goal:** the games' moment-to-moment feedback, as pack data.

- **Day-advance sequence:** End-Day plays a per-skin transition — P5R: black
  slash wipe + "results"-style tick of the day's checklist; P3R: moon-phase
  cross-fade; Metaphor: calendar-page turn. All under 400 ms and skippable
  (accessibility).
- **Perfect-day splash:** when every authored step of a day is Done, a
  per-skin celebratory card (engine-triggered, skin-styled — P5R's all-out-
  attack typography energy, without copying its assets).
- **Reveal grammar:** spoiler reveals adopt the skin's motion token; checkbox
  taps get per-skin micro-animations (slash strike / moon fill / wax seal).
- **Sound (opt-in, muted by default):** packs may bundle short SFX
  (`art/sfx/tap.ogg`, `advance.ogg`, `complete.ogg`; ≤100 KB each, linted) —
  played only when the user enables "Skin sounds" in Settings; never on the
  widget.
- **Haptics:** light tick on mark-toggle and day advance (system setting
  respected).

**Acceptance:** animation timing lint (no blocking transition > 400 ms);
`reduce-motion` on = all of the above become plain fades or nothing; sounds
never play when disabled or on widget surfaces.

**Size:** M.

---

## Phase 17 — Widget, icon & launch parity

**Goal:** the skin doesn't stop at the app's edge.

- **Widget**: per-skin widget layouts via Glance (Phase 13–15 widget rows);
  skin colors/fonts already flow from the dark scheme — extend with the skin's
  shape/decoration (jagged ribbon / glass tile / gold frame). Widget preview
  screenshots become part of the parity matrix.
- **Launcher icon**: per-pack adaptive-icon overlays stay engine-owned (the
  sun/arrow icon is dayloop's), but a pack may declare
  `theme.art["launcherBadge"]` — a small motif badge the icon compositor
  overlays in its own corner. (True per-game launcher icons are an Android
  limitation — shortcut icon substitution is the accepted workaround and is
  out of scope unless requested.)
- **Cold-start parity**: the loading shell (Phase 7) adopts the active skin's
  background + motif mark so the app never flashes the engine look.

**Acceptance:** widget renders correctly at all Glance sizes per skin; cold
start shows the skin before first frame; theme-less packs unchanged.

**Size:** M.

---

## Phase 18 — Verification, budgets & release hardening

**Goal:** make the skins sustainable.

- **Screenshot tests** (JVM-rendered, e.g. Roborazzi) for the parity matrix:
  every surface × {engine, phantom, moonlight, royal} × {dark, light} becomes a
  CI screenshot with an intentional-diff review flow.
- **Contrast + budget lint** (from Phase 12) extended with:
  per-pack `images/` + fonts size budget (warn > 8 MB, fail > 20 MB — P3R's
  GIFs today total ~10.8 MB, so conversion to animated WebP is a work item);
  font file budgets; decoration art dimensions.
- **GIF → animated WebP conversion** for the four bundled GIFs (P3R ×3,
  Metaphor ×1) and Coil-based animated rendering, keeping first-frame
  BitmapFactory as the fallback path.
- **Strip pipeline**: a `strip-art` Gradle task that removes everything under
  `content/packs/*/images/`, `art/`, `art/fonts/`, `art/sfx/` + the
  `theme` blocks' art references, for the public-flip checklist (PLAN.md §9).
  Skins degrade to the engine look automatically (that's the schema contract).
- **Performance CI gate**: Macrobenchmark smoke on the Calendar + Day screens
  per skin; frame-time budget 16 ms p95 on the reference device profile.

**Acceptance:** CI green on all of the above; `strip-art` output builds and
runs with the engine look; budgets documented in `docs/data-coverage.md`.

**Size:** L.

---

## Screen-by-screen parity matrix (target state after Phases 13–17)

| Surface | Engine baseline | P5R Phantom | P3R Moonlight | Metaphor Royal |
|---|---|---|---|---|
| Onboarding carousel | Cover cards + dots | Torn-edge cards, slash selection ribbon | Glass cards, soft fade | Filigree-framed cards |
| Today | Dossier card + deadline banner | Jagged dossier, skewed ribbon header | Navy glass, moon phase | Parchment plaque, gold frame |
| Day | Step rows, chips, progress line | White jagged panels, slash strikes | Diamond slot tags, moon markers | Filigree dividers, laurel gains |
| Calendar | Kind-colored grid, deadline dot | Slash ribbon header, star today cell | Glass tiles, moon op icons | Itinerary strip, gold-ruled tiles |
| Bonds list/detail | List rows, rank ladder | Arcana cards | Tarot slips, link timeline | Embossed medallions |
| Deadlines | Banner list, kind chips | Calling-card banners | Red-moon chips | Mission stamps |
| Answers | Sheet cards | Exam papers, wax seal | Glass sheets | Scroll cards |
| Activities | List + detail | Job-case cards | Glass list | Ledger rows |
| Search | Plain results | Skewed result rows | Soft highlight | Gold-ruled rows |
| Settings | Cards | Jagged cards | Glass cards | Parchment cards |
| Media gallery | Kind-grouped rows | Kind-ribbon headers | Glass rows | Frame rows |
| Widget | Title/count/next deadline | Jagged ribbon | Glass tile | Gold frame |
| Cold start | Spinner shell | Motif splash | Moon splash | Crest splash |

Every cell is engine code driven by pack data — the matrix is the acceptance
checklist for Phases 13–15, screenshot-pinned in Phase 18.

---

## Deferred / unchanged

- P4G and future packs: skins are data, so a P4G pack would declare its own
  motif/tokens without engine change — the closed-set token list may grow by
  adding tokens, never by adding game-named code paths.
- Import packs from device storage: unchanged; skins ride in the pack.
- Localization of skin-supplied strings: pack-supplied labels are single-locale
  today; unchanged by v3.
- Dynamic wallpaper / Material You integration: the engine's schemes are
  pack-seeded, not wallpaper-seeded — unchanged deliberately (skins must not
  drift between launches).
