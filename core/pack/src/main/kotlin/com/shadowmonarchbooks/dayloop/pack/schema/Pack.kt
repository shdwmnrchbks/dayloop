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
    val capabilities: Capabilities = Capabilities(),
    /** Pack-supplied display vocabulary for engine terms (docs/PLAN.md §3.1). */
    val labels: Labels = Labels(),
)

@Serializable
data class CalendarRange(
    /** Inclusive ISO dates, e.g. "2016-04-09". */
    val startDate: String,
    val endDate: String,
    /** Days inside the range the player cannot act on (story-only, travel). */
    val nonPlayableDates: List<String> = emptyList(),
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
