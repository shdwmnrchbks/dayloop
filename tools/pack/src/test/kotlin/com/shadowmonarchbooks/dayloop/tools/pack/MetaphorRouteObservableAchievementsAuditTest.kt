package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorRouteObservableAchievementsAuditTest {

    @Test
    fun `Archetype Adept is guaranteed by the authored Elite Archetype mastery step`() {
        val loaded = loadMetaphor()
        val achievement = achievement("metaphor.achievement.archetype-adept")
        assertEquals(AchievementTrackingTypes.EVENT, achievement.tracking.type)
        assertEquals("metaphor.event.archetype-adept", achievement.tracking.event)
        assertEquals("2100-09-30", achievement.expectedBy)

        val event = event("metaphor.event.archetype-adept")
        assertEquals("2100-09-30", event.date)
        assertTrue(event.labelContains.contains("master any Elite Archetype", ignoreCase = true))

        val matches = day(event.date).steps.filter {
            it.label.contains(event.labelContains, ignoreCase = true)
        }
        assertEquals(1, matches.size)
        assertTrue(matches.single().label.contains("More's Task Chapter Six", ignoreCase = true))
    }

    @Test
    fun `Whats Yours Is Mine is guaranteed by the authored Jin steal`() {
        val achievement = achievement("metaphor.achievement.whats-yours-is-mine")
        assertEquals(AchievementTrackingTypes.EVENT, achievement.tracking.type)
        assertEquals("metaphor.event.whats-yours-is-mine", achievement.tracking.event)
        assertEquals("2100-10-05", achievement.expectedBy)

        val event = event("metaphor.event.whats-yours-is-mine")
        assertEquals("2100-10-05", event.date)
        assertTrue(event.labelContains.contains("Fortune God's Abacus", ignoreCase = true))

        val matches = day(event.date).steps.filter {
            it.label.contains(event.labelContains, ignoreCase = true)
        }
        assertEquals(1, matches.size)
        assertTrue(matches.single().label.startsWith("Steal", ignoreCase = true))
    }

    @Test
    fun `variable or cumulative achievements remain explicit`() {
        val expected = mapOf(
            "metaphor.achievement.stunning" to AchievementTrackingTypes.CONFIRMATION,
            "metaphor.achievement.united-front" to AchievementTrackingTypes.CONFIRMATION,
            "metaphor.achievement.no-mercy" to AchievementTrackingTypes.COUNTER,
            "metaphor.achievement.tactical-strike" to AchievementTrackingTypes.COUNTER,
            "metaphor.achievement.stray-elements" to AchievementTrackingTypes.COUNTER,
            "metaphor.achievement.teamwork-makes-the-dream-work" to AchievementTrackingTypes.COUNTER,
            "metaphor.achievement.hey-listen" to AchievementTrackingTypes.COUNTER,
            "metaphor.achievement.coliseum-champion" to AchievementTrackingTypes.COUNTER,
            "metaphor.achievement.money-is-power" to AchievementTrackingTypes.COUNTER,
            "metaphor.achievement.summon-mask-time" to AchievementTrackingTypes.CONFIRMATION,
            "metaphor.achievement.sword-surfer" to AchievementTrackingTypes.CONFIRMATION,
            "metaphor.achievement.closing-the-book" to AchievementTrackingTypes.CONFIRMATION,
        )

        expected.forEach { (id, type) ->
            assertEquals(type, achievement(id).tracking.type, id)
        }
    }

    @Test
    fun `observable achievement events resolve uniquely`() {
        listOf(
            "metaphor.event.archetype-adept",
            "metaphor.event.whats-yours-is-mine",
        ).forEach { id ->
            val anchor = event(id)
            val matches = day(anchor.date).steps.filter {
                it.label.contains(anchor.labelContains, ignoreCase = true)
            }
            assertEquals(1, matches.size, id)
        }
    }

    private fun achievement(id: String) = assertNotNull(
        assertNotNull(loadMetaphor().achievements).achievements.firstOrNull { it.id == id },
        id,
    )

    private fun event(id: String) = assertNotNull(
        assertNotNull(loadMetaphor().achievements).events.firstOrNull { it.id == id },
        id,
    )

    private fun day(date: String) = assertNotNull(
        loadMetaphor().walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .firstOrNull { it.date == date },
        date,
    )

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
