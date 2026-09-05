# P5R atmosphere and surface audit — rc7

This audit tracks the eight visual and settings changes requested after rc6. The implementation stays pack-driven: Kotlin branches on generic skin and art-slot tokens, never a game id.

| Area | rc7 contract |
| --- | --- |
| Theme | Compose and system bars stay on the dark presentation regardless of device appearance |
| Today art | `theme.art["today-day"]` and `theme.art["today-night"]` provide matching full-screen scenes with a black readability veil |
| Scene state | Night is selected only when every task in the pack's first authored time slot is Done; the crossfade is 1.2 seconds |
| Today actions | Pinned BACK and END DAY commands have full touch targets but no plate, border, shadow, or footer surface |
| Other pages | Slash-family pages use black with two low-opacity red radial haze fields |
| Calendar | Slash-family date cells are transparent; enlarged numerals carry the day-kind color and TODAY uses an oversized red plate |
| Confidants | Index rows are rectangular face strips cropped from each bond's existing banner, with a black horizontal readability gradient |
| Settings | Footer reads the generated `BuildConfig.VERSION_NAME`, so it cannot drift from the installed APK |

## Asset provenance and serving

The two user-supplied PNGs are stored verbatim as `content/packs/p5r/art/today-day.png` and `today-night.png`. SHA-256 checksums:

- Day: `7cdfd7bc2fa2336ed1058e21a5bdee7094a23485afbf30c8f056fa7b3e9d5ed1`
- Night: `21d21ddb9e8ae32352df118a624b82aef415261c84a3810575098d68a5fa5d31`

Both are declared in `pack.json`, so packlint validates that the installed content includes them. P5R content version advances to 10 and the app version advances to `0.12.0-rc7` (`versionCode 20`).

## Verification gate

The release workflow must assemble the candidate APK, run app/core/tool unit tests, and validate all three bundled packs before it may publish rc7 from merged `main`.
