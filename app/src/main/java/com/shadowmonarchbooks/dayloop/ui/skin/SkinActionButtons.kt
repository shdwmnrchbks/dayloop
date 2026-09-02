package com.shadowmonarchbooks.dayloop.ui.skin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Skin-aware primary action.
 *
 * Slash-language skins replace Material's tonal button container with a flat
 * accent command strip, white pasted-paper offset and hard keyline. Other
 * skins keep the stock Material button byte-for-byte at the call site.
 */
@Composable
fun SkinActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SlashActionButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        treatment = SlashActionTreatment.Primary,
        fallback = {
            Button(onClick = onClick, enabled = enabled, modifier = modifier) {
                Text(text)
            }
        },
    )
}

/** Skin-aware outlined action; non-slash skins retain Material OutlinedButton. */
@Composable
fun SkinOutlinedActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SlashActionButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        treatment = SlashActionTreatment.Outlined,
        fallback = {
            OutlinedButton(onClick = onClick, enabled = enabled, modifier = modifier) {
                Text(text)
            }
        },
    )
}

/** Skin-aware low-emphasis action; non-slash skins retain Material TextButton. */
@Composable
fun SkinTextActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    SlashActionButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        treatment = SlashActionTreatment.Text,
        fallback = {
            TextButton(onClick = onClick, enabled = enabled, modifier = modifier) {
                Text(text)
            }
        },
    )
}

private enum class SlashActionTreatment { Primary, Outlined, Text }

@Composable
private fun SlashActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    treatment: SlashActionTreatment,
    fallback: @Composable () -> Unit,
) {
    val skin = LocalSkin.current
    if (!skin.hasSkin || skin.motion != "slash") {
        fallback()
        return
    }

    val colors = MaterialTheme.colorScheme
    val primary = treatment == SlashActionTreatment.Primary
    val container = if (primary) colors.primary else colors.background
    val content = if (primary) colors.onPrimary else colors.onBackground
    val rotation = when (treatment) {
        SlashActionTreatment.Primary -> -1.4f
        SlashActionTreatment.Outlined -> 0.8f
        SlashActionTreatment.Text -> -0.6f
    }

    Box(
        modifier = modifier
            .graphicsLayer { alpha = if (enabled) 1f else 0.38f }
            .padding(end = if (primary) 3.dp else 0.dp, bottom = if (primary) 3.dp else 0.dp),
    ) {
        if (primary) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 3.dp, y = 3.dp)
                    .background(colors.onBackground, skin.shapes.chip),
            )
        }
        Surface(
            onClick = onClick,
            enabled = enabled,
            shape = skin.shapes.chip,
            color = container,
            contentColor = content,
            border = BorderStroke(1.dp, colors.onBackground),
            shadowElevation = 0.dp,
            modifier = Modifier
                .heightIn(min = 48.dp)
                .graphicsLayer { rotationZ = rotation },
        ) {
            Text(
                text = skin.cased(text, "display"),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            )
        }
    }
}
