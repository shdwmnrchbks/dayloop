package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class P3rElizabethDeadlineAuditTest {

    @Test
    fun `P3R surfaces every explicitly timed Elizabeth request`() {
        val deadlines = assertNotNull(loadP3r().deadlines).deadlines
        assertEquals(37, deadlines.size)

        val expected = linkedMapOf(
            "p3r.deadline.elizabeth.12-pine-resin" to "2009-06-06",
            "p3r.deadline.elizabeth.13-handheld-console" to "2009-06-06",
            "p3r.deadline.elizabeth.27-triangular-sword" to "2009-07-05",
            "p3r.deadline.elizabeth.28-protein" to "2009-07-05",
            "p3r.deadline.elizabeth.29-fashion" to "2009-07-05",
            "p3r.deadline.elizabeth.43-christmas-star" to "2009-08-04",
            "p3r.deadline.elizabeth.44-ocean" to "2009-08-04",
            "p3r.deadline.elizabeth.58-straw-millionaire" to "2009-08-31",
            "p3r.deadline.elizabeth.68-fruit-knife" to "2009-10-02",
            "p3r.deadline.elizabeth.69-machine-oil" to "2009-10-02",
            "p3r.deadline.elizabeth.76-glasses-wipe" to "2009-11-01",
            "p3r.deadline.elizabeth.94-furry-friend" to "2009-11-30",
            "p3r.deadline.elizabeth.95-featherman" to "2009-11-30",
            "p3r.deadline.elizabeth.97-christmas-present" to "2009-12-25",
        )

        val byId = deadlines.associateBy { it.id }
        expected.forEach { (id, date) ->
            val deadline = assertNotNull(byId[id], id)
            assertEquals("request", deadline.kind, id)
            assertEquals(date, deadline.date, id)
            assertEquals(null, deadline.window, id)
            assertTrue(deadline.label.contains("Elizabeth Request", ignoreCase = true), deadline.label)
        }

        assertEquals(expected.keys, deadlines.filter { it.id.startsWith("p3r.deadline.elizabeth.") }.map { it.id }.toSet())
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
