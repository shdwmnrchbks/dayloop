package com.shadowmonarchbooks.dayloop.pack.schema

import kotlinx.serialization.Serializable

/** confidants.json — bonds and their rank-step gates. */
@Serializable
data class BondsFile(val bonds: List<Bond>)

@Serializable
data class Bond(
    /** Immutable, pack-prefixed, e.g. "p5r.bond.fool". */
    val id: String,
    /** Display label, e.g. "Fool". */
    val label: String,
    /** Character display label; may be spoiler-sensitive, UI gates it. */
    val characterLabel: String? = null,
    val ranks: List<RankStep>,
)

/**
 * One rank step. [rank] is the rank number being reached (1-based, strictly
 * increasing). [gates] must hold for the step to be actionable.
 *
 * [scheduledFor] is deliberately separate from availability: it is the date a
 * pack's authored completion route chooses to perform this rank. A relationship
 * can be available on many dates before/after that point, so route authors must
 * never overload [availableFrom] with the chosen route date.
 */
@Serializable
data class RankStep(
    val rank: Int,
    val gates: Condition? = null,
    /** Optional route-selected rank-up date (ISO date). */
    val scheduledFor: String? = null,
    /** Optional explicit game availability window (ISO dates, inclusive). */
    val availableFrom: String? = null,
    val availableUntil: String? = null,
    val location: String? = null,
    /** Rewritten, spoiler-safe note shown behind progressive disclosure. */
    val notes: String? = null,
)
