package com.shadowmonarchbooks.dayloop.pack.schema

import kotlinx.serialization.Serializable

/**
 * pack.json — pack identity, calendar, and capability manifest (docs/PLAN.md §3.2).
 */
@Serializable
data class Pack(
    /** Immutable short slug, e.g. "p5r". Never reused across packs. */
    val packId: String,
    /** Display title of the game this pack covers. */
    val title: String,
    /** Bumped on every content change; saves stamp packId @ contentVersion. */
    val contentVersion: Int,
    /** "weekdayGrid" (P3R/P5R) or "dayCounter" (Metaphor). */
    val timeModel: String,
    val calendar: CalendarRange,
    /** Time slots the engine advances through, e.g. afternoon/evening. */
    val slots: List<Slot>,
    /** Social stats, engine-neutral ids with pack-supplied labels. */
    val stats: List<StatDef>,
    /**
     * Authored walkthrough routes (docs/PLAN.md Phase 5 "routes"): e.g. a
     * completion route and a casual one. Empty means the pack ships a single
     * implicit default route ("standard") at walkthrough/ top level.
     */
    val routes: List<RouteDef> = emptyList(),
    val capabilities: Capabilities = Capabilities(),
    /** Pack-supplied display vocabulary for engine terms (docs/PLAN.md §3.1). */
    val labels: Labels = Labels(),
)

@Serializable
data class RouteDef(
    /** Immutable route id, e.g. "standard". Profiles pin one (docs/PLAN.md §3.7). */
    val id: String,
    /** Pack-supplied display label, e.g. "Completion" / "Casual". */
    val label: String,
    val description: String? = null,
)

/** Route identity helpers shared by the loader, lint, and the app. */
object Routes {
    /** The implicit route every pack has, even when none are declared. */
    const val DEFAULT = "standard"

    /** Declared routes, or the single implicit default when none are declared. */
    fun effective(pack: Pack): List<RouteDef> =
        pack.routes.ifEmpty { listOf(RouteDef(DEFAULT, "Standard")) }

    /** The id a profile should fall back to for [pack]. */
    fun defaultId(pack: Pack): String =
        pack.routes.firstOrNull()?.id ?: DEFAULT
}

@Serializable
data class CalendarRange(
    /** Inclusive ISO dates, e.g. "2016-04-09". */
    val startDate: String,
    val endDate: String,
    /** Days inside the range the player cannot act on (story-only, travel). */
    val nonPlayableDates: List<String> = emptyList(),
    /**
     * Game-days per month for `dayCounter` packs whose months differ from the
     * real calendar, starting at [startDate]'s month. A day-of-month above 31
     * is representable as long as it is declared here. Empty = real month
     * lengths; `weekdayGrid` packs must leave this empty.
     */
    val monthLengths: List<Int> = emptyList(),
    /**
     * In-game weekday cycle for `dayCounter` packs whose week differs from the
     * real 7-day week — engine-neutral lowercase tokens, e.g. a 5-day week.
     * Empty = mon..sun; `weekdayGrid` packs must leave this empty.
     */
    val weekdayCycle: List<String> = emptyList(),
    /** The date + cycle token [weekdayCycle] is anchored to; required when the cycle is declared. */
    val weekdayAnchor: WeekdayAnchor? = null,
)

@Serializable
data class WeekdayAnchor(
    /** ISO date inside the calendar the anchor applies to. */
    val date: String,
    /** One of [CalendarRange.weekdayCycle]'s tokens. */
    val weekday: String,
)

@Serializable
data class Slot(val id: String, val label: String)

@Serializable
data class StatDef(val id: String, val label: String)

@Serializable
data class Capabilities(
    val exams: Boolean = false,
    val weather: Boolean = false,
)

@Serializable
data class Labels(
    /** e.g. "Confidant" (P5R) / "Social Link" (P3/P4) / "Follower" (Metaphor). */
    val bond: String = "Bond",
    /** e.g. "Social Stat" / "Royal Virtue". */
    val stat: String = "Stat",
)
