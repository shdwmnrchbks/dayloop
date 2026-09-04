package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorDebateAuditTest {

    private data class Debate(
        val candidate: String,
        val date: String,
        val weekday: String,
        val answer: String,
        val eloquence: Int,
        val imagination: Int,
    )

    @Test
    fun `Metaphor completion route schedules all eight debates on valid dates`() {
        val expected = listOf(
            Debate("Loveless", "2100-07-06", "flamesday", "alcohol won't bring equality", 10, 5),
            Debate("Lina", "2100-07-12", "watersday", "what are your policies", 10, 5),
            Debate("Roger", "2100-07-14", "metalsday", "taxes benefit us all", 10, 5),
            Debate("Jin", "2100-07-23", "arboursday", "we should help anyone", 11, 5),
            Debate("Glodell", "2100-07-24", "metalsday", "you only parrot Louis", 11, 5),
            Debate("Rudolf", "2100-07-26", "flamesday", "the tribes must stand together", 11, 5),
            Debate("Milo", "2100-07-27", "watersday", "beauty is deeper than our skin", 11, 5),
            Debate("Julian", "2100-09-14", "metalsday", "the present day matters too", 15, 6),
        )
        val days = loadMetaphor().walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .associateBy { it.date }

        expected.forEach { debate ->
            val day = assertNotNull(days[debate.date], debate.candidate)
            assertEquals(debate.weekday, day.weekday, debate.candidate)
            val step = assertNotNull(
                day.steps.firstOrNull { it.label.startsWith("Debate ${debate.candidate}", ignoreCase = true) },
                "${debate.candidate} debate missing from ${debate.date}",
            )
            assertTrue(step.label.contains(debate.answer, ignoreCase = true), "${debate.candidate}: winning answer")
            assertEquals(debate.eloquence, step.statGains["eloquence"], "${debate.candidate}: Eloquence")
            assertEquals(debate.imagination, step.statGains["imagination"], "${debate.candidate}: Imagination")
        }

        val authoredDebates = days.values.sumOf { day ->
            day.steps.count { it.label.startsWith("Debate ", ignoreCase = true) }
        }
        assertEquals(8, authoredDebates, "Debate Me requires all eight candidate debates")
    }

    @Test
    fun `Metaphor debate activity documents every availability window`() {
        val activity = assertNotNull(loadMetaphor().activities).activities
            .single { it.id == "metaphor.activity.podium-debates" }
        val notes = activity.notes.orEmpty()
        listOf("Loveless", "Lina", "Roger", "Jin", "Glodell", "Rudolf", "Milo", "Julian").forEach { candidate ->
            assertTrue(notes.contains(candidate), "$candidate availability is missing from debate activity notes")
        }
        assertTrue(notes.contains("07/23-08/08"), "Jin's window must not regress to the invalid 07/21 route date")
        assertTrue(notes.contains("09/13-10/24"), "Julian's broad Altabury window should remain documented")
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
