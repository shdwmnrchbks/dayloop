package com.shadowmonarchbooks.dayloop.tools.pack

import java.nio.file.Files
import kotlin.io.path.writeText
import com.shadowmonarchbooks.dayloop.pack.LintIssue
import com.shadowmonarchbooks.dayloop.pack.schema.Activity
import com.shadowmonarchbooks.dayloop.pack.schema.ActivitiesFile
import com.shadowmonarchbooks.dayloop.pack.schema.Bond
import com.shadowmonarchbooks.dayloop.pack.schema.BondsFile
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.DeadlinesFile
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.pack.schema.RankStep
import com.shadowmonarchbooks.dayloop.pack.schema.Step
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PackLintTest {

    private fun tempDir() = Files.createTempDirectory("packlint-test")

    @Test
    fun `valid seed pack produces no errors`() {
        val dir = tempDir()
        Fixture.writePack(dir)
        val issues = PackLint.runOn(dir)
        assertEquals(emptyList(), issues.filter { it.severity == LintIssue.Severity.ERROR }, issues.toString())
    }

    @Test
    fun `wrong weekday against real calendar fails`() {
        val dir = tempDir()
        val wt = Fixture.validWalkthroughApril().copy(
            days = Fixture.validWalkthroughApril().days.map { if (it.date == "2016-04-11") it.copy(weekday = "fri") else it }
        )
        Fixture.writePack(dir, walkthroughs = listOf(wt))
        val errors = PackLint.runOn(dir).errorsIn("walkthrough")
        assertTrue(errors.any { "real calendar says 'mon'" in it.message }, errors.toString())
    }

    @Test
    fun `duplicate day across files fails`() {
        val dir = tempDir()
        Fixture.writePack(dir)
        // 2016-04-09 repeated inside a different month file
        val dup = Fixture.validWalkthroughApril().copy(month = "2016-05", days = listOf(Day("2016-04-09", "sat", "story")))
        Fixture.writeWalkthroughFile(dir, "2016-05.json", dup)
        val errors = PackLint.runOn(dir).errorsIn("walkthrough")
        assertTrue(errors.any { "also defined" in it.message }, errors.toString())
        assertTrue(errors.any { "does not belong in month file 2016-05" in it.message }, errors.toString())
    }

    @Test
    fun `day outside calendar range fails`() {
        val dir = tempDir()
        val wt = Fixture.validWalkthroughApril().copy(
            days = Fixture.validWalkthroughApril().days + Day("2016-05-01", "sun", "free")
        )
        Fixture.writePack(dir, walkthroughs = listOf(wt))
        val errors = PackLint.runOn(dir).errorsIn("walkthrough")
        assertTrue(errors.any { "outside the pack calendar range" in it.message }, errors.toString())
    }

    @Test
    fun `month field mismatch with file name fails`() {
        val dir = tempDir()
        Fixture.writePack(dir)
        // File named 2016-04.json whose month field claims 2016-03.
        Fixture.writeWalkthroughFile(dir, "2016-04.json", Fixture.validWalkthroughApril().copy(month = "2016-03"))
        val errors = PackLint.runOn(dir).errorsIn("walkthrough")
        assertTrue(errors.any { "does not match file name" in it.message }, errors.toString())
    }

    @Test
    fun `unknown stat reference fails`() {
        val dir = tempDir()
        val wt = Fixture.validWalkthroughApril().copy(
            days = Fixture.validWalkthroughApril().days.map {
                if (it.date == "2016-04-12") it.copy(steps = listOf(Step("x", statGains = mapOf("courage" to 1)))) else it
            }
        )
        Fixture.writePack(dir, walkthroughs = listOf(wt))
        val errors = PackLint.runOn(dir).errorsIn("walkthrough")
        assertTrue(errors.any { "unknown stat 'courage'" in it.message }, errors.toString())
    }

    @Test
    fun `unknown activity reference fails`() {
        val dir = tempDir()
        val wt = Fixture.validWalkthroughApril().copy(
            days = Fixture.validWalkthroughApril().days.map {
                if (it.date == "2016-04-10") it.copy(steps = listOf(Step("x", activityRef = "t1.activity.missing"))) else it
            }
        )
        Fixture.writePack(dir, walkthroughs = listOf(wt))
        val errors = PackLint.runOn(dir).errorsIn("walkthrough")
        assertTrue(errors.any { "unknown activity 't1.activity.missing'" in it.message }, errors.toString())
    }

    @Test
    fun `non-increasing bond ranks fail`() {
        val dir = tempDir()
        Fixture.writePack(dir)
        Fixture.writeBonds(
            dir,
            BondsFile(
                bonds = listOf(
                    Bond(
                        id = "t1.bond.fool",
                        label = "Fool",
                        ranks = listOf(
                            RankStep(rank = 1),
                            RankStep(rank = 3),
                            RankStep(rank = 2),
                        ),
                    )
                )
            )
        )
        val errors = PackLint.runOn(dir).errorsIn("confidants.json")
        assertTrue(errors.any { "strictly increase" in it.message }, errors.toString())
    }

    @Test
    fun `id deletion against baseline fails`() {
        val dir = tempDir()
        Fixture.writePack(dir)
        Fixture.writeActivities(dir, ActivitiesFile(activities = listOf(Activity("t1.activity.drink", "Fruit drink", "drink"))))
        // Establish baseline including the activity, then remove the activity.
        val first = PackLint.runOn(dir, writeBaseline = true)
        assertEquals(emptyList(), first.filter { it.severity == LintIssue.Severity.ERROR }, first.toString())
        Files.delete(dir.resolve("activities.json"))
        val errors = PackLint.runOn(dir).errorsIn("pack-ids.baseline.json")
        assertTrue(errors.any { "'t1.activity.drink' present in baseline is gone" in it.message }, errors.toString())
    }

    @Test
    fun `deadline outside calendar fails`() {
        val dir = tempDir()
        Fixture.writePack(dir)
        Fixture.writeDeadlines(
            dir,
            DeadlinesFile(deadlines = listOf(Deadline("t1.deadline.exam", "Exams", "exam", date = "2017-01-01")))
        )
        val errors = PackLint.runOn(dir).errorsIn("deadlines.json")
        assertTrue(errors.any { "outside the calendar range" in it.message }, errors.toString())
    }

    @Test
    fun `coverage warning mentions missing days`() {
        val dir = tempDir()
        // Single-day walkthrough inside a 4-day calendar -> 3 missing days.
        val wt = Fixture.validWalkthroughApril().copy(days = Fixture.validWalkthroughApril().days.take(1))
        Fixture.writePack(dir, walkthroughs = listOf(wt))
        val warnings = PackLint.runOn(dir).filter { it.severity == LintIssue.Severity.WARN }
        assertTrue(warnings.any { "3 day(s) not yet authored" in it.message }, warnings.toString())
    }
}
