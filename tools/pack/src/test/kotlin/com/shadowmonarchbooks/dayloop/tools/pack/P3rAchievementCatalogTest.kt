package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class P3rAchievementCatalogTest {

    @Test
    fun `P3R ships 48 Journey achievements with resolvable tracking anchors`() {
        val loaded = PackLoader.load(p3rDir())
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
        val pack = assertNotNull(loaded.pack)
        val catalog = assertNotNull(loaded.achievements)

        assertEquals(48, catalog.achievements.size)
        assertEquals(catalog.achievements.size, catalog.achievements.map { it.id }.toSet().size)
        assertEquals(catalog.events.size, catalog.events.map { it.id }.toSet().size)

        val eventIds = catalog.events.map { it.id }.toSet()
        val referencedEvents = catalog.achievements.flatMap { achievement ->
            buildList {
                achievement.tracking.event?.let(::add)
                addAll(achievement.tracking.events)
            }
        }.toSet()
        assertTrue(referencedEvents.all { it in eventIds }, "Missing achievement event anchor(s): ${referencedEvents - eventIds}")
        assertTrue(
            catalog.achievements.all { it.tracking.type in AchievementTrackingTypes.ALL },
            "Unknown achievement tracking type",
        )

        val start = pack.calendar.startDate
        val end = pack.calendar.endDate
        catalog.achievements.forEach { achievement ->
            achievement.availableFrom?.let { assertTrue(it in start..end, "${achievement.id}: availableFrom outside calendar") }
            achievement.expectedBy?.let { assertTrue(it in start..end, "${achievement.id}: expectedBy outside calendar") }
            achievement.tracking.date?.let { assertTrue(it in start..end, "${achievement.id}: story date outside calendar") }
        }

        val defaultDays = loaded.walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .associateBy { it.date }
        catalog.events.forEach { event ->
            val days = if (event.routeId == null || event.routeId == Routes.DEFAULT) defaultDays else {
                loaded.walkthroughs
                    .filter { it.routeId == event.routeId }
                    .flatMap { it.file.days }
                    .associateBy { it.date }
            }
            val day = days[event.date]
            assertNotNull(day, "${event.id}: no walkthrough day ${event.date}")
            val matches = day.steps.count { it.label.contains(event.labelContains, ignoreCase = true) }
            assertEquals(1, matches, "${event.id}: '${event.labelContains}' must match exactly one step on ${event.date}")
        }
    }

    private fun p3rDir(): Path {
        val candidates = listOf(
            Path.of("content", "packs", "p3r"),
            Path.of("..", "..", "content", "packs", "p3r"),
        )
        return candidates.firstOrNull { Files.isDirectory(it) }
            ?: error("Cannot locate content/packs/p3r from ${Path.of("").toAbsolutePath()}")
    }
}
