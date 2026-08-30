package com.shadowmonarchbooks.dayloop.ui.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.shadowmonarchbooks.dayloop.data.statLabels
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.DeadlineBanner
import com.shadowmonarchbooks.dayloop.ui.components.DayKindChip
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState
import com.shadowmonarchbooks.dayloop.ui.components.StepsList

/**
 * Read-only hero screen: current in-game day, its steps, and the next deadline.
 * The End-Day clock arrives in Phase 3; browsing moves the in-memory date.
 */
@Composable
fun TodayScreen(
    vm: DayloopViewModel,
    onOpenDay: (String) -> Unit,
    onOpenCalendar: () -> Unit,
) {
    val state by vm.state.collectAsState()
    val pack = state.selected
    val date = state.currentDate
    if (pack == null || date == null) {
        EmptyState("No pack content found in assets.")
        return
    }

    val day = pack.day(date)
    val upcoming = nextDeadline(pack.deadlines, date)

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(formatDate(date), style = MaterialTheme.typography.headlineSmall)
            DayKindChip(day?.dayKind ?: "free")
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

        Text("Steps", style = MaterialTheme.typography.titleMedium)
        StepsList(
            steps = day?.steps.orEmpty(),
            statLabels = pack.pack.statLabels(),
            activityLabels = pack.activities.mapValues { it.value.label },
        )

        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedButton(onClick = { vm.moveCurrent(-1) }, enabled = date != pack.sortedDates.first()) {
                Text("‹ Earlier")
            }
            OutlinedButton(onClick = { vm.moveCurrent(1) }, enabled = date != pack.sortedDates.last()) {
                Text("Later ›")
            }
            TextButton(onClick = onOpenCalendar) {
                Text("Calendar")
            }
        }

        TextButton(onClick = { onOpenDay(date) }) {
            Text("Open full day page")
        }
    }
}
