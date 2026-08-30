package com.shadowmonarchbooks.dayloop.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.Step

/** Vocabulary-free day-kind chip, spoiler-gated step rows, and deadline banner. */

@Composable
fun DayKindChip(kind: String) {
    val colors = when (kind) {
        "school" -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        "story" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "exam" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "forced" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
    Surface(shape = RoundedCornerShape(50), color = colors.first) {
        Text(
            text = kind.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelMedium,
            color = colors.second,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
        )
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * One walkthrough step. Spoiler steps collapse behind a tap (docs/PLAN.md §6.2);
 * stat gains and the activity label render as supporting text.
 */
@Composable
fun StepRow(
    index: Int,
    step: Step,
    statLabels: Map<String, String>,
    activityLabel: String?,
) {
    var revealed by remember(step.label, index) { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "$index.",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column {
            if (step.spoiler && !revealed) {
                Surface(
                    onClick = { revealed = true },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Text(
                        text = "Spoiler — tap to reveal",
                        style = MaterialTheme.typography.labelLarge,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            } else {
                Text(text = step.label, style = MaterialTheme.typography.bodyLarge)
                step.activityRef?.let { ref ->
                    activityLabel?.let { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
                if (step.statGains.isNotEmpty()) {
                    Text(
                        text = step.statGains.entries.joinToString(" · ") { (stat, gain) ->
                            "${statLabels[stat] ?: stat} +$gain"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }
    }
}

@Composable
fun StepsList(
    steps: List<Step>,
    statLabels: Map<String, String>,
    activityLabels: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    if (steps.isEmpty()) {
        Text(
            text = "Nothing scheduled for this day.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier,
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = modifier) {
        steps.forEachIndexed { i, step ->
            StepRow(index = i + 1, step = step, statLabels = statLabels, activityLabel = step.activityRef?.let(activityLabels::get))
        }
    }
}

@Composable
fun DeadlineBanner(deadline: Deadline, daysLeft: Long, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (daysLeft <= 3) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = if (daysLeft == 0L) "Due today" else "$daysLeft day(s) left",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (daysLeft <= 3) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            Text(
                text = deadline.label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (daysLeft <= 3) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}
