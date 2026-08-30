package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.Pack
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/** Engine-neutral computations over pack data — no game vocabulary here. */

fun parseDateOrNull(iso: String): LocalDate? = runCatching { LocalDate.parse(iso) }.getOrNull()

private val DateFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
private val MonthFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

/** "2016-06-01" -> "Wed, Jun 1"; falls back to the raw string when unparseable. */
fun formatDate(iso: String): String =
    parseDateOrNull(iso)?.format(DateFmt) ?: iso

/** "2016-06" -> "June 2016"; falls back to the raw string. */
fun formatMonth(month: String): String =
    runCatching { LocalDate.parse("$month-01").format(MonthFmt) }.getOrDefault(month)

/** Inclusive window start of a deadline, or its exact due date. */
fun deadlineStart(d: Deadline): String? = d.window?.start ?: d.date

/** Inclusive window end (or start when it is a single date). */
fun deadlineEnd(d: Deadline): String? = d.window?.end ?: d.date

/** Days from [fromIso] until a deadline's start (negative = in the past). */
fun daysUntil(fromIso: String, deadline: Deadline): Long? {
    val from = parseDateOrNull(fromIso) ?: return null
    val target = deadlineStart(deadline)?.let { parseDateOrNull(it) } ?: return null
    return ChronoUnit.DAYS.between(from, target)
}

/** The nearest deadline starting today or later, as (deadline, days-left). */
fun nextDeadline(deadlines: List<Deadline>, fromIso: String): Pair<Deadline, Long>? =
    deadlines.mapNotNull { d -> daysUntil(fromIso, d)?.let { d to it } }
        .filter { (_, days) -> days >= 0 }
        .minByOrNull { (_, days) -> days }

fun Pack.statLabels(): Map<String, String> = stats.associate { it.id to it.label }
