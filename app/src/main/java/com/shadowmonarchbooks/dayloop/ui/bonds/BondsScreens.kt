package com.shadowmonarchbooks.dayloop.ui.bonds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.data.describeCondition
import com.shadowmonarchbooks.dayloop.data.formatDate
import com.shadowmonarchbooks.dayloop.data.statLabels
import com.shadowmonarchbooks.dayloop.pack.schema.MediaKinds
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState
import com.shadowmonarchbooks.dayloop.ui.components.MediaImage
import com.shadowmonarchbooks.dayloop.ui.components.SkinTag
import com.shadowmonarchbooks.dayloop.ui.components.rememberAssetImage
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.skinDecor

/** Bond list — labels come from the pack ("Confidant", "Social Link", "Follower"). */
@Composable
fun BondsScreen(
    pack: LoadedPack?,
    onOpenBond: (String) -> Unit,
) {
    if (pack == null) {
        EmptyState("No pack selected.")
        return
    }
    if (pack.bonds.isEmpty()) {
        EmptyState("No bonds authored in this pack yet.")
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(pack.bonds, key = { it.id }) { bond ->
            val skin = LocalSkin.current
            if (skin.hasSkin) {
                // Arcana-card rows: the pack owns the silhouette. Ink/slash
                // skins add a hard keyline instead of Material elevation.
                Surface(
                    shape = skin.shapes.card,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (skin.shapeTokens["card"] == "jagged") {
                                Modifier.border(1.dp, MaterialTheme.colorScheme.outline, skin.shapes.card)
                            } else {
                                Modifier
                            },
                        )
                        .clickable { onOpenBond(bond.id) },
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = if (skin.motion == "slash") skin.cased(bond.label, "display") else bond.label,
                                style = if (skin.motion == "slash") {
                                    MaterialTheme.typography.displaySmall.copy(
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                        lineHeight = MaterialTheme.typography.titleLarge.lineHeight,
                                    )
                                } else {
                                    MaterialTheme.typography.titleMedium
                                },
                                color = if (skin.motion == "slash") {
                                    Color.White
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            )
                        },
                        supportingContent = {
                            val lastRouteDate = bond.ranks.lastOrNull()?.scheduledFor
                            Text(
                                buildString {
                                    append("${bond.ranks.size} rank(s)")
                                    if (lastRouteDate != null) {
                                        append(" · route max ${formatDate(lastRouteDate, pack.calendar)}")
                                    }
                                },
                                color = if (skin.motion == "slash") {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.skinDecor("panel"),
                    )
                }
            } else {
                ListItem(
                    headlineContent = { Text(bond.label, style = MaterialTheme.typography.titleMedium) },
                    supportingContent = {
                        val lastRouteDate = bond.ranks.lastOrNull()?.scheduledFor
                        Text(
                            buildString {
                                append("${bond.ranks.size} rank(s)")
                                if (lastRouteDate != null) {
                                    append(" · route max ${formatDate(lastRouteDate, pack.calendar)}")
                                }
                            },
                        )
                    },
                    modifier = Modifier.clickable { onOpenBond(bond.id) },
                )
                HorizontalDivider()
            }
        }
    }
}

/** Bond detail: route date, availability, gates, location, and anchored character art. */
@Composable
fun BondDetailScreen(
    bondId: String,
    pack: LoadedPack?,
) {
    val bond = pack?.bonds?.firstOrNull { it.id == bondId } ?: run {
        EmptyState("Bond not found in this pack.")
        return
    }
    val bondLabels = pack.bonds.associate { it.id to it.label }
    val statLabels = pack.pack.statLabels()
    val skin = LocalSkin.current
    val crown = skin.hasSkin && skin.motif == "crown"
    val slash = skin.hasSkin && skin.motion == "slash"
    val bondMedia = pack.mediaForBond(bondId)
    val background = bondMedia.firstOrNull { it.kind == MediaKinds.BANNER }
    val portraits = bondMedia.filter { it.kind == MediaKinds.PORTRAIT }

    Box(modifier = Modifier.fillMaxSize()) {
        background?.let { art ->
            rememberAssetImage(pack.assetOf(art))?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.BottomEnd,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .fillMaxWidth(0.76f)
                        .fillMaxHeight(0.58f)
                        .alpha(0.58f),
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            portraits.firstOrNull()?.let { portrait ->
                if (skin.hasSkin) {
                    Surface(
                        shape = skin.shapes.card,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .width(96.dp)
                            .aspectRatio(145f / 205f)
                            .then(
                                if (slash) Modifier.border(1.dp, MaterialTheme.colorScheme.outline, skin.shapes.card)
                                else Modifier
                            ),
                    ) {
                        MediaImage(
                            assetPath = pack.assetOf(portrait),
                            title = portrait.title,
                            size = 96.dp,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                } else {
                    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        MediaImage(
                            assetPath = pack.assetOf(portrait),
                            title = portrait.title,
                            size = 72.dp,
                            modifier = Modifier.padding(6.dp),
                        )
                    }
                }
            }
            Text(
                text = if (slash) skin.cased(bond.label, "display") else bond.label,
                style = if (slash) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineSmall,
            )
        }

        bond.characterLabel?.let { character ->
            Text(
                text = character,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        if (bond.ranks.any { it.scheduledFor != null }) {
            Text(
                text = "Route dates are the authored completion plan; they are not the same thing as general availability.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        bond.ranks.sortedBy { it.rank }.forEach { step ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                when {
                    crown -> {
                        Box(
                            modifier = Modifier.size(34.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            )
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .border(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f), CircleShape),
                            )
                            Text(
                                text = "${step.rank}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                    slash -> {
                        Surface(
                            shape = skin.shapes.chip,
                            color = MaterialTheme.colorScheme.primary,
                        ) {
                            Text(
                                text = "${step.rank}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 3.dp),
                            )
                        }
                    }
                    else -> Text(
                        text = "${step.rank}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    step.scheduledFor?.let {
                        SkinTag(
                            text = "Route · ${formatDate(it, pack.calendar)}",
                            container = MaterialTheme.colorScheme.primary,
                            content = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    listOfNotNull(
                        step.availableFrom?.let { "Available from ${formatDate(it, pack.calendar)}" },
                        step.availableUntil?.let { "Available until ${formatDate(it, pack.calendar)}" },
                    ).joinToString(" · ").takeIf { it.isNotEmpty() }?.let {
                        Text(text = it, style = MaterialTheme.typography.bodyMedium)
                    }
                    step.location?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    step.gates?.let { gate ->
                        Text(
                            text = "Requires: " + describeCondition(gate, statLabels, bondLabels),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    step.notes?.let { notes ->
                        Text(
                            text = notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
    }
}
