package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.GameCalendar
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Cross-reference tests over the bundled content packs (docs/ROADMAP-v2.md
 * Phase 9: "served accordingly" is checkable, not vibes). packlint enforces
 * the full rule set; these JVM tests pin the cross-references the app
 * resolves at render time, so a broken ref fails the build, not the UI.
 * Runs only where the repo checkout is present (no-ops otherwise).
 */
class PackContentTest {

    private fun contentPacksDir(): Path? {
        var dir: Path? = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        repeat(5) {
            val candidate = dir?.resolve("content")?.resolve("packs")
            if (candidate != null && candidate.isDirectory()) return candidate
            dir = dir?.parent
        }
        return null
    }

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
                    assertTrue(ref in deadlineIds, "$slug: answer sheet '${sheet.id}' references unknown deadline '$ref'")
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
                            assertTrue(slot in slotIds, "$slug: ${wt.location} day '${day.date}' uses undeclared slot '$slot'")
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

    @Test
    fun `bond route dates are calendar dates and respect explicit availability windows`() {
        loadPacks().forEach { (slug, _, loaded) ->
            val pack = loaded.pack ?: return@forEach
            val calendar = GameCalendar.of(pack.calendar) ?: return@forEach
            loaded.bonds?.bonds?.forEach { bond ->
                bond.ranks.forEach { step ->
                    step.scheduledFor?.let { routeDate ->
                        assertTrue(routeDate in calendar, "$slug: ${bond.id} rank ${step.rank} route date $routeDate is outside the calendar")
                        step.availableFrom?.let { from ->
                            assertTrue(routeDate >= from, "$slug: ${bond.id} rank ${step.rank} route date $routeDate is before $from")
                        }
                        step.availableUntil?.let { until ->
                            assertTrue(routeDate <= until, "$slug: ${bond.id} rank ${step.rank} route date $routeDate is after $until")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `p5r keeps route-selected confidant dates separate from game availability`() {
        val p5r = loadPacks().firstOrNull { it.first == "p5r" }?.third ?: return
        val bonds = p5r.bonds?.bonds?.associateBy { it.id }.orEmpty()
        val fool = bonds.getValue("p5r.bond.fool")
        val chariot = bonds.getValue("p5r.bond.chariot")
        val justice = bonds.getValue("p5r.bond.justice")
        val councilor = bonds.getValue("p5r.bond.councilor")

        assertEquals("Igor", fool.characterLabel)
        assertEquals("2016-04-12", fool.ranks.first { it.rank == 1 }.availableFrom)
        assertEquals("2016-04-24", fool.ranks.first { it.rank == 2 }.scheduledFor)
        assertNull(chariot.ranks.first { it.rank == 2 }.availableFrom, "route-selected Chariot rank date must not masquerade as availability")
        assertTrue(justice.ranks.first { it.rank == 8 }.notes.orEmpty().contains("not Akechi"), "Justice copy must not claim Akechi unlocks third semester")
        assertEquals("2016-11-17", councilor.ranks.first { it.rank == 9 }.availableUntil)
    }

    // ---- Media (docs/ROADMAP-v3.md Phase 11): bundled graphics all serve ----

    @Test
    fun `every bundled image is declared exactly once in media json`() {
        loadPacks().forEach { (slug, dir, loaded) ->
            val images = dir.resolve("images")
            if (!images.isDirectory()) return@forEach
            val declared = loaded.media?.media?.map { it.file }?.toSet().orEmpty()
            val bundled = Files.list(images).use { stream ->
                stream.map { "images/${it.name}" }.toList()
            }
            assertTrue(bundled.isNotEmpty(), "$slug ships no images but has an images/ dir")
            bundled.forEach { file ->
                assertTrue(file in declared, "$slug: $file is bundled but not declared in media.json")
            }
            assertEquals(bundled.size, declared.size, "$slug: media.json must declare exactly the bundled images")
        }
    }

    @Test
    fun `every media bond anchor resolves to a real bond`() {
        loadPacks().forEach { (slug, _, loaded) ->
            val bondIds = loaded.bonds?.bonds?.map { it.id }?.toSet().orEmpty()
            loaded.media?.media?.forEach { item ->
                item.bonds.forEach { bond ->
                    assertTrue(bond in bondIds, "$slug: media '${item.id}' references unknown bond '$bond'")
                }
            }
        }
    }

    @Test
    fun `every pack ships a media manifest covering its graphics`() {
        val counts = mutableMapOf<String, Int>()
        loadPacks().forEach { (slug, _, loaded) ->
            counts[slug] = loaded.media?.media?.size ?: 0
        }
        assertTrue((counts["p5r"] ?: 0) >= 53, "p5r must declare its 53 guide graphics, found ${counts["p5r"]}")
        assertTrue((counts["p3r"] ?: 0) >= 16, "p3r must declare its 16 guide graphics, found ${counts["p3r"]}")
        assertTrue((counts["metaphor"] ?: 0) >= 47, "metaphor must declare its 47 guide graphics, found ${counts["metaphor"]}")
    }
}
