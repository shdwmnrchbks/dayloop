package com.shadowmonarchbooks.dayloop.ui.skin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
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
) {
    val skin = LocalSkin.current
    if (!skin.hasSkin || skin.motion != "slash") {
        RadioButton(
            selected = selected,
            onClick = onClick,
            modifier = modifier,
        )
        return
    }

    val colors = MaterialTheme.colorScheme
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(9.dp)
            .graphicsLayer { rotationZ = if (selected) -3.5f else 1.5f }
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
