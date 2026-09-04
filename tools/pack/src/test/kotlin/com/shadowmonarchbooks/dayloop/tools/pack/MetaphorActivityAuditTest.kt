package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorActivityAuditTest {

    @Test
    fun `Metaphor books keep variable gains on dated route steps`() {
        val activities = assertNotNull(loadMetaphor().activities).activities.associateBy { it.id }
        val books = listOf(
            "metaphor.activity.book.new-world-travel-diary",
            "metaphor.activity.book.pride-and-persuasion",
            "metaphor.activity.book.bygone-days",
            "metaphor.activity.book.the-future-of-magic",
            "metaphor.activity.book.top-secret-poetry",
            "metaphor.activity.book.how-to-walk-outside-the-island",
            "metaphor.activity.book.literacy-workbook",
        )

        books.forEach { id ->
            val activity = activities.getValue(id)
            assertTrue(activity.statGains.isEmpty(), "$id: generic gain would hide final-session completion bonus")
            assertTrue(activity.notes.orEmpty().contains("dated walkthrough step"), id)
        }
    }

    @Test
    fun `Metaphor coliseum and beetle reusable facts stay explicit`() {
        val activities = assertNotNull(loadMetaphor().activities).activities.associateBy { it.id }

        assertEquals(10, activities.getValue("metaphor.activity.coliseum.ranked-league").statGains["courage"])
        assertEquals(10, activities.getValue("metaphor.activity.coliseum.gauntlet-challenge").statGains["courage"])

        val beetles = activities.getValue("metaphor.activity.gold-beetles").notes.orEmpty()
        assertTrue(beetles.contains("50 are obtainable"))
        assertTrue(beetles.contains("46 are needed"))
        assertTrue(beetles.contains("not required"))
    }

    private fun loadMetaphor() = PackLoader.load(metaphorDir()).also { loaded ->
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
    }

    private fun metaphorDir(): Path {
        val candidates = listOf(
            Path.of("content", "packs", "metaphor"),
            Path.of("..", "..", "content", "packs", "metaphor"),
        )
        return candidates.firstOrNull { Files.isDirectory(it) }
            ?: error("Cannot locate content/packs/metaphor from ${Path.of("").toAbsolutePath()}")
    }
}
