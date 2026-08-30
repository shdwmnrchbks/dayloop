package com.shadowmonarchbooks.dayloop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark-first (docs/PLAN.md §5): deep neutral surfaces with a lantern-warm accent.
// Pack theming rides in pack data later (docs/PLAN.md §3.5); this is the engine skin.

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

@Composable
fun DayloopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
