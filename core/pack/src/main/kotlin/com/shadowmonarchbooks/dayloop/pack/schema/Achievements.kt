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

/** One user-confirmable item in a pack-authored checklist or choice. */
@Serializable
data class AchievementTrackingItem(
    val id: String,
    val label: String,
    /** Optional deadline for a missable checklist item. */
    val dueBy: String? = null,
)

/**
 * Engine-neutral tracking rule. Event-backed rules resolve semantic anchors
 * against the current walkthrough and only count events whose step is DONE.
 * Checklist/choice definitions are authored here while their mutable state
 * belongs to the active player profile.
 */
@Serializable
data class AchievementTrackingRule(
    val type: String = AchievementTrackingTypes.MANUAL,
    /** Story date, or earliest date a manual choice/confirmation can be finalized. */
    val date: String? = null,
    val event: String? = null,
    val events: List<String> = emptyList(),
    val target: Int? = null,
    val items: List<AchievementTrackingItem> = emptyList(),
    /** Shared persistence key for a choice used by more than one achievement. */
    val stateKey: String? = null,
    /** Choice item ids that keep this achievement attainable. */
    val acceptedItems: List<String> = emptyList(),
    /** User-facing confirmation/choice guidance. */
    val prompt: String? = null,
    /** Optional display unit for a manual counter, e.g. "¥". */
    val unit: String? = null,
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
    const val CHECKLIST = "checklist"
    const val CHOICE = "choice"
    const val CONFIRMATION = "confirmation"
    const val CONDITIONAL = "conditional"
    const val MANUAL = "manual"

    val ALL: Set<String> = setOf(
        STORY_DATE,
        EVENT,
        ALL_EVENTS,
        ANY_EVENT,
        COUNTER,
        CHECKLIST,
        CHOICE,
        CONFIRMATION,
        CONDITIONAL,
        MANUAL,
    )
}
