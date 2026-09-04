package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorQuestRouteAuditTest {

    @Test
    fun `Virga route preserves Price of Hope and A Guiding Gift`() {
        val august19 = day("2100-08-19")
        val priceOfHope = assertNotNull(august19.steps.firstOrNull {
            it.label.contains("Price of Hope", ignoreCase = true)
        })
        assertEquals(10, priceOfHope.statGains["wisdom"])
        assertTrue(august19.steps.any {
            it.label.contains("A Guiding Gift", ignoreCase = true) &&
                it.label.contains("Polar Stones", ignoreCase = true)
        })

        val august20 = day("2100-08-20")
        assertTrue(august20.steps.any {
            it.label.contains("A Guiding Gift", ignoreCase = true) &&
                it.label.contains("turn in", ignoreCase = true)
        })
        assertTrue(august20.steps.any {
            it.label.contains("Imagination reaches rank 4", ignoreCase = true)
        })
    }

    @Test
    fun `September candidate hunt route preserves required side quests`() {
        val september13 = text("2100-09-13")
        assertTrue(september13.contains("Proof of Power", ignoreCase = true))
        assertTrue(september13.contains("The Fiend in the Frozen Forest", ignoreCase = true))
        assertTrue(september13.contains("Warmth in Winter", ignoreCase = true))
        assertTrue(september13.contains("Wayward Shepherd", ignoreCase = true))

        val september14 = day("2100-09-14")
        val warmth = assertNotNull(september14.steps.firstOrNull {
            it.label.contains("Warmth in Winter", ignoreCase = true) &&
                it.label.contains("complete", ignoreCase = true)
        })
        assertEquals(10, warmth.statGains["tolerance"])
        assertTrue(september14.steps.any { it.label.contains("Defeat Milo", ignoreCase = true) })

        assertTrue(day("2100-09-15").steps.any {
            it.label.contains("Defeat Milo", ignoreCase = true) &&
                it.label.contains("complete", ignoreCase = true)
        })
    }

    @Test
    fun `late September restores Wayward Shepherd and Icebeast rewards`() {
        val september18 = day("2100-09-18")
        assertTrue(september18.steps.any {
            it.label.contains("Imagination reaches rank 5", ignoreCase = true)
        })
        val wayward = assertNotNull(september18.steps.firstOrNull {
            it.label.contains("Wayward Shepherd", ignoreCase = true)
        })
        assertEquals(10, wayward.statGains["eloquence"])
        assertTrue(wayward.label.contains("Rusty Greatsword", ignoreCase = true))
        assertTrue(wayward.label.contains("Gold Beetle", ignoreCase = true))

        val september19 = day("2100-09-19")
        assertTrue(september19.steps.any {
            it.label.contains("Solstice Crossing", ignoreCase = true) &&
                it.statGains["wisdom"] == 10
        })
        assertTrue(september19.steps.any {
            it.label.contains("Fiend in the Frozen Forest", ignoreCase = true) &&
                it.label.contains("Gold Beetle", ignoreCase = true)
        })
    }

    @Test
    fun `September 26 collects the final quest set`() {
        val text = text("2100-09-26")
        listOf(
            "The Incarnate in the Woods",
            "The Cockatrice in the Clouds",
            "The Apostles of the Apocalypse",
            "Trial of the Dragon: Mad Mischief",
            "Heroes' Rest",
            "Bygone Legacy",
            "Deliver Hot Spring Water",
        ).forEach { phrase ->
            assertTrue(text.contains(phrase, ignoreCase = true), "09/26 missing $phrase")
        }
    }

    @Test
    fun `Disgraced Ruins route reaches Wisdom five and completes More chapter six`() {
        val september30 = day("2100-09-30")
        assertTrue(september30.steps.any {
            it.label.contains("Malibelo Stope", ignoreCase = true) &&
                it.statGains["wisdom"] == 10
        })
        assertTrue(september30.steps.any {
            it.label.contains("Wisdom reaches rank 5", ignoreCase = true)
        })
        val more = assertNotNull(september30.steps.firstOrNull {
            it.label.contains("More's Task Chapter Six", ignoreCase = true)
        })
        assertTrue(more.label.contains("Elite Archetype", ignoreCase = true))
        assertTrue(more.label.contains("Follower rank 8", ignoreCase = true))
        assertEquals(10, more.statGains["imagination"])
    }

    @Test
    fun `October closes Brothers Mercy Proof of Power and hot spring beetle chain`() {
        val october4 = assertNotNull(day("2100-10-04").steps.firstOrNull {
            it.label.contains("A Brother's Mercy", ignoreCase = true)
        })
        assertEquals(10, october4.statGains["tolerance"])

        val october11 = day("2100-10-11")
        assertTrue(october11.steps.any {
            it.label.contains("Ziocropos", ignoreCase = true) &&
                it.label.contains("Proof of Power", ignoreCase = true)
        })
        val proof = assertNotNull(october11.steps.firstOrNull {
            it.label.contains("complete 'Proof of Power'", ignoreCase = true)
        })
        assertEquals(10, proof.statGains["courage"])
        assertTrue(proof.label.contains("hot spring", ignoreCase = true))

        val october12 = assertNotNull(day("2100-10-12").steps.firstOrNull {
            it.label.contains("Deliver Hot Spring Water", ignoreCase = true)
        })
        assertEquals("metaphor.activity.gold-beetles", october12.activityRef)
        assertTrue(october12.label.contains("final Gold Beetle", ignoreCase = true))
    }

    @Test
    fun `Virga village sweep retains all five beetles`() {
        val august18 = assertNotNull(day("2100-08-18").steps.firstOrNull {
            it.activityRef == "metaphor.activity.gold-beetles"
        })
        assertTrue(august18.label.contains("five Gold Beetles", ignoreCase = true))
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
