package com.shadowmonarchbooks.dayloop.pack.schema

import kotlinx.serialization.Serializable

/** Pack-native achievement catalog plus semantic anchors into walkthrough steps. */
@Serializable
data class AchievementsFile(
    val achievements: List<AchievementDefinition> = emptyList(),
    val events: List<AchievementEventAnchor> = emptyList(),
)

@Serializable
data class AchievementDefinition(
    val id: String,
    val title: String,
    val description: String? = null,
    val scope: String = "base",
    /** Earliest date the achievement is meaningfully actionable in this route. */
    val availableFrom: String? = null,
    /** Route-specific checkpoint where the guide expects this to be earned. */
    val expectedBy: String? = null,
    val missable: Boolean = false,
    /** Optional MediaItem id; achievement data never requires an image. */
    val iconMediaRef: String? = null,
    val tracking: AchievementTrackingRule = AchievementTrackingRule(),
)

/**
 * Engine-neutral tracking rule. Event-backed rules resolve semantic anchors
 * against the current walkthrough and only count events whose step is DONE.
 */
@Serializable
data class AchievementTrackingRule(
    val type: String = AchievementTrackingTypes.MANUAL,
    val date: String? = null,
    val event: String? = null,
    val events: List<String> = emptyList(),
    val target: Int? = null,
)

/**
 * Stable semantic selector for an authored walkthrough step. The selector is
 * deliberately independent of step index, so inserting/reordering steps does
 * not silently retarget achievement progress.
 */
@Serializable
data class AchievementEventAnchor(
    val id: String,
    val date: String,
    val labelContains: String,
    val routeId: String? = null,
)

object AchievementTrackingTypes {
    const val STORY_DATE = "storyDate"
    const val EVENT = "event"
    const val ALL_EVENTS = "allEvents"
    const val ANY_EVENT = "anyEvent"
    const val COUNTER = "counter"
    const val CONDITIONAL = "conditional"
    const val MANUAL = "manual"

    val ALL: Set<String> = setOf(
        STORY_DATE,
        EVENT,
        ALL_EVENTS,
        ANY_EVENT,
        COUNTER,
        CONDITIONAL,
        MANUAL,
    )
}
