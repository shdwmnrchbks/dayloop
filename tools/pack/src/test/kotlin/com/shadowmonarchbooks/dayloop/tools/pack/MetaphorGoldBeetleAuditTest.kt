package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorGoldBeetleAuditTest {

    private val routedBeetles = linkedMapOf(
        "2100-06-02" to 1,
        "2100-06-05" to 1,
        "2100-06-11" to 2,
        "2100-06-15" to 1,
        "2100-06-17" to 1,
        "2100-06-27" to 2,
        "2100-06-29" to 1,
        "2100-07-07" to 1,
        "2100-07-10" to 1,
        "2100-07-20" to 5,
        "2100-07-30" to 2,
        "2100-08-01" to 1,
        "2100-08-03" to 1,
        "2100-08-04" to 1,
        "2100-08-18" to 5,
        "2100-08-20" to 1,
        "2100-08-23" to 1,
        "2100-08-24" to 2,
        "2100-08-27" to 1,
        "2100-08-28" to 1,
        "2100-09-09" to 5,
        "2100-09-10" to 1,
        "2100-09-18" to 1,
        "2100-09-19" to 1,
        "2100-09-21" to 1,
        "2100-09-24" to 1,
        "2100-09-25" to 1,
        "2100-09-27" to 2,
        "2100-09-30" to 1,
        "2100-10-03" to 1,
        "2100-10-07" to 1,
        "2100-10-11" to 1,
        "2100-10-12" to 1,
    )

    @Test
    fun `100 percent route explicitly accounts for all 50 Gold Beetles`() {
        assertEquals(50, routedBeetles.values.sum())
        routedBeetles.forEach { (date, count) ->
            val steps = day(date).steps.filter { it.activityRef == "metaphor.activity.gold-beetles" }
            assertTrue(steps.isNotEmpty(), "$date should route $count Gold Beetle(s)")
        }

        val exchange = text("2100-10-13")
        assertTrue(exchange.contains("all 50", ignoreCase = true))
        assertTrue(exchange.contains("46", ignoreCase = true))
        assertTrue(exchange.contains("All That Glitters", ignoreCase = true))
    }

    @Test
    fun `early route uses real locations instead of phantom Beetles`() {
        assertContains("2100-06-02", "Recruitment Centre")
        assertContains("2100-06-11", "Sunshade Row")
        assertContains("2100-06-11", "Comfort Concoctions")
        assertContains("2100-06-27", "exactly two")
        assertContains("2100-06-27", "fireplace")
        assertContains("2100-06-27", "desk/water pitcher")
        assertContains("2100-06-27", "cupboard is not a Beetle")
        assertContains("2100-06-29", "northwest log")
        assertContains("2100-07-07", "B5")
        assertContains("2100-07-20", "all five")
        assertContains("2100-07-20", "Seabreeze Street")
        assertContains("2100-07-20", "Merchants' Bazaar")
        assertContains("2100-07-20", "Arenafront Wharf")

        val july21 = text("2100-07-21")
        assertTrue(july21.contains("no two new Martira Beetles yet", ignoreCase = true))
    }

    @Test
    fun `Dental Distress is protected as a missable Beetle`() {
        val loaded = loadMetaphor()
        val deadline = assertNotNull(
            loaded.deadlines?.deadlines?.firstOrNull {
                it.id == "metaphor.deadline.missable.dental-distress"
            },
        )
        val window = assertNotNull(deadline.window)
        assertEquals("2100-07-23", window.start)
        assertEquals("2100-08-09", window.end)

        assertContains("2100-07-23", "Dental Distress")
        assertContains("2100-07-23", "August 9")
        assertContains("2100-07-29", "Toothbrush of Hygienia")
        assertContains("2100-08-04", "missable Gold Beetle")
    }

    @Test
    fun `August route preserves every silent dungeon and delayed Beetle`() {
        assertContains("2100-08-01", "far northeast")
        assertContains("2100-08-18", "all five")
        assertContains("2100-08-20", "post-temple")
        assertContains("2100-08-23", "east-side room")
        assertContains("2100-08-24", "both Gold Beetles")
        assertContains("2100-08-27", "sixth floor")
        assertContains("2100-08-28", "post-8/19")
        assertContains("2100-08-28", "Comfort Concoctions")
    }

    @Test
    fun `Altabury and late game route preserves the remaining Beetles`() {
        val altabury = text("2100-09-09")
        listOf(
            "five Gold Beetles",
            "Lunlumo Approach",
            "Blue Sky Bridge",
            "Opera House Square",
            "Whitepeaks Magic Shop",
        ).forEach { phrase -> assertTrue(altabury.contains(phrase, ignoreCase = true), "09/09 missing $phrase") }
        assertContains("2100-09-10", "sixth town Gold Beetle")
        assertContains("2100-09-10", "Skyward Tavern")
        assertContains("2100-09-24", "collapsed house")
        assertContains("2100-09-25", "east-side food stall")
        assertContains("2100-09-27", "two post-9/26 Gold Beetles")
        assertContains("2100-09-27", "MesmerEyes Apothecary")
        assertContains("2100-09-30", "west of the dungeon entrance")
        assertContains("2100-10-03", "crawl-space")
        assertContains("2100-10-07", "northern area")
        assertContains("2100-10-11", "north side of 2F")
        assertContains("2100-10-12", "50th and final Gold Beetle")
    }

    @Test
    fun `October Spire revisit is fallback only`() {
        val october10 = day("2100-10-10").steps.singleOrNull {
            it.activityRef == "metaphor.activity.gold-beetles"
        }
        assertNotNull(october10)
        assertTrue(october10.label.contains("If", ignoreCase = true))
        assertTrue(october10.label.contains("missed on 8/27", ignoreCase = true))
    }

    private fun assertContains(date: String, phrase: String) {
        assertTrue(text(date).contains(phrase, ignoreCase = true), "$date missing $phrase")
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
