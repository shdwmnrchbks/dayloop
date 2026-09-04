package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorOctoberAuditTest {

    @Test
    fun `Metaphor October route keeps late Follower and completion milestones`() {
        val days = octoberDays()

        fun step(date: String, text: String) = assertNotNull(
            days.getValue(date).steps.firstOrNull { it.label.contains(text, ignoreCase = true) },
            "$date: $text",
        )

        step("2100-10-01", "all possible beetle exchanges")
        step("2100-10-01", "Alonzo").also { assertTrue(it.label.contains("rank 7", ignoreCase = true)) }
        step("2100-10-04", "Basilio").also {
            assertTrue(it.label.contains("rank 8", ignoreCase = true))
            assertEquals(10, it.statGains["tolerance"])
        }
        step("2100-10-04", "Alonzo").also { assertTrue(it.label.contains("rank 8", ignoreCase = true)) }
        step("2100-10-05", "all Followers are now rank 8")
    }

    @Test
    fun `Metaphor October route preserves all four dragon trial clears`() {
        val days = octoberDays()
        val trials = mapOf(
            "2100-10-08" to "Devourer of Nations",
            "2100-10-09" to "Devourer of Stars",
            "2100-10-10" to "Devourer of Flames",
            "2100-10-12" to "Elegy of the Soul",
        )

        trials.forEach { (date, boss) ->
            assertNotNull(
                days.getValue(date).steps.firstOrNull { it.label.contains(boss, ignoreCase = true) },
                "$boss should remain on $date",
            )
        }
    }

    @Test
    fun `Metaphor October route does not compress the coliseum ladder`() {
        val days = octoberDays()
        val expectedRankedMatches = mapOf(
            "2100-10-05" to 1,
            "2100-10-11" to 1,
            "2100-10-13" to 2,
            "2100-10-15" to 1,
            "2100-10-17" to 2,
            "2100-10-18" to 1,
        )

        expectedRankedMatches.forEach { (date, expectedCount) ->
            val count = days.getValue(date).steps.count {
                it.activityRef == "metaphor.activity.coliseum.ranked-league" &&
                    (it.label.contains("Ranked League", ignoreCase = true) || it.label.contains("Hómo Luano", ignoreCase = true))
            }
            assertEquals(expectedCount, count, "$date ranked-league match count")
        }

        val gauntlet = days.getValue("2100-10-15").steps.single {
            it.activityRef == "metaphor.activity.coliseum.gauntlet-challenge"
        }
        assertTrue(gauntlet.label.contains("30 rounds", ignoreCase = true))
    }

    @Test
    fun `Metaphor October endgame keeps optional Star Shatterer branch separate from normal completion`() {
        val finalDay = octoberDays().getValue("2100-10-26")
        assertNotNull(finalDay.steps.firstOrNull { it.label.contains("Star Shatterer", ignoreCase = true) })
        assertNotNull(finalDay.steps.firstOrNull { it.label.contains("normal completion route", ignoreCase = true) })
        assertNotNull(finalDay.steps.firstOrNull { it.label.contains("Destroyer Charadrius", ignoreCase = true) })
    }

    private fun octoberDays() = assertNotNull(
        loadMetaphor().walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2100-10" },
    ).file.days.associateBy { it.date }

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
