package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RJulyRouteOrderAuditTest {

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
    fun `p5r July route preserves school exam story locks home slots and pyramid exception`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val july = loaded.walkthroughs.single { it.month == "2016-07" }.file
        val days = july.days.associateBy { it.date }

        listOf("2016-07-02", "2016-07-09").forEach { date ->
            assertEquals("school", days.getValue(date).dayKind, "$date contains a Royal classroom slot")
        }
        listOf("2016-07-13", "2016-07-14", "2016-07-15").forEach { date ->
            assertEquals("exam", days.getValue(date).dayKind)
        }
        listOf(
            "2016-07-10",
            "2016-07-16",
            "2016-07-17",
            "2016-07-18",
            "2016-07-20",
            "2016-07-21",
            "2016-07-22",
            "2016-07-23",
            "2016-07-24",
            "2016-07-25",
        ).forEach { date ->
            assertEquals("story", days.getValue(date).dayKind, "$date is constrained by fixed Royal story progression")
        }
        assertEquals("school", days.getValue("2016-07-19").dayKind)

        assertTrue(days.getValue("2016-07-10").steps.first().label.contains("confined to LeBlanc"))
        assertTrue(days.getValue("2016-07-17").steps.first().label.contains("Summer festival"))
        assertTrue(days.getValue("2016-07-17").steps.first().label.contains("evening free time resumes"))
        assertTrue(days.getValue("2016-07-18").steps.first().label.contains("Fireworks Festival"))
        assertTrue(days.getValue("2016-07-18").steps.first().label.contains("confined to LeBlanc"))

        val jul19 = days.getValue("2016-07-19")
        assertEquals(mapOf("charm" to 5), jul19.steps.single { it.label == "Exam results" }.statGains)
        assertTrue(jul19.steps.any { "Dandy Mirror" in it.label })
        assertEquals(mapOf("knowledge" to 2), jul19.steps.single { "Finals" in it.label }.statGains)

        val jul20 = days.getValue("2016-07-20")
        assertTrue(jul20.steps.first().label.contains("First contact with Alibaba"))
        val lockpicks = jul20.steps.single { "10 total" in it.label }
        assertEquals(mapOf("proficiency" to 5), lockpicks.statGains)
        assertTrue(jul20.steps.any { "Proficiency reaches rank 4" in it.label })

        val jul22 = days.getValue("2016-07-22")
        assertTrue(jul22.steps.first().label.contains("supermarket"))
        val flowerpedia = jul22.steps.single { it.activityRef == "p5r.activity.book.flowerpedia" }
        assertTrue("at LeBlanc" in flowerpedia.label)
        assertTrue("at the supermarket" !in flowerpedia.label)

        assertTrue(days.getValue("2016-07-25").steps.any { "First infiltration of the pyramid" in it.label })
        assertTrue(days.getValue("2016-07-26").steps.any { "Second infiltration" in it.label })
        val jul27 = days.getValue("2016-07-27")
        val callingCard = jul27.steps.indexOfFirst { "Calling Card" in it.label }
        val heist = jul27.steps.indexOfFirst { "Heist: steal the Treasure" in it.label }
        assertTrue(callingCard >= 0 && heist > callingCard, "Futaba's Palace sends the card and steals the Treasure on the same day")
    }
}
