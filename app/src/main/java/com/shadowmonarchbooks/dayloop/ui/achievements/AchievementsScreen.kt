package com.shadowmonarchbooks.dayloop.ui.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementDefinition
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import com.shadowmonarchbooks.dayloop.pack.schema.MediaItem
import com.shadowmonarchbooks.dayloop.pack.schema.MediaKinds
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState
import com.shadowmonarchbooks.dayloop.ui.components.MediaImage
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.skinDecor

/**
 * Profile-scoped achievement tracker.
 *
 * New packs can ship achievements.json rules that derive progress from the
 * in-game clock and DONE walkthrough steps. Packs that only ship achievement
 * media keep the legacy date/month availability tracker.
 */
@Composable
fun AchievementsScreen(vm: DayloopViewModel) {
    val state by vm.state.collectAsState()
    val pack = state.selected ?: run {
        EmptyState("No pack selected.")
        return
    }

    if (pack.achievements.isNotEmpty()) {
        RuleBasedAchievements(pack, vm, state.currentDate ?: pack.pack.calendar.startDate)
        return
    }

    val achievements = remember(pack) {
        pack.media.filter { it.kind == MediaKinds.ACHIEVEMENT }
    }
    if (achievements.isEmpty()) {
        EmptyState("This pack does not declare achievement data yet.")
        return
    }

    LegacyMediaAchievements(pack, vm, state.currentDate ?: pack.pack.calendar.startDate, achievements)
}

@Composable
private fun RuleBasedAchievements(
    pack: LoadedPack,
    vm: DayloopViewModel,
    currentDate: String,
) {
    val state by vm.state.collectAsState()
    val completedEvents = remember(pack.achievementEvents, state.days, state.marks, state.activeRouteId) {
        completedAchievementEvents(
            anchors = pack.achievementEvents,
            days = state.days,
            marks = state.marks,
            routeId = state.activeRouteId,
        )
    }
    val rows = remember(
        pack.achievements,
        completedEvents,
        state.earnedAchievements,
        state.achievementCounts,
        state.achievementChecklist,
        currentDate,
    ) {
        pack.achievements.map { achievement ->
            val checkedItems = state.achievementChecklist[achievement.id].orEmpty()
            val progress = achievementProgress(
                achievement = achievement,
                currentDate = currentDate,
                completedEvents = completedEvents,
                manualUnits = state.achievementCounts[achievement.id] ?: 0,
                checkedItems = checkedItems,
            )
            AchievementRowState(
                achievement = achievement,
                progress = progress,
                manualEarned = achievement.id in state.earnedAchievements,
                checkedItems = checkedItems,
            )
        }.sortedWith(
            compareBy<AchievementRowState> {
                when {
                    it.earned -> 1
                    it.progress.available -> 0
                    else -> 2
                }
            }.thenBy { it.achievement.expectedBy ?: it.achievement.availableFrom ?: "9999-99-99" }
                .thenBy { it.achievement.title },
        )
    }
    val earnedCount = rows.count { it.earned }
    val actionableCount = rows.count { !it.earned && it.progress.available }
    val upcomingCount = rows.size - earnedCount - actionableCount
    val autoCount = rows.count { it.progress.completed && it.progress.automatic && !it.manualEarned }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        item(key = "summary") {
            AchievementSummary(
                earned = earnedCount,
                total = rows.size,
                due = actionableCount,
                upcoming = upcomingCount,
                currentDate = currentDate,
                detail = "$autoCount earned automatically. DONE walkthrough steps and story progress update tracked achievements; cumulative gameplay goals keep profile-scoped counters/checklists, while choices and uncertain results stay confirmable.",
            )
        }
        items(rows, key = { it.achievement.id }) { row ->
            RuleAchievementRow(
                pack = pack,
                row = row,
                currentDate = currentDate,
                onEarnedChange = { earned ->
                    vm.setAchievementEarned(row.achievement.id, earned)
                },
                onProgressChange = { count ->
                    vm.setAchievementCount(row.achievement.id, count)
                },
                onChecklistItemChange = { itemId, checked ->
                    vm.setAchievementChecklistItem(row.achievement.id, itemId, checked)
                },
            )
        }
    }
}

private data class AchievementRowState(
    val achievement: AchievementDefinition,
    val progress: AchievementProgress,
    val manualEarned: Boolean,
    val checkedItems: Set<String> = emptySet(),
) {
    val earned: Boolean get() = manualEarned || progress.completed
}

@Composable
private fun RuleAchievementRow(
    pack: LoadedPack,
    row: AchievementRowState,
    currentDate: String,
    onEarnedChange: (Boolean) -> Unit,
    onProgressChange: (Int) -> Unit,
    onChecklistItemChange: (String, Boolean) -> Unit,
) {
    val achievement = row.achievement
    val progress = row.progress
    val earned = row.earned
    val icon = achievement.iconMediaRef?.let { id -> pack.media.firstOrNull { it.id == id } }
    val status = achievementStatus(achievement, progress, row.manualEarned, currentDate)
    val skin = LocalSkin.current

    Surface(
        shape = skin.shapes.card,
        color = if (earned) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.skinDecor("panel").padding(10.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(56.dp),
            ) {
                if (icon != null) {
                    MediaImage(
                        assetPath = pack.assetOf(icon),
                        title = achievement.title,
                        size = 56.dp,
                        modifier = Modifier.padding(5.dp),
                    )
                } else {
                    Text(
                        text = "★",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                achievement.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(2.dp))
                }
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (earned) MaterialTheme.colorScheme.onSecondaryContainer
                    else MaterialTheme.colorScheme.secondary,
                )
                when {
                    achievement.tracking.type == AchievementTrackingTypes.CHECKLIST -> {
                        AchievementChecklistControls(
                            achievement = achievement,
                            checkedItems = row.checkedItems,
                            enabled = progress.available && !row.manualEarned,
                            onItemChange = onChecklistItemChange,
                        )
                    }
                    !progress.automatic && progress.totalUnits > 1 -> {
                        AchievementCounterControls(
                            progress = progress,
                            onProgressChange = onProgressChange,
                        )
                    }
                }
            }
            Checkbox(
                checked = earned,
                enabled = !progress.completed,
                onCheckedChange = if (progress.completed) null else onEarnedChange,
            )
        }
    }
}

@Composable
private fun AchievementChecklistControls(
    achievement: AchievementDefinition,
    checkedItems: Set<String>,
    enabled: Boolean,
    onItemChange: (String, Boolean) -> Unit,
) {
    Spacer(Modifier.height(4.dp))
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        achievement.tracking.items.distinctBy { it.id }.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = item.id in checkedItems,
                    enabled = enabled,
                    onCheckedChange = { checked -> onItemChange(item.id, checked) },
                )
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun AchievementCounterControls(
    progress: AchievementProgress,
    onProgressChange: (Int) -> Unit,
) {
    Spacer(Modifier.height(2.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        TextButton(
            enabled = progress.available && progress.completedUnits > 0,
            onClick = { onProgressChange(progress.completedUnits - 1) },
        ) {
            Text("−1")
        }
        Text(
            text = "${progress.completedUnits} / ${progress.totalUnits}",
            style = MaterialTheme.typography.labelLarge,
        )
        TextButton(
            enabled = progress.available && progress.completedUnits < progress.totalUnits,
            onClick = { onProgressChange(progress.completedUnits + 1) },
        ) {
            Text("+1")
        }
    }
}

private fun achievementStatus(
    achievement: AchievementDefinition,
    progress: AchievementProgress,
    manualEarned: Boolean,
    currentDate: String,
): String {
    if (manualEarned) return "Earned"
    if (progress.completed) {
        return if (progress.automatic) "Earned automatically" else "Earned by tracked progress"
    }
    if (!progress.available) return "Upcoming · ${achievement.availableFrom}"

    val progressText = if (progress.totalUnits > 1) {
        "${progress.completedUnits} / ${progress.totalUnits} tracked"
    } else null
    val expected = achievement.expectedBy?.let { date ->
        if (currentDate >= date) "check now" else "expected by $date"
    }
    val base = when {
        progressText != null && !progress.automatic ->
            listOfNotNull(progressText, expected).joinToString(" · ")
        achievement.tracking.type in setOf(
            AchievementTrackingTypes.EVENT,
            AchievementTrackingTypes.ALL_EVENTS,
            AchievementTrackingTypes.ANY_EVENT,
            AchievementTrackingTypes.COUNTER,
            AchievementTrackingTypes.STORY_DATE,
        ) -> progressText ?: expected ?: "Tracked automatically"
        achievement.tracking.type == AchievementTrackingTypes.CONDITIONAL ->
            expected?.let { "Confirmation needed · $it" } ?: "Confirmation needed"
        else -> expected?.let { "Manual tracking · $it" } ?: "Manual tracking"
    }
    return if (achievement.missable) "$base · Missable" else base
}

@Composable
private fun LegacyMediaAchievements(
    pack: LoadedPack,
    vm: DayloopViewModel,
    currentDate: String,
    achievements: List<MediaItem>,
) {
    val state by vm.state.collectAsState()
    val earnedIds = state.earnedAchievements
    val earnedCount = achievements.count { it.id in earnedIds }
    val dueCount = achievements.count { it.id !in earnedIds && achievementIsDue(it, currentDate) }
    val upcomingCount = achievements.size - earnedCount - dueCount
    val ordered = remember(achievements, earnedIds, currentDate) {
        achievements.sortedWith(
            compareBy<MediaItem> {
                when {
                    it.id in earnedIds -> 1
                    achievementIsDue(it, currentDate) -> 0
                    else -> 2
                }
            }.thenBy { achievementAnchorSortKey(it) }.thenBy { it.title },
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        item(key = "summary") {
            AchievementSummary(
                earned = earnedCount,
                total = achievements.size,
                due = dueCount,
                upcoming = upcomingCount,
                currentDate = currentDate,
                detail = "Availability advances with End Day; earned status is confirmed manually for this pack.",
            )
        }
        items(ordered, key = { it.id }) { item ->
            LegacyAchievementRow(
                pack = pack,
                item = item,
                currentDate = currentDate,
                earned = item.id in earnedIds,
                onEarnedChange = { earned -> vm.setAchievementEarned(item.id, earned) },
            )
        }
    }
}

@Composable
private fun AchievementSummary(
    earned: Int,
    total: Int,
    due: Int,
    upcoming: Int,
    currentDate: String,
    detail: String,
) {
    val skin = LocalSkin.current
    Surface(
        shape = skin.shapes.card,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.skinDecor("panel").padding(14.dp),
        ) {
            Text(
                text = "$earned / $total earned",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "$due due or available · $upcoming upcoming",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "In-game date $currentDate. $detail",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
            )
        }
    }
}

@Composable
private fun LegacyAchievementRow(
    pack: LoadedPack,
    item: MediaItem,
    currentDate: String,
    earned: Boolean,
    onEarnedChange: (Boolean) -> Unit,
) {
    val due = achievementIsDue(item, currentDate)
    val status = when {
        earned -> "Earned"
        due -> "Due / available"
        else -> "Upcoming · ${achievementAnchorLabel(item)}"
    }
    val skin = LocalSkin.current
    Surface(
        shape = skin.shapes.card,
        color = if (earned) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.skinDecor("panel").padding(10.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                MediaImage(
                    assetPath = pack.assetOf(item),
                    title = item.title,
                    size = 56.dp,
                    modifier = Modifier.padding(5.dp),
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                item.caption?.let { caption ->
                    Text(
                        text = caption,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(2.dp))
                }
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (earned) MaterialTheme.colorScheme.onSecondaryContainer
                    else MaterialTheme.colorScheme.secondary,
                )
            }
            Checkbox(checked = earned, onCheckedChange = onEarnedChange)
        }
    }
}

internal fun achievementIsDue(item: MediaItem, currentDate: String): Boolean {
    item.dates.minOrNull()?.let { return currentDate >= it }
    item.months.minOrNull()?.let { return currentDate.take(7) >= it }
    return true
}

private fun achievementAnchorSortKey(item: MediaItem): String =
    item.dates.minOrNull() ?: item.months.minOrNull()?.plus("-01") ?: "0000-00-00"

private fun achievementAnchorLabel(item: MediaItem): String = when {
    item.dates.isNotEmpty() -> item.dates.minOrNull().orEmpty()
    item.months.isNotEmpty() -> item.months.minOrNull().orEmpty()
    else -> "any time"
}
