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
    /** Character display label shown on the bond detail page. */
    val characterLabel: String? = null,
    val ranks: List<RankStep>,
)

/**
 * One rank step. [rank] is the rank number being reached (1-based, strictly
 * increasing). [gates] must hold for the step to be actionable.
 */
@Serializable
data class RankStep(
    val rank: Int,
    val gates: Condition? = null,
    /** Optional explicit availability window (ISO dates, inclusive). */
    val availableFrom: String? = null,
    val availableUntil: String? = null,
    val location: String? = null,
    /** Optional rank guidance shown directly on the bond detail page. */
    val notes: String? = null,
)
