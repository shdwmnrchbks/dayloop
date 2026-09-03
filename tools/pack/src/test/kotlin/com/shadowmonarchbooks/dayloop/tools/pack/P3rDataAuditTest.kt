package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class P3rDataAuditTest {

    @Test
    fun `P3R identifies the authored completion route`() {
        val loaded = loadP3r()
        val pack = assertNotNull(loaded.pack)
        val route = assertNotNull(pack.routes.singleOrNull())

        assertEquals(Routes.DEFAULT, route.id)
        assertEquals("100% Completion Route", route.label)
        assertTrue(route.description.orEmpty().contains("not universal", ignoreCase = true))
    }

    @Test
    fun `P3R Social Links use canonical identities and route dates`() {
        val loaded = loadP3r()
        val bonds = assertNotNull(loaded.bonds).bonds
        val byId = bonds.associateBy { it.id }

        assertEquals(22, bonds.size)
        assertEquals("Kenji Tomochika", byId.getValue("p3r.bond.magician").characterLabel)
        assertEquals("Nozomi Suemitsu", byId.getValue("p3r.bond.moon").characterLabel)
        assertEquals("Maiko Oohashi", byId.getValue("p3r.bond.hanged-man").characterLabel)
        assertEquals("President Tanaka", byId.getValue("p3r.bond.devil").characterLabel)
        assertEquals("Mutatsu", byId.getValue("p3r.bond.tower").characterLabel)
        assertEquals("Keisuke Hiraga", byId.getValue("p3r.bond.fortune").characterLabel)
        assertEquals("Mamoru Hayase", byId.getValue("p3r.bond.star").characterLabel)
        assertEquals("Akinari Kamiki", byId.getValue("p3r.bond.sun").characterLabel)

        val automatic = setOf("p3r.bond.fool", "p3r.bond.death", "p3r.bond.judgment")
        bonds.filterNot { it.id in automatic }.forEach { bond ->
            assertEquals((1..10).toList(), bond.ranks.map { it.rank }, "${bond.id}: incomplete rank ladder")
            assertTrue(bond.ranks.all { it.availableFrom == null }, "${bond.id}: route dates must not live in availableFrom")
            assertTrue(bond.ranks.all { it.scheduledFor != null }, "${bond.id}: imported route rank needs scheduledFor")
        }

        val bondsWithExplicitAvailability = bonds
            .filter { bond -> bond.ranks.any { it.availableFrom != null } }
            .mapTo(mutableSetOf()) { it.id }
        assertEquals(automatic, bondsWithExplicitAvailability)

        fun scheduledFor(id: String, rank: Int) = assertNotNull(
            byId.getValue(id).ranks.firstOrNull { it.rank == rank },
            "$id rank $rank",
        ).scheduledFor

        assertEquals("2009-07-28", scheduledFor("p3r.bond.devil", 5))
        assertEquals("2009-09-01", scheduledFor("p3r.bond.devil", 10))
        assertEquals("2009-07-31", scheduledFor("p3r.bond.tower", 4))
        assertEquals("2009-09-07", scheduledFor("p3r.bond.lovers", 2))
        assertEquals("2009-09-10", scheduledFor("p3r.bond.lovers", 3))
    }

    @Test
    fun `P3R automatic Social Links preserve story skips and floor progression`() {
        val loaded = loadP3r()
        val byId = assertNotNull(loaded.bonds).bonds.associateBy { it.id }

        val fool = byId.getValue("p3r.bond.fool")
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7, 9, 10), fool.ranks.map { it.rank })
        assertEquals(
            listOf(
                "2009-04-18",
                "2009-04-20",
                "2009-05-09",
                "2009-07-07",
                "2009-07-22",
                "2009-11-02",
                "2009-11-04",
                "2009-11-28",
                "2009-12-31",
            ),
            fool.ranks.map { it.availableFrom },
        )
        assertTrue(fool.ranks.all { it.scheduledFor == null })

        val death = byId.getValue("p3r.bond.death")
        assertEquals(listOf(1, 3, 5, 6, 8, 10), death.ranks.map { it.rank })
        assertEquals(
            listOf("2009-06-12", "2009-07-12", "2009-08-07", "2009-09-12", "2009-10-06", "2009-11-04"),
            death.ranks.map { it.availableFrom },
        )
        assertTrue(death.ranks.all { it.scheduledFor == null })

        val judgment = byId.getValue("p3r.bond.judgment")
        assertEquals((1..10).toList(), judgment.ranks.map { it.rank })
        assertEquals("2009-12-31", judgment.ranks.first().availableFrom)
        assertTrue(judgment.ranks.drop(1).all { it.availableFrom == null && it.scheduledFor == null })
        val expectedFloors = listOf("227F", "230F", "236F", "241F", "246F", "247F", "253F", "254F", "255F")
        assertEquals(expectedFloors, judgment.ranks.drop(1).map { rank ->
            expectedFloors.first { floor -> rank.notes.orEmpty().contains(floor) }
        })
    }

    @Test
    fun `P3R walkthrough keeps Magician and Moon identities distinct`() {
        val loaded = loadP3r()
        val steps = loaded.walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { walkthrough -> walkthrough.file.days }
            .flatMap { day -> day.steps }

        val magicianRankSteps = steps.filter { it.label.contains("Magician reaches rank", ignoreCase = true) }
        val moonRankSteps = steps.filter { it.label.contains("Moon reaches rank", ignoreCase = true) }

        assertTrue(magicianRankSteps.isNotEmpty())
        assertTrue(moonRankSteps.isNotEmpty())
        assertTrue(magicianRankSteps.all { it.label.contains("Kenji", ignoreCase = true) })
        assertFalse(magicianRankSteps.any { it.label.contains("Junpei", ignoreCase = true) })
        assertTrue(moonRankSteps.all { it.label.contains("Nozomi", ignoreCase = true) })
        assertFalse(moonRankSteps.any { it.label.contains("Kenji", ignoreCase = true) })
    }

    @Test
    fun `P3R restored late July completion route steps`() {
        val loaded = loadP3r()
        val july = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-07" },
        ).file
        val days = july.days.associateBy { it.date }

        val july28 = assertNotNull(days["2009-07-28"])
        assertTrue(july28.steps.any { it.label.contains("track team training", ignoreCase = true) })
        assertTrue(july28.steps.any { it.label.contains("Devil reaches rank 5", ignoreCase = true) })

        val july31 = assertNotNull(days["2009-07-31"])
        assertTrue(july31.steps.any { it.label.contains("track team training", ignoreCase = true) })
        assertTrue(july31.steps.any { it.label.contains("Tower reaches rank 4", ignoreCase = true) })
    }

    @Test
    fun `P3R exam deadlines use complete windows and top-class requirements`() {
        val loaded = loadP3r()
        val deadlines = assertNotNull(loaded.deadlines).deadlines.associateBy { it.id }

        data class ExamCheck(val id: String, val start: String, val end: String, val academicsRank: Int)
        val checks = listOf(
            ExamCheck("p3r.deadline.exams.may", "2009-05-18", "2009-05-23", 3),
            ExamCheck("p3r.deadline.exams.july", "2009-07-14", "2009-07-18", 4),
            ExamCheck("p3r.deadline.exams.october", "2009-10-13", "2009-10-17", 5),
            ExamCheck("p3r.deadline.exams.december", "2009-12-14", "2009-12-19", 6),
        )

        checks.forEach { check ->
            val deadline = deadlines.getValue(check.id)
            val window = assertNotNull(deadline.window, check.id)
            assertEquals(check.start, window.start, check.id)
            assertEquals(check.end, window.end, check.id)
            assertTrue(deadline.label.contains("Academics rank ${check.academicsRank}"), deadline.label)
            assertTrue(deadline.label.contains("all answers correct", ignoreCase = true), deadline.label)
        }

        val finalExamDays = mapOf(
            "2009-05" to "2009-05-23",
            "2009-07" to "2009-07-18",
            "2009-10" to "2009-10-17",
            "2009-12" to "2009-12-19",
        )
        finalExamDays.forEach { (month, date) ->
            val walkthrough = assertNotNull(
                loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == month },
                month,
            ).file
            val day = assertNotNull(walkthrough.days.firstOrNull { it.date == date }, date)
            assertEquals("exam", day.dayKind, date)
            assertTrue(day.steps.any { it.label.contains("Final exam day", ignoreCase = true) }, date)
        }
    }

    @Test
    fun `P3R April prep entries are route targets rather than hard deadlines`() {
        val loaded = loadP3r()
        val deadlines = assertNotNull(loaded.deadlines).deadlines.associateBy { it.id }

        val firstTartarus = deadlines.getValue("p3r.deadline.tartarus.first-cycle")
        assertEquals("routeTarget", firstTartarus.kind)
        assertEquals("2009-04-20", assertNotNull(firstTartarus.window).start)
        assertEquals("2009-04-26", firstTartarus.window?.end)
        assertTrue(firstTartarus.label.startsWith("Route target:"))

        val muscleDrink = deadlines.getValue("p3r.deadline.aohige-sale.muscle-drink")
        assertEquals("routeTarget", muscleDrink.kind)
        assertEquals("2009-04-25", muscleDrink.date)
        assertTrue(muscleDrink.label.startsWith("Route prep:"))
        assertFalse(muscleDrink.label.contains("one-day", ignoreCase = true))
        assertFalse(muscleDrink.label.contains("missable", ignoreCase = true))
    }

    @Test
    fun `P3R answer sheets serve answer text rather than menu indices`() {
        val loaded = loadP3r()
        val answers = assertNotNull(loaded.answers).answers
        val deadlines = assertNotNull(loaded.deadlines).deadlines.map { it.id }.toSet()

        assertEquals(53, answers.size)
        assertTrue(answers.flatMap { it.answers }.all { answer ->
            answer.isNotBlank() && answer.toIntOrNull() == null
        })

        fun answer(id: String) = assertNotNull(answers.firstOrNull { it.id == id }, id)
        assertEquals(listOf("Vivid Carp Streamers"), answer("p3r.answers.class.2009-04-08").answers)
        assertEquals(listOf("Middens"), answer("p3r.answers.class.2009-04-18").answers)
        assertEquals(listOf("A"), answer("p3r.answers.class.2009-04-27").answers)

        answers.filter { it.kind == "exam" }.forEach { sheet ->
            val deadlineRef = assertNotNull(sheet.deadlineRef, "${sheet.id}: exam answer needs deadlineRef")
            assertTrue(deadlineRef in deadlines, "${sheet.id}: unresolved deadlineRef $deadlineRef")
        }
    }

    @Test
    fun `P3R April route uses Kenji for Magician and audited Courage gain`() {
        val loaded = loadP3r()
        val april = assertNotNull(
            loaded.walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-04" },
        ).file
        val days = april.days.associateBy { it.date }

        val april22 = assertNotNull(days["2009-04-22"])
        assertTrue(april22.steps.any { it.label.contains("Kenji Tomochika", ignoreCase = true) })
        assertFalse(april22.steps.any { it.label.contains("Junpei", ignoreCase = true) })

        listOf("2009-04-28", "2009-04-30").forEach { date ->
            val day = assertNotNull(days[date])
            assertTrue(day.steps.any { it.label.contains("Kenji", ignoreCase = true) && it.label.contains("Magician", ignoreCase = true) })
            assertFalse(day.steps.any { it.label.contains("Junpei", ignoreCase = true) })
        }

        val burger = assertNotNull(days["2009-04-26"])
            .steps
            .firstOrNull { it.label.contains("Mystery Burger", ignoreCase = true) }
        assertNotNull(burger)
        assertEquals(2, burger.statGains["courage"])
    }

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
