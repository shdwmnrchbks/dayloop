package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class P5RDecemberAuditTest {

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
    fun `p5r December route uses audited hidden points and calendar facts`() {
        val root = contentPacksDir() ?: return
        val p5r = PackLoader.load(root.resolve("p5r"))
        assertEquals(emptyList(), p5r.parseIssues)

        val days = p5r.walkthroughs.flatMap { it.file.days }
            .filter { it.date.startsWith("2016-12") }

        fun day(date: String) = days.first { it.date == date }
        fun step(date: String, text: String) = day(date).steps.first { it.label.contains(text) }
        fun gain(date: String, text: String): Map<String, Int> = step(date, text).statGains

        assertEquals(mapOf("kindness" to 5), gain("2016-12-01", "Mega Fertilizer"))
        assertEquals(mapOf("proficiency" to 3), gain("2016-12-01", "Hanged Man reaches rank 10"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-12-02", "Influenza"))
        assertEquals(mapOf("kindness" to 2), gain("2016-12-03", "Tower reaches rank 10"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-12-04", "Sunday drink"))
        assertEquals(mapOf("proficiency" to 2), gain("2016-12-05", "fish at Ichigaya"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-12-07", "Illuminate"))
        assertEquals(mapOf("kindness" to 7), gain("2016-12-07", "Clean Hard"))
        assertTrue(step("2016-12-07", "Clean Hard").label.contains("Craft of Cinema bonus active"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-12-08", "TV game show"))
        assertTrue(step("2016-12-09", "steal Shido's Heart").label.contains("finish Shido solo"))
        assertFalse(step("2016-12-09", "steal Shido's Heart").label.contains("finish Sido solo"))
        assertEquals(mapOf("charm" to 3), gain("2016-12-10", "Sincere Omelette"))
        assertEquals(mapOf("charm" to 2), gain("2016-12-11", "Sunday drink"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-12-12", "Approval"))
        assertEquals(mapOf("proficiency" to 2), gain("2016-12-13", "fish at Ichigaya"))
        assertEquals(mapOf("charm" to 3), gain("2016-12-14", "Punch Ouch"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-12-15", "TV game show"))
        assertEquals(mapOf("proficiency" to 2), gain("2016-12-16", "fish at Ichigaya"))
        assertEquals(mapOf("kindness" to 5), gain("2016-12-17", "Mega Fertilizer"))
        assertEquals(mapOf("charm" to 3), gain("2016-12-17", "Sincere Omelette"))
        assertEquals(mapOf("charm" to 3), gain("2016-12-18", "Punch Ouch"))
        assertEquals(mapOf("knowledge" to 3), gain("2016-12-19", "Study with the team"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-12-19", "Christmas"))
        assertEquals(mapOf("guts" to 7), gain("2016-12-19", "Merry Christmess"))
        assertTrue(step("2016-12-19", "Merry Christmess").label.contains("Craft of Cinema bonus active"))
        assertEquals(mapOf("charm" to 5), gain("2016-12-22", "special menu"))
        assertTrue(day("2016-12-22").steps.none { it.label.contains("Boss Undies") })
        assertEquals("story", day("2016-12-23").dayKind)
        assertTrue(day("2016-12-23").steps.any { it.label.contains("Boss Undies") })
        assertEquals(mapOf("kindness" to 3), gain("2016-12-23", "Train of Life"))
        assertEquals(mapOf("charm" to 5), gain("2016-12-24", "Exam results"))

        val promise = p5r.deadlines?.deadlines?.single { it.id == "p5r.deadline.missable.keep-the-promise" }
        assertEquals("2016-12-08", promise?.date)
        assertEquals(null, promise?.window)
        assertTrue(promise?.label.orEmpty().contains("Completion-route reminder"))
        assertTrue(promise?.label.orEmpty().contains("after Justice rank 10"))
    }
}
