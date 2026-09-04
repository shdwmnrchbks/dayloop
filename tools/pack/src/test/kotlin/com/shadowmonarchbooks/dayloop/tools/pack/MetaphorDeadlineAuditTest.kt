package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorDeadlineAuditTest {

    @Test
    fun `Metaphor operation entries use real game windows rather than route clear dates`() {
        val deadlines = assertNotNull(loadMetaphor().deadlines).deadlines.associateBy { it.id }

        data class WindowCheck(val id: String, val start: String, val end: String)
        val windows = listOf(
            WindowCheck("metaphor.deadline.mission.grand-cathedral", "2100-06-12", "2100-06-21"),
            WindowCheck("metaphor.deadline.mission.kriegante-castle", "2100-07-05", "2100-07-16"),
            WindowCheck("metaphor.deadline.mission.charadrius", "2100-07-23", "2100-08-12"),
            WindowCheck("metaphor.deadline.mission.dragon-temple", "2100-08-19", "2100-09-05"),
            WindowCheck("metaphor.deadline.mission.final-prep", "2100-09-13", "2100-09-22"),
            WindowCheck("metaphor.deadline.mission.skybound-avatar", "2100-09-26", "2100-10-25"),
            WindowCheck("metaphor.deadline.missable.julian-book", "2100-07-23", "2100-08-12"),
        )

        windows.forEach { check ->
            val deadline = deadlines.getValue(check.id)
            val window = assertNotNull(deadline.window, check.id)
            assertEquals(check.start, window.start, check.id)
            assertEquals(check.end, window.end, check.id)
            assertEquals(null, deadline.date, "${check.id}: window must not collapse to one route date")
        }
    }

    @Test
    fun `Metaphor mandatory story beats are not marked as palace deadlines`() {
        val deadlines = assertNotNull(loadMetaphor().deadlines).deadlines.associateBy { it.id }
        val storyDates = mapOf(
            "metaphor.deadline.mission.border-fort" to "2100-06-05",
            "metaphor.deadline.mission.nord-mines" to "2100-06-06",
            "metaphor.deadline.mission.opera-house" to "2100-09-10",
            "metaphor.deadline.mission.duel-louis" to "2100-09-23",
            "metaphor.deadline.story.tyrants-star" to "2100-10-26",
        )

        storyDates.forEach { (id, date) ->
            val entry = deadlines.getValue(id)
            assertEquals("other", entry.kind, id)
            assertEquals(date, entry.date, id)
            assertEquals(null, entry.window, id)
        }
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
