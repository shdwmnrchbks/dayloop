package com.shadowmonarchbooks.dayloop.ui.bonds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.data.formatDate
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState

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

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(pack.bonds, key = { it.id }) { bond ->
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

/** Bond detail: rank ladder with availability; character and notes behind taps. */
@Composable
fun BondDetailScreen(
    bondId: String,
    pack: LoadedPack?,
) {
    val bond = pack?.bonds?.firstOrNull { it.id == bondId } ?: run {
        EmptyState("Bond not found in this pack.")
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(bond.label, style = MaterialTheme.typography.headlineSmall)

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
                    step.availableFrom?.let {
                        Text(
                            text = "From ${formatDate(it, pack.calendar)}",
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
