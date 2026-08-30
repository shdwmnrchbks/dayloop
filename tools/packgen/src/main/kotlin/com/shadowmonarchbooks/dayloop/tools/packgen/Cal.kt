package com.shadowmonarchbooks.dayloop.tools.packgen

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeParseException

/** Calendar helpers (kept dependency-free for testability). */
object Cal {
    val WEEKDAY_OF_DAY: Map<DayOfWeek, String> = mapOf(
        DayOfWeek.MONDAY to "mon",
        DayOfWeek.TUESDAY to "tue",
        DayOfWeek.WEDNESDAY to "wed",
        DayOfWeek.THURSDAY to "thu",
        DayOfWeek.FRIDAY to "fri",
        DayOfWeek.SATURDAY to "sat",
        DayOfWeek.SUNDAY to "sun",
    )

    fun parseDate(iso: String): LocalDate? = try {
        LocalDate.parse(iso)
    } catch (_: DateTimeParseException) {
        null
    }

    fun weekdayOf(iso: String): String? = parseDate(iso)?.dayOfWeek?.let { WEEKDAY_OF_DAY[it] }
}
