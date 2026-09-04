package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorAchievementAuditTest {

    @Test
    fun `Metaphor ships all 44 base achievements including Entrusted`() {
        val loaded = loadMetaphor()
        val file = assertNotNull(loaded.achievements)
        val achievements = file.achievements

        assertEquals(44, achievements.size)
        assertEquals(44, achievements.map { it.id }.distinct().size)
        assertEquals(44, achievements.map { it.title }.distinct().size)

        val entrusted = achievements.single { it.id == "metaphor.achievement.entrusted" }
        assertEquals("Entrusted", entrusted.title)
        assertEquals("Overcome all trials to defeat Louis.", entrusted.description)
        assertEquals("2100-10-12", entrusted.expectedBy)
        assertEquals(null, entrusted.iconMediaRef, "Entrusted has no bundled source-guide icon; achievement data must not fake one")
        assertEquals("metaphor.event.elegy-of-the-soul", entrusted.tracking.event)
    }

    @Test
    fun `Metaphor achievement icon refs use the 43 real bundled guide icons`() {
        val loaded = loadMetaphor()
        val achievements = assertNotNull(loaded.achievements).achievements
        val mediaIds = assertNotNull(loaded.media).media.map { it.id }.toSet()
        val refs = achievements.mapNotNull { it.iconMediaRef }

        assertEquals(43, refs.size)
        assertEquals(43, refs.distinct().size)
        refs.forEach { ref -> assertTrue(ref in mediaIds, "missing achievement icon media: $ref") }
    }

    @Test
    fun `Metaphor route achievements use audited completion dates`() {
        val achievements = assertNotNull(loadMetaphor().achievements).achievements.associateBy { it.id }
        val expected = mapOf(
            "metaphor.achievement.allies-united" to "2100-06-05",
            "metaphor.achievement.out-of-the-fire" to "2100-06-06",
            "metaphor.achievement.calamity-averted" to "2100-06-12",
            "metaphor.achievement.dark-truths" to "2100-07-05",
            "metaphor.achievement.on-knifes-edge" to "2100-07-25",
            "metaphor.achievement.history-untold" to "2100-08-19",
            "metaphor.achievement.mission-accomplished" to "2100-09-10",
            "metaphor.achievement.his-majesty" to "2100-09-24",
            "metaphor.achievement.debate-me" to "2100-09-14",
            "metaphor.achievement.bookworm" to "2100-09-21",
            "metaphor.achievement.vista-viewer" to "2100-09-30",
            "metaphor.achievement.king-of-cuisine" to "2100-09-30",
            "metaphor.achievement.all-that-glitters" to "2100-10-01",
            "metaphor.achievement.hearts-as-one" to "2100-10-05",
            "metaphor.achievement.entrusted" to "2100-10-12",
            "metaphor.achievement.coliseum-champion" to "2100-10-15",
            "metaphor.achievement.skybound-hope" to "2100-10-16",
            "metaphor.achievement.coronation-of-the-king" to "2100-10-26",
        )

        expected.forEach { (id, date) -> assertEquals(date, achievements.getValue(id).expectedBy, id) }
    }

    @Test
    fun `Metaphor NG plus and variable grind achievements are not falsely pinned to route dates`() {
        val achievements = assertNotNull(loadMetaphor().achievements).achievements.associateBy { it.id }
        listOf(
            "metaphor.achievement.the-traveller",
            "metaphor.achievement.closing-the-book",
            "metaphor.achievement.archetype-hero",
            "metaphor.achievement.sword-surfer",
            "metaphor.achievement.no-mercy",
            "metaphor.achievement.tactical-strike",
            "metaphor.achievement.stray-elements",
            "metaphor.achievement.teamwork-makes-the-dream-work",
            "metaphor.achievement.money-is-power",
            "metaphor.achievement.hey-listen",
        ).forEach { id -> assertEquals(null, achievements.getValue(id).expectedBy, id) }
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
