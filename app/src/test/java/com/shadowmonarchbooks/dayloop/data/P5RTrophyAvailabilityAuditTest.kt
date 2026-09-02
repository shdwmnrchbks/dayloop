package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class P5RTrophyAvailabilityAuditTest {

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
    fun `p5r fixed story and location trophies use Royal availability instead of route-selected dates`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val achievements = loaded.achievements?.achievements.orEmpty().associateBy { it.title }
        fun available(title: String, date: String) {
            val achievement = achievements.getValue(title)
            assertEquals(date, achievement.availableFrom, "$title should use its independently checked Royal availability anchor")
            assertEquals(AchievementTrackingTypes.MANUAL, achievement.tracking.type)
            assertNull(achievement.expectedBy, "$title availability is not a completion-route deadline")
        }

        // Fixed story-resolution trophies. Palace heist dates are flexible, but
        // these trophy pop dates are not.
        available("Castle of Lust: Seized", "2016-05-02")
        available("Museum of Vanity: Repossessed", "2016-06-05")
        available("Bank of Gluttony: Cleaned Out", "2016-07-09")
        available("Pyramid of Wrath: Plundered", "2016-08-22")
        available("Spaceport of Greed: Obliterated", "2016-10-11")
        available("Casino of Jealousy: Bankrupted", "2016-11-20")
        available("Cruiser of Pride: Capsized", "2016-12-18")
        available("The Thorough Trickster", "2016-12-24")
        available("Take Back the Future", "2017-02-03")

        available("Spirit of Rebellion", "2016-04-11")
        available("I am Thou...", "2016-04-15")
        available("A Deadly Debut", "2016-04-18")
        available("Tactical Teamwork", "2016-04-18")
        available("Let's Blow It Up", "2016-04-18")
        available("You'd Better Hang On!", "2016-04-18")
        available("Technician", "2016-04-18")
        available("Phantom Thieves: Assemble!", "2016-05-05")
        available("Leblanc Buffer", "2016-05-05")
        available("One Step at a Time", "2016-05-07")
        available("Talent Thief", "2016-05-07")
        available("The Purpose of a Thief", "2016-05-18")
        available("Efficient Executioner", "2016-05-18")
        available("Trash Into Treasure", "2016-06-05")
        available("Dartslinger", "2016-06-06")
        available("A Hustler's Journey", "2016-06-06")
        available("A Serene Experience", "2016-06-06")
        available("Going Against the Crane", "2016-08-31")
        available("Awakening the Phantom Thieves", "2017-01-10")

        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        // Dayloop's route records the Apr 15 Persona-capture tutorial day, while
        // the optional Apr 18 trophy mechanics remain available even though this
        // completion route defers its long Castle run until Apr 24.
        assertTrue(days.getValue("2016-04-15").steps.any { "capture Personas" in it.label })
        assertEquals("2016-04-15", achievements.getValue("I am Thou...").availableFrom)
        assertTrue(days.getValue("2016-04-24").steps.any { "Infiltrate the castle" in it.label })
        assertEquals("2016-04-18", achievements.getValue("Tactical Teamwork").availableFrom)
        assertEquals("2016-04-18", achievements.getValue("Let's Blow It Up").availableFrom)
        assertEquals("2016-04-18", achievements.getValue("You'd Better Hang On!").availableFrom)
        assertEquals("2016-04-18", achievements.getValue("Technician").availableFrom)

        // The 5/7 tutorial Mementos request is mandatory and is the first
        // deterministic One Step at a Time trophy point.
        assertTrue(days.getValue("2016-05-07").steps.any { "Mementos" in it.label })
        assertEquals("2016-05-07", achievements.getValue("One Step at a Time").availableFrom)

        // The forced 6/5 Penguin Sniper introduction is not a playable
        // Dartslinger trophy opportunity. Earliest free-time play is after it.
        assertTrue(days.getValue("2016-06-05").steps.any { "darts" in it.label.lowercase() })
        assertEquals("2016-06-06", achievements.getValue("Dartslinger").availableFrom)

        // The Royal third-semester path awards this automatically during
        // Morgana's fixed Jan 10 awakening, before optional teammate awakenings.
        assertTrue(days.getValue("2017-01-10").steps.any { "Morgana's awakening" in it.label })
        assertEquals("2017-01-10", achievements.getValue("Awakening the Phantom Thieves").availableFrom)

        // The completion route deliberately postpones these optional trophies;
        // its chosen dates must not overwrite the facilities' real unlocks.
        assertTrue(days.getValue("2016-08-17").steps.any { "billiards" in it.label.lowercase() })
        assertTrue(days.getValue("2017-01-30").steps.any { "Kichijoji temple" in it.label })
        assertTrue(days.getValue("2016-09-02").steps.any { "arcade" in it.label.lowercase() })

        assertEquals("2016-06-06", achievements.getValue("A Hustler's Journey").availableFrom)
        assertEquals("2016-06-06", achievements.getValue("A Serene Experience").availableFrom)
        assertEquals("2016-08-31", achievements.getValue("Going Against the Crane").availableFrom)
    }
}
