package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorFollowerAuditTest {

    @Test
    fun `Metaphor identifies its authored completion route`() {
        val loaded = loadMetaphor()
        val pack = assertNotNull(loaded.pack)
        val route = assertNotNull(pack.routes.singleOrNull())

        assertEquals(Routes.DEFAULT, route.id)
        assertEquals("100% Completion Route", route.label)
        assertTrue(route.description.orEmpty().contains("not universal", ignoreCase = true))
    }

    @Test
    fun `Metaphor Follower route dates do not masquerade as availability`() {
        val bonds = assertNotNull(loadMetaphor().bonds).bonds
        assertEquals(14, bonds.size)
        assertTrue(bonds.all { it.ranks.map { rank -> rank.rank } == (1..8).toList() })

        val fixedStoryRanks = setOf(
            "metaphor.bond.gallica" to 1,
            "metaphor.bond.gallica" to 2,
            "metaphor.bond.gallica" to 5,
            "metaphor.bond.gallica" to 7,
            "metaphor.bond.gallica" to 8,
            "metaphor.bond.strohl" to 1,
            "metaphor.bond.hulkenberg" to 1,
            "metaphor.bond.heismay" to 1,
            "metaphor.bond.maria" to 1,
            "metaphor.bond.catherina" to 2,
            "metaphor.bond.catherina" to 3,
            "metaphor.bond.more" to 1,
        )

        bonds.forEach { bond ->
            bond.ranks.forEach { rank ->
                val key = bond.id to rank.rank
                if (key in fixedStoryRanks) {
                    assertNotNull(rank.availableFrom, "$key fixed story date")
                    assertEquals(null, rank.scheduledFor, "$key fixed story rank must not use scheduledFor")
                } else {
                    assertEquals(null, rank.availableFrom, "$key route date must not live in availableFrom")
                    assertNotNull(rank.scheduledFor, "$key completion-route rank needs scheduledFor")
                }
            }
        }

        val available = bonds.flatMap { bond ->
            bond.ranks.filter { it.availableFrom != null }.map { bond.id to it.rank }
        }.toSet()
        assertEquals(fixedStoryRanks, available)
    }

    @Test
    fun `More rank two is pinned to the authored June 12 turn-in`() {
        val more = assertNotNull(loadMetaphor().bonds).bonds.single { it.id == "metaphor.bond.more" }
        val rank2 = more.ranks.single { it.rank == 2 }

        assertEquals("2100-06-12", rank2.scheduledFor)
        assertEquals(null, rank2.availableFrom)
        assertTrue(rank2.notes.orEmpty().contains("Healer", ignoreCase = true))
    }

    private fun loadMetaphor() = PackLoader.load(metaphorDir()).also { loaded ->
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
    }

    private fun metaphorDir(): Path {
        val candidates = listOf(
            Path.of("content", "packs", "metaphor"),
            Path.of("..", "..", "content", "packs", "metaphor"),
        )
        return candidates.firstOrNull { Files.isDirectory(it) }
            ?: error("Cannot locate content/packs/metaphor from ${Path.of("").toAbsolutePath()}")
    }
}
