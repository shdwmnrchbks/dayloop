package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorNgPlusAuditTest {

    @Test
    fun `Closing the Book stays outside the first playthrough calendar`() {
        val loaded = loadMetaphor()
        val achievements = assertNotNull(loaded.achievements).achievements.associateBy { it.id }
        val closing = achievements.getValue("metaphor.achievement.closing-the-book")

        assertEquals(null, closing.availableFrom)
        assertEquals(null, closing.expectedBy)
        assertEquals("confirmation", closing.tracking.type)
        assertTrue(closing.tracking.prompt.orEmpty().contains("New Game+"))
        assertTrue(closing.tracking.prompt.orEmpty().contains("Book of Apocalypse"))

        val firstPlaythroughLabels = loaded.walkthroughs
            .filter { it.routeId == "standard" }
            .flatMap { it.file.days }
            .flatMap { it.steps }
            .map { it.label }

        listOf("Book of Apocalypse", "Redscale Apocalypse Dragon", "Closing the Book").forEach { ngPlusOnly ->
            assertTrue(firstPlaythroughLabels.none { ngPlusOnly in it }, "$ngPlusOnly leaked into the first-playthrough calendar")
        }
    }

    @Test
    fun `The Traveller explicitly depends on the New Game Plus achievement`() {
        val achievements = assertNotNull(loadMetaphor().achievements).achievements.associateBy { it.id }
        val traveller = achievements.getValue("metaphor.achievement.the-traveller")

        assertEquals(null, traveller.expectedBy)
        assertEquals("confirmation", traveller.tracking.type)
        assertTrue(traveller.tracking.prompt.orEmpty().contains("Closing the Book"))
        assertTrue(traveller.tracking.prompt.orEmpty().contains("New Game+"))
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
