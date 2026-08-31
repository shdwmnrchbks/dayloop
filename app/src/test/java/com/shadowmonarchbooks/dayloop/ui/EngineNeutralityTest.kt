package com.shadowmonarchbooks.dayloop.ui

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.relativeTo
import kotlin.streams.toList
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Engine-neutrality regression gate (docs/ROADMAP-v3.md Phase 12 non-negotiable
 * 1): no pack title ever appears in engine sources. Game vocabulary lives in
 * pack data — a title leaking into Kotlin means a per-game code path snuck in.
 *
 * Two checks over every .kt file under the engine source roots:
 *  1. raw text must not contain a full pack title (catches comments too), and
 *  2. no *string literal* may contain a title word (comments may explain the
 *     architecture by naming the games it must NOT know; executable strings
 *     may not).
 */
class EngineNeutralityTest {

    private val fullTitles = listOf(
        "Persona 5 Royal",
        "Persona 3 Reload",
        "Persona 4 Golden",
        "Metaphor: ReFantazio",
    )

    /** Words that only ever name games; forbidden inside executable strings. */
    private val titleWords = Regex(
        pattern = """(?i)\b(persona|metaphor|refantazio)\b""",
    )

    /** Quoted string literal candidates (naive but conservative for Kotlin). */
    private val stringLiteral = Regex("\"([^\"\\\n]*)\"|'([^'\\\n]*)'")

    private fun repoRoot(): Path? {
        var dir: Path? = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        repeat(5) {
            val candidate = dir?.resolve("settings.gradle.kts")
            if (candidate != null && candidate.isRegularFile()) return dir
            dir = dir?.parent
        }
        return null
    }

    private fun engineSources(root: Path): List<Path> {
        val roots = listOf(
            root.resolve("app/src/main/java"),
            root.resolve("core/pack/src/main/kotlin"),
            root.resolve("core/progress/src/main/kotlin"),
        ).filter { it.toFile().exists() }
        return roots.flatMap { r ->
            Files.walk(r).use { stream ->
                stream.filter { it.toString().endsWith(".kt") && it.isRegularFile() }.toList()
            }
        }
    }

    @Test
    fun `no pack title appears in engine sources`() {
        val root = repoRoot() ?: return
        val violations = mutableListOf<String>()
        for (file in engineSources(root)) {
            val text = file.readText()
            val rel = file.relativeTo(root)
            fullTitles.firstOrNull { it in text }?.let {
                violations += "$rel: full pack title \"$it\" in engine source"
            }
            stringLiteral.findAll(text).forEach { match ->
                val literal = match.value
                if (titleWords.containsMatchIn(literal)) {
                    violations += "$rel: game name inside string literal $literal"
                }
            }
        }
        assertTrue(violations.isEmpty(), violations.joinToString("\n"))
    }
}
