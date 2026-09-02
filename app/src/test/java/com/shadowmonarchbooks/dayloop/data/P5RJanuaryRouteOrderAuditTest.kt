package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RJanuaryRouteOrderAuditTest {

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
    fun `p5r January route preserves third-semester story lock school days and cleanup order`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val january = loaded.walkthroughs.associateBy { it.month }.getValue("2017-01").file
        val days = january.days.associateBy { it.date }

        listOf(
            "2017-01-01", "2017-01-02", "2017-01-03", "2017-01-04", "2017-01-05",
            "2017-01-06", "2017-01-07", "2017-01-08", "2017-01-09", "2017-01-10",
        ).forEach { date ->
            assertEquals("story", days.getValue(date).dayKind, "$date belongs to the fixed third-semester opening story sequence")
        }
        assertTrue(days.getValue("2017-01-02").steps.any { "First laboratory infiltration" in it.label })
        assertTrue(days.getValue("2017-01-09").steps.any { "Second laboratory infiltration" in it.label })
        assertTrue(days.getValue("2017-01-10").steps.any { "Morgana's awakening" in it.label })

        assertEquals("school", days.getValue("2017-01-11").dayKind)
        assertEquals("story", days.getValue("2017-01-12").dayKind)
        assertTrue(days.getValue("2017-01-12").steps.any { "Third laboratory infiltration" in it.label })

        listOf(
            "2017-01-13", "2017-01-14", "2017-01-16", "2017-01-17", "2017-01-18",
            "2017-01-19", "2017-01-20", "2017-01-21", "2017-01-23", "2017-01-24",
            "2017-01-25", "2017-01-26", "2017-01-27", "2017-01-28", "2017-01-30", "2017-01-31",
        ).forEach { date ->
            assertEquals("school", days.getValue(date).dayKind, "$date is a Royal school day with route freedom after school")
        }

        listOf("2017-01-14", "2017-01-21").forEach { date ->
            assertTrue(days.getValue(date).steps.any { "class question" in it.label }, "$date must retain its Saturday classroom question")
        }
        assertTrue(days.getValue("2017-01-25").steps.any { "during class" in it.label && "Chinese Sweets" in it.label })

        val jan26 = days.getValue("2017-01-26")
        assertTrue(jan26.steps.first().label.startsWith("After school:"))
        assertTrue(jan26.steps.first().label.contains("final laboratory infiltration"))
        assertTrue(jan26.steps.any { "Reach the Treasure" in it.label })

        val jan28 = days.getValue("2017-01-28")
        assertTrue(jan28.steps.any { it.label.startsWith("After school:") && "final Mementos visit" in it.label })
        assertTrue(jan28.steps.any { "Optional: defeat Jose" in it.label })

        assertTrue(days.getValue("2017-01-31").steps.any { "After-school/evening cleanup" in it.label })

        val aojiru = "p5r.activity.drink.fruit-drink"
        mapOf(
            "2017-01-15" to mapOf("proficiency" to 2),
            "2017-01-22" to mapOf("guts" to 2),
            "2017-01-29" to mapOf("kindness" to 2),
        ).forEach { (date, expectedGain) ->
            assertEquals("free", days.getValue(date).dayKind)
            val step = days.getValue(date).steps.single { "Sunday drink" in it.label }
            assertEquals(aojiru, step.activityRef)
            assertEquals(expectedGain, step.statGains)
        }

        val forcedOne = january.days.indexOfFirst { it.date == "2017-01-02" }
        val forcedTwo = january.days.indexOfFirst { it.date == "2017-01-09" }
        val forcedThree = january.days.indexOfFirst { it.date == "2017-01-12" }
        val route = january.days.indexOfFirst { it.date == "2017-01-26" }
        val mementos = january.days.indexOfFirst { it.date == "2017-01-28" }
        assertTrue(forcedOne < forcedTwo && forcedTwo < forcedThree && forcedThree < route && route < mementos)
    }
}
