package com.shadowmonarchbooks.dayloop.ui.achievements

import com.shadowmonarchbooks.dayloop.pack.schema.AchievementDefinition
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementEventAnchor
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.progress.StepKey
import com.shadowmonarchbooks.dayloop.progress.StepMark

internal data class AchievementProgress(
    val automatic: Boolean,
    val completed: Boolean,
    val completedUnits: Int = 0,
    val totalUnits: Int = 0,
    val available: Boolean = true,
)

/**
 * Resolve semantic event anchors to DONE walkthrough steps. An anchor is
 * intentionally ignored when its selector matches zero or multiple steps;
 * ambiguity must never silently award an achievement.
 */
internal fun completedAchievementEvents(
    anchors: List<AchievementEventAnchor>,
    days: Map<String, Day>,
    marks: Map<StepKey, StepMark>,
    routeId: String,
): Set<String> = buildSet {
    for (anchor in anchors) {
        if (anchor.routeId != null && anchor.routeId != routeId) continue
        val day = days[anchor.date] ?: continue
        val matches = day.steps.mapIndexedNotNull { index, step ->
            index.takeIf { step.label.contains(anchor.labelContains, ignoreCase = true) }
        }
        if (matches.size != 1) continue
        if (marks[StepKey(anchor.date, matches.single())] == StepMark.DONE) add(anchor.id)
    }
}

internal fun achievementProgress(
    achievement: AchievementDefinition,
    currentDate: String,
    completedEvents: Set<String>,
): AchievementProgress {
    val rule = achievement.tracking
    val available = achievement.availableFrom?.let { currentDate >= it } ?: true
    return when (rule.type) {
        AchievementTrackingTypes.STORY_DATE -> {
            // The clock represents the start of the current playable day. A
            // story event dated D is known complete once End Day advances past D.
            val date = rule.date ?: achievement.expectedBy
            AchievementProgress(
                automatic = date != null,
                completed = date != null && currentDate > date,
                completedUnits = if (date != null && currentDate > date) 1 else 0,
                totalUnits = if (date != null) 1 else 0,
                available = available,
            )
        }
        AchievementTrackingTypes.EVENT -> {
            val event = rule.event
            val done = event != null && event in completedEvents
            AchievementProgress(true, done, if (done) 1 else 0, if (event != null) 1 else 0, available)
        }
        AchievementTrackingTypes.ALL_EVENTS -> {
            val required = rule.events.distinct()
            val done = required.count { it in completedEvents }
            AchievementProgress(
                automatic = required.isNotEmpty(),
                completed = required.isNotEmpty() && done == required.size,
                completedUnits = done,
                totalUnits = required.size,
                available = available,
            )
        }
        AchievementTrackingTypes.ANY_EVENT -> {
            val required = rule.events.distinct()
            val done = required.any { it in completedEvents }
            AchievementProgress(
                automatic = required.isNotEmpty(),
                completed = done,
                completedUnits = if (done) 1 else 0,
                totalUnits = if (required.isNotEmpty()) 1 else 0,
                available = available,
            )
        }
        AchievementTrackingTypes.COUNTER -> {
            val required = rule.events.distinct()
            val target = rule.target ?: required.size
            val done = required.count { it in completedEvents }.coerceAtMost(target.coerceAtLeast(0))
            AchievementProgress(
                automatic = required.isNotEmpty() && target > 0,
                completed = target > 0 && done >= target,
                completedUnits = done,
                totalUnits = target.coerceAtLeast(0),
                available = available,
            )
        }
        // Conditional/manual achievements may still expose an expected date,
        // but Dayloop must not infer the in-game result from passage of time.
        else -> AchievementProgress(
            automatic = false,
            completed = false,
            available = available,
        )
    }
}
