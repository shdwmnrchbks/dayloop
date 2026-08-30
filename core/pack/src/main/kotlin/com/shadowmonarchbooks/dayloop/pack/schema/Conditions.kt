package com.shadowmonarchbooks.dayloop.pack.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Typed availability condition DSL (docs/PLAN.md §3.3).
 *
 * A small closed set of combinable predicates. New mechanics are additive:
 * extend this hierarchy, never encode mechanics into strings.
 */
@Serializable
sealed interface Condition

@Serializable
@SerialName("allOf")
data class AllOf(val allOf: List<Condition>) : Condition

@Serializable
@SerialName("anyOf")
data class AnyOf(val anyOf: List<Condition>) : Condition

/** True on the listed weekdays, e.g. ["tue","thu"]. */
@Serializable
@SerialName("weekdays")
data class Weekdays(val value: List<String>) : Condition

/** True when the pack's weather system reports the given state (optional capability; unused by the first three packs). */
@Serializable
@SerialName("weather")
data class Weather(val equals: String) : Condition

/** True when the named stat is at least [rank]. */
@Serializable
@SerialName("statGte")
data class StatGte(val stat: String, val rank: Int) : Condition

/** True when a story flag is set. Flag IDs are immutable (docs/PLAN.md §3.6). */
@Serializable
@SerialName("storyFlag")
data class StoryFlag(val id: String) : Condition

/** True when the named bond is at least [rank]. */
@Serializable
@SerialName("bondRankGte")
data class BondRankGte(val bond: String, val rank: Int) : Condition
