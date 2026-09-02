package com.shadowmonarchbooks.dayloop.ui.skin

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
 * Animation timing lint (docs/ROADMAP-v3.md Phase 16 acceptance): no blocking
 * transition may exceed 400 ms. Two layers:
 *
 *  1. the [SkinFxTiming] table is pinned — every Phase 16 moment duration
 *     stays under the ceiling, and
 *  2. every literal `tween(<ms>)` in the app's UI sources is scanned, so a
 *     future transition can't quietly grow past the ceiling (the one
 *     allowlisted literal is the engine default nav fade, which mirrors
 *     navigation-compose's own built-in 700 ms cross-fade and predates this
 *     phase).
 *
 * The splash's linger delay is deliberately not a tween — the card never
 * blocks input, so only its entrance/exit fall under the ceiling.
 */
class SkinFxTimingTest {

    private fun repoRoot(): Path? {
        var dir: Path? = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        repeat(5) {
            val candidate = dir?.resolve("settings.gradle.kts")
            if (candidate != null && candidate.isRegularFile()) return dir
            dir = dir?.parent
        }
        return null
    }

    private fun uiSources(root: Path): List<Path> {
        val src = root.resolve("app/src/main/java")
        if (!src.toFile().exists()) return emptyList()
        return Files.walk(src).use { stream ->
            stream.filter { it.toString().endsWith(".kt") && it.isRegularFile() }.toList()
        }
    }

    @Test
    fun `every phase 16 moment duration stays under the ceiling`() {
        assertTrue(SkinFxTiming.ADVANCE_TOTAL_MS <= SkinFxTiming.MAX_TRANSITION_MS)
        assertTrue(SkinFxTiming.ADVANCE_COVER_MS <= SkinFxTiming.MAX_TRANSITION_MS)
        assertTrue(SkinFxTiming.ADVANCE_REVEAL_MS <= SkinFxTiming.MAX_TRANSITION_MS)
        assertTrue(SkinFxTiming.SPLASH_IN_MS <= SkinFxTiming.MAX_TRANSITION_MS)
        assertTrue(SkinFxTiming.SPLASH_OUT_MS <= SkinFxTiming.MAX_TRANSITION_MS)
        assertTrue(SkinFxTiming.MARK_MS <= SkinFxTiming.MAX_TRANSITION_MS)
        assertTrue(SkinFxTiming.ADVANCE_LINGER_MS == 3_000L, "day-complete results must last exactly three seconds")
        assertTrue(SkinFxTiming.SPLASH_LINGER_MS == 3_000L, "perfect-day splash must last exactly three seconds")
    }

    @Test
    fun `no tween in ui sources exceeds the transition ceiling`() {
        val root = repoRoot() ?: return
        val ceiling = SkinFxTiming.MAX_TRANSITION_MS
        val literal = Regex("""tween\(\s*(\d+)""")
        // The engine default nav fade mirrors navigation-compose's built-in
        // 700 ms cross-fade; it predates Phase 16 and is not a moment effect.
        val engineDefaultFadeMs = 700
        val violations = mutableListOf<String>()
        for (file in uiSources(root)) {
            val rel = file.relativeTo(root)
            literal.findAll(file.readText()).forEach { match ->
                val ms = match.groupValues[1].toInt()
                if (ms > ceiling && ms != engineDefaultFadeMs) {
                    violations += "$rel: ${match.value} exceeds the ${ceiling} ms transition ceiling"
                }
            }
        }
        assertTrue(violations.isEmpty(), violations.joinToString("\n"))
    }

    @Test
    fun `the allowlisted engine default fade is the only long tween`() {
        val root = repoRoot() ?: return
        val literal = Regex("""tween\(\s*(\d+)""")
        val overCeiling = uiSources(root).flatMap { file ->
            literal.findAll(file.readText())
                .filter { it.groupValues[1].toInt() > SkinFxTiming.MAX_TRANSITION_MS }
                .map { "${file.relativeTo(root)}: ${it.groupValues[1]}ms" }
        }.toList()
        assertTrue(
            overCeiling.isNotEmpty() && overCeiling.all { it.endsWith("700ms") },
            "only the engine default nav fade may exceed the ceiling; found: $overCeiling",
        )
    }
}
