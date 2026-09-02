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

        available("Spirit of Rebellion", "2016-04-11")
        available("A Deadly Debut", "2016-04-18")
        available("Phantom Thieves: Assemble!", "2016-05-05")
        available("One Step at a Time", "2016-05-07")
        available("Talent Thief", "2016-05-07")
        available("Trash Into Treasure", "2016-06-05")
        available("Dartslinger", "2016-06-06")
        available("A Hustler's Journey", "2016-06-06")
        available("A Serene Experience", "2016-06-06")
        available("Going Against the Crane", "2016-08-31")

        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        // The 5/7 tutorial Mementos request is mandatory and is the first
        // deterministic One Step at a Time trophy point.
        assertTrue(days.getValue("2016-05-07").steps.any { "Mementos" in it.label })
        assertEquals("2016-05-07", achievements.getValue("One Step at a Time").availableFrom)

        // The forced 6/5 Penguin Sniper introduction is not a playable
        // Dartslinger trophy opportunity. Earliest free-time play is after it.
        assertTrue(days.getValue("2016-06-05").steps.any { "darts" in it.label.lowercase() })
        assertEquals("2016-06-06", achievements.getValue("Dartslinger").availableFrom)

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
