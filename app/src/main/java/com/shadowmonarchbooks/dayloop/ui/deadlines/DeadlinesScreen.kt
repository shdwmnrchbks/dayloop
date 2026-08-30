package com.shadowmonarchbooks.dayloop.ui.deadlines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.shadowmonarchbooks.dayloop.data.deadlineEnd
import com.shadowmonarchbooks.dayloop.data.deadlineStart
import com.shadowmonarchbooks.dayloop.data.daysUntil
import com.shadowmonarchbooks.dayloop.data.formatDate
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState

/**
 * All deadlines, soonest first. Deadline names stay visible (docs/PLAN.md §6.2);
 * story detail lives in the walkthrough spoilers, not here.
 */
@Composable
fun DeadlinesScreen(vm: DayloopViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val pack = state.selected ?: run {
        EmptyState("No pack selected.")
        return
    }
    if (pack.deadlines.isEmpty()) {
        EmptyState("No deadlines authored in this pack yet.")
        return
    }
    val current = state.currentDate ?: state.days.keys.sorted().firstOrNull()

    val sorted = pack.deadlines.sortedWith(
        compareBy({ deadlineStart(it) ?: "9999-12-31" }, { it.id }),
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        items(sorted, key = { it.id }) { deadline ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = deadline.label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        val start = deadlineStart(deadline)
                        val end = deadlineEnd(deadline)
                        Text(
                            text = when {
                                start != null && end != null && end != start ->
                                    "Window ${formatDate(start)} – ${formatDate(end)}"
                                start != null -> "Due ${formatDate(start)}"
                                else -> "Unscheduled"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    current?.let {
                        val days = daysUntil(it, deadline)
                        if (days != null) {
                            Text(
                                text = when {
                                    days < 0 -> "past"
                                    days == 0L -> "today"
                                    else -> "in $days d"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = if (days in 0..3) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
