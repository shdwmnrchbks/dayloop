package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.GameCalendar
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.Pack
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/** Engine-neutral computations over pack data — no game vocabulary here. */

fun parseDateOrNull(iso: String): LocalDate? = runCatching { LocalDate.parse(iso) }.getOrNull()

private val DateFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
private val MonthDayFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d")
private val MonthFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

/** "2016-06-01" -> "Wed, Jun 1"; falls back to the raw string when unparseable. */
fun formatDate(iso: String): String =
    parseDateOrNull(iso)?.format(DateFmt) ?: iso

/**
 * Pack-aware label for [iso]: for packs with a declared in-game weekday cycle
 * the cycle token leads the label ("Watersday, June 2"); otherwise the real
 * weekday ("Wed, Jun 1"). Falls back to the raw string for non-real dates.
 */
fun formatDate(iso: String, calendar: GameCalendar?): String {
    val token = calendar?.weekdayOf(iso)
    val monthDay = monthDayLabel(iso)
    if (token != null && monthDay != null) {
        return token.replaceFirstChar { it.uppercase() } + ", " + monthDay
    }
    return formatDate(iso)
}

/** "2016-06-01" -> "Jun 1" (real months only); null when unparseable. */
private fun monthDayLabel(iso: String): String? =
    runCatching { LocalDate.parse(iso).format(MonthDayFmt) }.getOrNull()

/** "2016-06" -> "June 2016"; falls back to the raw string. */
fun formatMonth(month: String): String =
    runCatching { LocalDate.parse("$month-01").format(MonthFmt) }.getOrDefault(month)

/** The pack's game calendar, or null for packs without a constructable one. */
fun gameCalendarOf(pack: Pack): GameCalendar? = GameCalendar.of(pack.calendar)

/** Inclusive window start of a deadline, or its exact due date. */
fun deadlineStart(d: Deadline): String? = d.window?.start ?: d.date

/** Inclusive window end (or start when it is a single date). */
fun deadlineEnd(d: Deadline): String? = d.window?.end ?: d.date

/** Days from [fromIso] until a deadline's start (negative = in the past). */
fun daysUntil(fromIso: String, deadline: Deadline, calendar: GameCalendar? = null): Long? {
    val target = deadlineStart(deadline) ?: return null
    if (calendar != null) {
        return calendar.diffDays(fromIso, target)?.toLong()
    }
    val from = parseDateOrNull(fromIso) ?: return null
    val to = parseDateOrNull(target) ?: return null
    return ChronoUnit.DAYS.between(from, to)
}

/** The nearest deadline starting today or later, as (deadline, days-left). */
fun nextDeadline(
    deadlines: List<Deadline>,
    fromIso: String,
    calendar: GameCalendar? = null,
): Pair<Deadline, Long>? =
    deadlines.mapNotNull { d -> daysUntil(fromIso, d, calendar)?.let { d to it } }
        .filter { (_, days) -> days >= 0 }
        .minByOrNull { (_, days) -> days }

fun Pack.statLabels(): Map<String, String> = stats.associate { it.id to it.label }
