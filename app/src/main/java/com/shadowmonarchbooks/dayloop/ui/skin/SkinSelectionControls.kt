package com.shadowmonarchbooks.dayloop.ui.skin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * Skin-aware single-choice marker.
 *
 * Slash-language skins replace Material's circular radio glyph with an angular
 * shard and diagonal ink mark while preserving a 48 dp semantic radio target.
 * Other skins retain the stock Material control.
 */
@Composable
fun SkinChoiceIndicator(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val skin = LocalSkin.current
    if (!skin.hasSkin || skin.motion != "slash") {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
        )
        return
    }

    val colors = MaterialTheme.colorScheme
    val alpha = if (enabled) 1f else 0.38f
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .selectable(
                selected = selected,
                enabled = enabled,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(9.dp)
            .graphicsLayer {
                rotationZ = if (selected) -3.5f else 1.5f
                this.alpha = alpha
            }
            .background(
                color = if (selected) colors.primary else colors.surfaceVariant,
                shape = skin.shapes.chip,
            )
            .border(1.dp, colors.onBackground, skin.shapes.chip),
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .width(17.dp)
                    .height(3.dp)
                    .graphicsLayer { rotationZ = -12f }
                    .background(colors.onPrimary),
            )
        }
    }
}

/**
 * Skin-aware binary checkbox marker.
 *
 * Slash-language skins use the same angular shard vocabulary as single-choice
 * controls, but keep checkbox semantics and a conventional check glyph. This
 * removes the rounded Material checkbox frame without sacrificing familiarity
 * or the 48 dp accessible target. Other skins retain Material's Checkbox.
 */
@Composable
fun SkinCheckboxIndicator(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val skin = LocalSkin.current
    if (!skin.hasSkin || skin.motion != "slash") {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = modifier,
        )
        return
    }

    val colors = MaterialTheme.colorScheme
    val interactive = enabled && onCheckedChange != null
    val alpha = if (enabled) 1f else 0.38f
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .toggleable(
                value = checked,
                enabled = interactive,
                role = Role.Checkbox,
                onValueChange = { value -> onCheckedChange?.invoke(value) },
            )
            .padding(9.dp)
            .graphicsLayer {
                rotationZ = if (checked) -2.5f else 1.5f
                this.alpha = alpha
            }
            .background(
                color = if (checked) colors.primary else colors.surfaceVariant,
                shape = skin.shapes.chip,
            )
            .border(1.dp, colors.onBackground, skin.shapes.chip),
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = colors.onPrimary,
                modifier = Modifier.size(19.dp),
            )
        }
    }
}
