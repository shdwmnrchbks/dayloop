package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class P3rAutomaticStoryRankAuditTest {

    @Test
    fun `P3R dated automatic Social Link ranks through July are surfaced in the walkthrough`() {
        val loaded = loadP3r()
        val bonds = assertNotNull(loaded.bonds).bonds.associateBy { it.id }
        val days = loaded.walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .associateBy { it.date }

        data class Expected(val bondId: String, val arcana: String, val rank: Int, val date: String)
        val expected = listOf(
            Expected("p3r.bond.fool", "Fool", 1, "2009-04-18"),
            Expected("p3r.bond.fool", "Fool", 2, "2009-04-20"),
            Expected("p3r.bond.fool", "Fool", 3, "2009-05-09"),
            Expected("p3r.bond.death", "Death", 1, "2009-06-12"),
            Expected("p3r.bond.fool", "Fool", 4, "2009-07-07"),
            Expected("p3r.bond.death", "Death", 3, "2009-07-12"),
            Expected("p3r.bond.fool", "Fool", 5, "2009-07-22"),
        )

        expected.forEach { check ->
            val rank = assertNotNull(
                bonds.getValue(check.bondId).ranks.firstOrNull { it.rank == check.rank },
                "${check.bondId} rank ${check.rank}",
            )
            assertEquals(check.date, rank.availableFrom, "${check.bondId} rank ${check.rank}")
            assertEquals(null, rank.scheduledFor, "automatic rank must not use scheduledFor")

            val day = assertNotNull(days[check.date], check.date)
            val expectedLabel = "${check.arcana} reaches rank ${check.rank}"
            assertEquals(
                1,
                day.steps.count { it.label.contains(expectedLabel, ignoreCase = true) },
                "$expectedLabel should appear exactly once on ${check.date}",
            )
        }

        val death3 = assertNotNull(days["2009-07-12"])
            .steps.firstOrNull { it.label.contains("Death reaches rank 3", ignoreCase = true) }
        assertNotNull(death3)
        assertTrue(death3.label.contains("skips rank 2", ignoreCase = true))
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
