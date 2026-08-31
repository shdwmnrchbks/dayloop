package com.shadowmonarchbooks.dayloop.ui.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.shadowmonarchbooks.dayloop.data.formatDate
import com.shadowmonarchbooks.dayloop.data.nextDeadline
import com.shadowmonarchbooks.dayloop.data.slotLabels
import com.shadowmonarchbooks.dayloop.data.statLabels
import com.shadowmonarchbooks.dayloop.progress.ProgressLogic
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.CarriedOverCard
import com.shadowmonarchbooks.dayloop.ui.components.CarriedStep
import com.shadowmonarchbooks.dayloop.ui.components.DeadlineBanner
import com.shadowmonarchbooks.dayloop.ui.components.DayKindChip
import com.shadowmonarchbooks.dayloop.ui.components.DayProgressLine
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState
import com.shadowmonarchbooks.dayloop.ui.components.SkinHeader
import com.shadowmonarchbooks.dayloop.ui.components.StepsList
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin

/**
 * Hero screen: the persisted in-game clock (End-Day), today's checkbox steps,
 * the carried-over queue, and the next deadline (docs/PLAN.md §5/§6).
 */
@Composable
fun TodayScreen(
    vm: DayloopViewModel,
    onOpenDay: (String) -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenActivities: () -> Unit = {},
    onOpenActivity: (String) -> Unit = {},
) {
    val state by vm.state.collectAsState()
    val pack = state.selected
    val date = state.currentDate
    if (pack == null || date == null) {
        EmptyState("No pack content found in assets.")
        return
    }

    val day = state.day(date)
    val upcoming = nextDeadline(pack.deadlines, date, pack.calendar)
    val carried = ProgressLogic.carriedOver(state.marks, date).mapNotNull { key ->
        state.day(key.date)?.steps?.getOrNull(key.index)?.let { step ->
            CarriedStep(key = key, label = step.label)
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        state.activeProfile?.let { profile ->
            val routeSuffix = if (pack.routes.size > 1) " · ${pack.routeLabel(state.activeRouteId)}" else ""
            Text(
                text = profile.name + routeSuffix,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Skinned packs render the date as a ribbon header in display type
            // (docs/ROADMAP-v3.md Phase 13); the engine look keeps headline text.
            SkinHeader(formatDate(date, pack.calendar), modifier = Modifier.weight(1f, fill = false))
            DayKindChip(day?.dayKind ?: "rest")
        }

        day?.notes?.let { notes ->
            Text(
                text = notes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        upcoming?.let { (deadline, days) ->
            DeadlineBanner(deadline = deadline, daysLeft = days)
        }

        if (state.orphans.isNotEmpty()) {
            OrphanBanner(count = state.orphans.size, onReview = onOpenSettings)
        }

        if (day != null) {
            DayProgressLine(ProgressLogic.dayProgress(state.marks, date, day.steps.size))
            Text("Steps", style = MaterialTheme.typography.titleMedium)
            StepsList(
                steps = day.steps,
                markAt = { index -> state.markAt(date, index) },
                onToggleMark = { index, mark -> vm.toggleMark(date, index, mark) },
                statLabels = pack.pack.statLabels(),
                activityLabels = pack.activities.mapValues { it.value.label },
                slotLabels = pack.pack.slotLabels(),
                onOpenActivity = onOpenActivity,
            )
        } else {
            Text(
                text = "No authored content for this date yet — coverage grows month by month. The clock still advances.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (carried.isNotEmpty()) {
            CarriedOverCard(
                items = carried,
                onToggleMark = { carriedDate, index, mark -> vm.toggleMark(carriedDate, index, mark) },
                formatDate = { formatDate(it, pack.calendar) },
            )
        }

        Spacer(Modifier.height(4.dp))
        val skin = LocalSkin.current
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                onClick = vm::endDay,
                enabled = state.hasNextDay(),
                // Skinned packs: the big slanted advance button (ROADMAP-v3
                // Phase 13) — the chip silhouette provides the slant.
                shape = if (skin.hasSkin) skin.shapes.chip else ButtonDefaults.shape,
                contentPadding = if (skin.hasSkin) {
                    PaddingValues(horizontal = 22.dp, vertical = 12.dp)
                } else {
                    ButtonDefaults.ContentPadding
                },
                colors = if (skin.hasSkin) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    ButtonDefaults.buttonColors()
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = if (skin.hasSkin) skin.cased("End day ›", "display") else "End day ›",
                    style = if (skin.hasSkin) MaterialTheme.typography.titleLarge else MaterialTheme.typography.labelLarge,
                )
            }
            OutlinedButton(
                onClick = vm::rerollDay,
                enabled = state.hasPreviousDay(),
            ) {
                Text("‹ Undo day")
            }
        }
        if (!state.hasNextDay()) {
            Text(
                text = "End of this pack's calendar.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TextButton(onClick = onOpenCalendar) {
                Text("Calendar")
            }
            TextButton(onClick = { onOpenDay(date) }) {
                Text("Open full day page")
            }
            // Activities browsing (docs/ROADMAP-v2.md Phase 9): shown when the
            // pack ships an activity catalog.
            if (pack.activities.isNotEmpty()) {
                TextButton(onClick = onOpenActivities) {
                    Text("Activities")
                }
            }
        }
    }
}

@Composable
private fun OrphanBanner(count: Int, onReview: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "$count saved mark(s) point at content that changed.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onReview) {
            Text("Review")
        }
    }
}
