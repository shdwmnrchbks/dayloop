package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorDataAuditTest {

    @Test
    fun `Metaphor identifies the authored completion route`() {
        val loaded = loadMetaphor()
        val pack = assertNotNull(loaded.pack)
        val route = assertNotNull(pack.routes.singleOrNull())

        assertEquals(Routes.DEFAULT, route.id)
        assertEquals("100% Completion Route", route.label)
        assertTrue(route.description.orEmpty().contains("not universal", ignoreCase = true))
    }

    @Test
    fun `Metaphor operation deadlines use actual failure dates`() {
        val loaded = loadMetaphor()
        val deadlines = assertNotNull(loaded.deadlines).deadlines.associateBy { it.id }

        val expected = mapOf(
            "metaphor.deadline.mission.prologue" to "2100-06-11",
            "metaphor.deadline.mission.grand-cathedral" to "2100-06-21",
            "metaphor.deadline.mission.kriegante-castle" to "2100-07-16",
            "metaphor.deadline.mission.charadrius" to "2100-08-12",
            "metaphor.deadline.mission.dragon-temple" to "2100-09-05",
            "metaphor.deadline.mission.prepare-final-battle" to "2100-09-22",
            "metaphor.deadline.mission.skybound-avatar" to "2100-10-25",
            "metaphor.deadline.mission.save-country" to "2100-10-26",
        )

        expected.forEach { (id, date) ->
            val deadline = deadlines.getValue(id)
            assertEquals("palace", deadline.kind, id)
            assertEquals(date, deadline.date, id)
            assertEquals(null, deadline.window, id)
        }
    }

    @Test
    fun `Metaphor route targets are not presented as hard deadlines`() {
        val loaded = loadMetaphor()
        val deadlines = assertNotNull(loaded.deadlines).deadlines.associateBy { it.id }

        val routeTargets = mapOf(
            "metaphor.deadline.mission.border-fort" to "2100-06-05",
            "metaphor.deadline.mission.nord-mines" to "2100-06-06",
            "metaphor.deadline.mission.opera-house" to "2100-09-10",
            "metaphor.deadline.mission.duel-louis" to "2100-09-23",
        )

        routeTargets.forEach { (id, date) ->
            val target = deadlines.getValue(id)
            assertEquals("routeTarget", target.kind, id)
            assertEquals(date, target.date, id)
            assertTrue(target.label.startsWith("Route target:"), target.label)
        }
    }

    @Test
    fun `Metaphor missable windows preserve their real availability`() {
        val loaded = loadMetaphor()
        val deadlines = assertNotNull(loaded.deadlines).deadlines.associateBy { it.id }
        val activities = assertNotNull(loaded.activities).activities.associateBy { it.id }

        val julian = deadlines.getValue("metaphor.deadline.missable.julian-book")
        assertEquals("2100-07-23", assertNotNull(julian.window).start)
        assertEquals("2100-08-13", julian.window?.end)
        assertTrue(
            activities.getValue("metaphor.activity.book.the-future-of-magic")
                .notes.orEmpty()
                .contains("08/13"),
        )

        val inn = deadlines.getValue("metaphor.deadline.missable.inn-cooking")
        assertEquals("2100-06-12", assertNotNull(inn.window).start)
        assertEquals("2100-09-25", inn.window?.end)
        assertTrue(
            activities.getValue("metaphor.activity.cooking.inn")
                .notes.orEmpty()
                .contains("before 09/26", ignoreCase = true),
        )
    }

    @Test
    fun `Metaphor route dungeon days can precede the actual deadline`() {
        val loaded = loadMetaphor()
        val days = loaded.walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .associateBy { it.date }

        assertTrue(assertNotNull(days["2100-06-12"]).steps.any {
            it.label.contains("cathedral", ignoreCase = true)
        })
        assertTrue(assertNotNull(days["2100-07-05"]).steps.any {
            it.label.contains("Kriegante Castle", ignoreCase = true)
        })
        assertTrue(assertNotNull(days["2100-07-25"]).steps.any {
            it.label.contains("Charadrius", ignoreCase = true)
        })
        assertTrue(assertNotNull(days["2100-08-19"]).steps.any {
            it.label.contains("Dragon Temple", ignoreCase = true)
        })
    }

    private fun loadMetaphor() = PackLoader.load(metaphorDir()).also { loaded ->
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
    }

    private fun metaphorDir(): Path {
        val candidates = listOf(
            Path.of("content", "packs", "metaphor"),
            Path.of("..", "..", "content", "packs", "metaphor"),
        )
        return candidates.firstOrNull { Files.isDirectory(it) }
            ?: error("Cannot locate content/packs/metaphor from ${Path.of("").toAbsolutePath()}")
    }
}
