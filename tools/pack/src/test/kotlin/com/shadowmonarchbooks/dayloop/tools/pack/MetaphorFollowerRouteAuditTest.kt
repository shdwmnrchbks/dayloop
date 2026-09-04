package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import com.shadowmonarchbooks.dayloop.pack.schema.StatGte
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorFollowerRouteAuditTest {

    @Test
    fun `every route scheduled Follower rank is authored on the same walkthrough day`() {
        val loaded = loadMetaphor()
        val days = loaded.walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .associateBy { it.date }
        val bonds = assertNotNull(loaded.bonds).bonds

        bonds.forEach { bond ->
            bond.ranks.forEach { rank ->
                val date = rank.scheduledFor ?: return@forEach
                val day = assertNotNull(days[date], "${bond.label} rank ${rank.rank} route date $date")
                val rankPattern = Regex("(?i)\\brank\\s+${rank.rank}\\b")
                assertTrue(
                    day.steps.any { step ->
                        step.label.contains(bond.label, ignoreCase = true) &&
                            rankPattern.containsMatchIn(step.label)
                    },
                    "${bond.label} rank ${rank.rank} is scheduled for $date but the walkthrough has no matching rank step",
                )
            }
        }
    }

    @Test
    fun `Metaphor Follower Royal Virtue gates match the game requirements`() {
        data class Gate(val bond: String, val rank: Int, val stat: String, val statRank: Int)

        val expected = listOf(
            Gate("metaphor.bond.strohl", 6, "imagination", 3),
            Gate("metaphor.bond.hulkenberg", 7, "wisdom", 4),
            Gate("metaphor.bond.heismay", 2, "eloquence", 2),
            Gate("metaphor.bond.heismay", 7, "imagination", 4),
            Gate("metaphor.bond.junah", 5, "imagination", 4),
            Gate("metaphor.bond.eupha", 6, "eloquence", 4),
            Gate("metaphor.bond.eupha", 7, "wisdom", 5),
            Gate("metaphor.bond.basilio", 6, "imagination", 5),
            Gate("metaphor.bond.basilio", 8, "eloquence", 5),
            Gate("metaphor.bond.maria", 2, "tolerance", 2),
            Gate("metaphor.bond.maria", 7, "tolerance", 4),
            Gate("metaphor.bond.catherina", 6, "tolerance", 5),
            Gate("metaphor.bond.alonzo", 1, "imagination", 2),
            Gate("metaphor.bond.alonzo", 2, "wisdom", 3),
            Gate("metaphor.bond.alonzo", 4, "tolerance", 3),
            Gate("metaphor.bond.alonzo", 7, "courage", 5),
            Gate("metaphor.bond.bardon", 5, "eloquence", 3),
            Gate("metaphor.bond.brigitta", 1, "wisdom", 2),
            Gate("metaphor.bond.brigitta", 5, "courage", 3),
            Gate("metaphor.bond.brigitta", 7, "courage", 4),
        )

        val bonds = assertNotNull(loadMetaphor().bonds).bonds.associateBy { it.id }
        expected.forEach { gate ->
            val rank = assertNotNull(
                bonds.getValue(gate.bond).ranks.firstOrNull { it.rank == gate.rank },
                "${gate.bond} rank ${gate.rank}",
            )
            val condition = assertIs<StatGte>(rank.gates, "${gate.bond} rank ${gate.rank}")
            assertEquals(gate.stat, condition.stat, "${gate.bond} rank ${gate.rank}")
            assertEquals(gate.statRank, condition.rank, "${gate.bond} rank ${gate.rank}")
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
