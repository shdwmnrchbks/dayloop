package com.shadowmonarchbooks.dayloop.pack.schema

import kotlinx.serialization.Serializable

/**
 * media.json — pack-supplied graphic manifest (docs/ROADMAP-v3.md Phase 11).
 *
 * Every image a pack bundles under `images/` is declared here exactly once
 * with engine-neutral metadata; packlint fails on orphaned image files and on
 * entries pointing at files that don't exist, so "all art shipped is served"
 * is checkable rather than vibes. Anchors are optional: items without any are
 * gallery-only.
 */
@Serializable
data class MediaFile(val media: List<MediaItem> = emptyList())

@Serializable
data class MediaItem(
    /** Immutable id, e.g. "p5r.media.achievement.easy-money". */
    val id: String,
    /** Pack-relative image path, e.g. "images/img001_5E1BA6BEEB5D8C6E.png". */
    val file: String,
    /**
     * Closed-set kind the engine serves by (achievement / month / section /
     * day / portrait / banner / backdrop / guide). Kinds map to serving
     * surfaces — never to game names.
     */
    val kind: String,
    /** Pack-supplied display title. */
    val title: String,
    val caption: String? = null,
    /** YYYY-MM month anchors (rendered on month-scoped surfaces). */
    val months: List<String> = emptyList(),
    /** ISO date anchors (rendered on the matching day pages). */
    val dates: List<String> = emptyList(),
    /** Bond ids this artwork belongs to (rendered on bond detail). */
    val bonds: List<String> = emptyList(),
    /** Optional inclusive lower bond-rank bound for rank-aware bond artwork. */
    val minBondRank: Int? = null,
    /** Optional inclusive upper bond-rank bound for rank-aware bond artwork. */
    val maxBondRank: Int? = null,
)

/** Serving-surface kind helpers shared by the store, lint, and UI. */
object MediaKinds {
    const val ACHIEVEMENT = "achievement"
    const val MONTH = "month"
    const val SECTION = "section"
    const val DAY = "day"
    const val PORTRAIT = "portrait"
    const val BANNER = "banner"
    const val BACKDROP = "backdrop"
    const val GUIDE = "guide"

    val ALL = setOf(ACHIEVEMENT, MONTH, SECTION, DAY, PORTRAIT, BANNER, BACKDROP, GUIDE)

    /** True when the item carries no anchor at all (gallery-only). */
    fun isUnanchored(item: MediaItem): Boolean =
        item.months.isEmpty() && item.dates.isEmpty() && item.bonds.isEmpty()
}
