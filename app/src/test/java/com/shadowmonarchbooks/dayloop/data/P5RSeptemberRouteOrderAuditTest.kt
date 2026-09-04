package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RSeptemberRouteOrderAuditTest {

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
    fun `p5r September route preserves Hawaii story holidays school slots and Spaceport order`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val months = loaded.walkthroughs.associateBy { it.month }
        val september = months.getValue("2016-09").file
        val october = months.getValue("2016-10").file
        val days = september.days.associateBy { it.date }
        val octDays = october.days.associateBy { it.date }

        listOf("2016-09-03", "2016-09-17", "2016-09-24").forEach { date ->
            assertEquals("school", days.getValue(date).dayKind, "$date contains a Royal classroom slot")
        }
        listOf("2016-09-07", "2016-09-08", "2016-09-09", "2016-09-10", "2016-09-11").forEach { date ->
            assertEquals("story", days.getValue(date).dayKind, "$date is part of the Hawaii school-trip block")
        }
        listOf("2016-09-12", "2016-09-13", "2016-09-15", "2016-09-16", "2016-09-18").forEach { date ->
            assertEquals("story", days.getValue(date).dayKind, "$date is constrained by fixed Royal story progression")
        }

        assertEquals("school", days.getValue("2016-09-14").dayKind)
        assertTrue(days.getValue("2016-09-14").steps.any { "Morgana/Haru search story" in it.label })

        val sep17 = days.getValue("2016-09-17")
        assertEquals(mapOf("knowledge" to 2), sep17.steps.single { "class question" in it.label }.statGains)
        assertTrue(sep17.steps.any { "Mementos pursuit/reunion" in it.label })
        assertTrue(sep17.steps.any { "Magician reaches rank 8" in it.label })

        val sep18 = days.getValue("2016-09-18")
        assertTrue(sep18.steps.first().label.contains("Haru/Okumura story"))
        assertTrue(sep18.steps.first().label.contains("confined to LeBlanc"))
        assertEquals("p5r.activity.dvd.mouse-md", sep18.steps.single { "Mouse M.D." in it.label }.activityRef)

        assertEquals("free", days.getValue("2016-09-19").dayKind)
        assertTrue(days.getValue("2016-09-19").steps.first().label.contains("Respect for the Aged Day"))
        assertEquals("free", days.getValue("2016-09-22").dayKind)
        assertTrue(days.getValue("2016-09-22").steps.first().label.contains("Autumnal Equinox Day"))

        val aojiru = "p5r.activity.drink.fruit-drink"
        mapOf(
            "2016-09-04" to mapOf("knowledge" to 2),
            "2016-09-25" to mapOf("charm" to 2),
        ).forEach { (date, expectedGain) ->
            val step = days.getValue(date).steps.single { "Sunday drink" in it.label }
            assertEquals(aojiru, step.activityRef, "$date must stay linked to the reusable Aojiru activity")
            assertEquals(expectedGain, step.statGains)
        }

        assertTrue(days.getValue("2016-09-15").steps.any { "First infiltration of the spaceport" in it.label })
        assertTrue(octDays.getValue("2016-10-04").steps.any { "second spaceport infiltration" in it.label })
        assertTrue(octDays.getValue("2016-10-05").steps.any { "Calling Card" in it.label })
        assertTrue(octDays.getValue("2016-10-06").steps.any { "Heist: steal Okumura's Treasure" in it.label })
    }
}
