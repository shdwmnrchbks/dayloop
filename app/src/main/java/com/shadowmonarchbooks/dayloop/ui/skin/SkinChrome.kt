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
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/** Engine-neutral navigation item consumed by the skin-aware bottom chrome. */
data class SkinNavItem(val route: String, val label: String, val icon: ImageVector)

private fun SkinSpec.isSlashChrome(): Boolean = hasSkin && motion == "slash"

/**
 * App-wide background treatment. Slash-language skins use a black field with
 * broad asymmetric red/white print fields, a diagonal guide line, and a small
 * halftone corner instead of Material tonal surface elevation. Other skins are
 * intentionally untouched.
 *
 * The content itself stays on the ordinary layout grid: these shapes are a
 * composition layer only, so readable text and accessible touch bounds never
 * inherit the visual skew.
 */
@Composable
fun Modifier.skinBackdrop(skin: SkinSpec): Modifier {
    if (!skin.isSlashChrome()) return this
    val colors = MaterialTheme.colorScheme
    val background = colors.background
    val red = colors.primary.copy(alpha = 0.30f)
    val redShade = colors.primaryContainer.copy(alpha = 0.22f)
    val white = colors.onBackground.copy(alpha = 0.11f)
    val keyline = colors.onBackground.copy(alpha = 0.32f)
    return this
        .background(background)
        .drawBehind {
            // Large off-axis fields make the page read as one split poster
            // composition instead of a stack of unrelated Material cards.
            val upperRed = Path().apply {
                moveTo(size.width * 0.54f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.28f)
                lineTo(size.width * 0.76f, size.height * 0.20f)
                close()
            }
            drawPath(upperRed, red)

            val lowerRed = Path().apply {
                moveTo(0f, size.height * 0.76f)
                lineTo(size.width * 0.24f, size.height * 0.82f)
                lineTo(size.width * 0.12f, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(lowerRed, redShade)

            val whiteSlash = Path().apply {
                moveTo(0f, size.height * 0.61f)
                lineTo(size.width * 0.43f, size.height * 0.73f)
                lineTo(size.width * 0.39f, size.height * 0.76f)
                lineTo(0f, size.height * 0.65f)
                close()
            }
            drawPath(whiteSlash, white)

            // The strong diagonal line is deliberately more visible than the
            // old corner tick: it acts as a gaze guide across the composition.
            drawLine(
                color = keyline,
                start = Offset(size.width * 0.03f, size.height * 0.58f),
                end = Offset(size.width * 0.48f, size.height * 0.72f),
                strokeWidth = 2.dp.toPx(),
            )

            // Sparse print dots, limited to one corner so the effect stays
            // decorative rather than becoming a noisy background texture.
            val dotColor = colors.onBackground.copy(alpha = 0.10f)
            val spacing = 12.dp.toPx()
            val radius = 1.25.dp.toPx()
            for (row in 0..5) {
                for (column in 0..7) {
                    val x = size.width - 6.dp.toPx() - column * spacing - if (row % 2 == 0) 0f else spacing / 2f
                    val y = 10.dp.toPx() + row * spacing
                    if (x >= size.width * 0.69f) {
                        drawCircle(dotColor, radius = radius, center = Offset(x, y))
                    }
                }
            }
        }
}

/**
 * Top app chrome. The slash family intentionally stops looking like a stock
 * Material toolbar: black field, layered red torn/ribbon title plate, offset
 * white paper edge, and hard icon treatment. All other skins keep the previous
 * Material bar.
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
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 3.dp, bottom = 3.dp),
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 3.dp, y = 3.dp)
                        .background(colors.onBackground, skin.shapes.header),
                )
                Surface(
                    shape = skin.shapes.header,
                    color = colors.primary,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { rotationZ = -1.2f }
                        .border(1.dp, colors.background, skin.shapes.header),
                ) {
                    Text(
                        text = skin.cased(title, "display"),
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onPrimary,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
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
 * pill indicator and tinted container with black ink, a visually rotated red
 * selection shard, and white keylines. The parent touch targets remain square
 * and stable; only the decorative selection layer rotates.
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
            items.forEachIndexed { index, item ->
                val selected = selectedRoute == item.route
                val content = if (selected) colors.onPrimary else colors.onBackground
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 54.dp)
                        .clickable { onSelect(item.route) }
                        .padding(horizontal = 2.dp, vertical = 5.dp),
                ) {
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(horizontal = 3.dp, vertical = 2.dp)
                                .graphicsLayer { rotationZ = if (index % 2 == 0) -2.4f else 2.0f }
                                .background(colors.primary, skin.shapes.chip)
                                .border(1.dp, colors.onBackground, skin.shapes.chip),
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = content,
                            modifier = Modifier.size(21.dp),
                        )
                        Text(
                            text = skin.cased(item.label, "display"),
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
    if (!skin.isSlashChrome()) {
        Surface(
            shape = if (skin.hasSkin) skin.shapes.chip else MaterialTheme.shapes.small,
            color = colors.surfaceVariant,
            contentColor = colors.onSurfaceVariant,
            shadowElevation = 0.dp,
            modifier = modifier,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
        return
    }

    Box(modifier = modifier.padding(end = 3.dp, bottom = 3.dp)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 3.dp, y = 3.dp)
                .background(colors.onBackground, skin.shapes.chip),
        )
        Surface(
            shape = skin.shapes.chip,
            color = colors.primary,
            contentColor = colors.onPrimary,
            shadowElevation = 0.dp,
            modifier = Modifier
                .graphicsLayer { rotationZ = -1.2f }
                .border(1.dp, colors.background, skin.shapes.chip),
        ) {
            Text(
                text = skin.cased(text, "display"),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
    }
}
