package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RAprilRouteOrderAuditTest {

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
    fun `p5r April route preserves story locks prerequisites stat thresholds and palace order`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val april = loaded.walkthroughs.single { it.month == "2016-04" }.file
        val days = april.days.associateBy { it.date }

        listOf("2016-04-13", "2016-04-14", "2016-04-15", "2016-04-16", "2016-04-17").forEach { date ->
            assertEquals("story", days.getValue(date).dayKind, "$date is still in Royal's mandatory opening sequence")
        }

        val apr18 = days.getValue("2016-04-18")
        val semesters = apr18.steps.single { "Semesters" in it.label }
        assertEquals(mapOf("knowledge" to 2), semesters.statGains)
        assertTrue(apr18.steps.any { "Clean the attic floor" in it.label })

        val apr19 = days.getValue("2016-04-19")
        val nutrientPurchase = apr19.steps.indexOfFirst { "Buy a Bio Nutrient" in it.label }
        val nutrientUse = apr19.steps.indexOfFirst { "Use the Bio Nutrient" in it.label }
        assertTrue(nutrientPurchase >= 0 && nutrientUse > nutrientPurchase, "the route must source Bio Nutrient before feeding the plant")
        assertEquals(mapOf("kindness" to 3), apr19.steps[nutrientUse].statGains)

        val apr20 = days.getValue("2016-04-20")
        assertEquals(
            mapOf("knowledge" to 5),
            apr20.steps.single { "library (rainy-day bonus)" in it.label }.statGains,
        )
        assertEquals(
            mapOf("knowledge" to 5),
            apr20.steps.single { "LeBlanc in the evening" in it.label }.statGains,
        )

        val blossom = days.getValue("2016-04-27").steps.single { "Blossom" in it.label }
        val knowledgeThroughApr27 = april.days
            .filter { it.date <= "2016-04-27" }
            .flatMap { it.steps }
            .sumOf { it.statGains["knowledge"] ?: 0 }
        assertEquals(36, knowledgeThroughApr27, "the Apr 27 crossword is included in the end-of-day total")
        assertEquals(
            34,
            knowledgeThroughApr27 - (blossom.statGains["knowledge"] ?: 0),
            "the Apr 27 class answer reaches Royal Knowledge rank 2 before the Blossom crossword",
        )

        val gutsThroughApr22 = april.days
            .filter { it.date <= "2016-04-22" }
            .flatMap { it.steps }
            .sumOf { it.statGains["guts"] ?: 0 }
        assertTrue(gutsThroughApr22 >= 11, "Death rank 2 on Apr 23 requires Guts rank 2")

        assertTrue(days.getValue("2016-04-24").steps.any { "secure the route to the Treasure" in it.label })
        assertTrue(days.getValue("2016-04-25").steps.any { "Calling Card" in it.label })
        assertTrue(days.getValue("2016-04-26").steps.any { "Heist: steal the Treasure" in it.label })
    }
}
