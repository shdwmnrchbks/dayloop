package com.shadowmonarchbooks.dayloop.ui.achievements

import com.shadowmonarchbooks.dayloop.pack.schema.AchievementDefinition
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementEventAnchor
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingItem
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
    fun `conditional target remains supported for legacy packs`() {
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
    fun `checklist exposes authored partial progress`() {
        val achievement = achievement(
            type = AchievementTrackingTypes.CHECKLIST,
            items = listOf(
                AchievementTrackingItem("yukari", "Yukari"),
                AchievementTrackingItem("junpei", "Junpei"),
                AchievementTrackingItem("akihiko", "Akihiko"),
            ),
        )

        val progress = achievementProgress(
            achievement = achievement,
            currentDate = DATE,
            completedEvents = emptySet(),
            checkedItems = setOf("yukari", "akihiko"),
        )

        assertFalse(progress.completed)
        assertFalse(progress.automatic)
        assertEquals(2, progress.completedUnits)
        assertEquals(3, progress.totalUnits)
    }

    @Test
    fun `checklist ignores stale item ids and completes when all authored items are checked`() {
        val achievement = achievement(
            type = AchievementTrackingTypes.CHECKLIST,
            items = listOf(
                AchievementTrackingItem("yukari", "Yukari"),
                AchievementTrackingItem("junpei", "Junpei"),
            ),
        )

        val partial = achievementProgress(
            achievement = achievement,
            currentDate = DATE,
            completedEvents = emptySet(),
            checkedItems = setOf("yukari", "removed-item"),
        )
        val complete = achievementProgress(
            achievement = achievement,
            currentDate = DATE,
            completedEvents = emptySet(),
            checkedItems = setOf("yukari", "junpei", "removed-item"),
        )

        assertEquals(1, partial.completedUnits)
        assertFalse(partial.completed)
        assertEquals(2, complete.completedUnits)
        assertTrue(complete.completed)
    }

    @Test
    fun `accepted choice becomes confirmable only at its checkpoint`() {
        val achievement = achievement(
            type = AchievementTrackingTypes.CHOICE,
            date = "2009-12-31",
            acceptedItems = listOf("spare"),
            items = listOf(
                AchievementTrackingItem("spare", "Spare"),
                AchievementTrackingItem("kill", "Kill"),
            ),
        )

        val before = achievementProgress(
            achievement = achievement,
            currentDate = "2009-12-30",
            completedEvents = emptySet(),
            selectedChoice = "spare",
        )
        val ready = achievementProgress(
            achievement = achievement,
            currentDate = "2009-12-31",
            completedEvents = emptySet(),
            selectedChoice = "spare",
        )

        assertFalse(before.conditionReady)
        assertTrue(ready.conditionReady)
        assertFalse(ready.completed)
        assertEquals(1, ready.completedUnits)
        assertEquals(1, ready.totalUnits)
    }

    @Test
    fun `non qualifying choice never becomes confirmable`() {
        val achievement = achievement(
            type = AchievementTrackingTypes.CHOICE,
            acceptedItems = listOf("spare"),
            items = listOf(
                AchievementTrackingItem("spare", "Spare"),
                AchievementTrackingItem("kill", "Kill"),
            ),
        )

        val progress = achievementProgress(
            achievement = achievement,
            currentDate = "2010-03-05",
            completedEvents = emptySet(),
            selectedChoice = "kill",
        )

        assertFalse(progress.conditionReady)
        assertFalse(progress.completed)
        assertEquals(0, progress.completedUnits)
    }

    @Test
    fun `confirmation tracks deterministic prerequisite without auto earning`() {
        val achievement = achievement(
            type = AchievementTrackingTypes.CONFIRMATION,
            date = DATE,
            event = "gardening",
        )

        val progress = achievementProgress(
            achievement = achievement,
            currentDate = DATE,
            completedEvents = setOf("gardening"),
        )

        assertTrue(progress.conditionReady)
        assertEquals(true, progress.prerequisiteTracked)
        assertFalse(progress.completed)
        assertFalse(progress.automatic)
    }

    @Test
    fun `confirmation waits for result date`() {
        val achievement = achievement(
            type = AchievementTrackingTypes.CONFIRMATION,
            date = "2009-07-24",
        )

        assertFalse(
            achievementProgress(
                achievement,
                "2009-07-23",
                emptySet(),
            ).conditionReady,
        )
        assertTrue(
            achievementProgress(
                achievement,
                "2009-07-24",
                emptySet(),
            ).conditionReady,
        )
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
        date: String? = null,
        event: String? = null,
        events: List<String> = emptyList(),
        target: Int? = null,
        items: List<AchievementTrackingItem> = emptyList(),
        acceptedItems: List<String> = emptyList(),
    ) = AchievementDefinition(
        id = "fixture",
        title = "Fixture",
        tracking = AchievementTrackingRule(
            type = type,
            date = date,
            event = event,
            events = events,
            target = target,
            items = items,
            acceptedItems = acceptedItems,
        ),
    )

    private companion object {
        const val DATE = "2009-06-19"
    }
}
