package com.shadowmonarchbooks.dayloop.ui.answers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.shadowmonarchbooks.dayloop.data.formatDate
import com.shadowmonarchbooks.dayloop.data.byId
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.AnswerKindChip
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState

/**
 * Exam/test answer sheets (docs/PLAN.md Phase 5): every structured answer the
 * pack carries, chronological. Tapping a sheet opens its in-game day.
 */
@Composable
fun AnswersScreen(
    vm: DayloopViewModel = hiltViewModel(),
    onOpenDay: (String) -> Unit,
) {
    val state by vm.state.collectAsState()
    val pack = state.selected ?: run {
        EmptyState("No pack selected.")
        return
    }
    val sheets = pack.answersByDate.values.sortedBy { it.date }
    if (sheets.isEmpty()) {
        EmptyState("No answer sheets in this pack yet — exams and class questions appear here.")
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        items(sheets, key = { it.id }) { sheet ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDay(sheet.date) },
            ) {
                androidx.compose.foundation.layout.Column(
                    Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AnswerKindChip(sheet.kind)
                        Text(
                            text = sheet.label,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = formatDate(sheet.date, pack.calendar),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    // deadlineRef cross-link rendered as the referenced
                    // deadline's label (docs/ROADMAP-v2.md Phase 9).
                    pack.deadlines.byId(sheet.deadlineRef)?.let { dl ->
                        Text(
                            text = "Deadline: ${dl.label}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                    sheet.answers.forEachIndexed { i, answer ->
                        Text(
                            text = "${i + 1}. $answer",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}
