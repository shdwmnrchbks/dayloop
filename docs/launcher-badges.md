# Launcher badge authoring — Phase 17c

Packs may decorate Dayloop-owned launcher **shortcut** identity with one small motif badge:

```json
"theme": {
  "art": {
    "launcherBadge": "art/launcher-badge.png"
  }
}
```

The slot name is exactly `launcherBadge`. The file is optional; omitting it keeps the existing Dayloop launcher treatment.

## Asset rules

- PNG only, so transparency and dimensions are deterministic across packlint and Android.
- Square, 48–256 px on each side.
- Maximum 128 KB.
- Pack-relative path; no absolute paths, backslashes, or `..` traversal.
- Use a small original/owned motif. Do **not** ship a game-owned launcher icon, platform icon, or copied logo as the badge.
- Keep the graphic simple: the runtime scales it into roughly 38% of the Dayloop shortcut icon and places it over a neutral circular plate.

`./gradlew packlint "-Ppack=content/packs/<slug>"` rejects missing, malformed, non-PNG, non-square, out-of-range, or oversized launcher badges.

## Android behavior

Android application launcher icons are compiled adaptive-icon resources. Arbitrary pack assets in `assets/` cannot safely replace the primary installed-app icon at runtime. Phase 17c therefore keeps Dayloop's primary sun/arrow identity unchanged and uses the Android-supported dynamic launcher-shortcut surface for pack decoration.

When the active pack declares `launcherBadge`, Dayloop publishes/updates one dynamic shortcut whose icon is the Dayloop launcher icon with the badge composited over it. Switching packs updates that shortcut. If the active pack has no badge, Dayloop removes the dynamic shortcut and falls back to the unchanged primary launcher icon.

The three bundled skins ship original abstract badge motifs for end-to-end coverage; none are official game launcher icons.
