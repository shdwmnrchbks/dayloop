package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AllOf
import com.shadowmonarchbooks.dayloop.pack.schema.AnyOf
import com.shadowmonarchbooks.dayloop.pack.schema.BondRankGte
import com.shadowmonarchbooks.dayloop.pack.schema.Condition
import com.shadowmonarchbooks.dayloop.pack.schema.StatGte
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Cross-reference tests over the bundled content packs (docs/ROADMAP-v2.md
 * Phase 9: "served accordingly" is checkable, not vibes). packlint enforces
 * the full rule set; these JVM tests pin the cross-references the app
 * resolves at render time, so a broken ref fails the build, not the UI.
 * Runs only where the repo checkout is present (no-ops otherwise).
 */
class PackContentTest {

    /** The repo's content/packs directory, or null when not running in a checkout. */
    private fun contentPacksDir(): Path? {
        var dir: Path? = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        repeat(5) {
            val candidate = dir?.resolve("content")?.resolve("packs")
            if (candidate != null && candidate.isDirectory()) return candidate
            dir = dir?.parent
        }
        return null
    }

    /** Loads every pack under content/packs; asserts the checkout ships some. */
    private fun loadPacks(): List<Triple<String, Path, com.shadowmonarchbooks.dayloop.pack.PackLoadResult>> {
        val root = contentPacksDir() ?: return emptyList()
        val results = Files.list(root).use { stream ->
            stream.filter { it.isDirectory() }
                .sorted()
                .map { dir -> Triple(dir.name, dir, PackLoader.load(dir)) }
                .toList()
        }
        assertTrue(results.isNotEmpty(), "no packs found under $root")
        return results
    }

    @Test
    fun `all three packs load without parse errors`() {
        val names = loadPacks().map { it.first }
        assertTrue(
            listOf("p5r", "p3r", "metaphor").all { it in names },
            "expected p5r, p3r and metaphor; found $names",
        )
        loadPacks().forEach { (slug, _, loaded) ->
            assertEquals(emptyList(), loaded.parseIssues, "$slug must decode cleanly")
        }
    }

    @Test
    fun `every answer sheet deadlineRef resolves to a real deadline`() {
        loadPacks().forEach { (slug, _, loaded) ->
            val deadlineIds = loaded.deadlines?.deadlines?.map { it.id }?.toSet().orEmpty()
            loaded.answers?.answers?.forEach { sheet ->
                sheet.deadlineRef?.let { ref ->
                    assertTrue(
                        ref in deadlineIds,
                        "$slug: answer sheet '${sheet.id}' references unknown deadline '$ref'",
                    )
                }
            }
        }
    }

    @Test
    fun `every walkthrough activityRef resolves to a real activity`() {
        loadPacks().forEach { (slug, _, loaded) ->
            val activityIds = loaded.activities?.activities?.map { it.id }?.toSet().orEmpty()
            loaded.walkthroughs.forEach { wt ->
                wt.file.days.forEach { day ->
                    day.steps.forEach { step ->
                        step.activityRef?.let { ref ->
                            assertTrue(
                                ref in activityIds,
                                "$slug: ${wt.location} day '${day.date}' references unknown activity '$ref'",
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `every step slot resolves to a declared slot`() {
        loadPacks().forEach { (slug, _, loaded) ->
            val pack = loaded.pack ?: return@forEach
            val slotIds = pack.slots.map { it.id }.toSet()
            loaded.walkthroughs.forEach { wt ->
                wt.file.days.forEach { day ->
                    day.steps.forEach { step ->
                        step.slot?.let { slot ->
                            assertTrue(
                                slot in slotIds,
                                "$slug: ${wt.location} day '${day.date}' uses undeclared slot '$slot'",
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `gate references resolve to declared stats and bonds`() {
        loadPacks().forEach { (slug, _, loaded) ->
            val pack = loaded.pack ?: return@forEach
            val statIds = pack.stats.map { it.id }.toSet()
            val bondIds = loaded.bonds?.bonds?.map { it.id }?.toSet().orEmpty()

            fun check(condition: Condition, where: String) {
                when (condition) {
                    is AllOf -> condition.allOf.forEach { check(it, where) }
                    is AnyOf -> condition.anyOf.forEach { check(it, where) }
                    is StatGte -> assertTrue(
                        condition.stat in statIds,
                        "$slug: $where gate references unknown stat '${condition.stat}'",
                    )
                    is BondRankGte -> assertTrue(
                        condition.bond in bondIds,
                        "$slug: $where gate references unknown bond '${condition.bond}'",
                    )
                    else -> Unit
                }
            }

            loaded.bonds?.bonds?.forEach { bond ->
                bond.ranks.forEach { step ->
                    step.gates?.let { check(it, "bond '${bond.id}' rank ${step.rank}") }
                }
            }
        }
    }
}
