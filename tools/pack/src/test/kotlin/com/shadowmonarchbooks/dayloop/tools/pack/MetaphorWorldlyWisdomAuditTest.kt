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

class MetaphorWorldlyWisdomAuditTest {

    private val newMapNodes = linkedMapOf(
        "metaphor.event.map-belega-corridor" to Pair("2100-06-15", "Clear Belega Corridor"),
        "metaphor.event.map-imps-den" to Pair("2100-06-29", "Clear Imp's Den"),
        "metaphor.event.map-man-eaters-grotto" to Pair("2100-07-07", "Clear Man-Eater's Grotto"),
        "metaphor.event.map-abandoned-tomb" to Pair("2100-07-09", "Enter the Abandoned Tomb"),
        "metaphor.event.map-tomb-of-lament" to Pair("2100-07-30", "Tomb of Lament"),
        "metaphor.event.map-gracia-forest" to Pair("2100-08-01", "Gracia Forest"),
        "metaphor.event.map-forsaken-tower" to Pair("2100-08-03", "Forsaken Tower"),
        "metaphor.event.map-land-of-ceremony" to Pair("2100-08-23", "Land of Ceremony"),
        "metaphor.event.map-orbwise-path" to Pair("2100-08-24", "Orbwise Path: collect both Gold Beetles"),
        "metaphor.event.map-spire-of-blind-faith" to Pair("2100-08-27", "Climb the Spire of Blind Faith"),
        "metaphor.event.map-loveless-runner" to Pair("2100-09-14", "Loveless boards the runner"),
        "metaphor.event.map-milo-runner" to Pair("2100-09-15", "Milo's duel"),
        "metaphor.event.map-rudolf-runner" to Pair("2100-09-16", "Rudolf's duel"),
        "metaphor.event.map-mt-vulkano" to Pair("2100-09-18", "Clear Mt. Vulkano"),
        "metaphor.event.map-everfrost-forest" to Pair("2100-09-19", "Clear Everfrost Forest"),
        "metaphor.event.map-scoundrels-hold" to Pair("2100-09-21", "Clear Scoundrel's Hold"),
        "metaphor.event.map-disgraced-ruins" to Pair("2100-09-30", "At Disgraced Ruins"),
        "metaphor.event.map-manor-of-the-ascendant" to Pair("2100-10-03", "Manor of the Ascendant"),
        "metaphor.event.map-jin-runner" to Pair("2100-10-05", "Jin's runner"),
        "metaphor.event.map-abandoned-path" to Pair("2100-10-07", "Clear the Abandoned Path"),
        "metaphor.event.map-tower-of-insolence" to Pair("2100-10-11", "Tower of Insolence"),
    )

    private val reusedMapNodes = listOf(
        "metaphor.event.town-komero",
        "metaphor.event.town-inundo",
        "metaphor.event.town-ligno",
        "metaphor.event.town-silento",
        "metaphor.event.town-malva",
        "metaphor.event.drawing-peregrine-falls",
        "metaphor.event.drawing-prismatic-sea",
        "metaphor.event.drawing-solstice-crossing",
        "metaphor.event.drawing-decaying-estate",
        "metaphor.event.drawing-malibelo-stope",
        "metaphor.event.drawing-sporico-cave",
    )

    @Test
    fun `Worldly Wisdom tracks all nontrivial map nodes through Skybound Avatar`() {
        val achievement = worldlyWisdom()
        assertEquals(AchievementTrackingTypes.ALL_EVENTS, achievement.tracking.type)
        assertEquals("2100-06-15", achievement.availableFrom)
        assertEquals("2100-10-16", achievement.expectedBy)
        assertEquals(33, achievement.tracking.events.size)
        assertEquals(33, achievement.tracking.events.distinct().size)
        assertEquals("metaphor.event.skybound-hope", achievement.tracking.events.last())

        newMapNodes.keys.forEach { event ->
            assertTrue(event in achievement.tracking.events, "Worldly Wisdom missing $event")
        }
        reusedMapNodes.forEach { event ->
            assertTrue(event in achievement.tracking.events, "Worldly Wisdom missing reused $event")
        }
    }

    @Test
    fun `new Worldly Wisdom map events resolve to exactly one authored route step`() {
        val events = assertNotNull(loadMetaphor().achievements).events.associateBy { it.id }

        newMapNodes.forEach { (eventId, route) ->
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
    fun `temporary candidate runner map markers are all protected`() {
        val expected = mapOf(
            "metaphor.event.map-loveless-runner" to "2100-09-14",
            "metaphor.event.map-milo-runner" to "2100-09-15",
            "metaphor.event.map-rudolf-runner" to "2100-09-16",
            "metaphor.event.map-jin-runner" to "2100-10-05",
        )
        val events = assertNotNull(loadMetaphor().achievements).events.associateBy { it.id }
        expected.forEach { (id, date) ->
            assertEquals(date, assertNotNull(events[id], id).date)
            assertTrue(id in worldlyWisdom().tracking.events)
        }
    }

    @Test
    fun `Worldly Wisdom reuses audited town and vista events instead of duplicating them`() {
        val events = worldlyWisdom().tracking.events
        reusedMapNodes.forEach { reused -> assertTrue(reused in events, reused) }
        assertEquals(5, events.count { it.startsWith("metaphor.event.town-") })
        assertEquals(6, events.count { it.startsWith("metaphor.event.drawing-") })
        assertEquals(21, events.count { it.startsWith("metaphor.event.map-") })
    }

    @Test
    fun `Skybound Avatar is the final Worldly Wisdom gate`() {
        val achievement = worldlyWisdom()
        assertEquals("2100-10-16", achievement.expectedBy)
        assertEquals("metaphor.event.skybound-hope", achievement.tracking.events.last())

        val skybound = assertNotNull(loadMetaphor().achievements)
            .events
            .single { it.id == "metaphor.event.skybound-hope" }
        assertEquals("2100-10-16", skybound.date)
        assertTrue(day("2100-10-16").steps.any {
            it.label.contains("Skybound Hope unlocks", ignoreCase = true)
        })
    }

    private fun worldlyWisdom() = assertNotNull(loadMetaphor().achievements)
        .achievements
        .single { it.id == "metaphor.achievement.worldly-wisdom" }

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
