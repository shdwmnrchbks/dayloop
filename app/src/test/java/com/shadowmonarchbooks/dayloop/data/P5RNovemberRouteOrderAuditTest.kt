package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RNovemberRouteOrderAuditTest {

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
    fun `p5r November route preserves holiday school Casino story lock and presumed-dead free time`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val november = loaded.walkthroughs.associateBy { it.month }.getValue("2016-11").file
        val days = november.days.associateBy { it.date }

        assertEquals("free", days.getValue("2016-11-03").dayKind)
        assertTrue(days.getValue("2016-11-03").steps.first().label.contains("Culture Day"))

        listOf("2016-11-05", "2016-11-12").forEach { date ->
            assertEquals("school", days.getValue(date).dayKind, "$date contains a Royal classroom slot")
        }
        assertTrue(days.getValue("2016-11-05").steps.any { "during class" in it.label })
        assertTrue(days.getValue("2016-11-12").steps.any { "class question" in it.label })

        listOf("2016-11-18", "2016-11-19", "2016-11-20", "2016-11-21", "2016-11-22", "2016-11-23", "2016-11-24").forEach { date ->
            assertEquals("story", days.getValue(date).dayKind, "$date is constrained by fixed Royal story progression")
        }

        val nov18 = days.getValue("2016-11-18")
        assertTrue(nov18.steps.first().label.contains("Calling Card"))
        assertTrue(nov18.steps.first().label.contains("confined to LeBlanc"))
        assertTrue(nov18.steps.any { "Send the Calling Card" in it.label })
        assertTrue(nov18.steps.any { "Featherman Seeker" in it.label })

        assertTrue(days.getValue("2016-11-19").steps.any { "Heist: steal Sae's Treasure" in it.label })
        assertTrue(days.getValue("2016-11-20").steps.any { "Interrogation" in it.label })
        assertTrue(days.getValue("2016-11-21").steps.any { "no free time" in it.label })

        listOf("2016-11-22", "2016-11-23", "2016-11-24").forEach { date ->
            assertTrue(
                days.getValue(date).steps.first().label.contains("confined to LeBlanc"),
                "$date should expose the Royal evening confinement",
            )
        }
        assertTrue(days.getValue("2016-11-24").steps.any { "First cruise-ship infiltration" in it.label })
        assertTrue(days.getValue("2016-11-24").steps.any { "Speaker of the House" in it.label })

        listOf("2016-11-25", "2016-11-26", "2016-11-27", "2016-11-28", "2016-11-29", "2016-11-30").forEach { date ->
            assertEquals("free", days.getValue(date).dayKind, "$date resumes route free time while Joker stays out of school")
        }
        listOf("2016-11-25", "2016-11-28", "2016-11-29", "2016-11-30").forEach { date ->
            assertTrue(days.getValue(date).steps.first().label.contains("no school") || days.getValue(date).steps.first().label.contains("cannot attend school"))
        }

        val aojiru = "p5r.activity.drink.fruit-drink"
        mapOf(
            "2016-11-06" to mapOf("proficiency" to 2),
            "2016-11-13" to mapOf("guts" to 2),
            "2016-11-27" to mapOf("kindness" to 2),
        ).forEach { (date, expectedGain) ->
            val step = days.getValue(date).steps.single { "Sunday drink" in it.label }
            assertEquals(aojiru, step.activityRef, "$date must stay linked to the reusable Aojiru activity")
            assertEquals(expectedGain, step.statGains)
        }

        val cardIndex = november.days.indexOfFirst { it.date == "2016-11-18" }
        val heistIndex = november.days.indexOfFirst { it.date == "2016-11-19" }
        val interrogationIndex = november.days.indexOfFirst { it.date == "2016-11-20" }
        val revealIndex = november.days.indexOfFirst { it.date == "2016-11-21" }
        val shidoIndex = november.days.indexOfFirst { it.date == "2016-11-24" }
        val freedomIndex = november.days.indexOfFirst { it.date == "2016-11-25" }
        assertTrue(cardIndex < heistIndex && heistIndex < interrogationIndex && interrogationIndex < revealIndex)
        assertTrue(revealIndex < shidoIndex && shidoIndex < freedomIndex)
    }
}
