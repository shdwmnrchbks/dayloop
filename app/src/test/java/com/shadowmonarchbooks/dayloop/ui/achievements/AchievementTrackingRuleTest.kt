package com.shadowmonarchbooks.dayloop.ui.achievements

import com.shadowmonarchbooks.dayloop.pack.schema.AchievementDefinition
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementEventAnchor
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingRule
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.pack.schema.Step
import com.shadowmonarchbooks.dayloop.progress.StepKey
import com.shadowmonarchbooks.dayloop.progress.StepMark
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AchievementTrackingRuleTest {

    @Test
    fun `DONE matching step completes semantic event`() {
        val events = completedAchievementEvents(
            anchors = listOf(anchor("event.one", "Seafood Full Course")),
            days = mapOf(DATE to day("Evening: eat the Seafood Full Course")),
            marks = mapOf(StepKey(DATE, 0) to StepMark.DONE),
            routeId = "default",
        )

        assertEquals(setOf("event.one"), events)
    }

    @Test
    fun `SKIP does not complete semantic event`() {
        val events = completedAchievementEvents(
            anchors = listOf(anchor("event.one", "Seafood Full Course")),
            days = mapOf(DATE to day("Evening: eat the Seafood Full Course")),
            marks = mapOf(StepKey(DATE, 0) to StepMark.SKIP),
            routeId = "default",
        )

        assertTrue(events.isEmpty())
    }

    @Test
    fun `ambiguous selector never awards event`() {
        val events = completedAchievementEvents(
            anchors = listOf(anchor("event.one", "feed the cat")),
            days = mapOf(DATE to day("Feed the cat", "Feed the cat again")),
            marks = mapOf(
                StepKey(DATE, 0) to StepMark.DONE,
                StepKey(DATE, 1) to StepMark.DONE,
            ),
            routeId = "default",
        )

        assertTrue(events.isEmpty())
    }

    @Test
    fun `all events exposes partial progress before completion`() {
        val achievement = achievement(
            type = AchievementTrackingTypes.ALL_EVENTS,
            events = listOf("one", "two", "three"),
        )

        val progress = achievementProgress(achievement, DATE, setOf("one", "three"))

        assertFalse(progress.completed)
        assertEquals(2, progress.completedUnits)
        assertEquals(3, progress.totalUnits)
    }

    @Test
    fun `counter completes at target`() {
        val achievement = achievement(
            type = AchievementTrackingTypes.COUNTER,
            events = listOf("one", "two", "three", "four", "five"),
            target = 5,
        )

        val progress = achievementProgress(
            achievement,
            DATE,
            setOf("one", "two", "three", "four", "five"),
        )

        assertTrue(progress.completed)
        assertTrue(progress.automatic)
        assertEquals(5, progress.completedUnits)
        assertEquals(5, progress.totalUnits)
    }

    @Test
    fun `manual target exposes explicit partial progress`() {
        val achievement = achievement(
            type = AchievementTrackingTypes.MANUAL,
            target = 10,
        )

        val progress = achievementProgress(
            achievement = achievement,
            currentDate = DATE,
            completedEvents = emptySet(),
            manualUnits = 4,
        )

        assertFalse(progress.completed)
        assertFalse(progress.automatic)
        assertEquals(4, progress.completedUnits)
        assertEquals(10, progress.totalUnits)
    }

    @Test
    fun `conditional target completes from explicit progress without becoming automatic`() {
        val achievement = achievement(
            type = AchievementTrackingTypes.CONDITIONAL,
            target = 9,
        )

        val progress = achievementProgress(
            achievement = achievement,
            currentDate = DATE,
            completedEvents = emptySet(),
            manualUnits = 9,
        )

        assertTrue(progress.completed)
        assertFalse(progress.automatic)
        assertEquals(9, progress.completedUnits)
        assertEquals(9, progress.totalUnits)
    }

    @Test
    fun `manual progress is clamped to target`() {
        val achievement = achievement(
            type = AchievementTrackingTypes.MANUAL,
            target = 10,
        )

        val progress = achievementProgress(
            achievement = achievement,
            currentDate = DATE,
            completedEvents = emptySet(),
            manualUnits = 99,
        )

        assertEquals(10, progress.completedUnits)
        assertTrue(progress.completed)
    }

    @Test
    fun `story achievement completes only after its story date`() {
        val achievement = AchievementDefinition(
            id = "story",
            title = "Story",
            tracking = AchievementTrackingRule(
                type = AchievementTrackingTypes.STORY_DATE,
                date = DATE,
            ),
        )

        assertFalse(achievementProgress(achievement, DATE, emptySet()).completed)
        assertTrue(achievementProgress(achievement, "2009-06-20", emptySet()).completed)
    }

    @Test
    fun `manual achievement without target is never inferred`() {
        val achievement = achievement(type = AchievementTrackingTypes.MANUAL)

        assertFalse(achievementProgress(achievement, "2010-03-05", emptySet()).completed)
        assertFalse(achievementProgress(achievement, "2010-03-05", emptySet()).automatic)
    }

    private fun anchor(id: String, label: String) = AchievementEventAnchor(
        id = id,
        date = DATE,
        labelContains = label,
    )

    private fun day(vararg labels: String) = Day(
        date = DATE,
        weekday = "fri",
        steps = labels.map { label -> Step(label = label) },
    )

    private fun achievement(
        type: String,
        events: List<String> = emptyList(),
        target: Int? = null,
    ) = AchievementDefinition(
        id = "fixture",
        title = "Fixture",
        tracking = AchievementTrackingRule(type = type, events = events, target = target),
    )

    private companion object {
        const val DATE = "2009-06-19"
    }
}
