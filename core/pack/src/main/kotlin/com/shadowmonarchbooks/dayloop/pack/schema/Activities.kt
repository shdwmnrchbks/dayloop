package com.shadowmonarchbooks.dayloop.pack.schema

import kotlinx.serialization.Serializable

/** activities.json — catalog of repeatable / named activities. */
@Serializable
data class ActivitiesFile(val activities: List<Activity>)

@Serializable
data class Activity(
    /** Immutable, pack-prefixed, e.g. "p5r.activity.book.zorro". */
    val id: String,
    /** Short display label in our own words. */
    val label: String,
    /** book | dvd | videoGame | drink | shop | hangout | exam | other */
    val kind: String,
    /** stat id -> points gained on completion. */
    val statGains: Map<String, Int> = emptyMap(),
    val location: String? = null,
    val notes: String? = null,
    val spoiler: Boolean = false,
)
