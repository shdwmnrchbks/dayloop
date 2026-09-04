package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorStoryAchievementAuditTest {

    @Test
    fun `September 24 preserves the Prince story beat and His Majesty unlock`() {
        val day = day("2100-09-24")

        assertTrue(day.steps.any { it.label.contains("Ancient Eldan Sanctum", ignoreCase = true) })
        assertTrue(day.steps.any {
            it.label.contains("Prince Archetype", ignoreCase = true) &&
                it.label.contains("His Majesty", ignoreCase = true)
        })
        assertTrue(day.steps.any {
            it.activityRef == "metaphor.activity.gold-beetles" &&
                it.label.contains("one-time Gold Beetle", ignoreCase = true)
        })
    }

    @Test
    fun `King of Cuisine route cooks the first twenty then the final Sublime Spoonful`() {
        val september19 = day("2100-09-19")
        val unlock = assertNotNull(september19.steps.firstOrNull {
            it.label.contains("20 recipes", ignoreCase = true) &&
                it.label.contains("Sublime Spoonful", ignoreCase = true)
        })
        assertTrue(unlock.label.contains("21st recipe", ignoreCase = true))

        val september30 = day("2100-09-30")
        val finalRecipe = assertNotNull(september30.steps.firstOrNull {
            it.label.contains("Sublime Spoonful", ignoreCase = true)
        })
        assertTrue(finalRecipe.label.contains("21st and final recipe", ignoreCase = true))
        assertTrue(finalRecipe.label.contains("King of Cuisine", ignoreCase = true))
    }

    @Test
    fun `Entrusted route completes all dragon trials before Essence of Power`() {
        val checks = listOf(
            "2100-10-09" to listOf("Mad Mischief", "Devourer of Nations"),
            "2100-10-10" to listOf("Heroes' Rest", "Devourer of Flames"),
            "2100-10-11" to listOf("Bygone Legacy", "Devourer of Stars"),
            "2100-10-12" to listOf("Essence of Power", "Elegy of the Soul", "Entrusted"),
        )

        checks.forEach { (date, phrases) ->
            val text = day(date).steps.joinToString("\n") { it.label }
            phrases.forEach { phrase ->
                assertTrue(text.contains(phrase, ignoreCase = true), "$date missing $phrase")
            }
        }
    }

    @Test
    fun `Skybound Hope is tied to the authored Skybound Avatar clear`() {
        val day = day("2100-10-16")
        assertTrue(day.steps.any {
            it.label.contains("Skybound Avatar Conquest", ignoreCase = true) &&
                it.label.contains("Skybound Hope", ignoreCase = true)
        })
    }

    @Test
    fun `Star Shatterer route preserves full power Destroyer Charadrius`() {
        val day = day("2100-10-26")
        assertTrue(day.steps.any {
            it.label.contains("Melancholia Crystal", ignoreCase = true) &&
                it.label.contains("ignore", ignoreCase = true)
        })
        assertTrue(day.steps.any {
            it.label.contains("full-power Destroyer Charadrius", ignoreCase = true) &&
                it.label.contains("Star Shatterer", ignoreCase = true)
        })
    }

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
