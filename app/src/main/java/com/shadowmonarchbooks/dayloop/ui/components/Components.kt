package com.shadowmonarchbooks.dayloop.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.Step
import com.shadowmonarchbooks.dayloop.progress.DayProgress
import com.shadowmonarchbooks.dayloop.progress.StepKey
import com.shadowmonarchbooks.dayloop.progress.StepMark

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
 * The Done / Skip / Later triad (docs/PLAN.md §6.1). Tapping the active mark
 * clears it. Deviating is always available and never destructive.
 */
@Composable
fun MarkActions(
    selected: StepMark?,
    onToggle: (StepMark) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        MarkButton(mark = StepMark.DONE, icon = { Icon(Icons.Filled.Check, "Done", Modifier.size(18.dp)) }, selected = selected, onToggle = onToggle)
        MarkButton(mark = StepMark.SKIP, icon = { Icon(Icons.Filled.Close, "Skip", Modifier.size(18.dp)) }, selected = selected, onToggle = onToggle)
        MarkButton(mark = StepMark.LATER, icon = { Icon(Icons.Filled.Refresh, "Later", Modifier.size(18.dp)) }, selected = selected, onToggle = onToggle)
    }
}

@Composable
private fun MarkButton(
    mark: StepMark,
    icon: @Composable () -> Unit,
    selected: StepMark?,
    onToggle: (StepMark) -> Unit,
) {
    val active = selected == mark
    IconButton(
        onClick = { onToggle(mark) },
        modifier = Modifier.size(30.dp),
        colors = if (active) {
            when (mark) {
                StepMark.DONE -> IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                StepMark.SKIP -> IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                StepMark.LATER -> IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            }
        } else {
            IconButtonDefaults.iconButtonColors()
        },
    ) {
        icon()
    }
}

/**
 * One walkthrough step with its persisted mark. Spoiler steps still collapse
 * behind a tap (docs/PLAN.md §6.2); marks render on top of the reveal state.
 */
@Composable
fun StepRow(
    index: Int,
    step: Step,
    mark: StepMark?,
    onToggleMark: (StepMark) -> Unit,
    statLabels: Map<String, String>,
    activityLabel: String?,
) {
    var revealed by remember(step.label, index) { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "${index + 1}.",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(modifier = Modifier.weight(1f)) {
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
                Text(
                    text = step.label,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (mark == StepMark.DONE) TextDecoration.LineThrough else null,
                    color = when (mark) {
                        StepMark.SKIP -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                        StepMark.LATER -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
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
        MarkActions(selected = mark, onToggle = onToggleMark)
    }
}

/** Checkbox-aware step list shared by Today and Day detail. */
@Composable
fun StepsList(
    steps: List<Step>,
    markAt: (Int) -> StepMark?,
    onToggleMark: (Int, StepMark) -> Unit,
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
            StepRow(
                index = i,
                step = step,
                mark = markAt(i),
                onToggleMark = { mark -> onToggleMark(i, mark) },
                statLabels = statLabels,
                activityLabel = step.activityRef?.let(activityLabels::get),
            )
        }
    }
}

/** Compact per-day tally, e.g. "2 of 5 done · 1 later · 1 skipped". */
@Composable
fun DayProgressLine(progress: DayProgress, modifier: Modifier = Modifier) {
    if (progress.total == 0) return
    val parts = mutableListOf("${progress.done} of ${progress.total} done")
    if (progress.deferred > 0) parts += "${progress.deferred} later"
    if (progress.skipped > 0) parts += "${progress.skipped} skipped"
    Text(
        text = parts.joinToString(" · "),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

/** One row of the carried-over queue: a deferred step from an earlier day. */
data class CarriedStep(val key: StepKey, val label: String)

/**
 * Deferred steps from earlier days (docs/PLAN.md §6.1: deviating changes
 * suggestions, never punishes). Marks here write to the original day.
 */
@Composable
fun CarriedOverCard(
    items: List<CarriedStep>,
    onToggleMark: (date: String, index: Int, mark: StepMark) -> Unit,
    formatDate: (String) -> String,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Carried over from earlier days",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = formatDate(item.key.date),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                        )
                    }
                    MarkActions(
                        selected = StepMark.LATER,
                        onToggle = { mark -> onToggleMark(item.key.date, item.key.index, mark) },
                    )
                }
            }
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
