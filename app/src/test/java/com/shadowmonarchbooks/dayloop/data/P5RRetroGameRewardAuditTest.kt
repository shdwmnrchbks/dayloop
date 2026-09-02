package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class P5RRetroGameRewardAuditTest {

    private fun contentPacksDir(): Path? {
        var dir: Path? = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        repeat(5) {
            val candidate = dir?.resolve("content")?.resolve("packs")
            if (candidate != null && candidate.isDirectory()) return candidate
            dir = dir?.parent
        }
        return null
    }

    @Test
    fun `p5r route names the audited Royal retro-game badge rewards at the correct completion milestones`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
        val allDays = loaded.walkthroughs.flatMap { it.file.days }
        val days = allDays.associateBy { it.date }

        fun label(date: String, marker: String): String =
            days.getValue(date).steps.single { marker in it.label }.label

        val expectedSessions = mapOf(
            "p5r.activity.videoGame.star-forneus" to 3,
            "p5r.activity.videoGame.gambla-goemon" to 2,
            "p5r.activity.videoGame.featherman-seeker" to 3,
            "p5r.activity.videoGame.punch-ouch" to 3,
            "p5r.activity.videoGame.train-of-life" to 3,
            "p5r.activity.videoGame.power-intuition" to 3,
            "p5r.activity.videoGame.golfer-sarutahiko" to 3,
        )
        expectedSessions.forEach { (activityRef, sessions) ->
            val actual = allDays.sumOf { day -> day.steps.count { it.activityRef == activityRef } }
            assertEquals(sessions, actual, "$activityRef must keep the Royal number of successful sessions needed for its completion card")
        }

        val fiveGameClaim = label("2017-01-23", "five completed retro games")
        listOf(
            "Forneus Badge",
            "Gambla Badge",
            "Featherman Badge",
            "Punch Badge",
            "Train Badge",
        ).forEach { badge ->
            assertTrue(badge in fiveGameClaim, "Jan 23 completed-game redemption should identify $badge")
        }

        val powerCompletion = label("2017-01-26", "Power Intuition (6/7)")
        assertTrue("Power Intuition" in powerCompletion)
        val powerReward = label("2017-01-28", "PI Badge")
        assertTrue("Power Intuition" in powerReward)
        assertFalse(powerReward.contains("final retro-game accessory", ignoreCase = true))

        val golferCompletion = label("2017-01-30", "Golfer Sarutahiko (7/7)")
        assertTrue("Golfer Sarutahiko" in golferCompletion)
        val golferReward = label("2017-01-31", "Golfer Badge")
        assertTrue("Golfer Sarutahiko" in golferReward)

        // The route's numbered completion milestones must remain ordered: Train is
        // the fifth completed game, Power Intuition the sixth, and Golfer the seventh.
        assertTrue("(5/7)" in label("2017-01-21", "Train of Life"))
        assertTrue("(6/7)" in powerCompletion)
        assertTrue("(7/7)" in golferCompletion)
    }
}
