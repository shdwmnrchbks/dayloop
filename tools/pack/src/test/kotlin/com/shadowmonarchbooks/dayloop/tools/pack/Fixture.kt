package com.shadowmonarchbooks.dayloop.tools.pack

import java.nio.file.Files
import java.nio.file.Path
import com.shadowmonarchbooks.dayloop.pack.LintIssue
import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Activity
import com.shadowmonarchbooks.dayloop.pack.schema.ActivitiesFile
import com.shadowmonarchbooks.dayloop.pack.schema.AnswersFile
import com.shadowmonarchbooks.dayloop.pack.schema.Bond
import com.shadowmonarchbooks.dayloop.pack.schema.BondsFile
import com.shadowmonarchbooks.dayloop.pack.schema.CalendarRange
import com.shadowmonarchbooks.dayloop.pack.schema.Capabilities
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.DeadlinesFile
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.pack.schema.Labels
import com.shadowmonarchbooks.dayloop.pack.schema.Pack
import com.shadowmonarchbooks.dayloop.pack.schema.PackTheme
import com.shadowmonarchbooks.dayloop.pack.schema.SkinFont
import com.shadowmonarchbooks.dayloop.pack.schema.SkinShapes
import com.shadowmonarchbooks.dayloop.pack.schema.SkinTypography
import com.shadowmonarchbooks.dayloop.pack.schema.Slot
import com.shadowmonarchbooks.dayloop.pack.schema.StatDef
import com.shadowmonarchbooks.dayloop.pack.schema.Step
import com.shadowmonarchbooks.dayloop.pack.schema.WalkthroughFile
import kotlin.io.path.writeBytes
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

    /** Writes a walkthrough month file under a route subdirectory (docs/PLAN.md Phase 5). */
    fun writeRouteWalkthrough(dir: Path, routeId: String, file: WalkthroughFile) {
        val route = dir.resolve("walkthrough").resolve(routeId)
        Files.createDirectories(route)
        route.resolve("${file.month}.json").writeText(PackLoader.json.encodeToString(file))
    }

    fun writeAnswers(dir: Path, answers: AnswersFile) {
        dir.resolve("answers.json").writeText(PackLoader.json.encodeToString(answers))
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

    // ---- Skin DSL fixture (docs/ROADMAP-v3.md Phase 12) ----

    /** 1×1 transparent PNG — a real decodable image for decor/art slots. */
    private const val ONE_PX_PNG_B64 =
        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="

    /**
     * A pack declaring every skin token: all four shape slots, all three font
     * roles, all three decor slots, a motion token, a motif family, and all
     * three sound moments (docs/ROADMAP-v3.md Phase 16).
     */
    fun skinPack() = validPack().copy(
        theme = PackTheme(
            accent = "#A61E22",
            accentDark = "#D9433C",
            style = "vibrant",
            motif = "masks",
            art = mapOf(
                "icon" to "art/icon.png",
                "card" to "art/card.png",
            ),
            shapes = SkinShapes(card = "jagged", chip = "slash", header = "ribbon", frame = "cut"),
            typography = SkinTypography(
                chrome = SkinFont(file = "art/fonts/chrome.otf", case = "upper"),
                display = SkinFont(file = "art/fonts/display.ttf", case = "upper", italic = true, tracking = -0.02),
                title = SkinFont(file = "art/fonts/title.ttf"),
                body = null,
            ),
            decor = mapOf(
                "header" to "art/decor-header.png",
                "panel" to "art/decor-panel.png",
                "divider" to "art/decor-divider.png",
            ),
            motion = "slash",
            sfx = mapOf(
                "tap" to "art/sfx/tap.ogg",
                "advance" to "art/sfx/advance.ogg",
                "complete" to "art/sfx/complete.ogg",
            ),
        ),
    )

    /** Writes every art/font/sfx file the skin pack's theme references. */
    fun writeSkinArt(dir: Path, theme: PackTheme = skinPack().theme!!) {
        val png = java.util.Base64.getDecoder().decode(ONE_PX_PNG_B64)
        val fakeFont = "FAKE-TTF-FOR-LINT-FIXTURE".encodeToByteArray()
        val fakeOgg = "FAKE-OGG-FOR-LINT-FIXTURE".encodeToByteArray()
        (theme.art.values + theme.decor.values).forEach { rel ->
            val target = dir.resolve(rel)
            Files.createDirectories(target.parent)
            target.writeBytes(png)
        }
        theme.typography?.let { typography ->
            listOfNotNull(typography.chrome, typography.display, typography.title, typography.body).forEach { font ->
                val target = dir.resolve(font.file)
                Files.createDirectories(target.parent)
                target.writeBytes(fakeFont)
            }
        }
        theme.sfx.values.forEach { rel ->
            val target = dir.resolve(rel)
            Files.createDirectories(target.parent)
            target.writeBytes(fakeOgg)
        }
    }
}

fun PackLint.runOn(dir: Path, writeBaseline: Boolean = false): List<LintIssue> = run(dir, writeBaseline)

fun List<LintIssue>.errorsIn(locationPart: String): List<LintIssue> =
    filter { it.severity == LintIssue.Severity.ERROR && locationPart in it.location }
