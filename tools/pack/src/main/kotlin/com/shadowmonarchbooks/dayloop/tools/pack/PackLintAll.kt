package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.LintIssue
import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.theme.LauncherBadgeRules
import java.nio.file.Path

/**
 * Aggregate packlint entry point. Phase 17c reserves the camelCase
 * `launcherBadge` art slot; the older generic art-slot rule only accepted
 * lowercase slug names, so that one legacy name error is replaced with the
 * dedicated launcher-badge validator while every other PackLint rule remains
 * unchanged.
 */
object PackLintAll {
    fun run(packDir: Path, writeBaseline: Boolean): List<LintIssue> {
        val hasLauncherBadge = PackLoader.load(packDir).pack?.theme?.art
            ?.containsKey(LauncherBadgeRules.SLOT) == true
        val base = PackLint.run(packDir, writeBaseline).filterNot { issue ->
            hasLauncherBadge &&
                issue.location == "pack.json" &&
                issue.message == "theme.art['${LauncherBadgeRules.SLOT}'] slot name is not a lowercase slug token"
        }
        return base + LauncherBadgeLint.run(packDir)
    }
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

    val issues = PackLintAll.run(dir, writeBaseline)
    val errors = issues.filter { it.severity == LintIssue.Severity.ERROR }
    val warnings = issues.filter { it.severity == LintIssue.Severity.WARN }

    println("packlint: $dir")
    warnings.forEach { println("  WARN  [${it.location}] ${it.message}") }
    errors.forEach { println("  ERROR [${it.location}] ${it.message}") }
    println("packlint summary: ${errors.size} error(s), ${warnings.size} warning(s)")

    if (errors.isNotEmpty()) kotlin.system.exitProcess(1)
}
