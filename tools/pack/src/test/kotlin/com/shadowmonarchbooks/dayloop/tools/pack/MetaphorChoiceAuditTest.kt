package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorChoiceAuditTest {

    @Test
    fun `Metaphor content version includes audited response guidance`() {
        assertEquals(5, assertNotNull(loadMetaphor().pack).contentVersion)
    }

    @Test
    fun `New World Travel Diary keeps every best response on the authored reading dates`() {
        val june24 = bookStep("2100-06-24", "new-world-travel-diary")
        listOf(
            "You've matured.",
            "Maybe I'll write a book about it.",
            "Teach me more.",
        ).forEach { answer ->
            assertTrue(june24.label.contains(answer, ignoreCase = true), "06/24 missing $answer")
        }

        assertTrue(
            bookStep("2100-06-25", "new-world-travel-diary")
                .label.contains("The pavilion.", ignoreCase = true),
        )
        assertTrue(
            bookStep("2100-07-17", "new-world-travel-diary")
                .label.contains("Sounds pretty.", ignoreCase = true),
        )
    }

    @Test
    fun `Future of Magic keeps all three best responses`() {
        val expected = mapOf(
            "2100-07-30" to "It won't work for everyone.",
            "2100-08-01" to "immediately is too rash",
            "2100-08-02" to "we have to live and fight on",
        )

        expected.forEach { (date, answer) ->
            assertTrue(
                bookStep(date, "the-future-of-magic").label.contains(answer, ignoreCase = true),
                "$date missing $answer",
            )
        }
    }

    @Test
    fun `Virga route buys the two fixed Queen Honey jars`() {
        val text = day("2100-08-20").steps.joinToString("\n") { it.label }
        assertTrue(text.contains("Amblyrhy's Water Jug", ignoreCase = true))
        assertTrue(text.contains("buy both Queen's Honey Jars", ignoreCase = true))
        assertTrue(text.contains("only stocks two", ignoreCase = true))
    }

    @Test
    fun `both planned Tail Bait sessions target one Lord and two Queen Honey jars`() {
        listOf("2100-08-25", "2100-09-07").forEach { date ->
            val fishing = assertNotNull(
                day(date).steps.singleOrNull { it.activityRef == "metaphor.activity.fishing" },
                "$date fishing step",
            )
            val text = fishing.label
            assertTrue(text.contains("1 Lord of the Lake", ignoreCase = true), "$date missing Lord target")
            assertTrue(text.contains("2 Queen's Honey Jars", ignoreCase = true), "$date missing honey target")
            listOf("Give in to naptime", "Thrash and Splash", "Think like a fish").forEach { answer ->
                assertTrue(text.contains(answer, ignoreCase = true), "$date missing $answer")
            }
        }
    }

    @Test
    fun `Queen of Cuisine Soul supplies the third Lord before the final recipe`() {
        val september19 = day("2100-09-19").steps.joinToString("\n") { it.label }
        assertTrue(september19.contains("Indestructible Honey Cake", ignoreCase = true))
        assertTrue(september19.contains("save one for a request", ignoreCase = true))

        val soul = assertNotNull(day("2100-09-26").steps.firstOrNull {
            it.label.contains("Queen of Cuisine: Soul", ignoreCase = true)
        })
        assertTrue(soul.label.contains("saved Indestructible Honey Cake", ignoreCase = true))
        assertTrue(soul.label.contains("1 Lord of the Lake", ignoreCase = true))
        assertEquals(10, soul.statGains["eloquence"])

        val september30 = day("2100-09-30").steps.joinToString("\n") { it.label }
        assertTrue(september30.contains("Sublime Spoonful", ignoreCase = true))
        assertTrue(september30.contains("21st and final recipe", ignoreCase = true))
    }

    @Test
    fun `October fishing is optional rather than a first playthrough cooking dependency`() {
        listOf("2100-10-12", "2100-10-17").forEach { date ->
            val fishing = assertNotNull(
                day(date).steps.singleOrNull { it.activityRef == "metaphor.activity.fishing" },
                "$date fishing step",
            )
            assertTrue(fishing.label.startsWith("Optional", ignoreCase = true), "$date should be optional")
        }

        val october12 = day("2100-10-12").steps.joinToString("\n") { it.label }
        assertTrue(october12.contains("recipe inventory was already secured", ignoreCase = true))

        val october17 = day("2100-10-17").steps.joinToString("\n") { it.label }
        assertTrue(october17.contains("NG+ stockpile", ignoreCase = true))
        assertTrue(october17.contains("carry into New Game+", ignoreCase = true))
    }

    private fun bookStep(date: String, activitySuffix: String) = assertNotNull(
        day(date).steps.singleOrNull {
            it.activityRef == "metaphor.activity.book.$activitySuffix"
        },
        "$date $activitySuffix book step",
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
