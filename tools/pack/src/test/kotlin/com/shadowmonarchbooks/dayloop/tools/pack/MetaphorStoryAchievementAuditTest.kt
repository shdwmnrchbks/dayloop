package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `Bookworm route explicitly finishes every book with the final reading reward`() {
        data class BookFinish(
            val date: String,
            val title: String,
            val stat: String,
            val gain: Int,
        )

        val finishes = listOf(
            BookFinish("2100-07-17", "New World Travel Diary", "imagination", 20),
            BookFinish("2100-07-10", "Pride and Persuasion", "eloquence", 20),
            BookFinish("2100-07-19", "Bygone Days", "tolerance", 20),
            BookFinish("2100-08-02", "The Future of Magic", "wisdom", 20),
            BookFinish("2100-08-15", "Top Secret Poetry! Do Not Read!", "courage", 22),
            BookFinish("2100-09-09", "How to Walk Outside the Island", "tolerance", 26),
            BookFinish("2100-09-21", "Literacy Workbook", "imagination", 26),
        )

        finishes.forEach { expected ->
            val step = assertNotNull(
                day(expected.date).steps.singleOrNull {
                    it.label.contains("finish", ignoreCase = true) &&
                        it.label.contains(expected.title, ignoreCase = true)
                },
                "${expected.title} final reading on ${expected.date}",
            )
            assertEquals(expected.gain, step.statGains[expected.stat], expected.title)
        }

        assertTrue(day("2100-09-21").steps.any {
            it.label.contains("Bookworm unlocks", ignoreCase = true)
        })
    }

    @Test
    fun `Top Secret Poetry route reaches Courage rank four on its final reading`() {
        val august14 = day("2100-08-14")
        assertTrue(august14.steps.any {
            it.label.contains("Start 'Top Secret Poetry", ignoreCase = true) && it.slot == "afternoon"
        })
        assertTrue(august14.steps.any {
            it.label.contains("Continue 'Top Secret Poetry", ignoreCase = true) && it.slot == "night"
        })
        assertTrue(day("2100-08-15").steps.any {
            it.label.contains("Courage reaches rank 4", ignoreCase = true)
        })
        assertTrue(day("2100-08-28").steps.none {
            it.label.contains("Courage reaches rank 4", ignoreCase = true)
        })
    }

    @Test
    fun `How to Walk Outside the Island has all three route readings`() {
        val dates = listOf("2100-09-07", "2100-09-08", "2100-09-09")
        dates.forEach { date ->
            assertTrue(day(date).steps.any {
                it.activityRef == "metaphor.activity.book.how-to-walk-outside-the-island"
            }, "$date missing How to Walk Outside the Island")
        }
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
    fun `All That Glitters route performs the required beetle exchange`() {
        val cleanup = day("2100-10-13").steps.joinToString("\n") { it.label }
        assertTrue(cleanup.contains("Elderly Entomophile", ignoreCase = true))
        assertTrue(cleanup.contains("at least 46 Gold Beetles", ignoreCase = true))
        assertTrue(cleanup.contains("All That Glitters unlocks", ignoreCase = true))
    }

    @Test
    fun `Summon Mask Time cleanup does not depend on Skybound-only materials`() {
        val october13 = day("2100-10-13").steps.joinToString("\n") { it.label }
        assertTrue(october13.contains("Special Experiment", ignoreCase = true))
        assertTrue(october13.contains("Wary Shopkeep", ignoreCase = true))
        assertTrue(october13.contains("Krozelli", ignoreCase = true))
        assertTrue(october13.contains("do not assume", ignoreCase = true))

        val october17 = day("2100-10-17").steps.joinToString("\n") { it.label }
        assertTrue(october17.contains("every remaining Masked Dancer mask", ignoreCase = true))
        assertTrue(october17.contains("Summoner vessel", ignoreCase = true))
        assertTrue(october17.contains("Summon Mask Time", ignoreCase = true))
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
