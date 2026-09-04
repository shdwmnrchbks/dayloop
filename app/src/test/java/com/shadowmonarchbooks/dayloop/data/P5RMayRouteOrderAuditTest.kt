package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RMayRouteOrderAuditTest {

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
    fun `p5r May route preserves story locks exam state palace order and Temperance gate`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val months = loaded.walkthroughs.associateBy { it.month }
        val april = months.getValue("2016-04").file
        val may = months.getValue("2016-05").file
        val days = may.days.associateBy { it.date }

        listOf("2016-05-02", "2016-05-03", "2016-05-04", "2016-05-05").forEach { date ->
            assertEquals("story", days.getValue(date).dayKind, "$date is story-constrained even though LeBlanc evening actions remain usable")
        }
        listOf("2016-05-11", "2016-05-12", "2016-05-13", "2016-05-14").forEach { date ->
            assertEquals("exam", days.getValue(date).dayKind, "$date is part of the May midterm block")
        }
        assertEquals("story", days.getValue("2016-05-15").dayKind)

        val may10 = days.getValue("2016-05-10")
        assertEquals(
            mapOf("knowledge" to 2),
            may10.steps.single { "Malaise" in it.label }.statGains,
        )
        val ryujiStudy = may10.steps.single { "study with Ryuji" in it.label }
        assertEquals(mapOf("knowledge" to 5), ryujiStudy.statGains)

        val knowledgeThroughMay10 = (april.days + may.days.filter { it.date <= "2016-05-10" })
            .flatMap { it.steps }
            .sumOf { it.statGains["knowledge"] ?: 0 }
        assertEquals(84, knowledgeThroughMay10, "the route crosses Royal's 82-point Knowledge rank-3 threshold during the May 10 Ryuji study")
        assertEquals(79, knowledgeThroughMay10 - (ryujiStudy.statGains["knowledge"] ?: 0))

        val may7Book = days.getValue("2016-05-07").steps.single { "Medjed Menace" in it.label }
        assertTrue("rank 2" !in may7Book.label.lowercase(), "Knowledge rank 2 was already reached in April")

        val may8Shopping = days.getValue("2016-05-08").steps.single { "Bio Nutrients Set" in it.label }
        assertTrue("Buy" in may8Shopping.label)
        assertTrue("Mega Fertilizer" in may8Shopping.label)

        assertEquals(
            mapOf("knowledge" to 2),
            days.getValue("2016-05-18").steps.single { "Gallery" in it.label }.statGains,
        )
        assertEquals(
            mapOf("knowledge" to 2),
            days.getValue("2016-05-31").steps.single { "Japanese" in it.label }.statGains,
        )
        val knowledgeThroughMay31 = (april.days + may.days)
            .flatMap { it.steps }
            .sumOf { it.statGains["knowledge"] ?: 0 }
        assertEquals(110, knowledgeThroughMay31, "the full May no-time crossword chain preserves the state used by June")

        val may15Craft = days.getValue("2016-05-15").steps.single { "craft 3 lock picks" in it.label.lowercase() }
        assertEquals(mapOf("proficiency" to 3), may15Craft.statGains)
        assertTrue("5 lock picks total" in may15Craft.label)

        val may20 = days.getValue("2016-05-20")
        assertEquals(
            mapOf("charm" to 5),
            may20.steps.single { "top 10" in it.label }.statGains,
        )
        assertTrue(may20.steps.any { "Tough Belt" in it.label })
        val palaceCombat = may20.steps.single { "third museum infiltration" in it.label }
        assertTrue("not the final Madarame boss" in palaceCombat.tip.orEmpty())

        assertEquals(
            mapOf("kindness" to 5),
            days.getValue("2016-05-21").steps.single { "Mega Fertilizer" in it.label }.statGains,
        )

        val gutsThroughMay8 = (april.days + may.days.filter { it.date <= "2016-05-08" })
            .flatMap { it.steps }
            .sumOf { it.statGains["guts"] ?: 0 }
        assertEquals(38, gutsThroughMay8, "the route reaches Royal Guts rank 3 before Maidwatch")

        assertTrue(days.getValue("2016-05-16").steps.any { "First infiltration" in it.label })
        assertTrue(days.getValue("2016-05-19").steps.any { "Second museum infiltration" in it.label })
        assertTrue(days.getValue("2016-05-20").steps.any { "third museum infiltration" in it.label })
        assertTrue(days.getValue("2016-05-23").steps.any { "fourth museum infiltration" in it.label && "secure the Treasure route" in it.label })
        assertTrue(days.getValue("2016-05-24").steps.any { "Calling Card" in it.label })
        assertTrue(days.getValue("2016-05-25").steps.any { "Heist: steal Madarame's Treasure" in it.label })

        assertTrue(days.getValue("2016-05-27").steps.any { "Maidwatch" in it.label })
        assertTrue(days.getValue("2016-05-28").steps.any { "Temperance reaches rank 1" in it.label })
        val temperanceRank1 = loaded.bonds?.bonds.orEmpty()
            .single { it.label == "Temperance" }
            .ranks.single { it.rank == 1 }
        assertEquals("2016-05-28", temperanceRank1.scheduledFor)
    }
}
