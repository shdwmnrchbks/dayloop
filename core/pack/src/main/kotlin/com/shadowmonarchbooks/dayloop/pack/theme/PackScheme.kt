package com.shadowmonarchbooks.dayloop.pack.theme

import com.materialkolor.hct.Hct
import com.materialkolor.scheme.DynamicScheme
import com.materialkolor.scheme.SchemeContent
import com.materialkolor.scheme.SchemeExpressive
import com.materialkolor.scheme.SchemeTonalSpot
import com.materialkolor.scheme.SchemeVibrant
import com.shadowmonarchbooks.dayloop.pack.schema.PackTheme

/**
 * Seed → Material tonal scheme mapping, shared by the app renderer and
 * packlint (docs/ROADMAP-v2.md Phase 10 / docs/ROADMAP-v3.md Phase 12). Living
 * here means the contrast rule validates the *exact* colors the app will
 * render for a pack's declared seeds — one source of truth, no drift.
 *
 * Everything in this file is engine-neutral: scheme variants are keyed by the
 * closed-set `theme.style` token; no game names, no Compose types (plain ARGB
 * ints, so JVM tests and packlint can consume them).
 */

/** Closed-set scheme character tokens (pack.json `theme.style`). */
val THEME_STYLES: Set<String> = setOf("tonalSpot", "vibrant", "expressive", "content")

/** Builds the dynamic scheme variant for a `theme.style` token (null = calm default). */
fun buildScheme(style: String?, seed: Hct, dark: Boolean): DynamicScheme = when (style) {
    "vibrant" -> SchemeVibrant(seed, dark, 0.0)
    "expressive" -> SchemeExpressive(seed, dark, 0.0)
    "content" -> SchemeContent(seed, dark, 0.0)
    else -> SchemeTonalSpot(seed, dark, 0.0)
}

/**
 * Every Material 3 color role the renderer materializes for a pack scheme,
 * with its DynamicColor resolver. The keys are the engine vocabulary used by
 * [schemeArgb] and [contrastPairs]; the app maps them onto MaterialTheme's
 * ColorScheme roles 1:1.
 */
private val SCHEME_ROLES: List<Pair<String, (com.materialkolor.dynamiccolor.MaterialDynamicColors) -> com.materialkolor.dynamiccolor.DynamicColor>> = listOf(
    "primary" to { m -> m.primary() },
    "onPrimary" to { m -> m.onPrimary() },
    "primaryContainer" to { m -> m.primaryContainer() },
    "onPrimaryContainer" to { m -> m.onPrimaryContainer() },
    "inversePrimary" to { m -> m.inversePrimary() },
    "secondary" to { m -> m.secondary() },
    "onSecondary" to { m -> m.onSecondary() },
    "secondaryContainer" to { m -> m.secondaryContainer() },
    "onSecondaryContainer" to { m -> m.onSecondaryContainer() },
    "tertiary" to { m -> m.tertiary() },
    "onTertiary" to { m -> m.onTertiary() },
    "tertiaryContainer" to { m -> m.tertiaryContainer() },
    "onTertiaryContainer" to { m -> m.onTertiaryContainer() },
    "error" to { m -> m.error() },
    "onError" to { m -> m.onError() },
    "errorContainer" to { m -> m.errorContainer() },
    "onErrorContainer" to { m -> m.onErrorContainer() },
    "background" to { m -> m.background() },
    "onBackground" to { m -> m.onBackground() },
    "surface" to { m -> m.surface() },
    "onSurface" to { m -> m.onSurface() },
    "surfaceVariant" to { m -> m.surfaceVariant() },
    "onSurfaceVariant" to { m -> m.onSurfaceVariant() },
    "surfaceTint" to { m -> m.surfaceTint() },
    "inverseSurface" to { m -> m.inverseSurface() },
    "inverseOnSurface" to { m -> m.inverseOnSurface() },
    "outline" to { m -> m.outline() },
    "outlineVariant" to { m -> m.outlineVariant() },
    "scrim" to { m -> m.scrim() },
    "surfaceDim" to { m -> m.surfaceDim() },
    "surfaceBright" to { m -> m.surfaceBright() },
    "surfaceContainerLowest" to { m -> m.surfaceContainerLowest() },
    "surfaceContainerLow" to { m -> m.surfaceContainerLow() },
    "surfaceContainer" to { m -> m.surfaceContainer() },
    "surfaceContainerHigh" to { m -> m.surfaceContainerHigh() },
    "surfaceContainerHighest" to { m -> m.surfaceContainerHighest() },
)

/**
 * Materializes a pack's scheme for [dark] mode into role name → ARGB int.
 * Returns null when the theme declares no parseable seed (the engine then
 * renders its own fixed palette — never lint-checked as pack data).
 */
fun schemeArgb(theme: PackTheme, dark: Boolean): Map<String, Int>? {
    val seedArgb = theme.seedArgb(dark) ?: return null
    val scheme = buildScheme(theme.style, Hct.fromInt(seedArgb), dark)
    val m = com.materialkolor.dynamiccolor.MaterialDynamicColors()
    return SCHEME_ROLES.associate { (name, role) -> name to role(m).getArgb(scheme) }
}

/**
 * The text-carrying role pairs the engine actually composes (role rendered as
 * text/foreground, role rendered as its container/background), used by the
 * packlint contrast rule (docs/ROADMAP-v3.md guardrail 5).
 */
val CONTRAST_PAIRS: List<Pair<String, String>> = listOf(
    "onBackground" to "background",
    "onSurface" to "surface",
    "onSurfaceVariant" to "surface",
    "onSurfaceVariant" to "surfaceVariant",
    "onPrimary" to "primary",
    "onPrimaryContainer" to "primaryContainer",
    "onSecondary" to "secondary",
    "onSecondaryContainer" to "secondaryContainer",
    "onTertiary" to "tertiary",
    "onTertiaryContainer" to "tertiaryContainer",
    "onError" to "error",
    "onErrorContainer" to "errorContainer",
    "inverseOnSurface" to "inverseSurface",
)

/** One measured contrast pair: foreground role, background role, WCAG ratio. */
data class ContrastResult(val foreground: String, val background: String, val ratio: Double)

/** WCAG 2.x relative contrast math over ARGB ints (shared: lint + JVM tests). */
object Wcag {
    private fun channel(c: Int): Double {
        val s = c / 255.0
        return if (s <= 0.03928) s / 12.92 else Math.pow((s + 0.055) / 1.055, 2.4)
    }

    /** WCAG 2.x relative luminance of an ARGB int. */
    fun relativeLuminance(argb: Int): Double {
        val r = channel((argb shr 16) and 0xFF)
        val g = channel((argb shr 8) and 0xFF)
        val b = channel(argb and 0xFF)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    /** WCAG 2.x contrast ratio (1.0 .. 21.0) between two ARGB ints. */
    fun contrastRatio(a: Int, b: Int): Double {
        val la = relativeLuminance(a)
        val lb = relativeLuminance(b)
        val lighter = maxOf(la, lb)
        val darker = minOf(la, lb)
        return (lighter + 0.05) / (darker + 0.05)
    }

    /** WCAG AA for normal body text. */
    const val AA_NORMAL = 4.5

    /** WCAG AA for large text (≥ 18pt / 14pt bold) — used where the UI renders large display type. */
    const val AA_LARGE = 3.0
}

/**
 * Measures every text-carrying pair of a materialized scheme. Empty when the
 * theme declares no seeds.
 */
fun contrastResults(theme: PackTheme, dark: Boolean): List<ContrastResult> {
    val scheme = schemeArgb(theme, dark) ?: return emptyList()
    return CONTRAST_PAIRS.map { (fg, bg) ->
        ContrastResult(fg, bg, Wcag.contrastRatio(scheme.getValue(fg), scheme.getValue(bg)))
    }
}
