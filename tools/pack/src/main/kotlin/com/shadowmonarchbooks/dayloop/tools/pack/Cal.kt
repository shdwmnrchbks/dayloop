package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.tools.pack.schema.AllOf
import com.shadowmonarchbooks.dayloop.tools.pack.schema.AnyOf
import com.shadowmonarchbooks.dayloop.tools.pack.schema.BondRankGte
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Condition
import com.shadowmonarchbooks.dayloop.tools.pack.schema.StatGte
import com.shadowmonarchbooks.dayloop.tools.pack.schema.StoryFlag
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Weather
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Weekdays
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeParseException

/** Shared helpers for calendar and reference validation. */
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

    val WEEKDAYS = WEEKDAY_OF_DAY.values.toSet()

    fun parseDate(iso: String): LocalDate? = try {
        LocalDate.parse(iso)
    } catch (_: DateTimeParseException) {
        null
    }

    /** Real-world weekday of an ISO date, or null if unparseable. */
    fun weekdayOf(iso: String): String? = parseDate(iso)?.dayOfWeek?.let { WEEKDAY_OF_DAY[it] }

    /** All dates in [start, end], or empty if either bound is invalid. */
    fun datesBetween(start: String, end: String): List<LocalDate> {
        val s = parseDate(start) ?: return emptyList()
        val e = parseDate(end) ?: return emptyList()
        if (e < s) return emptyList()
        return generateSequence(s) { it.plusDays(1) }.takeWhile { it <= e }.toList()
    }
}

/** Walks a condition tree, invoking [onLeaf] for every leaf predicate. */
fun walkConditions(c: Condition, onLeaf: (Condition) -> Unit) {
    when (c) {
        is AllOf -> c.allOf.forEach { walkConditions(it, onLeaf) }
        is AnyOf -> c.anyOf.forEach { walkConditions(it, onLeaf) }
        is Weekdays, is Weather, is StatGte, is StoryFlag, is BondRankGte -> onLeaf(c)
    }
}
