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

class MetaphorVistaViewerAuditTest {

    private val drawings = linkedMapOf(
        "metaphor.event.drawing-tree-of-prophecy" to Pair("2100-06-25", "Tree of Prophecy"),
        "metaphor.event.drawing-midnight-sunsands" to Pair("2100-07-02", "Midnight Sunsands"),
        "metaphor.event.drawing-city-ruins" to Pair("2100-07-18", "City Ruins"),
        "metaphor.event.drawing-peregrine-falls" to Pair("2100-07-30", "Peregrine Falls"),
        "metaphor.event.drawing-towering-seawall" to Pair("2100-08-15", "Towering Seawall"),
        "metaphor.event.drawing-prismatic-sea" to Pair("2100-08-22", "Prismatic Sea"),
        "metaphor.event.drawing-sporico-cave" to Pair("2100-08-26", "Sporico Cave"),
        "metaphor.event.drawing-colerodio-cliff" to Pair("2100-09-08", "Colerodio Cliff"),
        "metaphor.event.drawing-murky-graveyard" to Pair("2100-09-09", "Murky Graveyard"),
        "metaphor.event.drawing-solstice-crossing" to Pair("2100-09-19", "Solstice Crossing"),
        "metaphor.event.drawing-malibelo-stope" to Pair("2100-09-30", "Malibelo Stope Drawing"),
        "metaphor.event.drawing-decaying-estate" to Pair("2100-10-11", "Decaying Estate Drawing"),
    )

    @Test
    fun `Vista Viewer is automatically tracked from all twelve journey drawings`() {
        val achievement = assertNotNull(loadMetaphor().achievements)
            .achievements
            .single { it.id == "metaphor.achievement.vista-viewer" }

        assertEquals(AchievementTrackingTypes.ALL_EVENTS, achievement.tracking.type)
        assertEquals("2100-06-25", achievement.availableFrom)
        assertEquals("2100-10-11", achievement.expectedBy)
        assertEquals(drawings.keys.toList(), achievement.tracking.events)
        assertEquals(12, achievement.tracking.events.distinct().size)
    }

    @Test
    fun `every Vista Viewer event resolves to its authored travel stop`() {
        val loaded = loadMetaphor()
        val events = assertNotNull(loaded.achievements).events.associateBy { it.id }

        drawings.forEach { (eventId, route) ->
            val (date, phrase) = route
            val event = assertNotNull(events[eventId], eventId)
            assertEquals(date, event.date, eventId)
            assertEquals(phrase, event.labelContains, eventId)

            val matches = day(date).steps.count { step ->
                step.label.contains(phrase, ignoreCase = true)
            }
            assertEquals(1, matches, "$eventId should resolve to exactly one $date walkthrough step")
        }
    }

    @Test
    fun `late route explicitly obtains the two drawings that needed audit fixes`() {
        val september30 = text("2100-09-30")
        assertTrue(september30.contains("Malibelo Stope Drawing", ignoreCase = true))
        assertTrue(september30.contains("receive", ignoreCase = true))
        assertTrue(!september30.contains("Malibelo Slope", ignoreCase = true))

        val october11 = text("2100-10-11")
        assertTrue(october11.contains("Decaying Estate Drawing", ignoreCase = true))
        assertTrue(october11.contains("awards", ignoreCase = true))
        assertTrue(october11.contains("Tower of Insolence", ignoreCase = true))
    }

    @Test
    fun `Vista Viewer completes on the final Decaying Estate drawing`() {
        val achievement = assertNotNull(loadMetaphor().achievements)
            .achievements
            .single { it.id == "metaphor.achievement.vista-viewer" }
        assertEquals("2100-10-11", achievement.expectedBy)
        assertEquals("metaphor.event.drawing-decaying-estate", achievement.tracking.events.last())
    }

    private fun text(date: String): String = day(date).steps.joinToString("\n") { it.label }

    private fun day(date: String) = assertNotNull(
        loadMetaphor().walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .firstOrNull { it.date == date },
        date,
    )

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
