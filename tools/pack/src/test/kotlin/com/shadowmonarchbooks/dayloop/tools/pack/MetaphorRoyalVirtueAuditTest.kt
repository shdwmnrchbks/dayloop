package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import com.shadowmonarchbooks.dayloop.pack.schema.StatGte
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorRoyalVirtueAuditTest {

    @Test
    fun `all scheduled Follower virtue gates are proven before their route step`() {
        val loaded = loadMetaphor()
        val days = loaded.walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .sortedBy { it.date }

        val thresholds = mapOf(
            "courage" to mapOf(2 to 30, 3 to 100, 4 to 180, 5 to 240),
            "wisdom" to mapOf(2 to 16, 3 to 80, 4 to 160, 5 to 280),
            "tolerance" to mapOf(2 to 40, 3 to 100, 4 to 160, 5 to 210),
            "eloquence" to mapOf(2 to 40, 3 to 80, 4 to 130, 5 to 170),
            "imagination" to mapOf(2 to 50, 3 to 120, 4 to 200, 5 to 280),
        )

        val milestonePattern = Regex(
            "(?i)\\b(Courage|Wisdom|Tolerance|Eloquence|Imagination) reaches rank ([2-5])\\b",
        )

        // The walkthrough deliberately does not annotate every point-bearing action.
        // statGains are exact known points. An explicit "reaches rank N" line is a
        // stronger route fact, so clamp the running total to that rank's threshold.
        // This produces a conservative lower bound without inventing omitted rewards.
        val lowerBound = mutableMapOf<String, Int>()
        val totalsBeforeStep = mutableMapOf<Pair<String, Int>, Map<String, Int>>()
        days.forEach { day ->
            day.steps.forEachIndexed { index, step ->
                totalsBeforeStep[day.date to index] = lowerBound.toMap()

                step.statGains.forEach { (stat, points) ->
                    lowerBound[stat] = lowerBound.getOrDefault(stat, 0) + points
                }

                val milestone = milestonePattern.find(step.label)
                if (milestone != null) {
                    val stat = milestone.groupValues[1].lowercase()
                    val rank = milestone.groupValues[2].toInt()
                    val threshold = thresholds.getValue(stat).getValue(rank)
                    lowerBound[stat] = max(lowerBound.getOrDefault(stat, 0), threshold)
                }
            }
        }

        val gatedRanks = assertNotNull(loaded.bonds).bonds.flatMap { bond ->
            bond.ranks.mapNotNull { rank ->
                val gate = rank.gates as? StatGte ?: return@mapNotNull null
                val scheduledFor = rank.scheduledFor ?: return@mapNotNull null
                Triple(bond, rank, gate) to scheduledFor
            }
        }
        assertTrue(gatedRanks.isNotEmpty(), "Metaphor should have scheduled stat-gated Follower ranks")

        gatedRanks.forEach { (entry, date) ->
            val (bond, rank, gate) = entry
            val day = assertNotNull(days.firstOrNull { it.date == date }, "$date for ${bond.label} rank ${rank.rank}")
            val rankPattern = Regex("(?i)\\brank\\s+${rank.rank}\\b")
            val stepIndex = day.steps.indexOfFirst { step ->
                step.label.contains(bond.label, ignoreCase = true) && rankPattern.containsMatchIn(step.label)
            }
            assertTrue(
                stepIndex >= 0,
                "${bond.label} rank ${rank.rank} is scheduled for $date but has no matching walkthrough step",
            )

            val required = assertNotNull(
                thresholds[gate.stat]?.get(gate.rank),
                "Royal Virtue threshold for ${gate.stat} rank ${gate.rank}",
            )
            val proven = totalsBeforeStep.getValue(date to stepIndex).getOrDefault(gate.stat, 0)
            assertTrue(
                proven >= required,
                "${bond.label} rank ${rank.rank} needs ${gate.stat} rank ${gate.rank} ($required points) " +
                    "before its $date walkthrough step, but the authored route only proves a $proven-point lower bound",
            )
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
