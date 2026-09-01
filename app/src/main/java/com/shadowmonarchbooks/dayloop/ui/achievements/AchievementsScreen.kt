package com.shadowmonarchbooks.dayloop.ui.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.data.LoadedPack
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
 * The pack's achievement media is the source of truth for what can be tracked.
 * Date/month anchors drive availability automatically as the persisted in-game
 * clock advances; earning is explicit because reaching a date never proves the
 * player actually satisfied an achievement condition.
 */
@Composable
fun AchievementsScreen(vm: DayloopViewModel) {
    val state by vm.state.collectAsState()
    val pack = state.selected ?: run {
        EmptyState("No pack selected.")
        return
    }
    val achievements = remember(pack) {
        pack.media.filter { it.kind == MediaKinds.ACHIEVEMENT }
    }
    if (achievements.isEmpty()) {
        EmptyState("This pack does not declare achievement data yet.")
        return
    }

    val currentDate = state.currentDate ?: pack.pack.calendar.startDate
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
            )
        }
        items(ordered, key = { it.id }) { item ->
            AchievementRow(
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
                text = "Tracked against in-game date $currentDate. Availability advances with End Day; Earned stays manual.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
            )
        }
    }
}

@Composable
private fun AchievementRow(
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
