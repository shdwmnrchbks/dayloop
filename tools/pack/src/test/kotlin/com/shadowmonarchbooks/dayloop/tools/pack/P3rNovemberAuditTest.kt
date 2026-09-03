package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class P3rNovemberAuditTest {

    @Test
    fun `P3R November automatic Social Link story ranks stay represented`() {
        val november = novemberDays()

        val november2 = assertNotNull(november["2009-11-02"])
        assertTrue(november2.steps.any { it.label.contains("Fool reaches rank 6", ignoreCase = true) })

        val november4 = assertNotNull(november["2009-11-04"])
        assertEquals("story", november4.dayKind)
        assertTrue(november4.steps.any { it.label.contains("Death reaches rank 10", ignoreCase = true) })
        assertTrue(november4.steps.any { it.label.contains("MAX", ignoreCase = true) && it.label.contains("Death", ignoreCase = true) })
        assertTrue(november4.steps.any { it.label.contains("skips rank 9", ignoreCase = true) })
        assertTrue(november4.steps.any { it.label.contains("Fool reaches rank 7", ignoreCase = true) })

        val november28 = assertNotNull(november["2009-11-28"])
        assertTrue(november28.steps.any { it.label.contains("Fool reaches rank 9", ignoreCase = true) })
        assertTrue(november28.steps.any { it.label.contains("skips rank 8", ignoreCase = true) })
    }

    @Test
    fun `P3R November Elizabeth request 96 preserves the Kyoto prerequisite chain`() {
        val november = novemberDays()

        val november6 = assertNotNull(november["2009-11-06"])
        val acceptIndex = november6.steps.indexOfFirst {
            it.label.contains("accept Requests #92", ignoreCase = true) &&
                it.label.contains("#93", ignoreCase = true) &&
                it.label.contains("#94", ignoreCase = true) &&
                it.label.contains("#96", ignoreCase = true)
        }
        val friendlyIndex = november6.steps.indexOfFirst {
            it.label.contains("Request #96", ignoreCase = true) &&
                it.label.contains("Friendly Student", ignoreCase = true) &&
                it.label.contains("before", ignoreCase = true) &&
                it.label.contains("Kyoto", ignoreCase = true)
        }
        assertTrue(acceptIndex >= 0)
        assertTrue(friendlyIndex > acceptIndex)

        val november17 = assertNotNull(november["2009-11-17"])
        val drinks = assertNotNull(november17.steps.firstOrNull { it.label.contains("Request #96", ignoreCase = true) })
        listOf("Durian Soda", "Jumbo Juice", "V6").forEach { drink ->
            assertTrue(drinks.label.contains(drink, ignoreCase = true), drinks.label)
        }

        val november28 = assertNotNull(november["2009-11-28"])
        val completion = assertNotNull(november28.steps.firstOrNull { it.label.contains("Request #96", ignoreCase = true) })
        listOf("Durian Soda", "Jumbo Juice", "V6", "Friendly Student", "¥5,000", "Oden Juice", "Elizabeth", "complete").forEach { term ->
            assertTrue(completion.label.contains(term, ignoreCase = true), completion.label)
        }
    }

    @Test
    fun `P3R November new request batch closes restroom flowers and timed party-item chains`() {
        val november = novemberDays()
        val november6 = assertNotNull(november["2009-11-06"])

        val restroom = assertNotNull(november6.steps.firstOrNull { it.label.contains("Request #92", ignoreCase = true) })
        assertTrue(restroom.label.contains("restroom", ignoreCase = true))
        assertTrue(restroom.label.contains("scrub harder", ignoreCase = true))
        assertTrue(restroom.label.contains("complete", ignoreCase = true))

        val flowers = assertNotNull(november6.steps.firstOrNull { it.label.contains("Request #93", ignoreCase = true) })
        assertTrue(flowers.label.contains("rooftop", ignoreCase = true))
        assertTrue(flowers.label.contains("water", ignoreCase = true))
        assertTrue(flowers.label.contains("complete", ignoreCase = true))

        val request94 = assertNotNull(november6.steps.firstOrNull {
            it.label.contains("Request #94", ignoreCase = true) && it.label.contains("Gourmet Dog Food", ignoreCase = true)
        })
        assertTrue(request94.label.contains("Request #95", ignoreCase = true))
        val request95 = assertNotNull(november6.steps.firstOrNull {
            it.label.contains("Request #95", ignoreCase = true) && it.label.contains("Featherman", ignoreCase = true)
        })
        assertTrue(request95.label.contains("November 30", ignoreCase = true))
    }

    @Test
    fun `P3R November forced travel and career blocks remain story days`() {
        val november = novemberDays()
        assertTrue(assertNotNull(november["2009-11-03"]).steps.any { it.label.contains("Full moon", ignoreCase = true) })

        listOf("2009-11-17", "2009-11-18", "2009-11-19").forEach { date ->
            val day = assertNotNull(november[date], date)
            assertEquals("story", day.dayKind)
            assertTrue(day.steps.any { it.label.contains("Kyoto", ignoreCase = true) })
        }
        listOf("2009-11-24", "2009-11-25", "2009-11-26").forEach { date ->
            val day = assertNotNull(november[date], date)
            assertEquals("story", day.dayKind)
            assertTrue(day.steps.any { it.label.contains("Career Experience", ignoreCase = true) })
        }
    }

    private fun novemberDays() = assertNotNull(
        loadP3r().walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-11" },
        "2009-11",
    ).file.days.associateBy { it.date }

    private fun loadP3r() = PackLoader.load(p3rDir()).also { loaded ->
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
    }

    private fun p3rDir(): Path {
        val candidates = listOf(
            Path.of("content", "packs", "p3r"),
            Path.of("..", "..", "content", "packs", "p3r"),
        )
        return candidates.firstOrNull { Files.isDirectory(it) }
            ?: error("Cannot locate content/packs/p3r from ${Path.of("").toAbsolutePath()}")
    }
}
