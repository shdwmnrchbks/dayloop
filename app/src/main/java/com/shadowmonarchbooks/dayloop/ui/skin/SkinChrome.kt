package com.shadowmonarchbooks.dayloop.ui.skin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/** Engine-neutral navigation item consumed by the skin-aware bottom chrome. */
data class SkinNavItem(val route: String, val label: String, val icon: ImageVector)

private fun SkinSpec.isSlashChrome(): Boolean = hasSkin && motion == "slash"

/**
 * App-wide background treatment. Slash-language skins use a black field with
 * sparse red and white diagonal print marks instead of Material tonal surface
 * elevation. Other skins are intentionally untouched.
 */
@Composable
fun Modifier.skinBackdrop(skin: SkinSpec): Modifier {
    if (!skin.isSlashChrome()) return this
    val background = MaterialTheme.colorScheme.background
    val red = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
    val white = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.16f)
    return this
        .background(background)
        .drawBehind {
            val band = size.width * 0.20f
            drawLine(
                color = red,
                start = Offset(size.width - band, 0f),
                end = Offset(size.width, size.height * 0.18f),
                strokeWidth = 28f,
            )
            drawLine(
                color = white,
                start = Offset(0f, size.height * 0.78f),
                end = Offset(size.width * 0.28f, size.height),
                strokeWidth = 3f,
            )
        }
}

/**
 * Top app chrome. The slash family intentionally stops looking like a stock
 * Material toolbar: black field, red torn/ribbon title plate, white keyline,
 * hard icon treatment. All other skins keep the previous Material bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinTopBar(
    title: String,
    canGoBack: Boolean,
    onBack: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val skin = LocalSkin.current
    val colors = MaterialTheme.colorScheme
    if (!skin.isSlashChrome()) {
        TopAppBar(
            modifier = Modifier
                .background(colors.surface)
                .skinDecor("header"),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            navigationIcon = {
                if (canGoBack) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            },
            title = { Text(title, style = MaterialTheme.typography.titleMedium) },
            actions = {
                IconButton(onClick = onOpenSearch) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            },
        )
        return
    }

    Surface(color = colors.background, shadowElevation = 0.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .drawBehind {
                    drawLine(
                        color = colors.onBackground,
                        start = Offset(0f, size.height - 1f),
                        end = Offset(size.width, size.height - 1f),
                        strokeWidth = 2f,
                    )
                }
                .padding(horizontal = 8.dp, vertical = 7.dp),
        ) {
            if (canGoBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.onBackground,
                    )
                }
            }
            Surface(
                shape = skin.shapes.header,
                color = colors.primary,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, colors.onBackground, skin.shapes.header),
            ) {
                Text(
                    text = skin.cased(title, "display"),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onPrimary,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            IconButton(onClick = onOpenSearch) {
                Icon(Icons.Filled.Search, contentDescription = "Search", tint = colors.onBackground)
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = colors.onBackground)
            }
        }
    }
}

/**
 * Bottom navigation for all skins. Slash-language packs replace Material's
 * pill indicator and tinted container with black ink, white keylines and a red
 * angular selected shard.
 */
@Composable
fun SkinBottomBar(
    items: List<SkinNavItem>,
    selectedRoute: String?,
    onSelect: (String) -> Unit,
) {
    val skin = LocalSkin.current
    val colors = MaterialTheme.colorScheme
    if (!skin.isSlashChrome()) {
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    selected = selectedRoute == item.route,
                    onClick = { onSelect(item.route) },
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(item.label) },
                )
            }
        }
        return
    }

    Surface(
        color = colors.background,
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = colors.onBackground,
                    start = Offset(0f, 1f),
                    end = Offset(size.width, 1f),
                    strokeWidth = 2f,
                )
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 5.dp),
        ) {
            items.forEach { item ->
                val selected = selectedRoute == item.route
                val container = if (selected) colors.primary else Color.Transparent
                val content = if (selected) colors.onPrimary else colors.onBackground
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 54.dp)
                        .background(container, skin.shapes.chip)
                        .then(
                            if (selected) Modifier.border(1.dp, colors.onBackground, skin.shapes.chip)
                            else Modifier
                        )
                        .clickable { onSelect(item.route) }
                        .padding(horizontal = 2.dp, vertical = 5.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = content,
                            modifier = Modifier.size(21.dp),
                        )
                        Text(
                            text = if (selected) skin.cased(item.label, "display") else item.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = content,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

/** A small route/source banner that follows the same generic skin vocabulary. */
@Composable
fun SkinRouteBadge(text: String, modifier: Modifier = Modifier) {
    val skin = LocalSkin.current
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = if (skin.hasSkin) skin.shapes.chip else MaterialTheme.shapes.small,
        color = if (skin.isSlashChrome()) colors.primary else colors.surfaceVariant,
        contentColor = if (skin.isSlashChrome()) colors.onPrimary else colors.onSurfaceVariant,
        shadowElevation = 0.dp,
        modifier = modifier.then(
            if (skin.isSlashChrome()) Modifier.border(1.dp, colors.outline, skin.shapes.chip)
            else Modifier
        ),
    ) {
        Text(
            text = if (skin.isSlashChrome()) skin.cased(text, "display") else text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}
