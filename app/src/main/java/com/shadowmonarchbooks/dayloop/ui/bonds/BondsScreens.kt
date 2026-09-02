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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
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
                // Arcana-card rows (docs/ROADMAP-v3.md Phase 13): each bond is
                // a card in the pack's card silhouette; dividers go away.
                // Crown language (Phase 15): the row's panel decor (parchment
                // fill + filigree frame) renders behind the list content.
                Surface(
                    shape = skin.shapes.card,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenBond(bond.id) },
                ) {
                    ListItem(
                        headlineContent = { Text(bond.label, style = MaterialTheme.typography.titleMedium) },
                        supportingContent = {
                            val lastFrom = bond.ranks.lastOrNull()?.availableFrom
                            Text(
                                buildString {
                                    append("${bond.ranks.size} rank(s)")
                                    if (lastFrom != null) append(" · latest from ${formatDate(lastFrom, pack.calendar)}")
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
                        val lastFrom = bond.ranks.lastOrNull()?.availableFrom
                        Text(
                            buildString {
                                append("${bond.ranks.size} rank(s)")
                                if (lastFrom != null) append(" · latest from ${formatDate(lastFrom, pack.calendar)}")
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

/** Bond detail: rank ladder with availability, gates, location, and anchored character art. */
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
    // Crown language (docs/ROADMAP-v3.md Phase 15): follower ranks render as
    // embossed medallions — a filled crest with a double ring and the rank
    // number at its center.
    val crown = LocalSkin.current.hasSkin && LocalSkin.current.motif == "crown"
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
        // Pack-supplied portrait art (docs/ROADMAP-v3.md Phase 11): media.json
        // items anchored to this bond render beside the arcana label. Skinned
        // packs frame it as a tarot-card slip at the native 145×205 portrait
        // ratio (docs/ROADMAP-v3.md Phase 14).
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            portraits.firstOrNull()?.let { portrait ->
                val skin = LocalSkin.current
                if (skin.hasSkin) {
                    Surface(
                        shape = skin.shapes.card,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .width(96.dp)
                            .aspectRatio(145f / 205f),
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
            Text(bond.label, style = MaterialTheme.typography.headlineSmall)
        }

        bond.characterLabel?.let { character ->
            Text(
                text = character,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        bond.ranks.sortedBy { it.rank }.forEach { step ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (crown) {
                    // Embossed medallion (Phase 15): filled crest, double ring,
                    // the rank number in the center of the crest.
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
                } else {
                    Text(
                        text = "${step.rank}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Column {
                    listOfNotNull(
                        step.availableFrom?.let { "From ${formatDate(it, pack.calendar)}" },
                        step.availableUntil?.let { "Until ${formatDate(it, pack.calendar)}" },
                    ).joinToString(" · ").takeIf { it.isNotEmpty() }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    step.location?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    // The §3.3 promise ("why is this locked today?") rendered as
                    // pack-supplied predicates → one spoiler-safe line
                    // (docs/ROADMAP-v2.md Phase 9).
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
