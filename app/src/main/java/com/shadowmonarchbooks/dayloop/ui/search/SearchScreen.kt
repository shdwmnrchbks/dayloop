package com.shadowmonarchbooks.dayloop.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.shadowmonarchbooks.dayloop.data.formatDate
import com.shadowmonarchbooks.dayloop.data.searchPack
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState

/**
 * In-app search across the selected pack (docs/PLAN.md Phase 5): walkthrough
 * steps, bond details, activities, deadlines, and answer sheets. Results use
 * the active profile's route for days.
 */
@Composable
fun SearchScreen(
    vm: DayloopViewModel = hiltViewModel(),
    onOpenDay: (String) -> Unit,
    onOpenBond: (String) -> Unit,
) {
    val state by vm.state.collectAsState()
    val pack = state.selected ?: run {
        EmptyState("No pack selected.")
        return
    }

    var query by remember { mutableStateOf("") }
    // Guarded surface (docs/ROADMAP-v2.md Phase 8): answer hits only exist for
    // packs declaring the capability, so results can never point at one the
    // pack lacks.
    val answersByDate = if (pack.pack.capabilities.answers) pack.answersByDate else emptyMap()
    val hits = remember(query, state.days, pack.id()) {
        searchPack(
            query = query,
            days = state.days,
            bonds = pack.bonds,
            activities = pack.activities,
            deadlines = pack.deadlines,
            answersByDate = answersByDate,
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            placeholder = { Text("Search steps, bonds, activities…") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
        )

        if (query.isNotBlank() && hits.isEmpty) {
            EmptyState("Nothing matches \"$query\" in this pack.")
            return
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        ) {
            if (hits.days.isNotEmpty()) {
                item { SectionHeader("Days") }
                items(hits.days, key = { "day-" + it.date }) { hit ->
                    ResultRow(
                        title = formatDate(hit.date, pack.calendar),
                        snippet = hit.snippet,
                        onClick = { onOpenDay(hit.date) },
                    )
                }
            }
            if (hits.bonds.isNotEmpty()) {
                item { SectionHeader(pack.pack.labels.bond + "s") }
                items(hits.bonds, key = { "bond-" + it.bondId }) { hit ->
                    ResultRow(
                        title = hit.label,
                        snippet = hit.snippet,
                        onClick = { onOpenBond(hit.bondId) },
                    )
                }
            }
            if (hits.activities.isNotEmpty()) {
                item { SectionHeader("Activities") }
                items(hits.activities, key = { "act-" + it.activityId }) { hit ->
                    ResultRow(title = hit.label, snippet = null, onClick = null)
                }
            }
            if (hits.deadlines.isNotEmpty()) {
                item { SectionHeader("Deadlines") }
                items(hits.deadlines, key = { "dl-" + it.deadlineId }) { hit ->
                    ResultRow(title = hit.label, snippet = null, onClick = null)
                }
            }
            if (hits.answers.isNotEmpty()) {
                item { SectionHeader("Answers") }
                items(hits.answers, key = { "ans-" + it.date }) { hit ->
                    ResultRow(
                        title = "${hit.label} · ${formatDate(hit.date, pack.calendar)}",
                        snippet = hit.snippet,
                        onClick = { onOpenDay(hit.date) },
                    )
                }
            }
        }
    }
}

/** Stable cache key for a pack's content identity. */
private fun com.shadowmonarchbooks.dayloop.data.LoadedPack.id(): String =
    "${pack.packId}@${pack.contentVersion}"

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
    )
}

@Composable
private fun ResultRow(
    title: String,
    snippet: String?,
    onClick: (() -> Unit)?,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            snippet?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
            }
        }
    }
}
