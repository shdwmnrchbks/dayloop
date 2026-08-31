package com.shadowmonarchbooks.dayloop.ui.bonds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.data.describeCondition
import com.shadowmonarchbooks.dayloop.data.formatDate
import com.shadowmonarchbooks.dayloop.data.statLabels
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState
import com.shadowmonarchbooks.dayloop.ui.components.MediaImage
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin

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

/** Bond detail: rank ladder with availability, gates, and location; character and notes behind taps. */
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
        val portraits = pack.mediaForBond(bondId)
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
            var shown by remember(bond.id) { mutableStateOf(false) }
            if (shown) {
                Text(
                    text = character,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            } else {
                Surface(
                    onClick = { shown = true },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Text(
                        text = "Show character",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }
        }

        bond.ranks.sortedBy { it.rank }.forEach { step ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "${step.rank}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
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
                        var revealed by remember(bond.id, step.rank) { mutableStateOf(false) }
                        if (revealed) {
                            Text(
                                text = notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            Text(
                                text = "Note — tap to show",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}
