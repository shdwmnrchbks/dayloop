package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
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
    fun `p5r Jazz Jin separates first trophy opportunity from route Akechi timing and Sunday skill choices`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        val achievement = loaded.achievements?.achievements.orEmpty()
            .single { it.title == "A Night in Kichijoji" }
        assertEquals("2016-06-26", achievement.availableFrom)
        assertNull(achievement.expectedBy, "Jazz Jin is state-gated by Justice rank 4, not a completion-route deadline")

        val justice = loaded.confidants?.bonds.orEmpty().single { it.label == "Justice" }
        val routeRank4 = justice.ranks.single { it.rank == 4 }
        assertEquals("2016-08-06", routeRank4.scheduledFor)
        assertNull(routeRank4.availableFrom, "the route's Aug 6 Justice rank 4 must not be presented as universal availability")

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
