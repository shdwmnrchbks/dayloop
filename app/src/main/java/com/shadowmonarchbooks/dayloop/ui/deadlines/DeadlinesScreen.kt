package com.shadowmonarchbooks.dayloop.ui.deadlines

import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
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
import com.shadowmonarchbooks.dayloop.ui.components.SkinTag
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.skinDecor

/**
 * All deadlines, soonest first. Deadline names stay visible (docs/PLAN.md §6.2);
 * story detail lives in the walkthrough spoilers, not here. The closed-set
 * kind (palace | exam | missable | request | other) chips each row
 * (docs/ROADMAP-v2.md Phase 9).
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
            val skin = LocalSkin.current
            val crown = skin.hasSkin && skin.motif == "crown"
            val slash = skin.hasSkin && skin.motion == "slash"
            val skinnedCard = crown || slash
            val cardShape = if (skinnedCard) skin.shapes.card else RoundedCornerShape(12.dp)
            val cardColor = if (slash) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceVariant
            Surface(
                shape = cardShape,
                color = cardColor,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (slash) Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = cardShape,
                        ) else Modifier,
                    ),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (crown) Modifier.skinDecor("panel") else Modifier)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (skinnedCard) skin.cased(deadline.label, "display") else deadline.label,
                            style = if (skinnedCard) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (slash) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface,
                        )
                        val start = deadlineStart(deadline)
                        val end = deadlineEnd(deadline)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            when {
                                slash -> SkinTag(
                                    text = skin.cased(pack.pack.labels.deadlineKind(deadline.kind), "display"),
                                    container = MaterialTheme.colorScheme.primary,
                                    content = MaterialTheme.colorScheme.onPrimary,
                                )
                                crown -> SkinTag(
                                    text = pack.pack.labels.deadlineKind(deadline.kind),
                                    container = MaterialTheme.colorScheme.tertiaryContainer,
                                    content = MaterialTheme.colorScheme.onTertiaryContainer,
                                )
                                else -> DeadlineKindChip(pack.pack.labels.deadlineKind(deadline.kind))
                            }
                            Text(
                                text = when {
                                    start != null && end != null && end != start ->
                                        "Window ${formatDate(start, pack.calendar)} – ${formatDate(end, pack.calendar)}"
                                    start != null -> "Due ${formatDate(start, pack.calendar)}"
                                    else -> "Unscheduled"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (slash) {
                                    MaterialTheme.colorScheme.onBackground
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    }
                    current?.let {
                        val days = daysUntil(it, deadline, pack.calendar)
                        if (days != null) {
                            if (skinnedCard) {
                                val urgent = days in 0..3
                                Surface(
                                    shape = if (slash) skin.shapes.chip else skin.shapes.header,
                                    color = when {
                                        urgent -> MaterialTheme.colorScheme.error
                                        slash -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.primaryContainer
                                    },
                                    shadowElevation = 0.dp,
                                ) {
                                    Text(
                                        text = skin.cased(
                                            when {
                                                days < 0 -> "past"
                                                days == 0L -> "due today"
                                                else -> "in $days d"
                                            },
                                            "display",
                                        ),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = when {
                                            urgent -> MaterialTheme.colorScheme.onError
                                            slash -> MaterialTheme.colorScheme.onPrimary
                                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    )
                                }
                            } else {
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
}

/** Chip for the deadline's closed-set kind; the display name is pack-supplied
 *  via `labels.deadlineKinds` (docs/ROADMAP-v2.md Phase 10), falling back to
 *  the capitalized token. */
@Composable
private fun DeadlineKindChip(label: String) {
    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.tertiaryContainer) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}
