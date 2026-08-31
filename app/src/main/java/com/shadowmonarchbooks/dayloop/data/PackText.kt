package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.schema.AllOf
import com.shadowmonarchbooks.dayloop.pack.schema.AnyOf
import com.shadowmonarchbooks.dayloop.pack.schema.BondRankGte
import com.shadowmonarchbooks.dayloop.pack.schema.Condition
import com.shadowmonarchbooks.dayloop.pack.schema.StatGte
import com.shadowmonarchbooks.dayloop.pack.schema.StoryFlag
import com.shadowmonarchbooks.dayloop.pack.schema.Weather
import com.shadowmonarchbooks.dayloop.pack.schema.Weekdays

/**
 * Human-readable renderings of pack data (docs/ROADMAP-v2.md Phase 9): pure,
 * engine-neutral, JVM-testable. The UI prints these strings as-is; no game
 * vocabulary ever leaks in from Kotlin — labels come from the pack.
 */

/**
 * One-line, spoiler-safe description of a rank-step gate (docs/PLAN.md §3.3:
 * "why is this locked today?"). [statLabels]/[bondLabels] resolve ids to the
 * pack's display labels; unknown ids fall back to the raw id so text never
 * goes blank. Story flags stay generic on purpose — flag ids are story
 * spoilers and the walkthrough carries the specifics.
 */
fun describeCondition(
    condition: Condition,
    statLabels: Map<String, String> = emptyMap(),
    bondLabels: Map<String, String> = emptyMap(),
): String = when (condition) {
    is AllOf -> condition.allOf.joinToString(" and ") { describeChild(it, statLabels, bondLabels) }
    is AnyOf -> condition.anyOf.joinToString(" or ") { describeChild(it, statLabels, bondLabels) }
    is Weekdays -> "on " + condition.value.joinToString("/") { it.replaceFirstChar { c -> c.uppercase() } }
    is StatGte -> "Needs ${statLabels[condition.stat] ?: condition.stat} ${condition.rank}+"
    is BondRankGte -> "Needs ${bondLabels[condition.bond] ?: condition.bond} rank ${condition.rank}+"
    is StoryFlag -> "After the story advances"
    is Weather -> "In ${condition.equals} weather"
}

/** Nested composite conditions get parenthesized so "and"/"or" stay readable. */
private fun describeChild(
    condition: Condition,
    statLabels: Map<String, String>,
    bondLabels: Map<String, String>,
): String {
    val text = describeCondition(condition, statLabels, bondLabels)
    return if (condition is AllOf || condition is AnyOf) "($text)" else text
}
