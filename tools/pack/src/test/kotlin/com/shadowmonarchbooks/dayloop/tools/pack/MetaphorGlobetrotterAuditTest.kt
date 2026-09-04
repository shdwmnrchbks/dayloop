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

class MetaphorGlobetrotterAuditTest {

    private val towns = linkedMapOf(
        "metaphor.event.town-komero" to Pair("2100-07-07", "Komero stop"),
        "metaphor.event.town-inundo" to Pair("2100-07-29", "Inundo stop"),
        "metaphor.event.town-silento" to Pair("2100-08-27", "Silento stop"),
        "metaphor.event.town-malva" to Pair("2100-09-14", "Malva stop"),
        "metaphor.event.town-ligno" to Pair("2100-09-20", "Ligno stop"),
    )

    @Test
    fun `Globetrotter tracks exactly the five required towns`() {
        val achievement = assertNotNull(loadMetaphor().achievements)
            .achievements
            .single { it.id == "metaphor.achievement.globetrotter" }

        assertEquals(AchievementTrackingTypes.ALL_EVENTS, achievement.tracking.type)
        assertEquals("2100-07-07", achievement.availableFrom)
        assertEquals("2100-09-20", achievement.expectedBy)
        assertEquals(towns.keys.toList(), achievement.tracking.events)
        assertEquals(5, achievement.tracking.events.distinct().size)
    }

    @Test
    fun `every Globetrotter town event resolves to one authored route stop`() {
        val loaded = loadMetaphor()
        val events = assertNotNull(loaded.achievements).events.associateBy { it.id }

        towns.forEach { (eventId, route) ->
            val (date, phrase) = route
            val event = assertNotNull(events[eventId], eventId)
            assertEquals(date, event.date, eventId)
            assertEquals(phrase, event.labelContains, eventId)
            assertEquals(
                1,
                day(date).steps.count { it.label.contains(phrase, ignoreCase = true) },
                "$eventId should resolve to exactly one $date walkthrough step",
            )
        }
    }

    @Test
    fun `Silento is explicitly protected because no quest forces the visit`() {
        val silento = assertNotNull(day("2100-08-27").steps.singleOrNull {
            it.label.contains("Silento stop", ignoreCase = true)
        })
        assertTrue(silento.label.contains("No quest forces", ignoreCase = true))
        assertTrue(silento.label.contains("do not skip", ignoreCase = true))
        assertTrue(silento.label.contains("Globetrotter", ignoreCase = true))
    }

    @Test
    fun `Malva route buys both Warmth in Winter and cooking supplies`() {
        val malva = assertNotNull(day("2100-09-14").steps.singleOrNull {
            it.label.contains("Malva stop", ignoreCase = true)
        })
        assertTrue(malva.label.contains("Durable Spider Silk", ignoreCase = true))
        assertTrue(malva.label.contains("Altabury Wheat Flour", ignoreCase = true))

        val night = text("2100-09-14")
        assertTrue(night.contains("complete 'Warmth in Winter'", ignoreCase = true))
    }

    @Test
    fun `Ligno is the authored fifth and final Globetrotter town`() {
        val ligno = assertNotNull(day("2100-09-20").steps.singleOrNull {
            it.label.contains("Ligno stop", ignoreCase = true)
        })
        assertTrue(ligno.label.contains("Scoundrel's Hold", ignoreCase = true))
        assertTrue(ligno.label.contains("fifth Globetrotter town", ignoreCase = true))

        val achievement = assertNotNull(loadMetaphor().achievements)
            .achievements
            .single { it.id == "metaphor.achievement.globetrotter" }
        assertEquals("metaphor.event.town-ligno", achievement.tracking.events.last())
        assertEquals("2100-09-20", achievement.expectedBy)
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
