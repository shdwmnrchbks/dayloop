package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RBattingCageAuditTest {

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
    fun `p5r Batter Up availability and route stay distinct from the Thieves Den home run grind`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val achievement = loaded.achievements?.achievements.orEmpty()
            .single { it.title == "Batter Up!" }
        assertEquals("Hit a home run at the batting cages.", achievement.description)
        assertEquals("2016-04-18", achievement.availableFrom, "Royal's Yongen-Jaya batting cages are usable in the April early game")
        assertEquals(AchievementTrackingTypes.EVENT, achievement.tracking.type)
        assertEquals("2016-08-18", achievement.expectedBy)

        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }
        val august18 = days.getValue("2016-08-18").steps.single { "batting cages" in it.label.lowercase() }
        assertTrue("home run" in august18.label.lowercase())
        assertTrue("reload" in august18.label.lowercase())
        assertEquals(mapOf("proficiency" to 2), august18.statGains)

        val battingScience = loaded.activities?.activities.orEmpty()
            .single { it.id == "p5r.activity.book.batting-science" }
        assertTrue(battingScience.location.orEmpty().contains("Shinjuku bookstore"))
        assertTrue(battingScience.notes.orEmpty().contains("after using the batting cages"))
        assertTrue(battingScience.notes.orEmpty().contains("Third Eye"))

        val bookPurchase = days.getValue("2016-08-18").steps.single { "Batting Science" in it.label }
        assertTrue("Shinjuku" in bookPurchase.label)

        // Royal's separate Thieves' Den Home Run King award requires 30 home
        // runs. Dayloop's first-class trophy catalog should not inflate the much
        // simpler Batter Up! requirement into that unrelated completion grind.
        val description = achievement.description.orEmpty()
        assertTrue(description.contains("Hit a ball"))
        assertTrue(!description.contains("home run", ignoreCase = true))
        assertTrue(!loaded.deadlines?.deadlines.orEmpty().any { it.label.contains("30 home runs", ignoreCase = true) })
    }
}
