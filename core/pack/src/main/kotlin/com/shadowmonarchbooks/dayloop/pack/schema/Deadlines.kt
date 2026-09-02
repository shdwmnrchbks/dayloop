package com.shadowmonarchbooks.dayloop.pack.schema

import kotlinx.serialization.Serializable

/** deadlines.json — hard time gates and authored route targets the UI surfaces prominently. */
@Serializable
data class DeadlinesFile(val deadlines: List<Deadline>)

@Serializable
data class Deadline(
    /** Immutable, pack-prefixed, e.g. "p5r.deadline.palace1.letter". */
    val id: String,
    val label: String,
    /** palace | exam | missable | request | routeTarget | other */
    val kind: String,
    /** Exact due/target date (ISO), if the entry is a single day. */
    val date: String? = null,
    /** Inclusive open/target window, if the entry spans days. */
    val window: DateWindow? = null,
)

@Serializable
data class DateWindow(
    val start: String,
    val end: String? = null,
)
