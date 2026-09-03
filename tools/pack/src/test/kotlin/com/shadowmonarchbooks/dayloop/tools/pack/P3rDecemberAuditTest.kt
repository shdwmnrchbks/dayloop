package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class P3rDecemberAuditTest {

    @Test
    fun `P3R December timed Christmas request preserves accept-before-item ordering`() {
        val december = decemberDays()
        val december4 = assertNotNull(december["2009-12-04"])
        val acceptIndex = december4.steps.indexOfFirst {
            it.label.contains("Request #97", ignoreCase = true) && it.label.contains("accept", ignoreCase = true)
        }
        val itemIndex = december4.steps.indexOfFirst {
            it.label.contains("Request #97", ignoreCase = true) &&
                it.label.contains("Eccentric Man", ignoreCase = true) &&
                it.label.contains("Christmas Present", ignoreCase = true)
        }
        assertTrue(acceptIndex >= 0)
        assertTrue(itemIndex > acceptIndex)
        assertTrue(december4.steps[itemIndex].label.contains("Thank-You", ignoreCase = true))
        assertTrue(december4.steps[itemIndex].label.contains("December 25", ignoreCase = true))
        assertTrue(december4.steps[itemIndex].label.contains("complete", ignoreCase = true))
    }

    @Test
    fun `P3R December rescue batch is surfaced and cleared by the actionable cutoff`() {
        val december = decemberDays()
        val december22 = assertNotNull(december["2009-12-22"])
        val warning = assertNotNull(december22.steps.firstOrNull { it.label.contains("missing persons", ignoreCase = true) })
        assertTrue(warning.label.contains("209F", ignoreCase = true))
        assertTrue(warning.label.contains("221F", ignoreCase = true))
        assertTrue(warning.label.contains("December 30", ignoreCase = true))

        val december30 = assertNotNull(december["2009-12-30"])
        val tartarus = assertNotNull(december30.steps.firstOrNull { it.label.contains("Tartarus", ignoreCase = true) })
        assertTrue(tartarus.label.contains("209F", ignoreCase = true))
        assertTrue(tartarus.label.contains("221F", ignoreCase = true))
        assertTrue(tartarus.label.contains("December 30", ignoreCase = true))
        assertTrue(tartarus.label.contains("rescue", ignoreCase = true))
        assertTrue(december30.notes.orEmpty().contains("last actionable day", ignoreCase = true))
    }

    @Test
    fun `P3R December 31 stays on the good ending and orders Fool before Judgment`() {
        val december = decemberDays()
        val december31 = assertNotNull(december["2009-12-31"])
        assertEquals("story", december31.dayKind)

        val decisionIndex = december31.steps.indexOfFirst {
            it.label.contains("Spare Ryoji", ignoreCase = true) && it.label.contains("good-ending", ignoreCase = true)
        }
        val foolIndex = december31.steps.indexOfFirst { it.label.contains("Fool reaches rank 10", ignoreCase = true) }
        val judgmentIndex = december31.steps.indexOfFirst { it.label.contains("Judgment reaches rank 1", ignoreCase = true) }
        assertTrue(decisionIndex >= 0)
        assertTrue(foolIndex > decisionIndex)
        assertTrue(judgmentIndex > foolIndex)
        assertTrue(december31.steps[foolIndex].label.contains("MAX", ignoreCase = true))
        assertTrue(december31.notes.orEmpty().contains("continue into January", ignoreCase = true))
    }

    @Test
    fun `P3R December exam and full moon structure remains represented`() {
        val december = decemberDays()
        val december2 = assertNotNull(december["2009-12-02"])
        assertTrue(december2.notes.orEmpty().contains("Full moon", ignoreCase = true))
        assertTrue(december2.notes.orEmpty().contains("evening slot", ignoreCase = true))

        (14..19).forEach { day ->
            val date = "2009-12-${day.toString().padStart(2, '0')}"
            assertEquals("exam", assertNotNull(december[date], date).dayKind)
        }
        assertTrue(assertNotNull(december["2009-12-19"]).steps.any { it.label.contains("Final exam day", ignoreCase = true) })
    }

    private fun decemberDays() = assertNotNull(
        loadP3r().walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-12" },
        "2009-12",
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
