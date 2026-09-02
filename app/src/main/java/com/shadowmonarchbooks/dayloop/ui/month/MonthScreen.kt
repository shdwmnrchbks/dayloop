package com.shadowmonarchbooks.dayloop.ui.month

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.shadowmonarchbooks.dayloop.data.deadlineEnd
import com.shadowmonarchbooks.dayloop.data.deadlineStart
import com.shadowmonarchbooks.dayloop.data.formatMonth
import com.shadowmonarchbooks.dayloop.data.parseDateOrNull
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.achievements.MonthlyAchievementChecklist
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState
import com.shadowmonarchbooks.dayloop.ui.components.MediaImage
import com.shadowmonarchbooks.dayloop.ui.components.SkinHeader
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.SkinSectionHeader
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.MediaItem
import com.shadowmonarchbooks.dayloop.pack.schema.MediaKinds
import java.time.LocalDate
import kotlin.math.abs

private val WeekHeaders = listOf("M", "T", "W", "T", "F", "S", "S")

internal data class CalendarMediaMarker(
    val assetPath: String,
    val title: String,
    val kind: String,
)

/** A horizontal swipe changes exactly one month and clamps at either end. */
internal fun monthIndexAfterSwipe(
    current: Int,
    last: Int,
    dragPx: Float,
    thresholdPx: Float,
): Int = when {
    abs(dragPx) < thresholdPx -> current
    dragPx < 0f -> (current + 1).coerceAtMost(last)
    else -> (current - 1).coerceAtLeast(0)
}

/**
 * Slash-calendar placement for the three reusable guide graphics. The month
 * opener replaces the generic marker on each due date. The first section
 * graphic marks the month's authored start; remaining section graphics mark
 * deadline-range starts. Multiple meanings may share one calendar cell.
 */
internal fun slashCalendarMarkerItems(
    month: String,
    authoredDates: Set<String>,
    deadlines: List<Deadline>,
    media: List<MediaItem>,
): Map<String, List<MediaItem>> {
    val result = linkedMapOf<String, MutableList<MediaItem>>()
    fun add(date: String?, item: MediaItem?) {
        if (date == null || item == null || !date.startsWith("$month-")) return
        result.getOrPut(date) { mutableListOf() }.add(item)
    }

    val monthOpener = media.firstOrNull { it.kind == MediaKinds.MONTH }
    deadlines.forEach { deadline -> add(deadlineEnd(deadline), monthOpener) }

    val sectionMarkers = media.filter { it.kind == MediaKinds.SECTION }
    sectionMarkers.firstOrNull()
        ?.takeIf { month in it.months }
        ?.let { marker -> add(authoredDates.filter { it.startsWith("$month-") }.minOrNull(), marker) }
    sectionMarkers.drop(1).forEach { marker ->
        if (month in marker.months) {
            deadlines.forEach { deadline -> add(deadlineStart(deadline), marker) }
        }
    }

    return result.mapValues { (_, items) -> items.distinctBy(MediaItem::id) }
}

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
    val skin = LocalSkin.current
    val markerItems = when {
        skin.motion == "slash" -> slashCalendarMarkerItems(
            month = month,
            authoredDates = state.days.keys,
            deadlines = pack.deadlines,
            media = pack.media,
        )
        skin.motif == "moon" -> pack.media
            .filter { it.kind == MediaKinds.DAY && it.dates.isNotEmpty() }
            .flatMap { item -> item.dates.map { date -> date to item } }
            .groupBy({ it.first }, { it.second })
        else -> emptyMap()
    }
    val dateMarkers = markerItems.mapValues { (_, items) ->
        items.map { item ->
            CalendarMediaMarker(
                assetPath = pack.assetOf(item),
                title = item.title,
                kind = item.kind,
            )
        }
    }
    val swipeThresholdPx = with(LocalDensity.current) { 56.dp.toPx() }
    val monthMedia = pack.mediaForMonth(month)
    val formerMonthArt = monthMedia.firstOrNull { it.kind == MediaKinds.MONTH }
    val formerSectionArt = monthMedia.filter { it.kind == MediaKinds.SECTION }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(index, months.size, swipeThresholdPx) {
                var dragPx = 0f
                detectHorizontalDragGestures(
                    onDragStart = { dragPx = 0f },
                    onHorizontalDrag = { change, amount ->
                        change.consume()
                        dragPx += amount
                    },
                    onDragEnd = {
                        index = monthIndexAfterSwipe(
                            current = index,
                            last = months.lastIndex,
                            dragPx = dragPx,
                            thresholdPx = swipeThresholdPx,
                        )
                    },
                    onDragCancel = { dragPx = 0f },
                )
            }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(onClick = { if (index > 0) index-- }, enabled = index > 0) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month")
            }
            // Keep the title plate's rc4 geometry while moving its artwork
            // into calendar cells: these invisible slots preserve the exact
            // text position and background width shown in the reference.
            formerMonthArt?.let { Spacer(Modifier.size(40.dp)) }
            SkinHeader(text = formatMonth(month), modifier = Modifier.weight(1f))
            formerSectionArt.forEach { Spacer(Modifier.size(22.dp)) }
            IconButton(onClick = { if (index < months.lastIndex) index++ }, enabled = index < months.lastIndex) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month")
            }
        }

        CalendarGrid(
            days = state.days,
            deadlines = pack.deadlines,
            month = month,
            clockDate = state.currentDate,
            calendar = pack.calendar,
            onOpenDay = onOpenDay,
            dateMarkers = dateMarkers,
        )

        val monthAchievements = pack.mediaForMonth(month).filter { it.kind == "achievement" }
        if (monthAchievements.isNotEmpty()) {
            SkinSectionHeader("Achievements this month")
            MonthlyAchievementChecklist(
                pack = pack,
                state = state,
                month = month,
                onEarnedChange = vm::setAchievementEarned,
            )
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
    dateMarkers: Map<String, List<CalendarMediaMarker>> = emptyMap(),
) {
    val deadlineDates = deadlines.mapNotNull { deadlineEnd(it) }.toSet()

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
                                markers = dateMarkers[iso].orEmpty(),
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
    dateMarkers: Map<String, List<CalendarMediaMarker>> = emptyMap(),
) {
    val first = parseDateOrNull("$month-01") ?: return
    val daysInMonth = first.lengthOfMonth()
    val leadDays = (first.dayOfWeek.value + 6) % 7

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
                                markers = dateMarkers[iso].orEmpty(),
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
    /** Pack marker art rendered on its derived or authored calendar date. */
    markers: List<CalendarMediaMarker> = emptyList(),
) {
    val skin = LocalSkin.current
    val crown = skin.hasSkin && skin.motif == "crown"
    val slash = skin.hasSkin && skin.motion == "slash"
    val inverted = skin.motif == "moon" && day?.dayKind == "forced"
    val container = when (day?.dayKind) {
        "school" -> MaterialTheme.colorScheme.surfaceVariant
        "story" -> MaterialTheme.colorScheme.primaryContainer
        "exam" -> MaterialTheme.colorScheme.errorContainer
        "forced" -> if (inverted) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.tertiaryContainer
        "free" -> MaterialTheme.colorScheme.secondaryContainer
        else -> null
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Surface(
            shape = if (skin.hasSkin) skin.shapes.card else RoundedCornerShape(10.dp),
            color = if (isClockDate && skin.hasSkin && container != null) {
                MaterialTheme.colorScheme.primary
            } else {
                container ?: MaterialTheme.colorScheme.surface
            },
            tonalElevation = if (slash || container == null) 0.dp else 2.dp,
            border = when {
                isClockDate && !skin.hasSkin -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                slash && day != null -> BorderStroke(
                    if (isClockDate) 1.5.dp else 0.8.dp,
                    MaterialTheme.colorScheme.onBackground.copy(alpha = if (isClockDate) 1f else 0.55f),
                )
                crown && day != null -> BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                else -> null
            },
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (day != null) Modifier.clickable { onOpenDay(iso) } else Modifier,
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
                if (markers.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 2.dp),
                    ) {
                        markers.take(3).forEach { marker ->
                            MediaImage(
                                assetPath = marker.assetPath,
                                title = marker.title,
                                size = if (marker.kind == MediaKinds.MONTH) 16.dp else 11.dp,
                            )
                        }
                    }
                } else if (hasDeadline) {
                    when {
                        slash -> Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 3.dp)
                                .size(9.dp)
                                .background(MaterialTheme.colorScheme.primary, skin.shapes.chip),
                        )
                        crown -> {
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
                        }
                        else -> Box(
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
