package com.shadowmonarchbooks.dayloop.pack.schema

import kotlinx.serialization.Serializable

/** deadlines.json — hard time gates the UI surfaces prominently. */
@Serializable
data class DeadlinesFile(val deadlines: List<Deadline>)

@Serializable
data class Deadline(
    /** Immutable, pack-prefixed, e.g. "p5r.deadline.palace1.letter". */
    val id: String,
    val label: String,
    /** palace | exam | missable | request | other */
    val kind: String,
    /** Exact due date (ISO), if the deadline is a single day. */
    val date: String? = null,
    /** Inclusive open window, if the deadline spans days. */
    val window: DateWindow? = null,
)

@Serializable
data class DateWindow(
    val start: String,
    val end: String? = null,
)
