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

class P3rJuneAuditTest {

    @Test
    fun `P3R June social-stat route matches audited raw point thresholds`() {
        val loaded = loadP3r()
        fun monthDays(value: String) = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == value },
            value,
        ).file.days.associateBy { it.date }

        val april = monthDays("2009-04")
        val may = monthDays("2009-05")
        val june = monthDays("2009-06")

        fun total(days: Map<String, Day>, stat: String, through: String? = null): Int = days.values
            .filter { through == null || it.date <= through }
            .flatMap { it.steps }
            .sumOf { it.statGains[stat] ?: 0 }

        fun gain(date: String, label: String, stat: String): Int {
            val day = assertNotNull(june[date], date)
            val step = assertNotNull(day.steps.firstOrNull { it.label.contains(label, ignoreCase = true) }, "$date: $label")
            return assertNotNull(step.statGains[stat], "$date: $label -> $stat")
        }

        val endMayAcademics = total(april, "academics") + total(may, "academics")
        val endMayCharm = total(april, "charm") + total(may, "charm")
        val endMayCourage = total(april, "courage") + total(may, "courage")
        assertEquals(61, endMayAcademics)
        assertEquals(52, endMayCharm)
        assertEquals(57, endMayCourage)

        assertEquals(4, gain("2009-06-01", "High School of Youth", "charm"))
        assertEquals(4, gain("2009-06-05", "House of the Deceased", "courage"))
        assertEquals(4, gain("2009-06-07", "Weekend Wilduck Set", "courage"))
        assertEquals(1, gain("2009-06-09", "part-time at Chagall", "charm"))
        assertEquals(1, gain("2009-06-09", "part-time at Chagall", "courage"))
        assertEquals(2, gain("2009-06-16", "Junpei's reading", "courage"))
        assertEquals(2, gain("2009-06-19", "karaoke", "courage"))
        assertEquals(2, gain("2009-06-20", "Akihiko's DVD", "courage"))
        assertEquals(2, gain("2009-06-23", "Yukari's DVD", "charm"))
        assertEquals(2, gain("2009-06-25", "Fuuka's reading", "academics"))
        assertEquals(3, gain("2009-06-26", "Prodigy Platter", "academics"))
        assertEquals(4, gain("2009-06-28", "Seafood Full Course", "academics"))

        val courageByJune5 = endMayCourage + total(june, "courage", "2009-06-05")
        assertEquals(61, courageByJune5)
        assertTrue(courageByJune5 >= 60)

        val charmByJune17 = endMayCharm + total(june, "charm", "2009-06-17")
        assertEquals(70, charmByJune17)

        val courageByJune19 = endMayCourage + total(june, "courage", "2009-06-19")
        assertEquals(81, courageByJune19)
        assertTrue(courageByJune19 >= 80)

        val charmRankStep = assertNotNull(june["2009-06-17"])
            .steps.firstOrNull { it.label.contains("Charm reaches rank 5", ignoreCase = true) }
        assertNotNull(charmRankStep)

        val courageRankStep = assertNotNull(june["2009-06-19"])
            .steps.firstOrNull { it.label.contains("Courage reaches rank 6", ignoreCase = true) }
        assertNotNull(courageRankStep)
    }

    @Test
    fun `P3R June fixed gameplay unlock labels stay on independently supported dates`() {
        val loaded = loadP3r()
        val june = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-06" },
            "2009-06",
        ).file.days.associateBy { it.date }

        val june13 = assertNotNull(june["2009-06-13"])
        assertTrue(june13.steps.any { it.label.contains("Theurgy", ignoreCase = true) })
        assertTrue(june13.steps.any { it.label.contains("combat uniforms", ignoreCase = true) })

        val june16 = assertNotNull(june["2009-06-16"])
        assertTrue(june16.steps.any { it.label.contains("dorm hangouts unlock", ignoreCase = true) })
        assertTrue(june16.steps.any { it.label.contains("Junpei's reading", ignoreCase = true) })

        val june18 = assertNotNull(june["2009-06-18"])
        assertTrue(june18.steps.any { it.label.contains("Missing Persons", ignoreCase = true) })
        assertTrue(june18.steps.any { it.label.contains("rescue missions unlock", ignoreCase = true) })
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
