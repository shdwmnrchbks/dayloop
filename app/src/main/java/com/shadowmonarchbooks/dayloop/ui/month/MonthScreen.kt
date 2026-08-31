package com.shadowmonarchbooks.dayloop.ui.month

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.shadowmonarchbooks.dayloop.data.deadlineStart
import com.shadowmonarchbooks.dayloop.data.formatMonth
import com.shadowmonarchbooks.dayloop.data.parseDateOrNull
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState
import com.shadowmonarchbooks.dayloop.ui.components.MediaImage
import com.shadowmonarchbooks.dayloop.ui.components.MediaStrip
import com.shadowmonarchbooks.dayloop.ui.components.SkinHeader
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import java.time.LocalDate

private val WeekHeaders = listOf("M", "T", "W", "T", "F", "S", "S")

/** Month grid over authored months only; tap an authored day for its detail. */
@Composable
fun MonthScreen(
    vm: DayloopViewModel,
    onOpenDay: (String) -> Unit,
) {
    val state by vm.state.collectAsState()
    val pack = state.selected ?: run {
        EmptyState("No pack selected.")
        return
    }
    val months = state.authoredMonths
    if (months.isEmpty()) {
        EmptyState("No authored months in this pack yet.")
        return
    }

    var index by remember(pack.slug) {
        mutableIntStateOf(
            months.indexOf(state.currentDate?.take(7)).takeIf { it >= 0 } ?: 0,
        )
    }
    val month = months[index]
    // Moon-language packs (docs/ROADMAP-v3.md Phase 14): dates the pack marks
    // with day-anchored media (full-moon operations) render the marker art in
    // their calendar cell instead of the generic deadline dot.
    val dateMarkers: Map<String, Pair<String, String>> =
        if (LocalSkin.current.motif == "moon") {
            pack.media.filter { it.kind == "day" && it.dates.isNotEmpty() }
                .flatMap { item -> item.dates.map { date -> date to (pack.assetOf(item) to item.title) } }
                .toMap()
        } else {
            emptyMap()
        }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(onClick = { if (index > 0) index-- }, enabled = index > 0) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month")
            }
            // Pack-supplied month opener art + section markers
            // (docs/ROADMAP-v3.md Phase 11), when this pack declares them.
            val monthMedia = pack.mediaForMonth(month)
            monthMedia.firstOrNull { it.kind == "month" }?.let { opener ->
                MediaImage(assetPath = pack.assetOf(opener), title = opener.title, size = 40.dp)
            }
            // Skinned packs render the month as a ribbon header in display
            // type (docs/ROADMAP-v3.md Phase 13); engine look keeps title text.
            SkinHeader(text = formatMonth(month), modifier = Modifier.weight(1f))
            monthMedia.filter { it.kind == "section" }.forEach { marker ->
                MediaImage(assetPath = pack.assetOf(marker), title = marker.title, size = 22.dp)
            }
            IconButton(onClick = { if (index < months.lastIndex) index++ }, enabled = index < months.lastIndex) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month")
            }
        }

        CalendarGrid(days = state.days, deadlines = pack.deadlines, month = month, clockDate = state.currentDate, calendar = pack.calendar, onOpenDay = onOpenDay, dateMarkers = dateMarkers)

        // Achievements the guide ties to this month (facts, spoiler-safe):
        // pack-supplied icon + title chips (docs/ROADMAP-v3.md Phase 11).
        val monthAchievements = pack.mediaForMonth(month).filter { it.kind == "achievement" }
        if (monthAchievements.isNotEmpty()) {
            Text(
                text = "Achievements this month",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            MediaStrip(items = monthAchievements.map { pack.assetOf(it) to it.title })
        }
    }
}

@Composable
private fun CalendarGrid(
    days: Map<String, com.shadowmonarchbooks.dayloop.pack.schema.Day>,
    deadlines: List<com.shadowmonarchbooks.dayloop.pack.schema.Deadline>,
    month: String,
    clockDate: String?,
    calendar: com.shadowmonarchbooks.dayloop.pack.GameCalendar?,
    onOpenDay: (String) -> Unit,
    dateMarkers: Map<String, Pair<String, String>> = emptyMap(),
) {
    val deadlineDates = deadlines.mapNotNull { deadlineStart(it) }.toSet()

    // Cycle-aware grid: dayCounter packs may declare an in-game week of any
    // length; the columns follow the pack's cycle order. weekdayGrid packs
    // (no cycle) keep the real 7-column Monday-first grid.
    val cycle = calendar?.cycleTokens.orEmpty()
    if (cycle.isEmpty() || calendar == null) {
        RealMonthGrid(days, deadlineDates, month, clockDate, onOpenDay, dateMarkers)
        return
    }
    val dates = calendar.datesInMonth(month)
    val lead = dates.firstOrNull()?.let { calendar.cyclePosition(it) } ?: 0
    val columns = cycle.size

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            cycle.forEach { token ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = token.replaceFirstChar { it.uppercase() }.take(1),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        val totalCells = lead + dates.size
        val rows = (totalCells + columns - 1) / columns
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (col in 0 until columns) {
                    val cell = row * columns + col
                    val dayIndex = cell - lead
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {
                        if (dayIndex in dates.indices) {
                            val iso = dates[dayIndex]
                            DayCell(
                                iso = iso,
                                dayNumber = iso.takeLast(2).toInt(),
                                day = days[iso],
                                hasDeadline = iso in deadlineDates,
                                isClockDate = iso == clockDate,
                                marker = dateMarkers[iso],
                                onOpenDay = onOpenDay,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Real-calendar 7-column Monday-first grid (weekdayGrid packs). */
@Composable
private fun RealMonthGrid(
    days: Map<String, com.shadowmonarchbooks.dayloop.pack.schema.Day>,
    deadlineDates: Set<String>,
    month: String,
    clockDate: String?,
    onOpenDay: (String) -> Unit,
    dateMarkers: Map<String, Pair<String, String>> = emptyMap(),
) {
    val first = parseDateOrNull("$month-01") ?: return
    val daysInMonth = first.lengthOfMonth()
    val leadDays = (first.dayOfWeek.value + 6) % 7 // Monday-first grid

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            WeekHeaders.forEach { h ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = h,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        val totalCells = leadDays + daysInMonth
        val rows = (totalCells + 6) / 7
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (col in 0 until 7) {
                    val cell = row * 7 + col
                    val dayNumber = cell - leadDays + 1
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {
                        if (dayNumber in 1..daysInMonth) {
                            val iso = "%s-%02d".format(month, dayNumber)
                            DayCell(
                                iso = iso,
                                dayNumber = dayNumber,
                                day = days[iso],
                                hasDeadline = iso in deadlineDates,
                                isClockDate = iso == clockDate,
                                marker = dateMarkers[iso],
                                onOpenDay = onOpenDay,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    iso: String,
    dayNumber: Int,
    day: com.shadowmonarchbooks.dayloop.pack.schema.Day?,
    hasDeadline: Boolean,
    isClockDate: Boolean,
    onOpenDay: (String) -> Unit,
    modifier: Modifier = Modifier,
    /** Moon-language packs (Phase 14): date-marked media (asset, title) renders in the cell. */
    marker: Pair<String, String>? = null,
) {
    val skin = LocalSkin.current
    // Crown language (docs/ROADMAP-v3.md Phase 15): authored cells become
    // gold-ruled itinerary tiles (hairline border; fills stay kind-colored),
    // and deadline dates wear a wax-stamp mark instead of the generic dot.
    val crown = skin.hasSkin && skin.motif == "crown"
    // Moon-language packs: Dark-Hour block days invert to a darker glass
    // (docs/ROADMAP-v3.md Phase 14) — the cell uses the inverse roles.
    val inverted = skin.motif == "moon" && day?.dayKind == "forced"
    val container = when (day?.dayKind) {
        "school" -> MaterialTheme.colorScheme.surfaceVariant
        "story" -> MaterialTheme.colorScheme.primaryContainer
        "exam" -> MaterialTheme.colorScheme.errorContainer
        "forced" -> if (inverted) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.tertiaryContainer
        "free" -> MaterialTheme.colorScheme.secondaryContainer
        else -> null // authored months may skip a day; render it dimmed
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Surface(
            // Skinned packs: the clock date is a solid accent burst
            // (ROADMAP-v3 Phase 13); the engine look keeps the border ring.
            shape = if (isClockDate && skin.hasSkin) skin.shapes.card else RoundedCornerShape(10.dp),
            color = if (isClockDate && skin.hasSkin && container != null) {
                MaterialTheme.colorScheme.primary
            } else {
                container ?: MaterialTheme.colorScheme.surface
            },
            tonalElevation = if (container == null) 0.dp else 2.dp,
            border = when {
                isClockDate && !skin.hasSkin -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                crown && day != null -> BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                else -> null
            },
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (day != null) {
                        Modifier.clickable { onOpenDay(iso) }
                    } else {
                        Modifier
                    },
                ),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = dayNumber.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isClockDate) FontWeight.SemiBold else null,
                    color = when {
                        day == null -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        inverted -> MaterialTheme.colorScheme.inverseOnSurface
                        isClockDate && skin.hasSkin && container != null -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
                if (marker != null) {
                    // The pack's own moon marker art replaces the generic
                    // deadline dot on exactly the dates it anchors.
                    MediaImage(
                        assetPath = marker.first,
                        title = marker.second,
                        size = 16.dp,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 2.dp),
                    )
                } else if (hasDeadline) {
                    if (crown) {
                        // Wax-stamp mark (Phase 15): a ringed crimson disc —
                        // the mission-stamp read — instead of the plain dot.
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 2.dp)
                                .size(9.dp)
                                .border(1.dp, MaterialTheme.colorScheme.error, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(3.5.dp)
                                    .background(MaterialTheme.colorScheme.error, CircleShape),
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 3.dp)
                                .size(5.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                        )
                    }
                }
            }
        }
    }
}
