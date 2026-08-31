package com.shadowmonarchbooks.dayloop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.dynamiccolor.DynamicColor
import com.materialkolor.dynamiccolor.MaterialDynamicColors
import com.materialkolor.hct.Hct
import com.materialkolor.scheme.DynamicScheme
import com.materialkolor.scheme.SchemeContent
import com.materialkolor.scheme.SchemeExpressive
import com.materialkolor.scheme.SchemeTonalSpot
import com.materialkolor.scheme.SchemeVibrant
import com.shadowmonarchbooks.dayloop.pack.schema.PackTheme

// Engine skin (dark-first, docs/PLAN.md §5): deep neutral surfaces with a
// lantern-warm accent. It is the fallback for packs that declare no `theme`.
// Pack theming rides in pack data (docs/PLAN.md §3.5, docs/ROADMAP-v2.md
// Phase 10): the pack supplies seed colors + a style token, [packColorScheme]
// maps them to full Material 3 schemes — no game names or per-game colors
// live in Kotlin.

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE8B84B),
    onPrimary = Color(0xFF241A00),
    primaryContainer = Color(0xFF5C4300),
    onPrimaryContainer = Color(0xFFFFE08D),
    secondary = Color(0xFF9AC9FF),
    onSecondary = Color(0xFF002F5C),
    secondaryContainer = Color(0xFF1E4573),
    onSecondaryContainer = Color(0xFFD4E3FF),
    tertiary = Color(0xFFE4BAD8),
    onTertiary = Color(0xFF44263A),
    tertiaryContainer = Color(0xFF5C3A50),
    onTertiaryContainer = Color(0xFFFFD8EE),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF17130E),
    onBackground = Color(0xFFEDE1D4),
    surface = Color(0xFF17130E),
    onSurface = Color(0xFFEDE1D4),
    surfaceVariant = Color(0xFF4F4539),
    onSurfaceVariant = Color(0xFFD3C4B4),
    outline = Color(0xFF9C8F80),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF7A5900),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDEA1),
    onPrimaryContainer = Color(0xFF261A00),
    secondary = Color(0xFF4D6076),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1E4FF),
    onSecondaryContainer = Color(0xFF081D32),
    tertiary = Color(0xFF6C5670),
    onTertiary = Color(0xFFFFFFFF),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFF8F1),
    onBackground = Color(0xFF1E1B16),
    surface = Color(0xFFFFF8F1),
    onSurface = Color(0xFF1E1B16),
    surfaceVariant = Color(0xFFEDE1D4),
    onSurfaceVariant = Color(0xFF4F4539),
    outline = Color(0xFF807568),
)

/**
 * Engine-neutral scheme-character token (pack.json `theme.style`) → material
 * scheme variant. Tokens the pack doesn't declare fall back to the calm
 * tonal-spot default; packlint validates the closed set.
 */
private fun buildScheme(style: String?, seed: Hct, dark: Boolean): DynamicScheme = when (style) {
    "vibrant" -> SchemeVibrant(seed, dark, 0.0)
    "expressive" -> SchemeExpressive(seed, dark, 0.0)
    "content" -> SchemeContent(seed, dark, 0.0)
    else -> SchemeTonalSpot(seed, dark, 0.0)
}

/**
 * Builds the pack's Material 3 [ColorScheme] for [dark] mode from its declared
 * seeds and style token. Hand-tuning lives in the pack data (accent seeds are
 * chosen per pack); this mapping is game-neutral and identical for every pack.
 * A theme without parseable colors falls back to the engine skin.
 */
fun packColorScheme(theme: PackTheme, dark: Boolean): ColorScheme {
    val seedArgb = theme.seedArgb(dark) ?: return if (dark) DarkColors else LightColors
    val scheme = buildScheme(theme.style, Hct.fromInt(seedArgb), dark)
    val m = MaterialDynamicColors()
    fun c(role: () -> DynamicColor): Color = Color(role().getArgb(scheme))
    return if (dark) {
        darkColorScheme(
            primary = c(m::primary),
            onPrimary = c(m::onPrimary),
            primaryContainer = c(m::primaryContainer),
            onPrimaryContainer = c(m::onPrimaryContainer),
            inversePrimary = c(m::inversePrimary),
            secondary = c(m::secondary),
            onSecondary = c(m::onSecondary),
            secondaryContainer = c(m::secondaryContainer),
            onSecondaryContainer = c(m::onSecondaryContainer),
            tertiary = c(m::tertiary),
            onTertiary = c(m::onTertiary),
            tertiaryContainer = c(m::tertiaryContainer),
            onTertiaryContainer = c(m::onTertiaryContainer),
            error = c(m::error),
            onError = c(m::onError),
            errorContainer = c(m::errorContainer),
            onErrorContainer = c(m::onErrorContainer),
            background = c(m::background),
            onBackground = c(m::onBackground),
            surface = c(m::surface),
            onSurface = c(m::onSurface),
            surfaceVariant = c(m::surfaceVariant),
            onSurfaceVariant = c(m::onSurfaceVariant),
            surfaceTint = c(m::surfaceTint),
            inverseSurface = c(m::inverseSurface),
            inverseOnSurface = c(m::inverseOnSurface),
            outline = c(m::outline),
            outlineVariant = c(m::outlineVariant),
            scrim = c(m::scrim),
            surfaceDim = c(m::surfaceDim),
            surfaceBright = c(m::surfaceBright),
            surfaceContainerLowest = c(m::surfaceContainerLowest),
            surfaceContainerLow = c(m::surfaceContainerLow),
            surfaceContainer = c(m::surfaceContainer),
            surfaceContainerHigh = c(m::surfaceContainerHigh),
            surfaceContainerHighest = c(m::surfaceContainerHighest),
        )
    } else {
        lightColorScheme(
            primary = c(m::primary),
            onPrimary = c(m::onPrimary),
            primaryContainer = c(m::primaryContainer),
            onPrimaryContainer = c(m::onPrimaryContainer),
            inversePrimary = c(m::inversePrimary),
            secondary = c(m::secondary),
            onSecondary = c(m::onSecondary),
            secondaryContainer = c(m::secondaryContainer),
            onSecondaryContainer = c(m::onSecondaryContainer),
            tertiary = c(m::tertiary),
            onTertiary = c(m::onTertiary),
            tertiaryContainer = c(m::tertiaryContainer),
            onTertiaryContainer = c(m::onTertiaryContainer),
            error = c(m::error),
            onError = c(m::onError),
            errorContainer = c(m::errorContainer),
            onErrorContainer = c(m::onErrorContainer),
            background = c(m::background),
            onBackground = c(m::onBackground),
            surface = c(m::surface),
            onSurface = c(m::onSurface),
            surfaceVariant = c(m::surfaceVariant),
            onSurfaceVariant = c(m::onSurfaceVariant),
            surfaceTint = c(m::surfaceTint),
            inverseSurface = c(m::inverseSurface),
            inverseOnSurface = c(m::inverseOnSurface),
            outline = c(m::outline),
            outlineVariant = c(m::outlineVariant),
            scrim = c(m::scrim),
            surfaceDim = c(m::surfaceDim),
            surfaceBright = c(m::surfaceBright),
            surfaceContainerLowest = c(m::surfaceContainerLowest),
            surfaceContainerLow = c(m::surfaceContainerLow),
            surfaceContainer = c(m::surfaceContainer),
            surfaceContainerHigh = c(m::surfaceContainerHigh),
            surfaceContainerHighest = c(m::surfaceContainerHighest),
        )
    }
}

@Composable
fun DayloopTheme(
    theme: PackTheme? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = when {
            theme != null -> packColorScheme(theme, darkTheme)
            darkTheme -> DarkColors
            else -> LightColors
        },
        content = content,
    )
}
