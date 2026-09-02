package com.shadowmonarchbooks.dayloop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin

/**
 * Pack icon for surfaces that already render inside the active skin.
 *
 * Slash-language skins use the declared card silhouette so their icon frame
 * does not reintroduce rounded Material geometry inside otherwise-angular
 * chrome. Other skins delegate to the long-standing [PackIcon] implementation,
 * preserving onboarding and engine fallback behavior byte-for-byte.
 */
@Composable
fun SkinPackIcon(
    iconAsset: String?,
    title: String,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
) {
    val skin = LocalSkin.current
    if (!skin.hasSkin || skin.motion != "slash") {
        PackIcon(iconAsset = iconAsset, title = title, modifier = modifier, size = size)
        return
    }

    val bitmap = rememberAssetImage(iconAsset)
    Surface(
        shape = skin.shapes.card,
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 0.dp,
        modifier = modifier.size(size),
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = title
                        .split(Regex("[^\\p{L}\\p{N}]+"))
                        .filter { it.isNotBlank() }
                        .take(3)
                        .map { it.first().uppercaseChar() }
                        .joinToString("")
                        .ifEmpty { "?" },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(2.dp),
                )
            }
        }
    }
}
