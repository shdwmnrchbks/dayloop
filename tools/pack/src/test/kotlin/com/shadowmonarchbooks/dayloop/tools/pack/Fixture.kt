package com.shadowmonarchbooks.dayloop.tools.pack

import java.nio.file.Files
import java.nio.file.Path
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Activity
import com.shadowmonarchbooks.dayloop.tools.pack.schema.ActivitiesFile
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Bond
import com.shadowmonarchbooks.dayloop.tools.pack.schema.BondsFile
import com.shadowmonarchbooks.dayloop.tools.pack.schema.CalendarRange
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Capabilities
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.tools.pack.schema.DeadlinesFile
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Day
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Labels
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Pack
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Slot
import com.shadowmonarchbooks.dayloop.tools.pack.schema.StatDef
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Step
import com.shadowmonarchbooks.dayloop.tools.pack.schema.WalkthroughFile
import kotlin.io.path.writeText
import kotlinx.serialization.encodeToString

/** Builds an on-disk minimal-but-valid pack for rule tests. */
object Fixture {

    fun validPack() = Pack(
        packId = "t1",
        title = "Test Game",
        contentVersion = 1,
        timeModel = "weekdayGrid",
        calendar = CalendarRange(startDate = "2016-04-09", endDate = "2016-04-12"),
        slots = listOf(Slot("afternoon", "Afternoon"), Slot("evening", "Evening")),
        stats = listOf(StatDef("knowledge", "Knowledge"), StatDef("charm", "Charm")),
        capabilities = Capabilities(exams = true),
        labels = Labels(bond = "Confidant", stat = "Social Stat"),
    )

    fun validWalkthroughApril() = WalkthroughFile(
        month = "2016-04",
        days = listOf(
            Day("2016-04-09", "sat", "story", steps = listOf(Step("Arrive in town"))),
            Day("2016-04-10", "sun", "free", steps = listOf(Step("Flip the sign"))),
            Day("2016-04-11", "mon", "story", steps = listOf(Step("First infiltration"))),
            Day("2016-04-12", "tue", "school", steps = listOf(Step("Answer the class question", statGains = mapOf("knowledge" to 1)))),
        ),
    )

    fun writePack(dir: Path, pack: Pack = validPack(), walkthroughs: List<WalkthroughFile> = listOf(validWalkthroughApril())) {
        Files.createDirectories(dir)
        dir.resolve("pack.json").writeText(PackLoader.json.encodeToString(pack))
        val wt = dir.resolve("walkthrough")
        Files.createDirectories(wt)
        walkthroughs.forEach { wt.resolve("${it.month}.json").writeText(PackLoader.json.encodeToString(it)) }
    }

    /** Writes a walkthrough file under an explicit file name (for mismatch tests). */
    fun writeWalkthroughFile(dir: Path, fileName: String, file: WalkthroughFile) {
        val wt = dir.resolve("walkthrough")
        Files.createDirectories(wt)
        wt.resolve(fileName).writeText(PackLoader.json.encodeToString(file))
    }

    fun writeBonds(dir: Path, bonds: BondsFile) {
        dir.resolve("confidants.json").writeText(PackLoader.json.encodeToString(bonds))
    }

    fun writeActivities(dir: Path, activities: ActivitiesFile) {
        dir.resolve("activities.json").writeText(PackLoader.json.encodeToString(activities))
    }

    fun writeDeadlines(dir: Path, deadlines: DeadlinesFile) {
        dir.resolve("deadlines.json").writeText(PackLoader.json.encodeToString(deadlines))
    }
}

fun PackLint.runOn(dir: Path, writeBaseline: Boolean = false): List<LintIssue> = run(dir, writeBaseline)

fun List<LintIssue>.errorsIn(locationPart: String): List<LintIssue> =
    filter { it.severity == LintIssue.Severity.ERROR && locationPart in it.location }
