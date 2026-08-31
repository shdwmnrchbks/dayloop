package com.shadowmonarchbooks.dayloop.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.pack.schema.AnswerSheet
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.Step
import com.shadowmonarchbooks.dayloop.progress.DayProgress
import com.shadowmonarchbooks.dayloop.progress.StepKey
import com.shadowmonarchbooks.dayloop.progress.StepMark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
 * The step's [slotLabel] (pack-supplied, e.g. "Afternoon") and a tappable
 * activity reference render as metadata lines (docs/ROADMAP-v2.md Phase 9:
 * fields the walkthrough carries get surfaced, not flattened).
 */
@Composable
fun StepRow(
    index: Int,
    step: Step,
    mark: StepMark?,
    onToggleMark: (StepMark) -> Unit,
    statLabels: Map<String, String>,
    activityLabel: String?,
    slotLabel: String? = null,
    onOpenActivity: (() -> Unit)? = null,
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
                if (slotLabel != null) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(bottom = 3.dp),
                    ) {
                        Text(
                            text = slotLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
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
                            modifier = if (onOpenActivity != null) {
                                Modifier
                                    .padding(top = 1.dp)
                                    .clickable(onClick = onOpenActivity)
                            } else {
                                Modifier.padding(top = 1.dp)
                            },
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
    slotLabels: Map<String, String> = emptyMap(),
    onOpenActivity: ((String) -> Unit)? = null,
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
                slotLabel = step.slot?.let(slotLabels::get),
                onOpenActivity = step.activityRef?.let { ref ->
                    onOpenActivity?.let { open -> { open(ref) } }
                },
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

/** Kind chip for answer sheets — "Exam" or "Class question". */
@Composable
fun AnswerKindChip(kind: String, modifier: Modifier = Modifier) {
    val colors = when (kind) {
        "exam" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
    Surface(shape = RoundedCornerShape(50), color = colors.first, modifier = modifier) {
        Text(
            text = when (kind) {
                "exam" -> "Exam"
                "classQuestion" -> "Class question"
                else -> kind.replaceFirstChar { it.uppercase() }
            },
            style = MaterialTheme.typography.labelMedium,
            color = colors.second,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
        )
    }
}

/**
 * One structured answer sheet (docs/PLAN.md Phase 5): the accepted answers
 * for an exam day or a class question. Answers are facts — always visible.
 * A resolved [deadlineLabel] surfaces the sheet's deadlineRef cross-link
 * (docs/ROADMAP-v2.md Phase 9).
 */
@Composable
fun AnswerSheetCard(
    sheet: AnswerSheet,
    modifier: Modifier = Modifier,
    onOpenAnswers: (() -> Unit)? = null,
    deadlineLabel: String? = null,
) {
    Surface(
        onClick = { onOpenAnswers?.invoke() },
        enabled = onOpenAnswers != null,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnswerKindChip(sheet.kind)
                Text(
                    text = sheet.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
            }
            deadlineLabel?.let {
                Text(
                    text = "Deadline: $it",
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

/** Up to three word-initials of the pack title — a generic, art-free tile. */
private fun monogramOf(title: String): String =
    title.split(Regex("[^\\p{L}\\p{N}]+"))
        .filter { it.isNotBlank() }
        .take(3)
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .ifEmpty { "?" }

/**
 * Decodes a packed asset image off the main thread; null when the asset is
 * missing or unreadable. Shared by pack tile icons and carousel cover art.
 */
@Composable
fun rememberAssetImage(path: String?): ImageBitmap? {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null, key1 = path) {
        value = path?.let { asset ->
            withContext(Dispatchers.IO) {
                runCatching {
                    context.assets.open(asset).use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }.value
}

/**
 * Pack-supplied tile icon (ROADMAP-v2 Phase 7): loads the pack's own
 * `art/icon.png` asset when it ships one, otherwise falls back to a monogram
 * tile. Engine stays neutral — the pack (or Phase 10 art) supplies the look.
 */
@Composable
fun PackIcon(
    iconAsset: String?,
    title: String,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
) {
    val bitmap = rememberAssetImage(iconAsset)
    val shape = RoundedCornerShape(22.dp)
    Surface(
        shape = shape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.size(size),
    ) {
        val bmp = bitmap
        if (bmp != null) {
            Image(
                bitmap = bmp,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = monogramOf(title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}
