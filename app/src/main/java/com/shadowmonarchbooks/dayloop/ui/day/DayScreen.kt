package com.shadowmonarchbooks.dayloop.ui.day

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowmonarchbooks.dayloop.data.byId
import com.shadowmonarchbooks.dayloop.data.formatDate
import com.shadowmonarchbooks.dayloop.data.nextDeadline
import com.shadowmonarchbooks.dayloop.data.slotLabels
import com.shadowmonarchbooks.dayloop.data.statLabels
import com.shadowmonarchbooks.dayloop.progress.ProgressLogic
import com.shadowmonarchbooks.dayloop.progress.StepMark
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.AnswerSheetCard
import com.shadowmonarchbooks.dayloop.ui.components.DayKindChip
import com.shadowmonarchbooks.dayloop.ui.components.DayProgressLine
import com.shadowmonarchbooks.dayloop.ui.components.DeadlineBanner
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState
import com.shadowmonarchbooks.dayloop.ui.components.MediaImage
import com.shadowmonarchbooks.dayloop.ui.components.MediaStrip
import com.shadowmonarchbooks.dayloop.ui.components.SkinHeader
import com.shadowmonarchbooks.dayloop.ui.components.StepsList
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.PerfectDaySplash
import com.shadowmonarchbooks.dayloop.ui.skin.SkinRouteBadge
import com.shadowmonarchbooks.dayloop.ui.skin.SkinSectionHeader

/**
 * Day detail: every step with its checkbox marks, plus prev/next browsing
 * over authored days. Browsing never moves the in-game clock.
 */
@Composable
fun DayScreen(
    date: String,
    vm: DayloopViewModel = hiltViewModel(),
    onOpenDay: (String) -> Unit = {},
    onOpenAnswers: () -> Unit = {},
    onOpenActivity: (String) -> Unit = {},
) {
    val state by vm.state.collectAsState()
    val pack = state.selected ?: run {
        EmptyState("No pack selected.")
        return
    }
    val day = state.day(date) ?: run {
        EmptyState("No authored content for $date.")
        return
    }

    val dates = state.days.keys.sorted()
    val idx = dates.indexOf(date)
    val prevDate = if (idx > 0) dates[idx - 1] else null
    val nextDate = if (idx in 0 until dates.lastIndex) dates[idx + 1] else null

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SkinHeader(formatDate(date, pack.calendar), modifier = Modifier.weight(1f, fill = false))
                if (LocalSkin.current.motif == "moon") {
                    pack.mediaForDate(date).firstOrNull { it.kind == "day" }?.let { marker ->
                        MediaImage(assetPath = pack.assetOf(marker), title = marker.title, size = 30.dp)
                    }
                }
                DayKindChip(day.dayKind)
            }

            // Walkthrough dates are route instructions, so the active route is
            // always visible on the page instead of being implied by a profile.
            SkinRouteBadge("Route · ${pack.routeLabel(state.activeRouteId)}")

            MediaStrip(items = pack.mediaForDate(date).map { pack.assetOf(it) to it.title })

            if (state.currentDate == date) {
                Text(
                    text = "Current in-game day",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            day.notes?.let { notes ->
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            state.currentDate?.let { current ->
                nextDeadline(pack.deadlines, current, pack.calendar)?.let { (deadline, days) ->
                    DeadlineBanner(
                        deadline = deadline,
                        daysLeft = days,
                        kindLabel = pack.pack.labels.deadlineKind(deadline.kind),
                    )
                }
            }

            if (pack.pack.capabilities.answers) {
                pack.answersByDate[date]?.let { sheet ->
                    AnswerSheetCard(
                        sheet = sheet,
                        onOpenAnswers = onOpenAnswers,
                        deadlineLabel = pack.deadlines.byId(sheet.deadlineRef)?.label,
                    )
                }
            }

            DayProgressLine(ProgressLogic.dayProgress(state.marks, date, day.steps.size))

            SkinSectionHeader("Steps")
            StepsList(
                steps = day.steps,
                markAt = { index -> state.markAt(date, index) },
                onToggleMark = { index, mark -> vm.toggleMark(date, index, mark) },
                statLabels = pack.pack.statLabels(),
                activityLabels = pack.activities.mapValues { it.value.label },
                slotLabels = pack.pack.slotLabels(),
                onOpenActivity = onOpenActivity,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(onClick = { prevDate?.let(onOpenDay) }, enabled = prevDate != null) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous authored day")
                }
                Text(
                    text = "Browse authored days",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { nextDate?.let(onOpenDay) }, enabled = nextDate != null) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next authored day")
                }
            }
        }

        PerfectDaySplash(
            allDone = day.steps.isNotEmpty() &&
                day.steps.indices.all { state.markAt(date, it) == StepMark.DONE },
            key = date,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
        )
    }
}
