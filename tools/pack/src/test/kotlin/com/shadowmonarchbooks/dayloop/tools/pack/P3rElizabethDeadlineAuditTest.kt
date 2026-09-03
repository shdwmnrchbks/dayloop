package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
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

    @Test
    fun `P3R completion route closes every timed Elizabeth request before cutoff`() {
        val loaded = loadP3r()

        fun labels(month: String, date: String): List<String> {
            val walkthrough = assertNotNull(
                loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == month },
                month,
            ).file
            val day = assertNotNull(walkthrough.days.firstOrNull { it.date == date }, date)
            return day.steps.map { it.label }
        }

        fun requireStep(month: String, date: String, vararg terms: String) {
            val dayLabels = labels(month, date)
            assertTrue(
                dayLabels.any { label -> terms.all { term -> label.contains(term, ignoreCase = true) } },
                "$date: expected a step containing ${terms.joinToString()}",
            )
        }

        // 6/6 deadline: acceptance is required before Yukari/Junpei will provide the items.
        requireStep("2009-05", "2009-05-10", "Request #12", "accept", "Pine Resin", "6/6")
        requireStep("2009-05", "2009-05-10", "Request #13", "accept", "Handheld", "6/6")

        // 7/5 deadline: #28 follows #27; #29 needs Black Quartz converted at Club Escapade.
        requireStep("2009-06", "2009-06-14", "Request #27", "Fencing Epee")
        requireStep("2009-06", "2009-06-14", "Request #28", "Amateur Protein")
        requireStep("2009-06", "2009-06-14", "Request #29", "accept")
        requireStep("2009-06", "2009-06-27", "Request #29", "Black Quartz")
        requireStep("2009-06", "2009-06-28", "Request #29", "fashionable", "July 5")

        // 8/4 deadline: both must be accepted before their item interactions.
        requireStep("2009-07", "2009-07-09", "#43", "#44", "accept")
        requireStep("2009-07", "2009-07-09", "Request #43", "Poinsettia", "August 4")
        requireStep("2009-07", "2009-07-20", "Request #44", "beach")
        requireStep("2009-07", "2009-07-23", "Request #44", "Yakushima", "August 4")

        // 8/31 deadline: complete the full barter chain rather than merely accepting it.
        requireStep("2009-08", "2009-08-08", "Request #58", "Wrapped Bandage", "Cat Ear Headband", "August 31")

        // 10/2 deadline: #69 follows #68 and both party-member items require acceptance first.
        requireStep("2009-09", "2009-09-10", "Request #68", "Fruit Knife", "October 2")
        requireStep("2009-09", "2009-09-10", "Request #69", "Machine Oil", "October 2")

        // 11/1 deadline: acceptance precedes the Ikutsuki handoff.
        requireStep("2009-10", "2009-10-06", "Request #76", "accept", "Glasses Wipe")
        requireStep("2009-10", "2009-10-07", "Request #76", "Glasses Wipe", "November 1")

        // 11/30 deadline: #95 becomes available after #94.
        requireStep("2009-11", "2009-11-06", "Request #94", "accept")
        requireStep("2009-11", "2009-11-06", "Request #94", "Gourmet Dog Food", "Request #95")
        requireStep("2009-11", "2009-11-06", "Request #95", "Featherman", "November 30")

        // 12/25 deadline: accept before the Eccentric Man exchange.
        requireStep("2009-12", "2009-12-04", "Request #97", "accept", "Eccentric Man")
        requireStep("2009-12", "2009-12-04", "Request #97", "Christmas Present", "December 25")
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
