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

class P3rMayAuditTest {

    @Test
    fun `P3R May social-stat route matches audited raw point thresholds`() {
        val loaded = loadP3r()
        fun monthDays(value: String) = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == value },
            value,
        ).file.days.associateBy { it.date }

        val april = monthDays("2009-04")
        val may = monthDays("2009-05")

        fun gain(days: Map<String, Day>, date: String, label: String, stat: String): Int {
            val day = assertNotNull(days[date], date)
            val step = assertNotNull(day.steps.firstOrNull { it.label.contains(label, ignoreCase = true) }, "$date: $label")
            return assertNotNull(step.statGains[stat], "$date: $label -> $stat")
        }

        val aprilAcademics = april.values.flatMap { it.steps }.sumOf { it.statGains["academics"] ?: 0 }
        val aprilCharm = april.values.flatMap { it.steps }.sumOf { it.statGains["charm"] ?: 0 }
        val aprilCourage = april.values.flatMap { it.steps }.sumOf { it.statGains["courage"] ?: 0 }
        assertEquals(18, aprilAcademics)
        assertEquals(18, aprilCharm)
        assertEquals(19, aprilCourage)

        assertEquals(4, gain(may, "2009-05-01", "Digital Cram School", "academics"))
        assertEquals(4, gain(may, "2009-05-02", "Lessons in Etiquette", "charm"))
        assertEquals(4, gain(may, "2009-05-03", "Virtual Diet", "charm"))
        assertEquals(4, gain(may, "2009-05-04", "Language Made Easy", "academics"))
        assertEquals(4, gain(may, "2009-05-05", "Animal Othello", "courage"))

        val bigEater = assertNotNull(may["2009-05-10"])
            .steps.firstOrNull { it.label.contains("Big Eater Challenge", ignoreCase = true) }
        assertNotNull(bigEater)
        assertEquals(mapOf("academics" to 4, "charm" to 4, "courage" to 4), bigEater.statGains)

        val academicsByMay1 = aprilAcademics + gain(may, "2009-05-01", "Digital Cram School", "academics")
        assertEquals(22, academicsByMay1)

        val charmBeforeBigEater = aprilCharm +
            gain(may, "2009-05-02", "Lessons in Etiquette", "charm") +
            gain(may, "2009-05-03", "Virtual Diet", "charm") +
            gain(may, "2009-05-06", "Answer the class question", "charm")
        val courageBeforeBigEater = aprilCourage +
            gain(may, "2009-05-05", "Animal Othello", "courage") +
            gain(may, "2009-05-08", "House of the Deceased", "courage")
        assertEquals(28, charmBeforeBigEater)
        assertEquals(27, courageBeforeBigEater)
        assertEquals(32, charmBeforeBigEater + bigEater.statGains.getValue("charm"))
        assertEquals(31, courageBeforeBigEater + bigEater.statGains.getValue("courage"))

        val academicsByMay17 = aprilAcademics + may.values
            .filter { it.date <= "2009-05-17" }
            .flatMap { it.steps }
            .sumOf { it.statGains["academics"] ?: 0 }
        assertEquals(55, academicsByMay17)
        assertEquals(5, gain(may, "2009-05-17", "study with everyone", "academics"))

        val courageByMay25 = aprilCourage + may.values
            .filter { it.date <= "2009-05-25" }
            .flatMap { it.steps }
            .sumOf { it.statGains["courage"] ?: 0 }
        assertEquals(45, courageByMay25)

        assertEquals(4, gain(may, "2009-05-25", "Exam results", "charm"))
        assertEquals(2, gain(may, "2009-05-25", "Stay awake", "academics"))
    }

    @Test
    fun `P3R May 10 request chain tells the user how to finish the drink request`() {
        val loaded = loadP3r()
        val may = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-05" },
            "2009-05",
        ).file
        val day = assertNotNull(may.days.firstOrNull { it.date == "2009-05-10" })

        val oldDocument = assertNotNull(day.steps.firstOrNull { it.label.contains("Request #2", ignoreCase = true) })
        assertTrue(oldDocument.label.contains("Old Document 01", ignoreCase = true))
        assertTrue(oldDocument.label.contains("22F", ignoreCase = true))

        val drinks = assertNotNull(day.steps.firstOrNull { it.label.contains("Request #9", ignoreCase = true) })
        assertTrue(drinks.label.contains("12 unique", ignoreCase = true))
        assertTrue(drinks.label.contains("Dorm 2F", ignoreCase = true))
        assertTrue(drinks.label.contains("Dorm 3F", ignoreCase = true))
        assertTrue(drinks.label.contains("Port Island Station", ignoreCase = true))
        assertTrue(drinks.label.contains("Strip Mall 3F", ignoreCase = true))
        assertTrue(drinks.label.contains("duplicates", ignoreCase = true))
        assertTrue(drinks.label.contains("Request #11", ignoreCase = true))
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
