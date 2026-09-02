package com.shadowmonarchbooks.dayloop.ui.activities

import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowmonarchbooks.dayloop.data.statLabels
import com.shadowmonarchbooks.dayloop.pack.schema.Activity
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState
import com.shadowmonarchbooks.dayloop.ui.components.SkinTag
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.skinDecor

/**
 * Activities browsing (docs/ROADMAP-v2.md Phase 9): the pack's activity
 * catalog — stat gains, locations/sources, notes — reachable from the Today
 * entry point, tappable activity references on steps, and search hits. Engine-
 * neutral: kinds/labels come from the pack; spoiler activities stay hidden
 * behind a tap (docs/PLAN.md §6.2).
 */
@Composable
fun ActivitiesScreen(
    vm: DayloopViewModel = hiltViewModel(),
    onOpenActivity: (String) -> Unit,
) {
    val state by vm.state.collectAsState()
    val pack = state.selected ?: run {
        EmptyState("No pack selected.")
        return
    }
    if (pack.activities.isEmpty()) {
        EmptyState("No activities authored in this pack yet.")
        return
    }

    val skin = LocalSkin.current
    val slash = skin.hasSkin && skin.motion == "slash"
    val sorted = pack.activities.values.sortedBy { it.label.lowercase() }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        items(sorted, key = { it.id }) { activity ->
            val cardShape = skin.shapes.card
            Surface(
                shape = cardShape,
                color = if (slash) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (slash) Modifier.border(1.dp, MaterialTheme.colorScheme.onBackground, cardShape)
                        else Modifier,
                    )
                    .clickable { onOpenActivity(activity.id) },
            ) {
                Column(Modifier.skinDecor("panel").padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActivityKindChip(activity.kind)
                        Text(
                            text = if (slash) skin.cased(activity.label, "display") else activity.label,
                            style = if (slash) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (slash) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (!activity.spoiler) {
                        ActivityMetadata(activity, pack.pack.statLabels(), slash)
                    } else {
                        Text(
                            text = "Spoiler — tap to open",
                            style = MaterialTheme.typography.labelMedium,
                            fontStyle = FontStyle.Italic,
                            color = if (slash) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }
        }
    }
}

/** Full activity detail: kind, location/source, stat gains, notes, walkthrough references. */
@Composable
fun ActivityDetailScreen(
    activityId: String,
    vm: DayloopViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    val pack = state.selected ?: run {
        EmptyState("No pack selected.")
        return
    }
    val activity = pack.activities[activityId] ?: run {
        EmptyState("Activity not found in this pack.")
        return
    }

    val references = state.days.values.sumOf { day ->
        day.steps.count { it.activityRef == activity.id }
    }
    val skin = LocalSkin.current
    val slash = skin.hasSkin && skin.motion == "slash"

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActivityKindChip(activity.kind)
            Text(
                text = if (slash) skin.cased(activity.label, "display") else activity.label,
                style = if (slash) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
            )
        }

        if (activity.spoiler) {
            var shown by remember(activity.id) { mutableStateOf(false) }
            if (shown) {
                ActivityBody(activity, pack, references)
            } else {
                val revealShape = skin.shapes.chip
                Surface(
                    onClick = { shown = true },
                    shape = revealShape,
                    color = if (slash) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (slash) Modifier.border(1.dp, MaterialTheme.colorScheme.onBackground, revealShape)
                            else Modifier,
                        ),
                ) {
                    Text(
                        text = "Spoiler — tap to reveal",
                        style = MaterialTheme.typography.labelLarge,
                        fontStyle = FontStyle.Italic,
                        color = if (slash) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    )
                }
            }
        } else {
            ActivityBody(activity, pack, references)
        }
    }
}

@Composable
private fun ActivityBody(activity: Activity, pack: com.shadowmonarchbooks.dayloop.data.LoadedPack, references: Int) {
    val skin = LocalSkin.current
    val slash = skin.hasSkin && skin.motion == "slash"
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        activity.location?.let {
            Text(
                text = "Location / source: $it",
                style = MaterialTheme.typography.bodyMedium,
                color = if (slash) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (activity.statGains.isNotEmpty()) {
            Text(
                text = "${pack.pack.labels.stat} gains",
                style = MaterialTheme.typography.titleSmall,
            )
            activity.statGains.forEach { (stat, gain) ->
                Text(
                    text = "${pack.pack.statLabels()[stat] ?: stat} +$gain",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (slash) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                )
            }
        }
        activity.notes?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (references > 0) {
            Text(
                text = "Referenced by $references step(s) in the walkthrough.",
                style = MaterialTheme.typography.labelMedium,
                color = if (slash) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ActivityMetadata(activity: Activity, statLabels: Map<String, String>, slash: Boolean) {
    val bits = buildList {
        activity.location?.let { add(it) }
        activity.statGains.entries.joinToString(" · ") { (stat, gain) ->
            "${statLabels[stat] ?: stat} +$gain"
        }.takeIf { it.isNotEmpty() }?.let { add(it) }
    }
    if (bits.isNotEmpty()) {
        Text(
            text = bits.joinToString(" — "),
            style = MaterialTheme.typography.labelMedium,
            color = if (slash) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

/** Neutral chip for the activity's closed-set kind — capitalized token, no game words. */
@Composable
private fun ActivityKindChip(kind: String) {
    SkinTag(
        text = kind.replaceFirstChar { it.uppercase() },
        container = MaterialTheme.colorScheme.secondaryContainer,
        content = MaterialTheme.colorScheme.onSecondaryContainer,
    )
}
