package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class P3rRescueDeadlineAuditTest {

    @Test
    fun `P3R exposes every actionable missing-person rescue cutoff`() {
        val deadlines = assertNotNull(loadP3r().deadlines).deadlines
        val byId = deadlines.associateBy { it.id }

        val expected = linkedMapOf(
            "p3r.deadline.missing-persons.2009-07-06" to "2009-07-06",
            "p3r.deadline.missing-persons.2009-08-05" to "2009-08-05",
            "p3r.deadline.missing-persons.2009-09-04" to "2009-09-04",
            "p3r.deadline.missing-persons.2009-10-03" to "2009-10-03",
            "p3r.deadline.missing-persons.2009-11-02" to "2009-11-02",
            "p3r.deadline.missing-persons.2009-12-01" to "2009-12-01",
            "p3r.deadline.missing-persons.2009-12-30" to "2009-12-30",
            "p3r.deadline.missing-persons.2010-01-30" to "2010-01-30",
        )

        val rescueDeadlines = deadlines.filter { it.id.startsWith("p3r.deadline.missing-persons.") }
        assertEquals(8, rescueDeadlines.size)
        assertEquals(expected.keys, rescueDeadlines.map { it.id }.toSet())

        expected.forEach { (id, date) ->
            val deadline = assertNotNull(byId[id], id)
            assertEquals("missable", deadline.kind, id)
            assertEquals(date, deadline.date, id)
            assertEquals(null, deadline.window, id)
            assertTrue(deadline.label.contains("rescue", ignoreCase = true), deadline.label)
        }

        assertTrue(byId.getValue("p3r.deadline.missing-persons.2009-09-04").label.contains("last actionable", ignoreCase = true))
        assertTrue(byId.getValue("p3r.deadline.missing-persons.2009-10-03").label.contains("Bunkichi", ignoreCase = true))
        assertTrue(byId.getValue("p3r.deadline.missing-persons.2009-11-02").label.contains("Maiko", ignoreCase = true))
        assertTrue(byId.getValue("p3r.deadline.missing-persons.2010-01-30").label.contains("Final", ignoreCase = true))
    }

    @Test
    fun `P3R completion route clears every missing-person batch before its cutoff`() {
        val loaded = loadP3r()
        val tartarusDates = loaded.walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .filter { day -> day.steps.any { it.label.contains("Tartarus", ignoreCase = true) } }
            .mapTo(mutableSetOf()) { it.date }

        val expectedBatchClearDates = listOf(
            "2009-06-27",
            "2009-08-03",
            "2009-09-04",
            "2009-10-01",
            "2009-11-02",
            "2009-11-29",
            "2009-12-30",
            "2010-01-15",
        )

        expectedBatchClearDates.forEach { date ->
            assertTrue(date in tartarusDates, "$date: authored route must retain the audited rescue-batch Tartarus visit")
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
