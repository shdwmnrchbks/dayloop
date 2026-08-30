package com.shadowmonarchbooks.dayloop.pack

import com.shadowmonarchbooks.dayloop.pack.schema.AllOf
import com.shadowmonarchbooks.dayloop.pack.schema.AnyOf
import com.shadowmonarchbooks.dayloop.pack.schema.BondRankGte
import com.shadowmonarchbooks.dayloop.pack.schema.Condition
import com.shadowmonarchbooks.dayloop.pack.schema.StatGte
import com.shadowmonarchbooks.dayloop.pack.schema.StoryFlag
import com.shadowmonarchbooks.dayloop.pack.schema.Weather
import com.shadowmonarchbooks.dayloop.pack.schema.Weekdays
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

/**
 * The ordered in-game calendar a pack runs on (docs/PLAN.md §3.2 time models).
 *
 * For `weekdayGrid` packs the game months are the real calendar months, so
 * [dates] equals the real date sequence and [weekdayOf] falls back to real
 * weekdays. A `dayCounter` pack may declare `monthLengths` (game months of any
 * length, including days above 31 — the date is then a formatted string, not a
 * [LocalDate]) and a `weekdayCycle` + [schema.WeekdayAnchor] pair for an
 * in-game week of any size. All lookups are pure string/index math — no game
 * vocabulary lives here.
 */
class GameCalendar private constructor(
    val startDate: String,
    val endDate: String,
    /** Every game date from [startDate] to [endDate], in order. */
    val dates: List<String>,
    private val index: Map<String, Int>,
    private val byMonth: Map<String, List<String>>,
    private val cycle: List<String>,
    private val anchorDateIndex: Int,
    private val anchorPosition: Int,
) {
    val size: Int get() = dates.size

    operator fun contains(iso: String): Boolean = iso in index

    /** Index of [iso] in the game calendar, or null when outside it. */
    fun indexOf(iso: String): Int? = index[iso]

    /** The game date strictly after [iso], or null at the end. */
    fun next(iso: String): String? = index[iso]?.takeIf { it + 1 < size }?.let { dates[it + 1] }

    /** The game date strictly before [iso], or null at the start. */
    fun previous(iso: String): String? = index[iso]?.takeIf { it > 0 }?.let { dates[it - 1] }

    /** Signed game-day distance from [fromIso] to [toIso]; null when either is outside. */
    fun diffDays(fromIso: String, toIso: String): Int? {
        val a = index[fromIso] ?: return null
        val b = index[toIso] ?: return null
        return b - a
    }

    /** Number of game days in [month] ("YYYY-MM"), or 0 when the month is not (fully) in range. */
    fun daysInMonth(month: String): Int = byMonth[month]?.size ?: 0

    /** All game dates of [month] ("YYYY-MM"), in order. */
    fun datesInMonth(month: String): List<String> = byMonth[month].orEmpty()

    /** Month keys ("YYYY-MM") spanned by this calendar, in order. */
    val monthKeys: List<String> get() = byMonth.keys.toList()

    /** Declared in-game weekday cycle tokens, in display order; empty when absent. */
    val cycleTokens: List<String> get() = cycle

    /** 0-based position of [iso]'s weekday inside the declared cycle, or null without a cycle. */
    fun cyclePosition(iso: String): Int? {
        if (cycle.isEmpty()) return null
        val i = index[iso] ?: return null
        return Math.floorMod(anchorPosition + (i - anchorDateIndex), cycle.size)
    }

    /**
     * Weekday token of [iso]: from the declared cycle when present, otherwise
     * the real-world weekday (null when the date is not a real ISO date).
     */
    fun weekdayOf(iso: String): String? {
        val pos = cyclePosition(iso) ?: return Cal.weekdayOf(iso)
        return cycle[pos]
    }

    companion object {
        private val SHAPE = Regex("""^(\d{4})-(\d{2})-(\d{2})$""")

        /**
         * Builds the calendar from a pack's [CalendarRange], or null when the
         * range is malformed (bad shape, end before start, or an end day
         * beyond its game month's length).
         */
        fun of(range: com.shadowmonarchbooks.dayloop.pack.schema.CalendarRange): GameCalendar? {
            val start = SHAPE.matchEntire(range.startDate) ?: return null
            val end = SHAPE.matchEntire(range.endDate) ?: return null
            val (sy, sm, sd) = start.destructured
            val (ey, em, ed) = end.destructured
            val startYear = sy.toInt()
            val startMonth = sm.toInt()
            val startDay = sd.toInt()
            val endYear = ey.toInt()
            val endMonth = em.toInt()
            val endDay = ed.toInt()
            if (startDay < 1 || endDay < 1) return null
            if (startYear > endYear || (startYear == endYear && startMonth > endMonth)) return null

            val months = mutableListOf<Pair<Int, Int>>() // year to month
            var (cy, cm) = startYear to startMonth
            while (true) {
                months += cy to cm
                if (cy == endYear && cm == endMonth) break
                if (cm == 12) { cy += 1; cm = 1 } else cm += 1
                if (months.size > 1200) return null // absurd range guard
            }

            val dates = mutableListOf<String>()
            val byMonth = linkedMapOf<String, MutableList<String>>()
            months.forEachIndexed { i, (y, m) ->
                val length = range.monthLengths.getOrNull(i) ?: java.time.YearMonth.of(y, m).lengthOfMonth()
                if (length < 1) return null
                val firstDay = if (i == 0) startDay else 1
                val lastDay = if (i == months.lastIndex) endDay else length
                if (lastDay > length) return null
                if (firstDay > lastDay) return null
                val key = "%04d-%02d".format(y, m)
                val list = byMonth.getOrPut(key) { mutableListOf() }
                for (d in firstDay..lastDay) {
                    val iso = "%04d-%02d-%02d".format(y, m, d)
                    dates += iso
                    list += iso
                }
            }
            if (dates.isEmpty()) return null

            val cycle = range.weekdayCycle
            val anchor = range.weekdayAnchor
            val index = dates.withIndex().associate { (i, iso) -> iso to i }
            var anchorIndex = -1
            var anchorPosition = -1
            if (cycle.isNotEmpty()) {
                if (anchor == null) return null
                anchorIndex = index[anchor.date] ?: return null
                anchorPosition = cycle.indexOf(anchor.weekday)
                if (anchorPosition < 0) return null
            }

            return GameCalendar(
                startDate = dates.first(),
                endDate = dates.last(),
                dates = dates,
                index = index,
                byMonth = byMonth,
                cycle = cycle,
                anchorDateIndex = anchorIndex,
                anchorPosition = anchorPosition,
            )
        }
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
