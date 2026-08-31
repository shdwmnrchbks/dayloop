# Persona 5 Royal — UI Design Language Reference

Skin-token research for the dayloop pack engine. Written from web sources plus programmatic color
analysis of 20 Game UI Database screenshots (1920×1080 JPGs downloaded to `build/ui-ref/img/`,
gitignored — text-only repo; sampling script `build/ui-ref/analyze-colors.ps1`, results
`build/ui-ref/color-analysis.txt`). Every claim is tagged `[verified: KEY]` (source seen/read
directly this session; full URLs in the index at the bottom) or `[inference]` (extrapolated from
sources, sampled data, or widely-reproduced community observation without a textable source).
Notes: Medium/Reddit/TCRF/Spriters block plain fetches — Medium + Spriters + TCRF were read via
archived/reader mirrors; the ResearchGate halftone figure was only verifiable from its search-result
caption title (page itself bot-gated).

Source keys used below:
- **FAM** = Famitsu CEDEC+KYUSHU 2017 session report (primary; Sutoh + Wada) — famitsu.com/news/201711/13145540.html
- **PC** = Persona Central English report of the same panel — personacentral.com/persona-5-panel-concept-development-ui/
- **GUD** = Game UI Database, Persona 5 Royal gallery — gameuidatabase.com/gameData.php?id=618
- **GUD:img** = specific GUD asset, cited by upload filename
- **FRUIT** = Medium @fruitcupkun, "Persona 5: Menus with Personality and Readability" (2019)
- **LIU** = Medium Xiaohai Liu, "Visual Design of Games — Practice & Analysis of Persona 5 Royal" (2020)
- **BOOT** = Medium Design Bootcamp, "How Persona 5's UI balances both style and substance" (2025)
- **JQ** = Medium Joseph Quick, "Persona 5 Royal: A Masterclass in UX" (2025)
- **MG** = madegooddesigns.com, "What Font Does Persona 5 Use?" (2026)
- **FB** = fontbolt.com, "Persona 5 Font" (community font ID aggregator)
- **GFL** = gamefontlibrary.com, Persona 5 font list
- **FAN** = megamitensei.fandom.com, "All-Out Attack"
- **TSR** = spriters-resource.com, Persona 5 Royal sheet index
- **ITCH** = itch.io dev-blog study of the P5 day-change animation (Regen Games Studios, 2023)
- **CM** = chelseamai.com P5R dashboard case study
- **MOM** = mechanicsofmagic.com (Stanford CS247G), "Visual Design of Games: Persona 5" (2022)
- **TCRF** = tcrf.net Proto:Persona 5 menu graphics page
- **RG** = ResearchGate figure caption "Menu in Persona 5 featuring decorative halftone patterns" (title-verified only; page bot-gated)
- **REDDIT** = r/Persona5 P5R fonts thread (indirect, via FB)

---

## 1. Graphic language

- Official design concept for the whole UI is **"pop punk"**: pop = mass-oriented, punk = anti-establishment [verified: FAM] [verified: PC]. The visual language is deliberately rule-breaking: high-contrast palettes, irregular shapes, asymmetrical alignment, silhouetted icons [verified: CM].
- **Panels are sharp-edged, non-parallel quadrilaterals**, not rounded rects: containers are cut at aggressive, mostly asymmetric angles; letters and panels alike look "torn and pasted" like collage cut-outs / protest posters / ransom notes [verified: MG]. Community design breakdowns describe the same: "irregular shapes, asymmetrical alignment" [verified: CM].
- Spike character: jagged, acute, *uneven* points rather than symmetric zigzag; no source states an exact spike count — treat spikes as irregular (8–16 uneven points on impact bursts), symmetric only when the shape is a deliberate "star burst" accent [inference, grounded in MG's "jagged angles / torn-paper edges" and CM's "irregular shapes"].
- **Layering is a strict 3-ink system**: black field, red field, white ink (plus near-black #181818 for depth). Sampled menu-class screens distribute roughly black 43–60%, red 13–33%, white 2–23% of pixels [verified: GUD + sampled stats]. The designers deliberately refused a fourth "sub-color" so the red would carry the identity alone [verified: FAM].
- **A white center line divides the pause menu** and guides the eye to the middle where the content sits; the menu reads as split compositions (one side black/white, the other red) [verified: FAM] [verified: FRUIT].
- Text containers behave like **angular speech bubbles / nameplates**: thick black outlines around white type, type sitting on red or white shards [inference from FRUIT's "blocky, stylized, thick black outline" typography + GUD:Persona-5-Royal03112021-110116-41608.jpg which samples 26% flat red #C01800 / 23% black in alternating panel regions].
- **Star/spark bursts**: comic "impact splash" shapes appear at emphasis moments (selection, weakness hits, all-out attack splash screens); the game even marks the all-out attack with a full-screen red takeover where enemies render as black silhouettes [verified: FAN (P5-strikers description "screen turns red and the enemies become silhouettes (emulating the All-Out Attacks from the original Persona 5)")]. Exact spike counts are not documented textually [inference].
- **Torn-edge treatment** is part of the brand: cut-paper collage edges, letters "skewed and clipped like collage cutouts" [verified: MG]. GUD captures show torn/jagged panel edges on headers and modal frames [inference, visual].
- Borders: thin white keylines separate red panels from black; heavy black outlines outline white text and portraits. No rounded-corner strokes anywhere — corners are mitered or slashed [inference, visual + MG].
- Asset-level taxonomy confirms the language is built from discrete decorated slots: separate ripped sheets exist for All-Out-Attack Portraits, Critical Hit Cut-ins, Battle Cut-ins, Buttons Icons, Dialogue Menu Portraits, Security Level, Tarot Cards, Loading Screens [verified: TSR].
- Portraits: black-and-white/duotone character art with bold outlines on flat red/black (GUD "Party"/"Stats" captures); TCRF documents many distinct Joker poses per pause-menu page (one per menu context) [verified: TCRF].

## 2. Color palette (sampled from 20 GUD 1080p captures)

- **Signature red** — dominant quantized buckets across captures: `#D81800`, `#C01800`, `#F01800`; mean of "red-classified" pixels per screen ranges `#B30F00`–`#F91802` (flat fields sample `#D81800`, brighter splashes `#F01800`, shaded red panels `#780000`/`#901800`) [verified: GUD + sampled stats]. Recommend tokens: core red `#DC1600`, bright accent red `#F01800`, deep shade red `#8E0E00` [inference — token values derived from sampled buckets].
- **Black** — `#000000` plus near-black `#181818` as the second-most-common bucket on most screens; black is the default surface (menus sit on black or dark world art) [verified: GUD + sampled stats].
- **White** — slightly-off white `#F0F0F0` bucket for text/panels; white is ink + occasional large panel (e.g. 15–23% white on settings/day screens) [verified: GUD + sampled stats].
- **Yellow** — rare accent, sampled bucket `#F0A848` (amber/gold) at up to 5.6% of one capture; 0–2% on most screens [verified: GUD + sampled stats]. Treat yellow as a *sparse* accent only (highlights, "NEW"/event markers), never a surface [inference from distribution].
- **Blue** — sub-percent quantities; community sources report a small blue splash marking the selected item in the pause menu of the base game [verified: MOM] [verified: FRUIT], and one sampled HUD-adjacent capture contains a `#4878F0` bucket [verified: GUD:Persona-5-Royal03112021-110213-51138.jpg]. Dark blue/teal captures are world art (night city / Metaverse), not UI ink [inference]. Famitsu confirms blue/yellow appear only as exceptions (e.g. HP/MP numbers) because P5 rejected sub-colors [verified: FAM].
- Distribution rules: red fields carry primary action zones and identity moments; black carries environment/negative space; white carries type and keylines. Both polarities of type exist — white-on-red, black-on-white, white-on-black — "some letters are black on white backgrounds, some are the opposite" [verified: LIU].
- Red on UI screens is *not* a flat fill everywhere: captures show red shading bands (`#780000`/`#900000`/`#901800` buckets) used to model depth inside red shapes — pack should support a red shade ramp, not just one red [verified: GUD + sampled stats] [inference for intent].

## 3. Typography

- Display type: **heavy condensed grotesque sans capitals, frequently rotated/skewed and overlaid on red shapes**; body/dialogue: cleaner neutral sans; the Japanese and Western releases use different localized fonts; Atlus has never published official font names; much of the display look is custom-drawn/animated, not typeset [verified: MG].
- Case: primary display voice is uppercase, with **deliberate case-breaking as a punk motif** — victory taglines mix case inside one phrase: "FREAKiN' BoRiNG", "OMG!! We are SO awesome", "Git Gud", "Beauty is Devotion", "Don't be so Cocky" [verified: FAN]. Liu observed mixed upper/lowercase and per-letter style drift inside single menu words ("Even the same letter would have different styles in different words") [verified: LIU]. Base P5 even stylized "All-out Attack" with a lowercase "o"; Royal reverted to "All-Out Attack" [verified: FAN].
- Slant: a strong **forward (right-leaning) oblique** is core to the look — the logo wordmark is italic-leaning (community match: Arial Bold Italic for "PERSONA5") [verified: FB], headers are "frequently rotated" [verified: MG], and menu items are "aligned along the oblique line" so the eye follows the diagonal [verified: LIU]. Estimate the display slant at ~10–15° [inference — no source states degrees].
- Tracking/weight: thick strokes, tight/condensed spacing, blocky "almost like images" letterforms with heavy black outlines [verified: LIU] [verified: FRUIT]; stroke contrast is low (poster weight, not serif). Tracking ≈ −1%…−4% with occasional manual kerning jitter for collage feel [inference].
- Known font identifications (community, unofficial):
  - Logo "P5" emblem ≈ **Markin LT Ultra Bold**; "PERSONA5" wordmark ≈ **Arial Bold Italic** (similar, not exact) [verified: FB].
  - Menu font fan recreations: **"Personal 5 Menu Font Prototype" by KanjiPlaysInc** (Reddit) and **"P5Hatty"** [verified: FB] (thread: REDDIT, access blocked — cited secondhand).
  - Recommended free stand-ins: **Anton** (closest), **Archivo Black**, **Oswald**, **Bebas Neue** [verified: MG].
  - Body/secondary UI font: **FOT-Rodin ProN** (Fontworks, via Adobe Fonts) per GameFontLibrary [verified: GFL]; tertiary slot links to a Fontworks "Lets" catalog font [verified: GFL]; CJK localization slot uses DynaFont **DFHei Bold (B5)** [verified: GFL]; "main font" slot links to an Asian foundry listing (asiafont.com boid=229) with no usable name [verified: GFL].
  - No official commercial identification exists; treat all names as community reconstructions [verified: MG].
- The logo itself is **bespoke artwork** by art director Masayoshi Suto and the in-house team — a stylized red numeral 5 with sharp kinetic cuts, not a typeface [verified: MG]. Key font and title logo were decided together with the main color during pre-production [verified: FAM].

## 4. Decoration motifs

- **Halftone dots**: an academic figure caption documents "Menu in Persona 5 featuring decorative halftone patterns" [verified: RG (caption title only)]. Halftone reads as print-comic shading: dot fields bridging red/black transitions, monochrome (white dots on red, red dots on black). Scale estimate: coarse dots ≈ 4–8 px diameter at 1080p near panel edges, fine ≈ 2 px fields; grids aligned to 0/90° or 45° [inference — density/scale not documented textually; grounded in RG + visual knowledge].
- **All-out attack cut-in style**: when triggered, cut-ins showcase participant bust art, then a full-screen red "splash" with black silhouettes; the final contributor gets a **"finishing touch" close-up** replacing the normal victory pose, with a unique per-character tagline [verified: FAN]. Taglines are typographic stamps (see §3 case). Caroline & Justine / Lavenza variants swap the background red for **blue** [verified: FAN]. All illustrations/finishing touches are viewable in-game in the Thieves Den gallery (without enemies/blood) [verified: FAN].
- **Ribbon/banner headers**: sub-headers and labels sit on skewed black or red ribbon bars with hard-mitered ends, often doubled (red bar over black offset shadow) [inference, visual from GUD captures].
- **Diagonal slash dividers**: the signature divider is a hard white line — the menu's white center line is explicitly a gaze-guidance device [verified: FAM]; diagonal white slashes between zones appear across screens [inference, visual].
- **Tarot/Arcana iconography**: full tarot-card asset sheet exists (Confidant arcana) [verified: TSR]; confidant UI uses arcana card motifs [inference, visual].
- **Calling-card / torn-paper framing**: "Calling Card" is an in-fiction diegetic UI piece; fan recreations of P5R screens reproduce its torn paper + stencil lettering [verified: CM].
- Per-asset decoration classes ripped by the community: All-Out-Attack Portraits, Critical Hit Cutins, Battle Cut-ins, Loading Screens ("TAKE YOUR TIME" variants), Security Level meters, Buttons Icons [verified: TSR]. Loading screens show Joker running with the line "take your time" [verified: JQ].
- Texture memory was packed "like Tetris", and PS3/PS4 dual-resolution shipping required lossless scaling path data — i.e. art is vector-ish shapes + packed bitmaps, friendly to procedural re-drawing [verified: FAM].

## 5. Motion grammar

- Menu transitions are **fast, hard-eased, non-bouncy**: elements slash/punch in rather than fade; designers tuned motion at "1 dot / 1 frame" granularity with dedicated UI programmers per part (battle, scenario) [verified: FAM]. Perceived speed: most transitions complete in a fraction of a second (sub-300 ms feel) [inference].
- **Wipe direction**: panels enter from screen edges with diagonal/oblique wipes matching the typography's slant; menu items align along oblique lines so the eye travels diagonally [inference from LIU's oblique-alignment + FRUIT's split-composition description; no source states exact wipe angles].
- **3D character model integration**: the menu 3D model moves with navigation — layout first in Photoshop, motion designer creates poses, a special tool places them, and a 2D illustration "key cut" is drawn to match the final pose [verified: FAM] [verified: PC]. Joker's poses parody a Hollywood star waving off paparazzi [verified: FAM]. Each menu context gets its own pose set (proto shows per-menu Joker image sets) [verified: TCRF].
- **Hierarchy motion**: moving down a level changes layout *and* camera angle to signal depth; **brightness/lighting shifts fluidly** — high-priority info spaces are brighter, low-priority darker [verified: FAM].
- **Day/date transition**: lasts ~12 s at 60 fps — current date holds ~6 s, the date changes over ~2 s, new date holds ~4 s; the new date is rendered in a **different color** from the old one [verified: ITCH]. Calendar/day changes feel like a "breather" beat between activities [verified: ITCH].
- **Cut-in reveals**: character bust slides in diagonally with a hard stop + splash burst + tagline stamp [inference, visual]; governed by the same pose/key-cut pipeline as menus [verified: FAM].
- Menu intent: "intuitive and casually guiding", where "casually" includes playfulness and surprise — motion is allowed to be playful, not just fast [verified: FAM].
- Loading: "TAKE YOUR TIME" running-Joker screens make waiting part of the persona [verified: JQ].

## 6. Per-surface notes

- **Title / settings (GUD "Title and Settings")**: black-dominant fields (43–60% black) with 13–19% red and low white; red appears as angled blocks/shards over the black world image [verified: GUD + sampled stats, e.g. GUD:Persona-5-Royal03112021-110111-77734.jpg: 55% black / 13.5% red / 5.4% white]. Title menu voice: oversized angled type, vertical stack [inference, visual].
- **Pause / system menu**: split-screen composition — center white line divides a black/white side from a red side; menu items stacked along an oblique; selected item enlarges and gains a color splash (blue in base P5 per community sources) [verified: FAM] [verified: FRUIT] [verified: MOM]. Joker's 3D model center-stage, changing pose per page [verified: FAM]. Bottom-right: conventional grouped button hints (small, last in hierarchy) [verified: LIU].
- **Status / stats (GUD "Stats and Resources")**: sampled capture GUD:Persona-5-Royal03112021-110112-69046.jpg = 32.7% red / 33.7% black / 21.3% white with a red left ¾ field and black right rail in the region grid — big red identity panel + dark data column [verified: GUD + sampled stats]. Character portraits duotone with bold outlines [inference, visual].
- **Calendar / day screens (GUD "Game States")**: one capture shows a solid red left rail against a mostly black field with 23% white (date/weekday surfaces) [verified: GUD + sampled stats, GUD:Persona-5-Royal03112021-110115-7501.jpg; identity of surface = inference from category]. Day-change animation grammar: hold → change (2 s) → hold, new date in a different color [verified: ITCH]. Calendar cell assets (numbers/days) exist as rippable sheets; community notes the P5 (non-Royal) rips are lower-res/JP-only — Royal calendar UI is a distinct asset family [verified: TSR (comment)].
- **Confidant screens**: confidant portrait sheets + arcana tarot card sheets + rank UI ripped as separate families [verified: TSR]; presentation = character art on black/red fields with arcana glyph accents, rank-up as a banner moment [inference, visual].
- **Battle HUD (GUD "HUD and Overlays")**: darkest captures of the set (black up to 66–80% when world is dark), UI kept to white type + sparse red; party status tiles bottom-right, action names near the party, enemy plate top-left, party banter top-right [verified: GUD + sampled stats] [verified: MOM]. Weakness/critical moments flash white/red with star bursts [inference, visual].
- **Modals / dialogs (GUD "Modals and Text")**: capture GUD:Persona-5-Royal03112021-110116-41608.jpg samples 26% flat `#C01800` + 23% black with an alternating red/black region grid — modal panels are split red/black angular cards, not boxes [verified: GUD + sampled stats]; confirmations follow standard RPG flows ("Are you sure?" guards) [verified: JQ].
- **Velvet Room / world spaces**: UI leaves the red/black system almost entirely — sampled captures under Stats/Resources run dark blue/gray (e.g. `#181830`, `#303048` buckets) because the environment art (Velvet Room, Mementos teal) dominates [verified: GUD + sampled stats, GUD:Persona-5-Royal03112021-110120-36515.jpg, -110212-99461.jpg]. Pack implication: world/backdrop fills, chrome stays tokenized [inference].

## 7. Skin-token implications (game-neutral)

- **Silhouette set — "jagged" family:**
  - Panels: irregular quadrilaterals with 1–2 corner cuts; edges non-parallel (skew 3–8°); avoid both rounded corners and perfect parallelograms [inference, grounded in MG/CM].
  - Spike parameters for `burst` shapes: 8–16 uneven points, inner radius ≈ 35–55% of outer, per-point angle jitter so no two spikes match; symmetric variant reserved for star accents [inference].
  - `slash` divider token: thin white band at 55–70° from horizontal, 2–6 px core with 1 px black keyline; also usable as a full-screen wipe edge [inference, grounded in FAM's white-line device].
  - `torn` edge painter: randomize edge amplitude 2–8 px with low frequency for collage/torn paper [inference, grounded in MG].
  - Speech-bubble containers: angular bubble with one sharp tail; thick black outline (≈3–5 px at 1080p) around white fill [inference, grounded in FRUIT].
- **Layer tokens:** always 3 inks — `surface` (black #000/#181818), `field` (red), `ink` (white #F0F0F0). Strokes: white keyline (1–2 px) between red and black; thick black outlines around white type. Support a red shade ramp (−25%/−50% lightness) inside red fields [inference from sampled shade buckets].
- **Color tokens:** `red.core ≈ #DC1600`, `red.bright ≈ #F01800`, `red.deep ≈ #8E0E00`, `black #000000`, `black.soft #181818`, `white #F0F0F0`, `accent.amber ≈ #F0A848` (≤5% usage), `accent.system-blue` optional for selection (base-P5 heritage; keep sparse) [verified: GUD + sampled stats for hexes; token split is inference].
- **Typography tokens:**
  - `display`: condensed grotesque, uppercase by default, forward italic (recommend 10–14°), tracking −1%…−4%, optional per-glyph rotation jitter ±3–8°, optional decorative mixed-case mode, thick outline stroke capability [verified: MG/LIU/FB for traits; numeric values inference].
  - `title`: display + larger size + red-on-white or white-on-red inversion; tagline stamp style supports all-caps with case-breaking glyphs [verified: FAN].
  - `body`: neutral humanist/neo-grotesque sans (Rodin-class), sentence case, no oblique, regular tracking [verified: GFL for Rodin identification; role mapping inference].
  - `numeric`: tabular figures; allow one semantic sub-color exception (P5 uses color for HP/MP-class data) [verified: FAM].
- **Procedural painters:**
  - `halftone`: params — dot scale small (≈2 px @1080p) for field texture, large (≈4–8 px) for edge zones; angle 45° or 0/90°; single-ink (white-on-red / red-on-black); density mask toward panel borders [inference, grounded in RG + sampled captures].
  - `burst`: star polygon painter for impact moments (see spike params) [inference].
  - `slash`: gradient-free diagonal wipe painter used for both dividers and transitions [inference].
- **Motion token mapping:**
  - `slash` → menu open/close: hard diagonal wipe from screen edge, ease-out, 150–350 ms, no overshoot; hierarchy change re-angles the layout [inference; 1-dot/1-frame tuning culture verified: FAM].
  - `flip`/pose-swap → menu page change drives a character key-pose swap (engine hook: per-page pose index) [verified: FAM for the behavior; token mapping inference].
  - `fade` → reserved for world-space backdrops only; chrome never soft-fades [inference].
  - Date roll: hold → swap (with color change) → hold; scale ≈ 6 s/2 s/4 s in P5, compress proportionally on smaller layouts [verified: ITCH for grammar].
  - Cut-in reveal: diagonal bust slide + burst overlay + tagline stamp; reserve a full-screen red monochrome takeover for "ultimate" moments; optional per-character accent hue (P5 precedent: blue for Caroline & Justine/Lavenza) [verified: FAN for grammar/hue-swap precedent; mapping inference].
  - Brightness-priority rule: raise surface lightness on the focused region, dim the rest — implement as a luminance token per pane [verified: FAM].
- **Anti-patterns to avoid** (would break the language): sub-color proliferation; symmetric even-radius bursts; rounded rectangles; centered symmetric layouts; body text in display font; large yellow fields [inference, from FAM's no-sub-color rule + MG/LIU/CM descriptions].

---

## Source URL index

1. FAM — https://www.famitsu.com/news/201711/13145540.html (Famitsu, CEDEC+KYUSHU 2017 UI session report, 2017-11-13)
2. PC — https://personacentral.com/persona-5-panel-concept-development-ui/ (Persona Central report of the same panel, 2017-11-13; read via Wayback snapshot 2024-05-18)
3. GUD — https://www.gameuidatabase.com/gameData.php?id=618 (Game UI Database — Persona 5 Royal gallery; sampled full-size captures include uploads/Persona-5-Royal03112021-110111-77734.jpg, -110112-69046.jpg, -110115-7501.jpg, -110116-41608.jpg, -110120-36515.jpg, -110212-99461.jpg, -110213-51138.jpg, et al.)
4. FRUIT — https://medium.com/@fruitcupkun/persona-5-menus-with-personality-and-readability-d6db2e0b253e
5. LIU — https://medium.com/game-design-fundamentals/visual-design-of-games-practice-analysis-of-persona-5-royal-61a5c18ba9c1
6. BOOT — https://medium.com/design-bootcamp/how-persona-5s-ui-balances-both-style-and-substance-de8cb1b807ef
7. JQ — https://medium.com/@josephquick/persona-5-royal-a-masterclass-in-ux-ace6a4da5dda
8. MG — https://madegooddesigns.com/persona-5-font/
9. FB — https://www.fontbolt.com/font/persona-5-font/
10. GFL — https://www.gamefontlibrary.com/games/persona-5-
11. FAN — https://megamitensei.fandom.com/wiki/All-Out_Attack
12. TSR — https://www.spriters-resource.com/ps4/persona5royal/
13. ITCH — https://itch.io/blog/533534/making-ui-come-to-life-study-of-persona-5
14. CM — https://www.chelseamai.com/persona
15. MOM — https://mechanicsofmagic.com/2022/04/23/visual-design-of-games-persona-5/
16. TCRF — https://tcrf.net/Proto:Persona_5/December_4th,_2015/Graphics/Menu_Graphics (read via Wayback snapshot 2025-06-06)
17. RG — https://www.researchgate.net/figure/Menu-in-Persona-5-featuring-decorative-halftone-patterns_fig1_351461270 (caption title verified via search result; page body bot-gated)
18. REDDIT — https://www.reddit.com/r/Persona5/comments/gmw6hz/persona_5_royal_fonts_if_i_find_more_about_the/ (blocked directly; cited via FB)
