package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class P3rJanuaryAuditTest {

    @Test
    fun `P3R January preserves the authored Social Link finish`() {
        val january = januaryDays()
        val expected = mapOf(
            "2010-01-08" to "Aeon reaches rank 1",
            "2010-01-09" to "Empress reaches rank 10",
            "2010-01-12" to "Chariot reaches rank 10",
            "2010-01-17" to "Star reaches rank 10",
            "2010-01-26" to "Fortune reaches rank 10",
            "2010-01-29" to "Aeon reaches rank 10",
        )
        expected.forEach { (date, phrase) ->
            val day = assertNotNull(january[date], date)
            assertTrue(day.steps.any { it.label.contains(phrase, ignoreCase = true) }, "$date: $phrase")
        }
        assertTrue(assertNotNull(january["2010-01-29"]).notes.orEmpty().contains("All Social Links", ignoreCase = true))
    }

    @Test
    fun `P3R January Adamah climb matches floor-driven Judgment progression`() {
        val january15 = assertNotNull(januaryDays()["2010-01-15"])
        val judgment = assertNotNull(january15.steps.firstOrNull { it.label.contains("Judgment reaches ranks 2–10", ignoreCase = true) })

        assertTrue(
            judgment.label.contains(
                "227F, 230F, 236F, 241F, 246F, 247F, 253F, 254F, and 255F respectively",
                ignoreCase = true,
            ),
            judgment.label,
        )

        val request99 = assertNotNull(january15.steps.firstOrNull { it.label.contains("Request #99", ignoreCase = true) })
        assertTrue(request99.label.contains("255F", ignoreCase = true))
        assertTrue(request99.label.contains("Shadow of the Void", ignoreCase = true))
        assertTrue(request99.label.contains("complete", ignoreCase = true))

        val request98 = assertNotNull(january15.steps.firstOrNull { it.label.contains("Request #98", ignoreCase = true) })
        assertTrue(request98.label.contains("Masakado", ignoreCase = true))
        assertTrue(request98.label.contains("Charge", ignoreCase = true))
    }

    @Test
    fun `P3R January final missing-person batch is cleared before January 30`() {
        val january = januaryDays()
        val january15 = assertNotNull(january["2010-01-15"])
        val rescue = assertNotNull(january15.steps.firstOrNull { it.label.contains("missing persons", ignoreCase = true) })
        assertTrue(rescue.label.contains("232F", ignoreCase = true))
        assertTrue(rescue.label.contains("250F", ignoreCase = true))
        assertTrue(rescue.label.contains("January 30", ignoreCase = true))

        val january30 = assertNotNull(january["2010-01-30"])
        assertTrue(january30.notes.orEmpty().contains("last actionable day", ignoreCase = true))
        val confirmation = assertNotNull(january30.steps.firstOrNull { it.label.contains("232F", ignoreCase = true) })
        assertTrue(confirmation.label.contains("250F", ignoreCase = true))
        assertTrue(confirmation.label.contains("rescued", ignoreCase = true))
    }

    @Test
    fun `P3R January Reaper request unlocks the ultimate-adversary request in order`() {
        val january = januaryDays()
        val january21 = assertNotNull(january["2010-01-21"])
        val reaper = assertNotNull(january21.steps.firstOrNull { it.label.contains("Request #100", ignoreCase = true) })
        assertTrue(reaper.label.contains("Reaper", ignoreCase = true))
        assertTrue(reaper.label.contains("Bloody Button", ignoreCase = true))
        assertTrue(reaper.label.contains("complete", ignoreCase = true))
        assertTrue(reaper.label.contains("accept Request #101", ignoreCase = true))

        val january30 = assertNotNull(january["2010-01-30"])
        val ultimate = assertNotNull(january30.steps.firstOrNull { it.label.contains("Request #101", ignoreCase = true) })
        assertTrue(ultimate.label.contains("accepted", ignoreCase = true))
        assertTrue(ultimate.label.contains("255F", ignoreCase = true))
        assertTrue(ultimate.label.contains("alone", ignoreCase = true))
        assertTrue(ultimate.label.contains("ultimate adversary", ignoreCase = true))
        assertTrue(ultimate.label.contains("complete", ignoreCase = true))
    }

    @Test
    fun `P3R January ends on the Promised Day`() {
        val january31 = assertNotNull(januaryDays()["2010-01-31"])
        assertEquals("story", january31.dayKind)
        assertTrue(january31.steps.any { it.label.contains("Promised Day", ignoreCase = true) })
        assertTrue(january31.steps.any { it.label.contains("final free time", ignoreCase = true) })
    }

    private fun januaryDays() = assertNotNull(
        loadP3r().walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2010-01" },
        "2010-01",
    ).file.days.associateBy { it.date }

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
