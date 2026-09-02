package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RThirdSemesterAuditTest {

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
    fun `p5r January and February route uses audited hidden points and active modifiers`() {
        val root = contentPacksDir() ?: return
        val p5r = PackLoader.load(root.resolve("p5r"))
        assertEquals(emptyList(), p5r.parseIssues)

        val days = p5r.walkthroughs.flatMap { it.file.days }
            .filter { it.date.startsWith("2017-01") || it.date.startsWith("2017-02") }

        fun day(date: String) = days.first { it.date == date }
        fun step(date: String, text: String) = day(date).steps.first { it.label.contains(text) }
        fun gain(date: String, text: String): Map<String, Int> = step(date, text).statGains

        assertEquals(mapOf("charm" to 2), gain("2017-01-11", "class question"))
        assertEquals(mapOf("knowledge" to 2), gain("2017-01-14", "class question"))
        assertEquals(mapOf("knowledge" to 2), gain("2017-01-14", "Resolution"))
        assertEquals(mapOf("proficiency" to 7), gain("2017-01-14", "March of the Lambs"))
        assertTrue(step("2017-01-14", "March of the Lambs").label.contains("Craft of Cinema bonus active"))
        assertEquals(mapOf("proficiency" to 2), gain("2017-01-15", "Sunday drink"))
        assertEquals("p5r.activity.drink.fruit-drink", step("2017-01-15", "Sunday drink").activityRef)
        assertEquals(mapOf("proficiency" to 2), gain("2017-01-16", "fish at Ichigaya"))
        assertEquals(mapOf("proficiency" to 3), gain("2017-01-17", "play darts"))
        assertEquals(mapOf("knowledge" to 2), gain("2017-01-18", "class question"))
        assertEquals(mapOf("guts" to 7), gain("2017-01-18", "Bite Club"))
        assertTrue(step("2017-01-18", "Bite Club").label.contains("Craft of Cinema bonus active"))
        assertEquals(mapOf("knowledge" to 2), gain("2017-01-19", "Dionysus"))
        assertEquals(
            mapOf("knowledge" to 3, "guts" to 3, "proficiency" to 3, "charm" to 3),
            gain("2017-01-19", "Big Bang Challenge"),
        )
        assertEquals(mapOf("kindness" to 3), gain("2017-01-20", "Train of Life"))
        assertEquals(mapOf("knowledge" to 2), gain("2017-01-21", "class question"))
        assertEquals(mapOf("kindness" to 3), gain("2017-01-21", "Train of Life"))
        assertEquals(mapOf("guts" to 2), gain("2017-01-22", "Sunday drink"))
        assertEquals(mapOf("knowledge" to 2), gain("2017-01-23", "Lachesis"))
        assertEquals(
            mapOf("knowledge" to 5, "guts" to 5, "proficiency" to 5, "charm" to 5),
            gain("2017-01-23", "Big Bang Challenge"),
        )
        assertEquals(mapOf("knowledge" to 2), gain("2017-01-24", "class question"))
        assertEquals(mapOf("guts" to 3), gain("2017-01-24", "Power Intuition"))
        assertEquals(mapOf("guts" to 3), gain("2017-01-25", "Power Intuition"))
        assertEquals(mapOf("guts" to 3), gain("2017-01-26", "Power Intuition"))
        assertEquals(mapOf("knowledge" to 2), gain("2017-01-27", "class question"))
        assertEquals(mapOf("charm" to 7), gain("2017-01-27", "Finding Beemo"))
        assertTrue(step("2017-01-27", "Finding Beemo").label.contains("Craft of Cinema bonus active"))
        assertEquals(mapOf("knowledge" to 2), gain("2017-01-27", "Orochi"))
        assertEquals(mapOf("proficiency" to 3), gain("2017-01-27", "Golfer Sarutahiko"))
        assertEquals(mapOf("proficiency" to 3), gain("2017-01-28", "Golfer Sarutahiko"))
        assertEquals(mapOf("kindness" to 2), gain("2017-01-29", "Sunday drink"))
        assertEquals(mapOf("proficiency" to 3), gain("2017-01-30", "Golfer Sarutahiko"))
        assertEquals(mapOf("kindness" to 7), gain("2017-02-01", "The Goodfather"))
        assertTrue(step("2017-02-01", "The Goodfather").label.contains("Craft of Cinema bonus active"))

        val answers = p5r.answers?.answers?.associateBy { it.date }.orEmpty()
        assertEquals(listOf("How numerous they are", "The Eight Million Gods"), answers.getValue("2017-01-11").answers)
        assertEquals(listOf("Iwate"), answers.getValue("2017-01-14").answers)
        assertEquals(listOf("Impressive"), answers.getValue("2017-01-18").answers)
        assertEquals(listOf("A snake"), answers.getValue("2017-01-21").answers)
        assertEquals(listOf("Kind-hearted", "Negative", "Resentful"), answers.getValue("2017-01-24").answers)
        assertEquals(listOf("To friends of friends of friends"), answers.getValue("2017-01-27").answers)

        val deadlines = p5r.deadlines?.deadlines?.associateBy { it.id }.orEmpty()
        assertEquals("2017-02-02", deadlines.getValue("p5r.deadline.missable.palace8-route").date)
        assertTrue(deadlines.getValue("p5r.deadline.missable.palace8-route").label.contains("secure the Treasure route"))
        assertEquals("2017-02-03", deadlines.getValue("p5r.deadline.palace8").date)
        assertTrue(deadlines.getValue("p5r.deadline.palace8").label.contains("final confrontation"))
    }
}
