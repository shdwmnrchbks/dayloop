package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.Cal
import com.shadowmonarchbooks.dayloop.pack.LintIssue
import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.BondRankGte
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import com.shadowmonarchbooks.dayloop.pack.schema.StatGte
import com.shadowmonarchbooks.dayloop.pack.schema.Weekdays
import com.shadowmonarchbooks.dayloop.pack.walkConditions
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
data class IdBaseline(
    val bonds: List<String>,
    val activities: List<String>,
    val deadlines: List<String>,
    // Older baselines predate answer sheets; treat them as empty (additions only).
    val answers: List<String> = emptyList(),
)

/**
 * Structural validation of a pack directory.
 *
 * Rules (docs/PLAN.md §2/§3.6):
 *  - calendar validity: every walkthrough day exists on the real calendar and
 *    its declared weekday matches reality (weekdayGrid packs)
 *  - no duplicate/out-of-range days; walkthrough month matches file name
 *  - routes: declared ids are slugs, walkthrough subdirectories match
 *    declarations, duplicate days are scoped per route
 *  - cross-references resolve: stats, slots, activity refs, bond refs
 *  - bonds: ranks strictly increasing, availability windows valid
 *  - deadlines: date or window present, inside the pack calendar
 *  - answer sheets: dates/kinds align with authored days (docs/PLAN.md Phase 5)
 *  - ID immutability: any ID present in pack-ids.baseline.json must still exist
 *    (deletions/renames fail lint; additions are fine)
 */
object PackLint {

    private val ACTIVITY_KINDS = setOf("book", "dvd", "videoGame", "drink", "shop", "hangout", "exam", "other")
    private val DEADLINE_KINDS = setOf("palace", "exam", "missable", "request", "other")
    private val DAY_KINDS = setOf("free", "school", "story", "exam", "forced")
    private val TIME_MODELS = setOf("weekdayGrid", "dayCounter")
    private val ANSWER_KINDS = setOf("exam", "classQuestion")

    fun run(packDir: Path, writeBaseline: Boolean): List<LintIssue> {
        val issues = mutableListOf<LintIssue>()
        if (!Files.isDirectory(packDir)) {
            return listOf(LintIssue(LintIssue.Severity.ERROR, "pack", "pack directory not found: $packDir"))
        }
        val loaded = PackLoader.load(packDir)
        issues += loaded.parseIssues
        val pack = loaded.pack ?: return issues

        val packId = pack.packId
        if (!Regex("^[a-z0-9]+(-[a-z0-9]+)*$").matches(packId)) {
            issues += err("pack.json", "packId '$packId' is not a lowercase slug")
        }
        if (pack.contentVersion < 1) issues += err("pack.json", "contentVersion must be >= 1")
        if (pack.timeModel !in TIME_MODELS) issues += err("pack.json", "unknown timeModel '${pack.timeModel}'")

        // Calendar
        val start = Cal.parseDate(pack.calendar.startDate)
        val end = Cal.parseDate(pack.calendar.endDate)
        if (start == null) issues += err("pack.json", "calendar.startDate is not ISO: '${pack.calendar.startDate}'")
        if (end == null) issues += err("pack.json", "calendar.endDate is not ISO: '${pack.calendar.endDate}'")
        if (start != null && end != null && end < start) {
            issues += err("pack.json", "calendar.endDate precedes startDate")
        }
        val nonPlayable = mutableSetOf<String>()
        pack.calendar.nonPlayableDates.forEach { d ->
            val parsed = Cal.parseDate(d)
            if (parsed == null) issues += err("pack.json", "nonPlayableDates entry is not ISO: '$d'")
            else if (start != null && end != null && (parsed < start || parsed > end)) {
                issues += err("pack.json", "nonPlayableDates entry '$d' is outside the calendar range")
            } else if (!nonPlayable.add(d)) {
                issues += err("pack.json", "nonPlayableDates entry '$d' duplicated")
            }
        }

        // Slots & stats
        val slotIds = pack.slots.map { it.id }
        if (pack.slots.isEmpty()) issues += err("pack.json", "slots must not be empty")
        slotIds.groupingBy { it }.eachCount().filterValues { it > 1 }.forEach { (id, n) ->
            issues += err("pack.json", "slot id '$id' declared $n times")
        }
        val statIds = pack.stats.map { it.id }
        if (pack.stats.isEmpty()) issues += err("pack.json", "stats must not be empty")
        statIds.groupingBy { it }.eachCount().filterValues { it > 1 }.forEach { (id, n) ->
            issues += err("pack.json", "stat id '$id' declared $n times")
        }

        // Routes (docs/PLAN.md Phase 5)
        val declaredRouteIds = pack.routes.map { it.id }
        declaredRouteIds.groupingBy { it }.eachCount().filterValues { it > 1 }.forEach { (id, n) ->
            issues += err("pack.json", "route id '$id' declared $n times")
        }
        pack.routes.forEach { route ->
            if (!Regex("^[a-z0-9]+(-[a-z0-9]+)*$").matches(route.id)) {
                issues += err("pack.json", "route id '${route.id}' is not a lowercase slug")
            }
            if (route.label.isBlank()) {
                issues += err("pack.json", "route '${route.id}' needs a display label")
            }
        }

        // Activities
        val activityIds = mutableSetOf<String>()
        loaded.activities?.activities?.forEach { a ->
            if (!activityIds.add(a.id)) issues += err("activities.json", "duplicate activity id '${a.id}'")
            if (!a.id.startsWith("$packId.activity.")) issues += err("activities.json", "activity id '${a.id}' must be prefixed '$packId.activity.'")
            if (a.kind !in ACTIVITY_KINDS) issues += err("activities.json", "activity '${a.id}' has unknown kind '${a.kind}'")
            a.statGains.forEach { (stat, _) ->
                if (stat !in statIds) issues += err("activities.json", "activity '${a.id}' references unknown stat '$stat'")
            }
        }

        // Bonds
        val bondIds = mutableSetOf<String>()
        loaded.bonds?.bonds?.forEach { b ->
            if (!bondIds.add(b.id)) issues += err("confidants.json", "duplicate bond id '${b.id}'")
            if (!b.id.startsWith("$packId.bond.")) issues += err("confidants.json", "bond id '${b.id}' must be prefixed '$packId.bond.'")
            var previous = 0
            b.ranks.forEach { step ->
                if (step.rank <= previous) {
                    issues += err("confidants.json", "bond '${b.id}' rank steps must strictly increase (found ${step.rank} after $previous)")
                }
                previous = step.rank
                listOfNotNull(step.availableFrom, step.availableUntil).forEach { d ->
                    val parsed = Cal.parseDate(d)
                    if (parsed == null) issues += err("confidants.json", "bond '${b.id}' rank ${step.rank} has non-ISO availability '$d'")
                    else if (start != null && end != null && (parsed < start || parsed > end)) {
                        issues += err("confidants.json", "bond '${b.id}' rank ${step.rank} availability '$d' is outside the calendar range")
                    }
                }
                val from = step.availableFrom?.let { Cal.parseDate(it) }
                val until = step.availableUntil?.let { Cal.parseDate(it) }
                if (from != null && until != null && until < from) {
                    issues += err("confidants.json", "bond '${b.id}' rank ${step.rank} availability window ends before it starts")
                }
                step.gates?.let { g ->
                    walkConditions(g) { leaf ->
                        when (leaf) {
                            is StatGte -> if (leaf.stat !in statIds) issues += err("confidants.json", "bond '${b.id}' rank ${step.rank} gate references unknown stat '${leaf.stat}'")
                            is BondRankGte -> if (leaf.bond !in bondIds) issues += err("confidants.json", "bond '${b.id}' rank ${step.rank} gate references unknown bond '${leaf.bond}'")
                            is Weekdays -> leaf.value.forEach { wd ->
                                if (wd !in Cal.WEEKDAYS) issues += err("confidants.json", "bond '${b.id}' rank ${step.rank} gate has invalid weekday '$wd'")
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }

        // Deadlines
        val deadlineIds = mutableSetOf<String>()
        loaded.deadlines?.deadlines?.forEach { d ->
            if (!deadlineIds.add(d.id)) issues += err("deadlines.json", "duplicate deadline id '${d.id}'")
            if (!d.id.startsWith("$packId.deadline.")) issues += err("deadlines.json", "deadline id '${d.id}' must be prefixed '$packId.deadline.'")
            if (d.kind !in DEADLINE_KINDS) issues += err("deadlines.json", "deadline '${d.id}' has unknown kind '${d.kind}'")
            if (d.date == null && d.window == null) {
                issues += err("deadlines.json", "deadline '${d.id}' needs a date or a window")
            }
            listOfNotNull(d.date, d.window?.start, d.window?.end).forEach { iso ->
                val parsed = Cal.parseDate(iso)
                if (parsed == null) issues += err("deadlines.json", "deadline '${d.id}' has non-ISO date '$iso'")
                else if (start != null && end != null && (parsed < start || parsed > end)) {
                    issues += err("deadlines.json", "deadline '${d.id}' date '$iso' is outside the calendar range")
                }
            }
            val ws = d.window?.start?.let { Cal.parseDate(it) }
            val we = d.window?.end?.let { Cal.parseDate(it) }
            if (ws != null && we != null && we < ws) {
                issues += err("deadlines.json", "deadline '${d.id}' window ends before it starts")
            }
        }

        // Walkthrough — validated per route; day uniqueness is scoped to a route
        val seenDatesByRoute = mutableMapOf<String, MutableSet<String>>()
        val routeDirsSeen = mutableSetOf<String>()
        val dayKindsByDate = mutableMapOf<String, MutableSet<String>>()
        loaded.walkthroughs.forEach { wt ->
            val loc = wt.location
            if (wt.routeId != Routes.DEFAULT) routeDirsSeen += wt.routeId
            if (!Regex("^\\d{4}-\\d{2}$").matches(wt.month)) {
                issues += err(loc, "file name must be YYYY-MM")
            }
            if (wt.file.month != wt.month) {
                issues += err(loc, "month field '${wt.file.month}' does not match file name")
            }
            val seen = seenDatesByRoute.getOrPut(wt.routeId) { mutableSetOf() }
            wt.file.days.forEach { day ->
                val parsed = Cal.parseDate(day.date)
                if (parsed == null) {
                    issues += err(loc, "day '${day.date}' is not an ISO date")
                    return@forEach
                }
                if (day.weekday !in Cal.WEEKDAYS) {
                    issues += err(loc, "day '${day.date}' has invalid weekday '${day.weekday}'")
                } else if (pack.timeModel == "weekdayGrid") {
                    val real = Cal.weekdayOf(day.date)
                    if (real != null && real != day.weekday) {
                        issues += err(loc, "day '${day.date}' claims weekday '${day.weekday}' but the real calendar says '$real'")
                    }
                }
                if (Regex("^\\d{4}-\\d{2}$").matches(wt.month) && !day.date.startsWith("${wt.month}-")) {
                    issues += err(loc, "day '${day.date}' does not belong in month file ${wt.month}")
                }
                if (start != null && end != null && (parsed < start || parsed > end)) {
                    issues += err(loc, "day '${day.date}' is outside the pack calendar range")
                }
                if (!seen.add(day.date)) {
                    issues += err(loc, "day '${day.date}' is defined twice in route '${wt.routeId}'")
                }
                if (day.dayKind !in DAY_KINDS) issues += err(loc, "day '${day.date}' has unknown dayKind '${day.dayKind}'")
                dayKindsByDate.getOrPut(day.date) { mutableSetOf() }.add(day.dayKind)
                day.steps.forEachIndexed { i, step ->
                    if (step.slot != null && step.slot !in slotIds) {
                        issues += err(loc, "day '${day.date}' step $i references unknown slot '${step.slot}'")
                    }
                    if (step.activityRef != null && step.activityRef !in activityIds) {
                        issues += err(loc, "day '${day.date}' step $i references unknown activity '${step.activityRef}'")
                    }
                    step.statGains.forEach { (stat, _) ->
                        if (stat !in statIds) issues += err(loc, "day '${day.date}' step $i references unknown stat '$stat'")
                    }
                }
            }
        }

        // Route directory ↔ declaration consistency
        routeDirsSeen.forEach { dir ->
            if (dir == Routes.DEFAULT) {
                issues += err("walkthrough/${Routes.DEFAULT}", "default route files live at walkthrough/ top level; the '$dir' subdirectory is reserved")
            } else if (dir !in declaredRouteIds) {
                issues += err("walkthrough/$dir", "walkthrough subdirectory '$dir' is not a declared route in pack.json")
            }
        }
        declaredRouteIds.forEach { id ->
            if (id != Routes.DEFAULT && id !in routeDirsSeen) {
                issues += err("pack.json", "route '$id' is declared but has no walkthrough/$id/ files")
            }
        }

        // Answer sheets (docs/PLAN.md Phase 5)
        val answerIds = mutableSetOf<String>()
        loaded.answers?.answers?.forEach { sheet ->
            if (!answerIds.add(sheet.id)) issues += err("answers.json", "duplicate answer sheet id '${sheet.id}'")
            if (!sheet.id.startsWith("$packId.answers.")) {
                issues += err("answers.json", "answer sheet id '${sheet.id}' must be prefixed '$packId.answers.'")
            }
            if (sheet.kind !in ANSWER_KINDS) {
                issues += err("answers.json", "answer sheet '${sheet.id}' has unknown kind '${sheet.kind}'")
            }
            if (sheet.answers.isEmpty()) {
                issues += err("answers.json", "answer sheet '${sheet.id}' has no answers")
            }
            val parsed = Cal.parseDate(sheet.date)
            when {
                parsed == null -> issues += err("answers.json", "answer sheet '${sheet.id}' has non-ISO date '${sheet.date}'")
                start != null && end != null && (parsed < start || parsed > end) ->
                    issues += err("answers.json", "answer sheet '${sheet.id}' date '${sheet.date}' is outside the calendar range")
                sheet.date !in dayKindsByDate ->
                    issues += err("answers.json", "answer sheet '${sheet.id}' date '${sheet.date}' has no authored day")
                // Exams are always authored as exam days; class questions may also
                // land on days tagged free (e.g. Saturday-class weeks), so only
                // the exam kind is cross-checked.
                sheet.kind == "exam" && "exam" !in dayKindsByDate[sheet.date].orEmpty() ->
                    issues += err("answers.json", "answer sheet '${sheet.id}' is an exam sheet but ${sheet.date} is not an authored exam day")
            }
            sheet.deadlineRef?.let { ref ->
                if (ref !in deadlineIds) {
                    issues += err("answers.json", "answer sheet '${sheet.id}' references unknown deadline '$ref'")
                }
            }
        }

        // Coverage (warn — packs grow incrementally), computed per route
        if (start != null && end != null) {
            val expected = Cal.datesBetween(pack.calendar.startDate, pack.calendar.endDate).map { it.toString() } - nonPlayable
            Routes.effective(pack).forEach { route ->
                val seen = seenDatesByRoute[route.id].orEmpty()
                val missing = expected.filter { it !in seen }
                val tag = if (pack.routes.isEmpty()) "" else "[route ${route.id}] "
                missing.groupBy { it.substring(0, 7) }.toSortedMap().forEach { (m, dates) ->
                    issues += LintIssue(
                        LintIssue.Severity.WARN,
                        "coverage",
                        "${tag}month $m: ${dates.size} day(s) not yet authored (first: ${dates.take(3).joinToString()})"
                    )
                }
            }
        }

        // ID immutability baseline
        val baselineFile = packDir.resolve("pack-ids.baseline.json")
        val current = IdBaseline(
            bonds = bondIds.toList().sorted(),
            activities = activityIds.toList().sorted(),
            deadlines = deadlineIds.toList().sorted(),
            answers = answerIds.toList().sorted(),
        )
        if (writeBaseline) {
            baselineFile.writeText(PackLoader.json.encodeToString(current))
            issues += LintIssue(LintIssue.Severity.WARN, "baseline", "wrote ${baselineFile.fileName} (${current.bonds.size} bonds, ${current.activities.size} activities, ${current.deadlines.size} deadlines, ${current.answers.size} answer sheets)")
        } else if (baselineFile.isRegularFile()) {
            val baseline = try {
                PackLoader.json.decodeFromString(IdBaseline.serializer(), baselineFile.readText())
            } catch (e: Exception) {
                issues += err("pack-ids.baseline.json", "unreadable baseline: ${e.message}")
                null
            }
            baseline?.let { b ->
                b.bonds.filter { it !in bondIds }.forEach {
                    issues += err("pack-ids.baseline.json", "bond id '$it' present in baseline is gone (deletion/rename forbidden; regenerate baseline deliberately)")
                }
                b.activities.filter { it !in activityIds }.forEach {
                    issues += err("pack-ids.baseline.json", "activity id '$it' present in baseline is gone (deletion/rename forbidden)")
                }
                b.deadlines.filter { it !in deadlineIds }.forEach {
                    issues += err("pack-ids.baseline.json", "deadline id '$it' present in baseline is gone (deletion/rename forbidden)")
                }
                b.answers.filter { it !in answerIds }.forEach {
                    issues += err("pack-ids.baseline.json", "answer sheet id '$it' present in baseline is gone (deletion/rename forbidden)")
                }
            }
        }

        return issues
    }

    private fun err(location: String, message: String) = LintIssue(LintIssue.Severity.ERROR, location, message)
}

fun main(args: Array<String>) {
    if (args.isEmpty() || args[0] != "validate") {
        System.err.println("usage: packlint validate --pack <dir> [--write-baseline]")
        kotlin.system.exitProcess(2)
    }
    var packDir: Path? = null
    var writeBaseline = false
    var i = 1
    while (i < args.size) {
        when (args[i]) {
            "--pack" -> { packDir = Path.of(args[i + 1]); i += 2 }
            "--write-baseline" -> { writeBaseline = true; i += 1 }
            else -> { System.err.println("unknown argument: ${args[i]}"); kotlin.system.exitProcess(2) }
        }
    }
    val dir = packDir ?: run {
        System.err.println("missing --pack <dir>")
        kotlin.system.exitProcess(2)
    }

    val issues = PackLint.run(dir, writeBaseline)
    val errors = issues.filter { it.severity == LintIssue.Severity.ERROR }
    val warnings = issues.filter { it.severity == LintIssue.Severity.WARN }

    println("packlint: $dir")
    warnings.forEach { println("  WARN  [${it.location}] ${it.message}") }
    errors.forEach { println("  ERROR [${it.location}] ${it.message}") }
    println("packlint summary: ${errors.size} error(s), ${warnings.size} warning(s)")

    if (errors.isNotEmpty()) kotlin.system.exitProcess(1)
}
