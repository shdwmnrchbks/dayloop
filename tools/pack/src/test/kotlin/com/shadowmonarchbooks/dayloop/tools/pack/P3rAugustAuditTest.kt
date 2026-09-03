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

class P3rAugustAuditTest {

    @Test
    fun `P3R August social-stat route matches audited raw point thresholds`() {
        val loaded = loadP3r()
        fun monthDays(value: String) = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == value },
            value,
        ).file.days.associateBy { it.date }

        val april = monthDays("2009-04")
        val may = monthDays("2009-05")
        val june = monthDays("2009-06")
        val july = monthDays("2009-07")
        val august = monthDays("2009-08")

        fun total(days: Map<String, Day>, stat: String, through: String? = null): Int = days.values
            .filter { through == null || it.date <= through }
            .flatMap { it.steps }
            .sumOf { it.statGains[stat] ?: 0 }

        fun gain(date: String, label: String, stat: String): Int {
            val day = assertNotNull(august[date], date)
            val step = assertNotNull(day.steps.firstOrNull { it.label.contains(label, ignoreCase = true) }, "$date: $label")
            return assertNotNull(step.statGains[stat], "$date: $label -> $stat")
        }

        val endJulyAcademics = total(april, "academics") + total(may, "academics") +
            total(june, "academics") + total(july, "academics")
        assertEquals(121, endJulyAcademics)

        assertEquals(2, gain("2009-08-01", "Mitsuru's reading", "academics"))
        assertEquals(4, gain("2009-08-02", "Seafood Full Course", "academics"))
        assertEquals(4, gain("2009-08-05", "You're the Answer", "academics"))
        assertEquals(2, gain("2009-08-09", "Junpei's Link Episode", "charm"))

        val summerSchoolDates = (10..15).map { day -> "2009-08-${day.toString().padStart(2, '0')}" }
        summerSchoolDates.forEach { date ->
            assertEquals(3, gain(date, "summer class", "academics"), date)
        }
        assertEquals(18, summerSchoolDates.sumOf { date -> gain(date, "summer class", "academics") })

        val filmStats = mapOf(
            "2009-08-17" to "courage",
            "2009-08-18" to "academics",
            "2009-08-19" to "charm",
            "2009-08-21" to "courage",
            "2009-08-22" to "courage",
            "2009-08-23" to "charm",
            "2009-08-24" to "academics",
            "2009-08-25" to "courage",
            "2009-08-26" to "courage",
            "2009-08-27" to "charm",
            "2009-08-28" to "academics",
            "2009-08-29" to "charm",
            "2009-08-30" to "academics",
            "2009-08-31" to "courage",
        )
        filmStats.forEach { (date, stat) ->
            assertEquals(4, gain(date, "Film Festival", stat), "$date -> $stat")
        }

        assertEquals(2, gain("2009-08-17", "Koromaru's TV", "academics"))
        assertEquals(2, gain("2009-08-19", "Yukari's DVD", "charm"))
        assertEquals(2, gain("2009-08-20", "Fuuka's reading", "academics"))
        assertEquals(2, gain("2009-08-24", "Koromaru's TV", "academics"))
        assertEquals(4, gain("2009-08-26", "You're the Answer", "academics"))
        assertEquals(2, gain("2009-08-31", "Koromaru's TV", "academics"))

        val academicsBeforeSummerSchool = endJulyAcademics + total(august, "academics", "2009-08-09")
        assertEquals(131, academicsBeforeSummerSchool)
        val academicsAfterSummerSchool = endJulyAcademics + total(august, "academics", "2009-08-15")
        assertEquals(149, academicsAfterSummerSchool)
        val academicsBeforeBebe = endJulyAcademics + total(august, "academics", "2009-08-17")
        assertEquals(151, academicsBeforeBebe)
        val academicsOnAugust18 = endJulyAcademics + total(august, "academics", "2009-08-18")
        assertEquals(155, academicsOnAugust18)

        val rankStep = assertNotNull(august["2009-08-18"])
            .steps.firstOrNull { it.label.contains("Academics reaches rank 5", ignoreCase = true) }
        assertNotNull(rankStep)
    }

    @Test
    fun `P3R August fixed story and shrine-request transitions stay represented`() {
        val loaded = loadP3r()
        val august = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-08" },
            "2009-08",
        ).file.days.associateBy { it.date }

        assertTrue(assertNotNull(august["2009-08-06"]).steps.any { it.label.contains("Full moon", ignoreCase = true) })
        val august7 = assertNotNull(august["2009-08-07"])
        assertTrue(august7.steps.any { it.label.contains("Death reaches rank 5", ignoreCase = true) })
        assertTrue(august7.steps.any { it.label.contains("skips rank 4", ignoreCase = true) })

        val request54Steps = listOf("2009-08-08", "2009-08-09", "2009-08-20").map { date ->
            val day = assertNotNull(august[date], date)
            assertNotNull(day.steps.firstOrNull { it.label.contains("Request #54", ignoreCase = true) }, date)
        }
        assertTrue(request54Steps[0].label.contains("accept", ignoreCase = true))
        assertTrue(request54Steps[0].label.contains("1 of 3", ignoreCase = true))
        assertTrue(request54Steps[1].label.contains("2 of 3", ignoreCase = true))
        assertTrue(request54Steps[2].label.contains("3 of 3", ignoreCase = true))
        assertTrue(request54Steps[2].label.contains("500-yen", ignoreCase = true))
        assertTrue(request54Steps[2].label.contains("complete", ignoreCase = true))
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
