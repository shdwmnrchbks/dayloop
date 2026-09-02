package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RJuneRouteOrderAuditTest {

    private fun contentPacksDir(): Path? {
        var dir: Path? = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        repeat(5) {
            val candidate = dir?.resolve("content")?.resolve("packs")
            if (candidate != null && candidate.isDirectory()) return candidate
            dir = dir?.parent
        }
        return null
    }

    @Test
    fun `p5r June route preserves story locks knowledge state lockpick chain and bank order`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val months = loaded.walkthroughs.associateBy { it.month }
        val april = months.getValue("2016-04").file
        val may = months.getValue("2016-05").file
        val june = months.getValue("2016-06").file
        val days = june.days.associateBy { it.date }

        assertEquals("school", days.getValue("2016-06-04").dayKind)
        assertEquals("school", days.getValue("2016-06-25").dayKind)
        listOf("2016-06-05", "2016-06-09", "2016-06-10", "2016-06-11", "2016-06-12", "2016-06-14").forEach { date ->
            assertEquals("story", days.getValue(date).dayKind, "$date has mandatory Royal story before or around the route's usable slot")
        }

        assertTrue(days.getValue("2016-06-05").steps.any { "Mandatory darts tutorial" in it.label })
        assertTrue(days.getValue("2016-06-09").steps.first().label.contains("TV station"))
        assertTrue(days.getValue("2016-06-10").steps.first().label.contains("Justice reaches rank 1 automatically"))
        assertTrue(days.getValue("2016-06-11").steps.first().label.contains("full day"))
        assertTrue(days.getValue("2016-06-12").steps.first().label.contains("free time resumes"))
        assertTrue(days.getValue("2016-06-14").steps.first().label.contains("Mandatory Makoto story"))

        val knowledgeThroughJun13 = (april.days + may.days + june.days.filter { it.date <= "2016-06-13" })
            .flatMap { it.steps }
            .sumOf { it.statGains["knowledge"] ?: 0 }
        assertEquals(126, knowledgeThroughJun13, "the Jun 13 class answer reaches Royal Knowledge rank 4 exactly")
        assertEquals(
            mapOf("knowledge" to 2),
            days.getValue("2016-06-13").steps.single { "Knowledge reaches rank 4" in it.label }.statGains,
        )

        val jun18 = days.getValue("2016-06-18")
        assertEquals("school", jun18.dayKind)
        assertTrue(jun18.steps.any { "finish with 3 lock picks total" in it.label })

        val jun19 = days.getValue("2016-06-19")
        val palaceIndex = jun19.steps.indexOfFirst { "First bank infiltration" in it.label }
        val shoppingIndex = jun19.steps.indexOfFirst { "Home Shopping Program" in it.label }
        val craftIndex = jun19.steps.indexOfFirst { "finish with 5 lock picks total" in it.label }
        val dvdIndex = jun19.steps.indexOfFirst { "The Running Dead" in it.label }
        assertTrue(palaceIndex == 0, "the forced daytime bank scouting happens before the evening route")
        assertTrue(shoppingIndex > palaceIndex && craftIndex > shoppingIndex && dvdIndex > craftIndex)

        assertTrue(days.getValue("2016-06-25").steps.any { "Secure the Treasure route" in it.label })
        assertTrue(days.getValue("2016-06-27").steps.any { "Calling Card" in it.label })
        assertTrue(days.getValue("2016-06-28").steps.any { "Heist: steal Kaneshiro's Treasure" in it.label })
    }
}
