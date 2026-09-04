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
    fun `Metaphor Royal Virtue rank markers match the completion route dates`() {
        val expected = mapOf(
            "wisdom" to mapOf(2 to "2100-06-14", 3 to "2100-07-30", 4 to "2100-08-15", 5 to "2100-09-30"),
            "tolerance" to mapOf(2 to "2100-06-21", 3 to "2100-07-27", 4 to "2100-08-29", 5 to "2100-09-09"),
            "imagination" to mapOf(2 to "2100-06-25", 3 to "2100-07-17", 4 to "2100-08-20", 5 to "2100-09-18"),
            "courage" to mapOf(2 to "2100-06-30", 3 to "2100-08-03", 4 to "2100-08-28", 5 to "2100-10-01"),
            "eloquence" to mapOf(2 to "2100-07-10", 3 to "2100-07-16", 4 to "2100-07-27", 5 to "2100-10-02"),
        )
        val marker = Regex(
            "^(Courage|Wisdom|Tolerance|Eloquence|Imagination) reaches rank ([2-5])",
            RegexOption.IGNORE_CASE,
        )
        val actual = mutableMapOf<Pair<String, Int>, String>()

        loadMetaphor().walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .forEach { day ->
                day.steps.forEach { step ->
                    val match = marker.find(step.label) ?: return@forEach
                    val stat = match.groupValues[1].lowercase()
                    val rank = match.groupValues[2].toInt()
                    val key = stat to rank
                    assertEquals(null, actual.put(key, day.date), "$stat rank $rank is marked more than once")
                }
            }

        val expectedFlat = expected.flatMap { (stat, ranks) ->
            ranks.map { (rank, date) -> (stat to rank) to date }
        }.toMap()
        assertEquals(expectedFlat, actual, "Every Royal Virtue rank 2-5 transition should match the audited route date")
    }

    @Test
    fun `Metaphor early Royal Virtue gains keep audited route values`() {
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
        assertEquals(6, gain("2100-06-18", "Chatty Elder", "imagination"))
        assertEquals(6, gain("2100-06-20", "Masked Man", "tolerance"))
        assertEquals(6, gain("2100-06-21", "Activist Woman", "eloquence"))
        assertEquals(10, gain("2100-06-30", "Goborn King", "courage"))
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
