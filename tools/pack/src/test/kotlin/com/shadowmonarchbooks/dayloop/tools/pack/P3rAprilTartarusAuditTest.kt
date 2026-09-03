package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class P3rAprilTartarusAuditTest {

    @Test
    fun `P3R April Thebel clear preserves request and gatekeeper prep`() {
        val april = assertNotNull(
            loadP3r().walkthroughs.firstOrNull { it.routeId == Routes.DEFAULT && it.month == "2009-04" },
        ).file
        val days = april.days.associateBy { it.date }

        val tartarus = assertNotNull(days["2009-04-23"])
            .steps
            .firstOrNull { it.label.contains("Tartarus", ignoreCase = true) }
        assertNotNull(tartarus)
        assertTrue(tartarus.label.contains("22F"))
        assertTrue(tartarus.label.contains("Old Document 01", ignoreCase = true))
        assertTrue(tartarus.label.contains("Odd Morsel", ignoreCase = true))
        assertTrue(tartarus.label.contains("5F Fire", ignoreCase = true))
        assertTrue(tartarus.label.contains("11F Wind", ignoreCase = true))
        assertTrue(tartarus.label.contains("Magic Hands", ignoreCase = true))
        assertTrue(tartarus.label.contains("17F Electricity", ignoreCase = true))

        val carry = assertNotNull(days["2009-04-23"])
            .steps
            .firstOrNull { it.label.contains("Hermit", ignoreCase = true) && it.label.contains("Hanged-Man", ignoreCase = true) }
        assertNotNull(carry)
        assertTrue(carry.label.contains("Nekomata + Omoikane", ignoreCase = true))
        assertTrue(carry.label.contains("Orpheus + Omoikane", ignoreCase = true))

        val muscleDrink = assertNotNull(days["2009-04-25"])
            .steps
            .firstOrNull { it.label.contains("Muscle Drink", ignoreCase = true) }
        assertNotNull(muscleDrink)
        assertTrue(muscleDrink.label.contains("Saturday", ignoreCase = true))
        assertFalse(muscleDrink.label.contains("one-day", ignoreCase = true))
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
