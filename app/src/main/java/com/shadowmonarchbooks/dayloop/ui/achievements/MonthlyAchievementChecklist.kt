package com.shadowmonarchbooks.dayloop.ui.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.pack.schema.MediaItem
import com.shadowmonarchbooks.dayloop.pack.schema.MediaKinds
import com.shadowmonarchbooks.dayloop.ui.DayloopUiState
import com.shadowmonarchbooks.dayloop.ui.components.MediaImage
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.SkinCheckboxIndicator

internal data class MonthlyAchievementRow(
    val id: String,
    val title: String,
    val media: MediaItem?,
    val checked: Boolean,
    /** Automatically-derived completion stays read-only; manual rows remain reversible. */
    val enabled: Boolean,
)

/** Achievements expected on one route date, including catalog entries without artwork. */
internal fun datedAchievementRows(
    pack: LoadedPack,
    state: DayloopUiState,
    date: String,
): List<MonthlyAchievementRow> {
    val completedEvents = completedAchievementEvents(
        anchors = pack.achievementEvents,
        days = state.days,
        marks = state.marks,
        routeId = state.activeRouteId,
    )
    return pack.achievements
        .filter { it.expectedBy == date }
        .map { achievement ->
            val progress = achievementProgress(
                achievement = achievement,
                currentDate = state.currentDate ?: pack.pack.calendar.startDate,
                completedEvents = completedEvents,
                manualUnits = state.achievementCounts[achievement.id] ?: 0,
                checkedItems = state.achievementChecklist[achievement.id].orEmpty(),
                selectedChoice = state.achievementChoices[achievement.tracking.stateKey ?: achievement.id],
            )
            val manualEarned = achievement.id in state.earnedAchievements
            MonthlyAchievementRow(
                id = achievement.id,
                title = achievement.title,
                media = achievement.iconMediaRef?.let { ref -> pack.media.firstOrNull { it.id == ref } },
                checked = manualEarned || progress.completed,
                enabled = !progress.automatic && progress.available && !progress.completed,
            )
        }
}

/**
 * Resolve the guide's month-anchored achievement art against the same
 * profile-scoped state used by the Achievements tab.
 */
internal fun monthlyAchievementRows(
    pack: LoadedPack,
    state: DayloopUiState,
    month: String,
): List<MonthlyAchievementRow> {
    val currentDate = state.currentDate ?: pack.pack.calendar.startDate
    val completedEvents = completedAchievementEvents(
        anchors = pack.achievementEvents,
        days = state.days,
        marks = state.marks,
        routeId = state.activeRouteId,
    )
    return pack.mediaForMonth(month)
        .filter { it.kind == MediaKinds.ACHIEVEMENT }
        .distinctBy { it.id }
        .map { media ->
            val achievement = pack.achievements.firstOrNull { it.id == media.id }
            val manualEarned = media.id in state.earnedAchievements
            val progress = achievement?.let { definition ->
                val choiceKey = definition.tracking.stateKey ?: definition.id
                achievementProgress(
                    achievement = definition,
                    currentDate = currentDate,
                    completedEvents = completedEvents,
                    manualUnits = state.achievementCounts[definition.id] ?: 0,
                    checkedItems = state.achievementChecklist[definition.id].orEmpty(),
                    selectedChoice = state.achievementChoices[choiceKey],
                )
            }
            MonthlyAchievementRow(
                id = media.id,
                title = achievement?.title ?: media.title,
                media = media,
                checked = manualEarned || progress?.completed == true,
                enabled = manualEarned ||
                    (progress?.automatic != true && progress?.available != false && progress?.completed != true),
            )
        }
}

/** True only on the final authored date in [date]'s month. */
internal fun isLastAuthoredDayOfMonth(date: String, days: Collection<String>): Boolean =
    days.asSequence().filter { it.take(7) == date.take(7) }.maxOrNull() == date

/** Shared monthly checklist used by Calendar, Today, and full Day pages. */
@Composable
internal fun MonthlyAchievementChecklist(
    pack: LoadedPack,
    state: DayloopUiState,
    month: String,
    onEarnedChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = remember(
        pack,
        month,
        state.currentDate,
        state.marks,
        state.earnedAchievements,
        state.achievementCounts,
        state.achievementChecklist,
        state.achievementChoices,
        state.activeRouteId,
    ) {
        monthlyAchievementRows(pack, state, month)
    }
    AchievementChecklistRows(pack, rows, onEarnedChange, modifier)
}

/** Route-date checklist used by Today and day detail instead of month-end dumping. */
@Composable
internal fun DatedAchievementChecklist(
    pack: LoadedPack,
    state: DayloopUiState,
    date: String,
    onEarnedChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = remember(
        pack,
        date,
        state.currentDate,
        state.marks,
        state.earnedAchievements,
        state.achievementCounts,
        state.achievementChecklist,
        state.achievementChoices,
        state.activeRouteId,
    ) { datedAchievementRows(pack, state, date) }
    AchievementChecklistRows(pack, rows, onEarnedChange, modifier)
}

@Composable
private fun AchievementChecklistRows(
    pack: LoadedPack,
    rows: List<MonthlyAchievementRow>,
    onEarnedChange: (String, Boolean) -> Unit,
    modifier: Modifier,
) {
    if (rows.isEmpty()) return

    val skin = LocalSkin.current
    val slashPanel = skin.hasSkin && skin.motion == "slash"
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier.fillMaxWidth()) {
        rows.forEach { row ->
            Surface(
                shape = skin.shapes.card,
                color = when {
                    slashPanel -> slashAchievementPanelColor
                    row.checked -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                contentColor = when {
                    slashPanel -> Color.White
                    row.checked -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .achievementEntryDecor(slashPanel)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    row.media?.let { media ->
                        MediaImage(
                            assetPath = pack.assetOf(media),
                            title = row.title,
                            size = 38.dp,
                        )
                    }
                    Text(
                        text = row.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    SkinCheckboxIndicator(
                        checked = row.checked,
                        enabled = row.enabled,
                        onCheckedChange = if (row.enabled) {
                            { checked -> onEarnedChange(row.id, checked) }
                        } else {
                            null
                        },
                        modifier = Modifier.size(42.dp),
                    )
                }
            }
        }
    }
}
