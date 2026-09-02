package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RJobRequestGateAuditTest {

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
    fun `p5r job linked requests have enough authored shifts before the August Mementos clear`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val allDays = loaded.walkthroughs.flatMap { it.file.days }
        val days = allDays.associateBy { it.date }

        fun workDates(marker: String): List<String> = allDays
            .filter { day -> day.steps.any { marker in it.label.lowercase() } }
            .map { it.date }

        val flowerDates = workDates("work at the flower shop")
        assertTrue(flowerDates.size >= 3)
        assertEquals(listOf("2016-07-19", "2016-08-05", "2016-08-10"), flowerDates.take(3))
        assertEquals(mapOf("kindness" to 4), days.getValue("2016-07-19").steps.single { "Work at the flower shop" in it.label }.statGains)
        assertEquals(mapOf("kindness" to 7), days.getValue("2016-08-05").steps.single { "work at the flower shop" in it.label.lowercase() }.statGains)
        assertEquals(mapOf("kindness" to 7), days.getValue("2016-08-10").steps.single { "work at the flower shop" in it.label.lowercase() }.statGains)

        val convenienceDates = workDates("work at the central street convenience store") +
            workDates("work at the convenience store")
        val orderedConvenienceDates = convenienceDates.distinct().sorted()
        assertTrue(orderedConvenienceDates.size >= 3)
        assertEquals(listOf("2016-07-31", "2016-08-07", "2016-08-12"), orderedConvenienceDates.take(3))
        assertEquals(mapOf("charm" to 4), days.getValue("2016-07-31").steps.single { "Central Street convenience store" in it.label }.statGains)
        assertEquals(mapOf("charm" to 7), days.getValue("2016-08-07").steps.single { "work at the convenience store" in it.label.lowercase() }.statGains)
        assertEquals(mapOf("charm" to 4), days.getValue("2016-08-12").steps.single { "work at the convenience store" in it.label.lowercase() }.statGains)

        val crossroadsDates = workDates("work at the crossroads bar")
        assertTrue(crossroadsDates.size >= 2)
        assertEquals(listOf("2016-08-07", "2016-08-08"), crossroadsDates.take(2))
        assertEquals(mapOf("charm" to 4, "kindness" to 2), days.getValue("2016-08-07").steps.single { "Crossroads Bar" in it.label }.statGains)
        assertEquals(mapOf("charm" to 3, "kindness" to 3), days.getValue("2016-08-08").steps.single { "Crossroads Bar" in it.label }.statGains)

        assertTrue(days.getValue("2016-08-02").steps.any { "We Aren't Just Your Slaves" in it.label })
        assertTrue(days.getValue("2016-08-03").steps.any { "Who's Been Assaulting People?" in it.label })
        assertTrue(days.getValue("2016-08-04").steps.any { "Calling for Justice for Cats" in it.label })
        assertTrue(days.getValue("2016-08-16").steps.any { "clear all 5 requests" in it.label.lowercase() })

        assertTrue(flowerDates.take(3).last() < "2016-08-16")
        assertTrue(orderedConvenienceDates.take(3).last() < "2016-08-16")
        assertTrue(crossroadsDates.take(2).last() < "2016-08-16")
    }
}
