package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class P5RJazzJinAuditTest {

    private fun contentPacksDir(): Path? {
        var dir: Path? = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        repeat(5) {
            val candidate = dir?.resolve("content")?.resolve("packs")
            if (candidate != null && candidate.isDirectory()) return candidate
            dir = dir?.parent
        }
        return null
    }

    @Test
    fun `p5r late Sunday Jazz Jin route keeps party skills and Futaba navigator skills mutually exclusive`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        fun jazzLabel(date: String): String =
            days.getValue(date).steps.single { "jazz club" in it.label.lowercase() }.label

        val alternatives = listOf(
            Triple("2016-12-04", "Heat Riser", "Support Plus 1"),
            Triple("2016-12-11", "Debilitate", "Support Plus 2"),
            Triple("2017-01-15", "Ali Dance", "Support Plus 3"),
            Triple("2017-01-22", "Arms Master", "Support Rate Up"),
        )

        alternatives.forEach { (date, partySkill, futabaSkill) ->
            val label = jazzLabel(date)
            assertTrue("non-Futaba" in label, "$date should identify the normal Sunday skill as a non-Futaba invite")
            assertTrue(partySkill in label, "$date should name $partySkill")
            assertTrue("alternatively invite Futaba" in label, "$date should present Futaba as an alternative invite")
            assertTrue(futabaSkill in label, "$date should name Futaba's progressive navigator skill $futabaSkill")
            assertTrue("instead" in label, "$date should make the Futaba reward mutually exclusive with the normal Sunday skill")
            assertFalse("also gains" in label.lowercase(), "$date must not imply Futaba receives both rewards from one visit")
            assertFalse("also learns" in label.lowercase(), "$date must not imply Futaba receives both rewards from one visit")
        }

        val spellMaster = jazzLabel("2017-01-29")
        assertTrue("non-Futaba" in spellMaster)
        assertTrue("Spell Master" in spellMaster)
        assertTrue("no further Jazz Jin skill after Support Rate Up" in spellMaster)
    }
}
