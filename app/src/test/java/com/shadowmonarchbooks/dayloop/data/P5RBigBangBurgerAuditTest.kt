package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class P5RBigBangBurgerAuditTest {

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
    fun `p5r completion route keeps the three Royal Big Bang Burger tiers and hidden point rewards`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        data class ExpectedTier(
            val date: String,
            val marker: String,
            val hiddenPoints: Int,
        )

        val tiers = listOf(
            ExpectedTier("2016-05-31", "(1 of 3)", 2),
            ExpectedTier("2017-01-19", "(2 of 3)", 3),
            ExpectedTier("2017-01-23", "(3 of 3)", 5),
        )

        val expectedStats = setOf("knowledge", "guts", "proficiency", "charm")

        tiers.forEach { tier ->
            val step = days.getValue(tier.date).steps.single { "Big Bang" in it.label }
            assertTrue(tier.marker in step.label, "${tier.date} must keep the completion-route tier order explicit")
            assertEquals(expectedStats, step.statGains.keys, "${tier.date} Big Bang success must reward every social stat except Kindness")
            expectedStats.forEach { stat ->
                assertEquals(tier.hiddenPoints, step.statGains.getValue(stat), "${tier.date} $stat hidden-point reward")
            }
            assertFalse("kindness" in step.statGains, "Big Bang Burger Challenge does not raise Kindness")
        }

        assertTrue(days.getValue("2017-01-23").steps.single { "Big Bang" in it.label }.label.contains("challenge is cleared"))

        // These are completion-route choices, not calendar gates. Do not create a
        // universal deadline merely because the route spaces the three clears out.
        val deadlineLabels = loaded.deadlines?.deadlines.orEmpty().map { it.label }
        assertFalse(deadlineLabels.any { it.contains("Big Bang", ignoreCase = true) })
    }
}
