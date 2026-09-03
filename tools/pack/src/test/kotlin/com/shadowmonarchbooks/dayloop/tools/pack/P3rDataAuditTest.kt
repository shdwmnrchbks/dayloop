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
