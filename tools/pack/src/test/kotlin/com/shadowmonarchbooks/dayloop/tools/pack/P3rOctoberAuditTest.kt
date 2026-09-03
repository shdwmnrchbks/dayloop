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

class P3rOctoberAuditTest {

    @Test
    fun `P3R October Academics route reaches Genius at the audited 230-point threshold`() {
        val loaded = loadP3r()
        fun monthDays(value: String) = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == value },
            value,
        ).file.days.associateBy { it.date }

        val monthsThroughSeptember = listOf("2009-04", "2009-05", "2009-06", "2009-07", "2009-08", "2009-09")
            .map(::monthDays)
        val october = monthDays("2009-10")

        fun total(days: Map<String, Day>, stat: String, through: String? = null): Int = days.values
            .filter { through == null || it.date <= through }
            .flatMap { it.steps }
            .sumOf { it.statGains[stat] ?: 0 }

        fun gain(date: String, label: String, stat: String): Int {
            val day = assertNotNull(october[date], date)
            val step = assertNotNull(day.steps.firstOrNull { it.label.contains(label, ignoreCase = true) }, "$date: $label")
            return assertNotNull(step.statGains[stat], "$date: $label -> $stat")
        }

        val endSeptemberAcademics = monthsThroughSeptember.sumOf { total(it, "academics") }
        assertEquals(187, endSeptemberAcademics)

        listOf("2009-10-01", "2009-10-03", "2009-10-09", "2009-10-23", "2009-10-29").forEach { date ->
            assertEquals(2, gain(date, "Stay awake", "academics"), date)
        }

        val facultyDates = listOf(
            "2009-10-06", "2009-10-07", "2009-10-08", "2009-10-09",
            "2009-10-10", "2009-10-17", "2009-10-19", "2009-10-20",
        )
        facultyDates.forEachIndexed { index, date ->
            assertEquals(2, gain(date, "Request #75", "academics"), date)
            val step = assertNotNull(october[date]).steps.first { it.label.contains("Request #75", ignoreCase = true) }
            assertTrue(step.label.contains("visit ${index + 1} of 8", ignoreCase = true), "$date: ${step.label}")
        }

        assertEquals(4, gain("2009-10-08", "study with Mitsuru and Akihiko", "academics"))
        assertEquals(4, gain("2009-10-09", "study with Yukari, Junpei, and Fuuka", "academics"))
        assertEquals(4, gain("2009-10-11", "study with Aigis, Ken, and Koromaru", "academics"))
        assertEquals(5, gain("2009-10-12", "study with everyone", "academics"))

        assertEquals(43, total(october, "academics"))
        val academicsByOctober29 = endSeptemberAcademics + total(october, "academics", "2009-10-29")
        assertEquals(230, academicsByOctober29)
        val rankStep = assertNotNull(october["2009-10-29"])
            .steps.firstOrNull { it.label.contains("Academics reaches rank 6", ignoreCase = true) }
        assertNotNull(rankStep)
    }

    @Test
    fun `P3R October deterministic Charm Courage and request gains stay structured`() {
        val loaded = loadP3r()
        val october = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-10" },
            "2009-10",
        ).file.days.associateBy { it.date }

        fun gain(date: String, label: String, stat: String): Int {
            val day = assertNotNull(october[date], date)
            val step = assertNotNull(day.steps.firstOrNull { it.label.contains(label, ignoreCase = true) }, "$date: $label")
            return assertNotNull(step.statGains[stat], "$date: $label -> $stat")
        }

        listOf("2009-10-07", "2009-10-10", "2009-10-19", "2009-10-22", "2009-10-26", "2009-10-30").forEach { date ->
            assertEquals(2, gain(date, "Answer the class question", "charm"), date)
        }
        assertEquals(4, gain("2009-10-19", "Exam results", "charm"))
        assertEquals(2, gain("2009-10-19", "Akihiko's Link Episode", "charm"))

        listOf("2009-10-07", "2009-10-21", "2009-10-26", "2009-10-28").forEach { date ->
            assertEquals(1, gain(date, "part-time at Chagall", "charm"), date)
            assertEquals(1, gain(date, "part-time at Chagall", "courage"), date)
        }
        assertEquals(2, gain("2009-10-24", "Akihiko's DVD", "courage"))
        assertEquals(2, gain("2009-10-30", "Ken's DVD", "courage"))
    }

    @Test
    fun `P3R October fixed story and Elizabeth prerequisite chains stay represented`() {
        val loaded = loadP3r()
        val october = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-10" },
            "2009-10",
        ).file.days.associateBy { it.date }

        val october1 = assertNotNull(october["2009-10-01"])
        val darkZone = assertNotNull(october1.steps.firstOrNull { it.label.contains("Dark Zone", ignoreCase = true) })
        assertTrue(darkZone.label.contains("tutorial", ignoreCase = true))
        assertTrue(darkZone.label.contains("does not count", ignoreCase = true))
        assertTrue(darkZone.label.contains("random Dark Zone", ignoreCase = true))

        val october6 = assertNotNull(october["2009-10-06"])
        val acceptIndex = october6.steps.indexOfFirst {
            it.label.contains("accept Requests #74", ignoreCase = true) &&
                it.label.contains("#75", ignoreCase = true) &&
                it.label.contains("#76", ignoreCase = true)
        }
        val sushiIndex = october6.steps.indexOfFirst {
            it.label.contains("Request #74", ignoreCase = true) && it.label.contains("Inari Sushi", ignoreCase = true)
        }
        val helmStartIndex = october6.steps.indexOfFirst {
            it.label.contains("Request #75", ignoreCase = true) && it.label.contains("visit 1 of 8", ignoreCase = true)
        }
        assertTrue(acceptIndex >= 0)
        assertTrue(sushiIndex > acceptIndex)
        assertTrue(helmStartIndex > acceptIndex)
        assertTrue(october6.steps.any { it.label.contains("Death reaches rank 8", ignoreCase = true) })
        assertTrue(october6.steps.any { it.label.contains("skips rank 7", ignoreCase = true) })

        val finalHelm = assertNotNull(october["2009-10-20"])
            .steps.firstOrNull { it.label.contains("Request #75", ignoreCase = true) }
        assertNotNull(finalHelm)
        assertTrue(finalHelm.label.contains("visit 8 of 8", ignoreCase = true))
        assertTrue(finalHelm.label.contains("Kanetsugu", ignoreCase = true))
        assertTrue(finalHelm.label.contains("report", ignoreCase = true))
        assertTrue(finalHelm.label.contains("complete", ignoreCase = true))

        assertTrue(assertNotNull(october["2009-10-04"]).steps.any { it.label.contains("Full moon", ignoreCase = true) })
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
