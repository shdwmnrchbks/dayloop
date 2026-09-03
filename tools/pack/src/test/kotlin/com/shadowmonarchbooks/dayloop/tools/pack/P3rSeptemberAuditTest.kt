package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class P3rSeptemberAuditTest {

    @Test
    fun `P3R September social-stat route matches audited month-end total`() {
        val loaded = loadP3r()
        fun monthDays(value: String) = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == value },
            value,
        ).file.days.associateBy { it.date }

        val monthsThroughAugust = listOf("2009-04", "2009-05", "2009-06", "2009-07", "2009-08")
            .map(::monthDays)
        val september = monthDays("2009-09")

        fun total(days: Map<String, Day>, stat: String): Int = days.values
            .flatMap { it.steps }
            .sumOf { it.statGains[stat] ?: 0 }

        fun gain(date: String, label: String, stat: String): Int {
            val day = assertNotNull(september[date], date)
            val step = assertNotNull(day.steps.firstOrNull { it.label.contains(label, ignoreCase = true) }, "$date: $label")
            return assertNotNull(step.statGains[stat], "$date: $label -> $stat")
        }

        val endAugustAcademics = monthsThroughAugust.sumOf { total(it, "academics") }
        assertEquals(177, endAugustAcademics)

        assertEquals(2, gain("2009-09-08", "Ken's Link Episode", "academics"))
        assertEquals(2, gain("2009-09-12", "Stay awake", "academics"))
        assertEquals(2, gain("2009-09-21", "Shinjiro's Link Episode", "academics"))
        assertEquals(2, gain("2009-09-24", "Stay awake", "academics"))
        assertEquals(2, gain("2009-09-28", "Stay awake", "academics"))
        assertEquals(10, total(september, "academics"))
        assertEquals(187, endAugustAcademics + total(september, "academics"))

        listOf("2009-09-01", "2009-09-10", "2009-09-11", "2009-09-14", "2009-09-26").forEach { date ->
            assertEquals(2, gain(date, "Answer the class question", "charm"), date)
        }
        listOf("2009-09-02", "2009-09-09", "2009-09-30").forEach { date ->
            assertEquals(2, gain(date, "Aigis's reading", "charm"), date)
        }
        assertEquals(2, gain("2009-09-03", "Junpei's reading", "courage"))
        assertEquals(2, gain("2009-09-04", "Shinjiro's Link Episode", "courage"))
        assertEquals(2, gain("2009-09-08", "Koromaru's Link Episode", "courage"))
        assertEquals(4, gain("2009-09-23", "Film Festival with Shinjiro", "courage"))

        listOf("2009-09-01", "2009-09-03", "2009-09-04").forEach { date ->
            val nurse = assertNotNull(september[date], date).steps
                .firstOrNull { it.label.contains("supplementary lesson", ignoreCase = true) }
            assertNotNull(nurse, date)
            assertTrue(nurse.statGains.isEmpty(), "$date supplementary lesson must not invent Social Stat points")
        }
    }

    @Test
    fun `P3R September fixed story and proof-of-bond transitions stay represented`() {
        val loaded = loadP3r()
        val september = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-09" },
            "2009-09",
        ).file.days.associateBy { it.date }

        val september1 = assertNotNull(september["2009-09-01"])
        val devilIndex = september1.steps.indexOfFirst { it.label.contains("Devil reaches rank 10", ignoreCase = true) }
        val request55Index = september1.steps.indexOfFirst {
            it.label.contains("Request #55", ignoreCase = true) &&
                it.label.contains("proof of a bond", ignoreCase = true)
        }
        assertTrue(devilIndex >= 0)
        assertTrue(request55Index > devilIndex)
        assertTrue(september1.steps[request55Index].label.contains("max-rank", ignoreCase = true))
        assertTrue(september1.steps[request55Index].label.contains("complete", ignoreCase = true))

        assertTrue(assertNotNull(september["2009-09-05"]).steps.any { it.label.contains("Full moon", ignoreCase = true) })
        val death = assertNotNull(september["2009-09-12"]).steps
            .firstOrNull { it.label.contains("Death reaches rank 6", ignoreCase = true) }
        assertNotNull(death)

        listOf("2009-09-19", "2009-09-20").forEach { date ->
            val day = assertNotNull(september[date], date)
            assertEquals("story", day.dayKind)
            assertTrue(day.notes.orEmpty().contains("Typhoon", ignoreCase = true))
            assertTrue(day.steps.isEmpty())
        }
    }

    private fun loadP3r() = PackLoader.load(p3rDir()).also { loaded ->
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
    }

    private fun p3rDir(): Path {
        val candidates = listOf(
            Path.of("content", "packs", "p3r"),
            Path.of("..", "..", "content", "packs", "p3r"),
        )
        return candidates.firstOrNull { Files.isDirectory(it) }
            ?: error("Cannot locate content/packs/p3r from ${Path.of("").toAbsolutePath()}")
    }
}
