package com.shadowmonarchbooks.dayloop.tools.pack

import java.nio.file.Files
import kotlin.io.path.writeText
import com.shadowmonarchbooks.dayloop.pack.LintIssue
import com.shadowmonarchbooks.dayloop.pack.schema.Activity
import com.shadowmonarchbooks.dayloop.pack.schema.ActivitiesFile
import com.shadowmonarchbooks.dayloop.pack.schema.AnswerSheet
import com.shadowmonarchbooks.dayloop.pack.schema.AnswersFile
import com.shadowmonarchbooks.dayloop.pack.schema.Bond
import com.shadowmonarchbooks.dayloop.pack.schema.BondsFile
import com.shadowmonarchbooks.dayloop.pack.schema.Capabilities
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.DeadlinesFile
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.pack.schema.Labels
import com.shadowmonarchbooks.dayloop.pack.schema.PackTheme
import com.shadowmonarchbooks.dayloop.pack.schema.RankStep
import com.shadowmonarchbooks.dayloop.pack.schema.RouteDef
import com.shadowmonarchbooks.dayloop.pack.schema.Step
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PackLintTest {

    private fun tempDir() = Files.createTempDirectory("packlint-test")

    /** A minimal dayCounter pack with a foreign in-game weekday cycle. */
    private fun dayCounterPack() = Fixture.validPack().copy(
        timeModel = "dayCounter",
        calendar = com.shadowmonarchbooks.dayloop.pack.schema.CalendarRange(
            startDate = "2100-06-02",
            endDate = "2100-06-30",
            monthLengths = listOf(30),
            weekdayCycle = listOf("metalsday", "idlesday", "flamesday", "watersday", "arboursday"),
            weekdayAnchor = com.shadowmonarchbooks.dayloop.pack.schema.WeekdayAnchor("2100-06-02", "watersday"),
        ),
    )

    private fun dayCounterWalkthrough(): com.shadowmonarchbooks.dayloop.pack.schema.WalkthroughFile {
        val cycle = listOf("metalsday", "idlesday", "flamesday", "watersday", "arboursday")
        // Cycle position of day d: June 2 = watersday (index 3).
        fun weekdayOf(d: Int): String {
            val anchor = 3
            return cycle[(anchor + (d - 2) % 5 + 5) % 5]
        }
        return com.shadowmonarchbooks.dayloop.pack.schema.WalkthroughFile(
            month = "2100-06",
            days = (2..30).map { d -> Day("2100-06-%02d".format(d), weekdayOf(d), "free") },
        )
    }

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
    fun `dayCounter pack with consistent cycle weekdays passes`() {
        val dir = tempDir()
        Fixture.writePack(dir, pack = dayCounterPack(), walkthroughs = listOf(dayCounterWalkthrough()))
        val issues = PackLint.runOn(dir)
        assertEquals(emptyList(), issues.filter { it.severity == LintIssue.Severity.ERROR }, issues.toString())
    }

    @Test
    fun `dayCounter day whose weekday breaks the cycle fails`() {
        val dir = tempDir()
        val wt = dayCounterWalkthrough().copy(
            days = dayCounterWalkthrough().days.map { if (it.date == "2100-06-03") it.copy(weekday = "metalsday") else it }
        )
        Fixture.writePack(dir, pack = dayCounterPack(), walkthroughs = listOf(wt))
        val errors = PackLint.runOn(dir).errorsIn("walkthrough")
        assertTrue(errors.any { "weekday cycle says 'arboursday'" in it.message }, errors.toString())
    }

    @Test
    fun `monthLengths on a weekdayGrid pack fails`() {
        val dir = tempDir()
        val pack = Fixture.validPack().copy(
            calendar = Fixture.validPack().calendar.copy(monthLengths = listOf(30)),
        )
        Fixture.writePack(dir, pack = pack)
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "must not declare game-month lengths" in it.message }, errors.toString())
    }

    @Test
    fun `duplicate day across files fails`() {
        val dir = tempDir()
        Fixture.writePack(dir)
        // 2016-04-09 repeated inside a different month file
        val dup = Fixture.validWalkthroughApril().copy(month = "2016-05", days = listOf(Day("2016-04-09", "sat", "story")))
        Fixture.writeWalkthroughFile(dir, "2016-05.json", dup)
        val errors = PackLint.runOn(dir).errorsIn("walkthrough")
        assertTrue(errors.any { "defined twice in route 'standard'" in it.message }, errors.toString())
        assertTrue(errors.any { "does not belong in month file 2016-05" in it.message }, errors.toString())
    }

    @Test
    fun `duplicate days are scoped per route`() {
        val dir = tempDir()
        val routes = listOf(RouteDef("standard", "Standard"), RouteDef("casual", "Casual"))
        Fixture.writePack(dir, pack = Fixture.validPack().copy(routes = routes))
        Fixture.writeRouteWalkthrough(dir, "casual", Fixture.validWalkthroughApril())
        val issues = PackLint.runOn(dir)
        assertEquals(emptyList(), issues.filter { it.severity == LintIssue.Severity.ERROR }, issues.toString())
    }

    @Test
    fun `walkthrough subdirectory must be a declared route`() {
        val dir = tempDir()
        Fixture.writePack(dir)
        Fixture.writeRouteWalkthrough(dir, "casual", Fixture.validWalkthroughApril())
        val errors = PackLint.runOn(dir).errorsIn("walkthrough/casual")
        assertTrue(errors.any { "not a declared route" in it.message }, errors.toString())
    }

    @Test
    fun `declared route without walkthrough files fails`() {
        val dir = tempDir()
        val routes = listOf(RouteDef("standard", "Standard"), RouteDef("casual", "Casual"))
        Fixture.writePack(dir, pack = Fixture.validPack().copy(routes = routes))
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "route 'casual' is declared but has no walkthrough" in it.message }, errors.toString())
    }

    @Test
    fun `answer sheets aligned with authored days pass`() {
        val dir = tempDir()
        // Make 2016-04-11 an exam day so both sheet kinds have a home.
        val wt = Fixture.validWalkthroughApril().copy(
            days = Fixture.validWalkthroughApril().days.map {
                if (it.date == "2016-04-11") it.copy(dayKind = "exam") else it
            }
        )
        // The pack ships answer sheets, so it must declare the capability.
        Fixture.writePack(
            dir,
            pack = Fixture.validPack().copy(capabilities = Capabilities(exams = true, answers = true)),
            walkthroughs = listOf(wt),
        )
        Fixture.writeDeadlines(
            dir,
            DeadlinesFile(deadlines = listOf(Deadline("t1.deadline.exam", "Exams", "exam", date = "2016-04-12"))),
        )
        Fixture.writeAnswers(
            dir,
            AnswersFile(
                answers = listOf(
                    AnswerSheet("t1.answers.class.2016-04-12", "2016-04-12", "classQuestion", "Class question", listOf("Four")),
                    AnswerSheet("t1.answers.exam.2016-04-11", "2016-04-11", "exam", "Exams", listOf("a"), deadlineRef = "t1.deadline.exam"),
                ),
            ),
        )
        val issues = PackLint.runOn(dir)
        assertEquals(emptyList(), issues.filter { it.severity == LintIssue.Severity.ERROR }, issues.toString())
    }

    @Test
    fun `misaligned answer sheets fail`() {
        val dir = tempDir()
        Fixture.writePack(dir, pack = Fixture.validPack().copy(capabilities = Capabilities(exams = true, answers = true)))
        Fixture.writeAnswers(
            dir,
            AnswersFile(
                answers = listOf(
                    // 2016-04-10 is a free day: fine for a class question, but not
                    // for an exam sheet, and the deadline ref does not exist.
                    AnswerSheet("t1.answers.class.2016-04-10", "2016-04-10", "classQuestion", "Class question", listOf("x")),
                    AnswerSheet("t1.answers.exam.2016-04-10", "2016-04-10", "exam", "Exams", listOf("y"), deadlineRef = "t1.deadline.missing"),
                ),
            ),
        )
        val errors = PackLint.runOn(dir).errorsIn("answers.json")
        assertTrue(errors.none { "2016-04-10' has no authored day" in it.message }, errors.toString())
        assertTrue(errors.any { "not an authored exam day" in it.message }, errors.toString())
        assertTrue(errors.any { "references unknown deadline 't1.deadline.missing'" in it.message }, errors.toString())
    }

    @Test
    fun `answers capability without answers file fails`() {
        val dir = tempDir()
        Fixture.writePack(
            dir,
            pack = Fixture.validPack().copy(capabilities = Capabilities(exams = true, answers = true)),
        )
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "capabilities.answers is true but answers.json" in it.message }, errors.toString())
    }

    @Test
    fun `answers file without the capability fails`() {
        val dir = tempDir()
        // validPack() declares no answers capability, so shipping sheets fails.
        Fixture.writePack(dir)
        Fixture.writeAnswers(
            dir,
            AnswersFile(answers = listOf(AnswerSheet("t1.answers.exam.2016-04-11", "2016-04-11", "exam", "Exams", listOf("a")))),
        )
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "ships answers.json but does not declare capabilities.answers" in it.message }, errors.toString())
    }

    @Test
    fun `empty answers file does not satisfy the capability`() {
        val dir = tempDir()
        Fixture.writePack(
            dir,
            pack = Fixture.validPack().copy(capabilities = Capabilities(exams = true, answers = true)),
        )
        Fixture.writeAnswers(dir, AnswersFile(answers = emptyList()))
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "capabilities.answers is true but answers.json" in it.message }, errors.toString())
    }

    @Test
    fun `day outside calendar range fails`() {
        val dir = tempDir()
        val wt = Fixture.validWalkthroughApril().copy(
            days = Fixture.validWalkthroughApril().days + Day("2016-05-01", "sun", "free")
        )
        Fixture.writePack(dir, walkthroughs = listOf(wt))
        val errors = PackLint.runOn(dir).errorsIn("walkthrough")
        assertTrue(errors.any { "not a date in this pack's calendar" in it.message }, errors.toString())
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
        assertTrue(errors.any { "not a date in this pack's calendar" in it.message }, errors.toString())
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

    // ---- Theme & vocabulary (docs/ROADMAP-v2.md Phase 10) ----

    @Test
    fun `valid theme block passes`() {
        val dir = tempDir()
        val theme = PackTheme(
            accent = "#A61E22",
            accentDark = "#D9433C",
            style = "vibrant",
            motif = "masks",
            art = mapOf("card" to "art/card.png"),
        )
        Fixture.writePack(dir, pack = Fixture.validPack().copy(theme = theme))
        Files.createDirectories(dir.resolve("art"))
        Files.write(dir.resolve("art/card.png"), byteArrayOf())
        val issues = PackLint.runOn(dir)
        assertEquals(emptyList(), issues.filter { it.severity == LintIssue.Severity.ERROR }, issues.toString())
    }

    @Test
    fun `malformed theme color fails`() {
        val dir = tempDir()
        val theme = PackTheme(accent = "red")
        Fixture.writePack(dir, pack = Fixture.validPack().copy(theme = theme))
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "theme color 'red' is not #RRGGBB or #AARRGGBB" in it.message }, errors.toString())
    }

    @Test
    fun `unknown theme style token fails`() {
        val dir = tempDir()
        val theme = PackTheme(accent = "#A61E22", style = "neon")
        Fixture.writePack(dir, pack = Fixture.validPack().copy(theme = theme))
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "theme.style 'neon'" in it.message }, errors.toString())
    }

    @Test
    fun `theme art slot pointing at a missing file fails`() {
        val dir = tempDir()
        val theme = PackTheme(art = mapOf("card" to "art/missing.png"))
        Fixture.writePack(dir, pack = Fixture.validPack().copy(theme = theme))
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "theme.art['card'] file not found: art/missing.png" in it.message }, errors.toString())
    }

    @Test
    fun `theme art slot escaping the pack dir fails`() {
        val dir = tempDir()
        val theme = PackTheme(art = mapOf("card" to "../other-pack/art/card.png"))
        Fixture.writePack(dir, pack = Fixture.validPack().copy(theme = theme))
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "theme.art['card'] must be a pack-relative path" in it.message }, errors.toString())
    }

    @Test
    fun `theme art slot with a non-image extension fails`() {
        val dir = tempDir()
        val theme = PackTheme(art = mapOf("card" to "art/card.gif"))
        Fixture.writePack(dir, pack = Fixture.validPack().copy(theme = theme))
        Files.createDirectories(dir.resolve("art"))
        Files.write(dir.resolve("art/card.gif"), byteArrayOf())
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "theme.art['card'] must be a" in it.message }, errors.toString())
    }

    @Test
    fun `unknown deadline kind label key fails`() {
        val dir = tempDir()
        val labels = Labels(bond = "Bond", stat = "Stat", deadlineKinds = mapOf("dungeon" to "Dungeon"))
        Fixture.writePack(dir, pack = Fixture.validPack().copy(labels = labels))
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "labels.deadlineKinds key 'dungeon'" in it.message }, errors.toString())
    }

    @Test
    fun `blank deadline kind label fails`() {
        val dir = tempDir()
        val labels = Labels(bond = "Bond", stat = "Stat", deadlineKinds = mapOf("palace" to " "))
        Fixture.writePack(dir, pack = Fixture.validPack().copy(labels = labels))
        val errors = PackLint.runOn(dir).errorsIn("pack.json")
        assertTrue(errors.any { "labels.deadlineKinds['palace'] needs a non-blank display label" in it.message }, errors.toString())
    }
}
