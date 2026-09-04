package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorRoyalVirtueAuditTest {

    @Test
    fun `Metaphor Royal Virtue rank markers cross the documented point thresholds`() {
        val thresholds = mapOf(
            "courage" to mapOf(2 to 30, 3 to 100, 4 to 180, 5 to 240),
            "wisdom" to mapOf(2 to 16, 3 to 80, 4 to 160, 5 to 280),
            "tolerance" to mapOf(2 to 40, 3 to 100, 4 to 160, 5 to 210),
            "eloquence" to mapOf(2 to 40, 3 to 80, 4 to 130, 5 to 170),
            "imagination" to mapOf(2 to 50, 3 to 120, 4 to 200, 5 to 280),
        )
        val marker = Regex(
            "^(Courage|Wisdom|Tolerance|Eloquence|Imagination) reaches rank ([2-5])",
            RegexOption.IGNORE_CASE,
        )
        val totals = thresholds.keys.associateWith { 0 }.toMutableMap()
        val beforeLastGain = thresholds.keys.associateWith { 0 }.toMutableMap()
        val seen = mutableSetOf<Pair<String, Int>>()

        val days = loadMetaphor().walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .sortedBy { it.date }

        days.forEach { day ->
            day.steps.forEach { step ->
                step.statGains.forEach { (stat, gain) ->
                    if (stat in totals) {
                        beforeLastGain[stat] = totals.getValue(stat)
                        totals[stat] = totals.getValue(stat) + gain
                    }
                }

                val match = marker.find(step.label) ?: return@forEach
                val stat = match.groupValues[1].lowercase()
                val rank = match.groupValues[2].toInt()
                val threshold = thresholds.getValue(stat).getValue(rank)
                val key = stat to rank

                assertTrue(seen.add(key), "$stat rank $rank is marked more than once")
                assertTrue(
                    beforeLastGain.getValue(stat) < threshold,
                    "$stat rank $rank should be crossed by the immediately preceding authored gain; " +
                        "already had ${beforeLastGain.getValue(stat)} before the last gain on ${day.date}",
                )
                assertTrue(
                    totals.getValue(stat) >= threshold,
                    "$stat rank $rank needs $threshold points but route data totals only ${totals.getValue(stat)} on ${day.date}",
                )
            }
        }

        val expectedMarkers = thresholds.flatMap { (stat, ranks) -> ranks.keys.map { stat to it } }.toSet()
        assertEquals(expectedMarkers, seen, "Every Royal Virtue rank 2-5 transition should be authored exactly once")
    }

    @Test
    fun `Metaphor June rank two arithmetic uses guide point units`() {
        val june = assertNotNull(
            loadMetaphor().walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2100-06" },
        ).file
        val days = june.days.associateBy { it.date }

        fun gain(date: String, label: String, stat: String): Int {
            val day = assertNotNull(days[date], date)
            val step = assertNotNull(day.steps.firstOrNull { it.label.contains(label, ignoreCase = true) }, "$date: $label")
            return assertNotNull(step.statGains[stat], "$date: $label -> $stat")
        }

        assertEquals(10, gain("2100-06-12", "Breath of Fresh Air", "tolerance"))
        assertEquals(16, gain("2100-06-14", "Young Nidia", "wisdom"))
        assertEquals(10, gain("2100-06-16", "Gupatauros", "courage"))
        assertEquals(10, gain("2100-06-18", "bounty reward", "courage"))
        assertEquals(10, gain("2100-06-30", "Goborn King", "courage"))

        val toleranceToRank2 = listOf(
            gain("2100-06-12", "Breath of Fresh Air", "tolerance"),
            gain("2100-06-13", "Fabienne", "tolerance"),
            gain("2100-06-19", "Fabienne", "tolerance"),
            gain("2100-06-20", "Masked Man", "tolerance"),
            gain("2100-06-21", "Fabienne", "tolerance"),
        ).sum()
        assertEquals(40, toleranceToRank2)
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
