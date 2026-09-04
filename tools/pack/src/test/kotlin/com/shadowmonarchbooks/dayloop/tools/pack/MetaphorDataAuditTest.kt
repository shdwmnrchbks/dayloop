package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorDataAuditTest {

    @Test
    fun `Metaphor identifies the authored completion route`() {
        val loaded = loadMetaphor()
        val pack = assertNotNull(loaded.pack)
        val route = assertNotNull(pack.routes.singleOrNull())

        assertEquals(Routes.DEFAULT, route.id)
        assertEquals("100% Completion Route", route.label)
        assertTrue(route.description.orEmpty().contains("not universal", ignoreCase = true))
    }

    @Test
    fun `Metaphor operation deadlines use actual failure dates`() {
        val loaded = loadMetaphor()
        val deadlines = assertNotNull(loaded.deadlines).deadlines.associateBy { it.id }

        val expected = mapOf(
            "metaphor.deadline.mission.prologue" to "2100-06-11",
            "metaphor.deadline.mission.grand-cathedral" to "2100-06-21",
            "metaphor.deadline.mission.kriegante-castle" to "2100-07-16",
            "metaphor.deadline.mission.charadrius" to "2100-08-12",
            "metaphor.deadline.mission.dragon-temple" to "2100-09-05",
            "metaphor.deadline.mission.prepare-final-battle" to "2100-09-22",
            "metaphor.deadline.mission.skybound-avatar" to "2100-10-25",
            "metaphor.deadline.mission.save-country" to "2100-10-26",
        )

        expected.forEach { (id, date) ->
            val deadline = deadlines.getValue(id)
            assertEquals("palace", deadline.kind, id)
            assertEquals(date, deadline.date, id)
            assertEquals(null, deadline.window, id)
        }
    }

    @Test
    fun `Metaphor route targets are not presented as hard deadlines`() {
        val loaded = loadMetaphor()
        val deadlines = assertNotNull(loaded.deadlines).deadlines.associateBy { it.id }

        val routeTargets = mapOf(
            "metaphor.deadline.mission.border-fort" to "2100-06-05",
            "metaphor.deadline.mission.nord-mines" to "2100-06-06",
            "metaphor.deadline.mission.opera-house" to "2100-09-10",
            "metaphor.deadline.mission.duel-louis" to "2100-09-23",
        )

        routeTargets.forEach { (id, date) ->
            val target = deadlines.getValue(id)
            assertEquals("routeTarget", target.kind, id)
            assertEquals(date, target.date, id)
            assertTrue(target.label.startsWith("Route target:"), target.label)
        }
    }

    @Test
    fun `Metaphor follower route dates are separate from game availability`() {
        val loaded = loadMetaphor()
        val bonds = assertNotNull(loaded.bonds).bonds.associateBy { it.id }

        fun rank(id: String, value: Int) = assertNotNull(
            bonds.getValue(id).ranks.firstOrNull { it.rank == value },
            "$id rank $value",
        )

        val gallica1 = rank("metaphor.bond.gallica", 1)
        assertEquals("2100-06-06", gallica1.availableFrom)
        assertEquals(null, gallica1.scheduledFor)
        val gallica3 = rank("metaphor.bond.gallica", 3)
        assertEquals("2100-07-05", gallica3.availableFrom)
        assertEquals("2100-07-05", gallica3.scheduledFor)
        val gallica4 = rank("metaphor.bond.gallica", 4)
        assertEquals("2100-07-23", gallica4.availableFrom)
        assertEquals("2100-07-25", gallica4.scheduledFor)
        val gallica6 = rank("metaphor.bond.gallica", 6)
        assertEquals("2100-08-19", gallica6.availableFrom)
        assertEquals("2100-08-19", gallica6.scheduledFor)
        val gallica8 = rank("metaphor.bond.gallica", 8)
        assertEquals("2100-09-24", gallica8.availableFrom)
        assertEquals(null, gallica8.scheduledFor)

        val strohl1 = rank("metaphor.bond.strohl", 1)
        assertEquals("2100-06-06", strohl1.availableFrom)
        assertEquals(null, strohl1.scheduledFor)
        val strohl2 = rank("metaphor.bond.strohl", 2)
        assertEquals("2100-06-13", strohl2.scheduledFor)
        assertEquals(null, strohl2.availableFrom)

        val maria1 = rank("metaphor.bond.maria", 1)
        assertEquals("2100-06-10", maria1.availableFrom)
        assertEquals(null, maria1.scheduledFor)
        val maria2 = rank("metaphor.bond.maria", 2)
        assertEquals("2100-06-30", maria2.scheduledFor)
        assertEquals(null, maria2.availableFrom)

        val catherina4 = rank("metaphor.bond.catherina", 4)
        assertEquals("2100-08-03", catherina4.scheduledFor)
        assertEquals("2100-07-23", catherina4.availableFrom)
        assertEquals("2100-08-13", catherina4.availableUntil)

        val more1 = rank("metaphor.bond.more", 1)
        assertEquals("2100-06-05", more1.availableFrom)
        assertEquals(null, more1.scheduledFor)
        val more2 = rank("metaphor.bond.more", 2)
        assertEquals(null, more2.availableFrom)
        assertEquals(null, more2.scheduledFor)
        val more3 = rank("metaphor.bond.more", 3)
        assertEquals("2100-07-07", more3.scheduledFor)
        assertEquals(null, more3.availableFrom)
    }

    @Test
    fun `Metaphor missable windows preserve their real availability`() {
        val loaded = loadMetaphor()
        val deadlines = assertNotNull(loaded.deadlines).deadlines.associateBy { it.id }
        val activities = assertNotNull(loaded.activities).activities.associateBy { it.id }

        val julian = deadlines.getValue("metaphor.deadline.missable.julian-book")
        assertEquals("2100-07-23", assertNotNull(julian.window).start)
        assertEquals("2100-08-13", julian.window?.end)
        assertTrue(
            activities.getValue("metaphor.activity.book.the-future-of-magic")
                .notes.orEmpty()
                .contains("08/13"),
        )

        val inn = deadlines.getValue("metaphor.deadline.missable.inn-cooking")
        assertEquals("2100-06-12", assertNotNull(inn.window).start)
        assertEquals("2100-09-25", inn.window?.end)
        assertTrue(
            activities.getValue("metaphor.activity.cooking.inn")
                .notes.orEmpty()
                .contains("before 09/26", ignoreCase = true),
        )
    }

    @Test
    fun `Metaphor route schedules every required candidate debate with audited virtue gains`() {
        val loaded = loadMetaphor()
        val days = loaded.walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .associateBy { it.date }

        data class DebateCheck(
            val date: String,
            val eloquence: Int,
            val imagination: Int,
        )

        val expected = mapOf(
            "Loveless" to DebateCheck("2100-07-06", 10, 5),
            "Lina" to DebateCheck("2100-07-12", 10, 5),
            "Roger" to DebateCheck("2100-07-14", 10, 5),
            "Jin" to DebateCheck("2100-07-23", 11, 5),
            "Glodell" to DebateCheck("2100-07-24", 11, 5),
            "Rudolf" to DebateCheck("2100-07-26", 11, 5),
            "Milo" to DebateCheck("2100-07-27", 11, 5),
            "Julian" to DebateCheck("2100-09-13", 15, 7),
        )

        expected.forEach { (candidate, check) ->
            val day = assertNotNull(days[check.date], "$candidate debate date")
            val step = assertNotNull(
                day.steps.singleOrNull { it.label.contains("Debate $candidate", ignoreCase = true) },
                "$candidate debate step on ${check.date}",
            )
            assertEquals(check.eloquence, step.statGains["eloquence"], "$candidate Eloquence")
            assertEquals(check.imagination, step.statGains["imagination"], "$candidate Imagination")
        }
    }

    @Test
    fun `Metaphor walkthrough copy agrees with corrected missable windows`() {
        val loaded = loadMetaphor()
        val days = loaded.walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .associateBy { it.date }

        val july23 = assertNotNull(days["2100-07-23"])
        assertTrue(july23.steps.any {
            it.label.contains("Future of Magic", ignoreCase = true) &&
                it.label.contains("August 13", ignoreCase = true)
        })

        val september22 = assertNotNull(days["2100-09-22"])
        assertTrue(september22.steps.any {
            it.label.contains("inn cooking", ignoreCase = true) &&
                it.label.contains("September 25", ignoreCase = true)
        })
    }

    @Test
    fun `Metaphor route dungeon days can precede the actual deadline`() {
        val loaded = loadMetaphor()
        val days = loaded.walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .associateBy { it.date }

        assertTrue(assertNotNull(days["2100-06-12"]).steps.any {
            it.label.contains("cathedral", ignoreCase = true)
        })
        assertTrue(assertNotNull(days["2100-07-05"]).steps.any {
            it.label.contains("Kriegante Castle", ignoreCase = true)
        })
        assertTrue(assertNotNull(days["2100-07-25"]).steps.any {
            it.label.contains("Charadrius", ignoreCase = true)
        })
        assertTrue(assertNotNull(days["2100-08-19"]).steps.any {
            it.label.contains("Dragon Temple", ignoreCase = true)
        })
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
