package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RDecemberRouteOrderAuditTest {

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
    fun `p5r December route preserves hiding school return finals election and year-end chronology`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val december = loaded.walkthroughs.associateBy { it.month }.getValue("2016-12").file
        val days = december.days.associateBy { it.date }

        val hidingFreeDays = listOf(
            "2016-12-01", "2016-12-02", "2016-12-03", "2016-12-04",
            "2016-12-05", "2016-12-06", "2016-12-07", "2016-12-08",
            "2016-12-10", "2016-12-11", "2016-12-12", "2016-12-13",
            "2016-12-14", "2016-12-15", "2016-12-16", "2016-12-17",
        )
        hidingFreeDays.forEach { date ->
            assertEquals("free", days.getValue(date).dayKind, "$date should be a route free-time day while Joker is out of school")
        }
        assertTrue(days.getValue("2016-12-01").steps.first().label.contains("no school until December 19"))

        assertEquals("story", days.getValue("2016-12-09").dayKind)
        val shido = days.getValue("2016-12-09").steps.single { "steal Shido's Heart" in it.label }
        assertTrue("Calling Card" in shido.label)

        assertEquals("story", days.getValue("2016-12-18").dayKind)
        assertTrue(days.getValue("2016-12-18").steps.first().label.contains("Election Day"))
        assertTrue(days.getValue("2016-12-18").steps.first().label.contains("confined to LeBlanc"))
        assertTrue(days.getValue("2016-12-18").steps.any { "Punch Ouch" in it.label })

        assertEquals("school", days.getValue("2016-12-19").dayKind)
        assertTrue(days.getValue("2016-12-19").steps.first().label.contains("returns to school"))
        assertTrue(days.getValue("2016-12-19").steps.first().label.contains("mandatory team study"))
        assertEquals(mapOf("knowledge" to 3), days.getValue("2016-12-19").steps.single { "Study with the team" in it.label }.statGains)

        listOf("2016-12-20", "2016-12-21", "2016-12-22").forEach { date ->
            assertEquals("exam", days.getValue(date).dayKind, "$date is part of the December finals block")
        }
        assertTrue(days.getValue("2016-12-22").steps.first().label.contains("free time resumes after the exam"))

        assertEquals("story", days.getValue("2016-12-23").dayKind)
        assertTrue(days.getValue("2016-12-23").steps.first().label.contains("confined to LeBlanc"))
        assertTrue(days.getValue("2016-12-23").steps.any { "Boss Undies" in it.label })
        assertTrue(days.getValue("2016-12-23").steps.any { "Magician reaches rank 10" in it.label })

        listOf(
            "2016-12-24", "2016-12-25", "2016-12-26", "2016-12-27",
            "2016-12-28", "2016-12-29", "2016-12-30", "2016-12-31",
        ).forEach { date ->
            assertEquals("story", days.getValue(date).dayKind, "$date is part of the year-end/finale story block")
        }
        assertTrue(days.getValue("2016-12-24").steps.any { "depths of Mementos" in it.label })
        assertTrue(days.getValue("2016-12-24").steps.any { "Yaldabaoth" in it.label })

        val aojiru = "p5r.activity.drink.fruit-drink"
        mapOf(
            "2016-12-04" to mapOf("knowledge" to 2),
            "2016-12-11" to mapOf("charm" to 2),
        ).forEach { (date, expectedGain) ->
            val step = days.getValue(date).steps.single { "Sunday drink" in it.label }
            assertEquals(aojiru, step.activityRef, "$date must stay linked to the reusable Aojiru activity")
            assertEquals(expectedGain, step.statGains)
        }

        val secondCruiser = december.days.indexOfFirst { it.date == "2016-12-08" }
        val shidoHeist = december.days.indexOfFirst { it.date == "2016-12-09" }
        val election = december.days.indexOfFirst { it.date == "2016-12-18" }
        val schoolReturn = december.days.indexOfFirst { it.date == "2016-12-19" }
        val finale = december.days.indexOfFirst { it.date == "2016-12-24" }
        assertTrue(secondCruiser < shidoHeist && shidoHeist < election && election < schoolReturn && schoolReturn < finale)
    }
}
