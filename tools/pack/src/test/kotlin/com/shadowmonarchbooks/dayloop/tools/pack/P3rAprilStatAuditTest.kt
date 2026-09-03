package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class P3rAprilStatAuditTest {

    @Test
    fun `P3R April route uses audited social-stat point units`() {
        val april = assertNotNull(
            loadP3r().walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-04" },
        ).file
        val days = april.days.associateBy { it.date }

        fun gain(date: String, label: String, stat: String): Int {
            val day = assertNotNull(days[date], date)
            val step = assertNotNull(
                day.steps.firstOrNull { it.label.contains(label, ignoreCase = true) },
                "$date: $label",
            )
            return assertNotNull(step.statGains[stat], "$date: $label -> $stat")
        }

        listOf("2009-04-08", "2009-04-18", "2009-04-27").forEach { date ->
            assertEquals(2, gain(date, "Answer the class question", "charm"), date)
        }
        listOf("2009-04-09", "2009-04-21", "2009-04-30").forEach { date ->
            assertEquals(2, gain(date, "Stay awake", "academics"), date)
        }
        listOf("2009-04-21", "2009-04-24").forEach { date ->
            assertEquals(2, gain(date, "Nurse's Office", "courage"), date)
        }

        assertEquals(4, gain("2009-04-21", "Screen Shot", "charm"))
        listOf("2009-04-21", "2009-04-24", "2009-04-28").forEach { date ->
            assertEquals(4, gain(date, "House of the Deceased", "courage"), date)
        }
        listOf("2009-04-22", "2009-04-25", "2009-04-29").forEach { date ->
            assertEquals(4, gain(date, "You're the Answer", "academics"), date)
        }
        assertEquals(3, gain("2009-04-26", "Mystery Burger", "courage"))
        listOf("2009-04-27", "2009-04-30").forEach { date ->
            assertEquals(4, gain(date, "High School of Youth", "charm"), date)
        }

        val april26Courage = listOf(
            gain("2009-04-21", "Nurse's Office", "courage"),
            gain("2009-04-21", "House of the Deceased", "courage"),
            gain("2009-04-24", "Nurse's Office", "courage"),
            gain("2009-04-24", "House of the Deceased", "courage"),
            gain("2009-04-26", "Mystery Burger", "courage"),
        ).sum()
        assertEquals(15, april26Courage, "Courage rank 2 threshold should be reached on April 26")
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
