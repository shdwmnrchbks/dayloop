package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5ROctoberRouteOrderAuditTest {

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
    fun `p5r October route preserves school holiday exams story confinement and Palace order`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val october = loaded.walkthroughs.associateBy { it.month }.getValue("2016-10").file
        val days = october.days.associateBy { it.date }

        listOf("2016-10-01", "2016-10-04", "2016-10-08", "2016-10-15", "2016-10-24", "2016-10-31").forEach { date ->
            assertEquals("school", days.getValue(date).dayKind, "$date is a Royal school day")
        }

        assertEquals("free", days.getValue("2016-10-10").dayKind)
        assertTrue(days.getValue("2016-10-10").steps.first().label.contains("Health and Sports Day"))

        listOf("2016-10-11", "2016-10-12", "2016-10-13", "2016-10-21", "2016-10-22", "2016-10-23", "2016-10-25", "2016-10-26", "2016-10-27", "2016-10-28", "2016-10-29").forEach { date ->
            assertEquals("story", days.getValue(date).dayKind, "$date is constrained by fixed Royal story progression")
        }

        listOf("2016-10-17", "2016-10-18", "2016-10-19", "2016-10-20").forEach { date ->
            assertEquals("exam", days.getValue(date).dayKind, "$date is part of the October midterm block")
        }
        assertTrue(days.getValue("2016-10-20").steps.first().label.contains("Final midterm exam day"))
        assertTrue(days.getValue("2016-10-20").steps.first().label.contains("confined to LeBlanc"))

        assertTrue(days.getValue("2016-10-11").steps.any { "Destinyland" in it.label && "no free time" in it.label })
        assertTrue(days.getValue("2016-10-12").steps.first().label.contains("confined to LeBlanc"))
        assertTrue(days.getValue("2016-10-13").steps.first().label.contains("confined to LeBlanc"))
        assertTrue(days.getValue("2016-10-21").steps.first().label.contains("Police questioning"))
        assertTrue(days.getValue("2016-10-21").steps.first().label.contains("confined to LeBlanc"))
        assertTrue(days.getValue("2016-10-22").steps.any { "Festival-committee work" in it.label && "evening" in it.label })
        assertTrue(days.getValue("2016-10-22").steps.any { "Duhvengers" in it.label && "evening" in it.label })
        assertTrue(days.getValue("2016-10-25").steps.first().label.contains("School-festival"))
        assertTrue(days.getValue("2016-10-27").steps.first().label.contains("confined to LeBlanc"))
        assertTrue(days.getValue("2016-10-29").steps.any { "First infiltration of the casino" in it.label })
        assertTrue(days.getValue("2016-10-29").steps.any { "confined to LeBlanc" in it.label })
        assertEquals("free", days.getValue("2016-10-30").dayKind)

        val aojiru = "p5r.activity.drink.fruit-drink"
        mapOf(
            "2016-10-02" to mapOf("proficiency" to 2),
            "2016-10-09" to mapOf("guts" to 2),
            "2016-10-16" to mapOf("kindness" to 2),
            "2016-10-23" to mapOf("knowledge" to 2),
            "2016-10-30" to mapOf("charm" to 2),
        ).forEach { (date, expectedGain) ->
            val step = days.getValue(date).steps.single { "Sunday drink" in it.label }
            assertEquals(aojiru, step.activityRef, "$date must stay linked to the reusable Aojiru activity")
            assertEquals(expectedGain, step.statGains)
        }

        assertTrue(days.getValue("2016-10-04").steps.any { "second spaceport infiltration" in it.label })
        assertTrue(days.getValue("2016-10-05").steps.any { "Calling Card" in it.label })
        assertTrue(days.getValue("2016-10-06").steps.any { "Heist: steal Okumura's Treasure" in it.label })
        assertTrue(days.getValue("2016-10-29").steps.any { "First infiltration of the casino" in it.label })
        assertTrue(days.getValue("2016-10-31").steps.any { "Empress reaches rank 1" in it.label })
    }
}
