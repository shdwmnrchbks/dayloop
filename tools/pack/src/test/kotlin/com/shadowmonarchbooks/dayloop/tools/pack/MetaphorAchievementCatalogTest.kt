package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorAchievementCatalogTest {

    @Test
    fun `Metaphor ships the complete audited achievement catalog`() {
        val loaded = loadMetaphor()
        val pack = assertNotNull(loaded.pack)
        val catalog = assertNotNull(loaded.achievements)

        assertTrue(
            pack.contentVersion >= 4,
            "The audited Metaphor achievement catalog requires contentVersion 4 or later",
        )
        assertEquals(44, catalog.achievements.size)
        assertEquals(44, catalog.achievements.map { it.id }.distinct().size)
        assertEquals(catalog.events.size, catalog.events.map { it.id }.distinct().size)
    }

    @Test
    fun `route observable completion achievements use semantic events`() {
        val achievements = achievementsById()

        fun assertEvent(id: String, event: String, expectedBy: String) {
            val achievement = achievements.getValue(id)
            assertEquals(AchievementTrackingTypes.EVENT, achievement.tracking.type, id)
            assertEquals(event, achievement.tracking.event, id)
            assertEquals(expectedBy, achievement.expectedBy, id)
        }

        assertEvent(
            "metaphor.achievement.bookworm",
            "metaphor.event.bookworm",
            "2100-09-21",
        )
        assertEvent(
            "metaphor.achievement.all-that-glitters",
            "metaphor.event.all-that-glitters",
            "2100-10-13",
        )
        assertEvent(
            "metaphor.achievement.king-of-cuisine",
            "metaphor.event.king-of-cuisine",
            "2100-09-30",
        )
        assertEvent(
            "metaphor.achievement.star-shatterer",
            "metaphor.event.star-shatterer",
            "2100-10-26",
        )
    }

    @Test
    fun `variable and external achievement state stays explicit`() {
        val achievements = achievementsById()

        val summonMask = achievements.getValue("metaphor.achievement.summon-mask-time")
        assertEquals(AchievementTrackingTypes.CONFIRMATION, summonMask.tracking.type)
        assertTrue(summonMask.tracking.prompt.orEmpty().contains("rotation", ignoreCase = true))

        val closingBook = achievements.getValue("metaphor.achievement.closing-the-book")
        assertEquals(AchievementTrackingTypes.CONFIRMATION, closingBook.tracking.type)
        assertTrue(closingBook.tracking.prompt.orEmpty().contains("New Game+", ignoreCase = true))

        val noMercy = achievements.getValue("metaphor.achievement.no-mercy")
        assertEquals(AchievementTrackingTypes.COUNTER, noMercy.tracking.type)
        assertEquals(50, noMercy.tracking.target)
    }

    @Test
    fun `Debate Me requires all eight audited candidate debates`() {
        val achievement = achievementsById().getValue("metaphor.achievement.debate-me")
        assertEquals(AchievementTrackingTypes.ALL_EVENTS, achievement.tracking.type)
        assertEquals(8, achievement.tracking.events.size)
        assertEquals(8, achievement.tracking.events.distinct().size)
        assertTrue(achievement.tracking.events.all { it.startsWith("metaphor.event.debate-") })
    }

    @Test
    fun `critical achievement anchors resolve to the corrected walkthrough`() {
        val events = assertNotNull(loadMetaphor().achievements).events.associateBy { it.id }

        assertEquals("2100-10-13", events.getValue("metaphor.event.special-experiment").date)
        assertTrue(
            events.getValue("metaphor.event.special-experiment")
                .labelContains.contains("For Science", ignoreCase = true),
        )
        assertEquals("2100-09-21", events.getValue("metaphor.event.bookworm").date)
        assertEquals("2100-10-13", events.getValue("metaphor.event.all-that-glitters").date)
    }

    private fun achievementsById() =
        assertNotNull(loadMetaphor().achievements).achievements.associateBy { it.id }

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
