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

class P3rJulyAuditTest {

    @Test
    fun `P3R July social-stat route matches audited raw point thresholds`() {
        val loaded = loadP3r()
        fun monthDays(value: String) = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == value },
            value,
        ).file.days.associateBy { it.date }

        val april = monthDays("2009-04")
        val may = monthDays("2009-05")
        val june = monthDays("2009-06")
        val july = monthDays("2009-07")

        fun total(days: Map<String, Day>, stat: String, through: String? = null): Int = days.values
            .filter { through == null || it.date <= through }
            .flatMap { it.steps }
            .sumOf { it.statGains[stat] ?: 0 }

        fun gain(date: String, label: String, stat: String): Int {
            val day = assertNotNull(july[date], date)
            val step = assertNotNull(day.steps.firstOrNull { it.label.contains(label, ignoreCase = true) }, "$date: $label")
            return assertNotNull(step.statGains[stat], "$date: $label -> $stat")
        }

        val endJuneAcademics = total(april, "academics") + total(may, "academics") + total(june, "academics")
        val endJuneCharm = total(april, "charm") + total(may, "charm") + total(june, "charm")
        val endJuneCourage = total(april, "courage") + total(may, "courage") + total(june, "courage")
        assertEquals(74, endJuneAcademics)
        assertEquals(84, endJuneCharm)
        assertEquals(85, endJuneCourage)

        assertEquals(4, gain("2009-07-02", "Seafood Full Course", "academics"))
        assertEquals(2, gain("2009-07-05", "Mitsuru's reading", "academics"))
        assertEquals(1, gain("2009-07-06", "part-time at Chagall", "charm"))
        assertEquals(1, gain("2009-07-06", "part-time at Chagall", "courage"))
        assertEquals(4, gain("2009-07-09", "study with Mitsuru and Akihiko", "academics"))
        assertEquals(4, gain("2009-07-10", "study with Yukari and Fuuka", "academics"))
        assertEquals(4, gain("2009-07-12", "Seafood Full Course", "academics"))
        assertEquals(5, gain("2009-07-13", "study with everyone", "academics"))
        assertEquals(4, gain("2009-07-24", "Exam results", "charm"))
        assertEquals(2, gain("2009-07-24", "Akihiko's Link Episode", "charm"))
        assertEquals(2, gain("2009-07-26", "Mitsuru's reading", "academics"))
        assertEquals(4, gain("2009-07-29", "You're the Answer", "academics"))
        assertEquals(2, gain("2009-07-30", "Fuuka's reading", "academics"))

        val academicsByJuly13 = endJuneAcademics + total(july, "academics", "2009-07-13")
        assertEquals(103, academicsByJuly13)
        assertTrue(academicsByJuly13 >= 100)
        val academicsRankStep = assertNotNull(july["2009-07-13"])
            .steps.firstOrNull { it.label.contains("Academics reaches rank 4", ignoreCase = true) }
        assertNotNull(academicsRankStep)

        val charmBeforeJuly24Results = endJuneCharm + july.values
            .filter { it.date < "2009-07-24" }
            .flatMap { it.steps }
            .sumOf { it.statGains["charm"] ?: 0 }
        assertEquals(96, charmBeforeJuly24Results)
        assertEquals(100, charmBeforeJuly24Results + gain("2009-07-24", "Exam results", "charm"))
        val charmRankStep = assertNotNull(july["2009-07-24"])
            .steps.firstOrNull { it.label.contains("Charm reaches rank 6", ignoreCase = true) }
        assertNotNull(charmRankStep)
    }

    @Test
    fun `P3R July fixed story ranks facilities and route milestones stay represented`() {
        val loaded = loadP3r()
        val july = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-07" },
            "2009-07",
        ).file.days.associateBy { it.date }

        val july7 = assertNotNull(july["2009-07-07"])
        assertTrue(july7.steps.any { it.label.contains("Full moon", ignoreCase = true) })
        assertTrue(july7.steps.any { it.label.contains("Fool reaches rank 4", ignoreCase = true) })

        val july12 = assertNotNull(july["2009-07-12"])
        assertTrue(july12.steps.any { it.label.contains("Death reaches rank 3", ignoreCase = true) })
        assertTrue(july12.steps.any { it.label.contains("skips rank 2", ignoreCase = true) })

        assertTrue(assertNotNull(july["2009-07-18"]).steps.any { it.label.contains("Mayoido Antiques unlocks", ignoreCase = true) })

        listOf("2009-07-20", "2009-07-21", "2009-07-22").forEach { date ->
            val day = assertNotNull(july[date], date)
            assertEquals("story", day.dayKind)
            assertTrue(day.steps.any { it.label.contains("Yakushima", ignoreCase = true) })
        }
        assertTrue(assertNotNull(july["2009-07-22"]).steps.any { it.label.contains("Fool reaches rank 5", ignoreCase = true) })

        val july25 = assertNotNull(july["2009-07-25"])
        assertTrue(july25.steps.any { it.label.contains("Lovers reaches rank 1", ignoreCase = true) })
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
