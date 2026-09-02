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
        available("Punch That Clock!", "2016-04-18")
        available("Easy Money", "2016-04-25")
        available("Tokyo Tourist", "2016-04-28")
        available("Phantom Thieves: Assemble!", "2016-05-05")
        available("Leblanc Buffer", "2016-05-05")
        available("One Step at a Time", "2016-05-07")
        available("Talent Thief", "2016-05-07")
        available("Jose's Favorite Customer", "2016-05-09")
        available("The Phantom Philatelist", "2016-05-09")
        available("The Deviated Cognition", "2016-05-09")
        available("The Purpose of a Thief", "2016-05-18")
        available("Efficient Executioner", "2016-05-18")
        available("A Grand Experiment", "2016-05-20")
        available("Trash Into Treasure", "2016-06-05")
        available("Intensive Training", "2016-06-05")
        available("Dartslinger", "2016-06-06")
        available("A Hustler's Journey", "2016-06-06")
        available("A Serene Experience", "2016-06-06")
        available("It's Showtime!", "2016-06-21")
        available("Accident-Prone", "2016-06-21")
        available("A Night in Kichijoji", "2016-06-26")
        available("Angler's Debut", "2016-07-04")
        available("The Search for Power", "2016-07-12")
        available("Success Built on Sacrifice", "2016-07-26")
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

        // Jobs and the weekly lottery both unlock with the first free-roam week.
        // Easy Money is still RNG; Apr 25 is merely the first possible posted
        // result from an Apr 18 ordinary ticket, never a guaranteed completion date.
        assertEquals("2016-04-18", achievements.getValue("Punch That Clock!").availableFrom)
        assertEquals("2016-04-25", achievements.getValue("Easy Money").availableFrom)

        // Independent Royal schedules demonstrate Chariot rank 4 and Ogikubo on
        // Apr 28, which immediately awards Tokyo Tourist. This completion route
        // delays the same rank to Jun 1; that route choice must not become the
        // trophy's availability metadata.
        assertEquals("2016-04-28", achievements.getValue("Tokyo Tourist").availableFrom)
        assertTrue(days.getValue("2016-06-01").steps.any { "Chariot reaches rank 4" in it.label })

        // Aiyatsbus opens on May 9. This is the first free Mementos exploration
        // state where Jose's flowers/stamps are usable and random deviations can
        // occur. All three remain manual because Jose/deviation encounters are
        // not guaranteed by this completion route on May 9.
        assertTrue(days.getValue("2016-05-09").steps.any { "Bark and Bite of a Bully" in it.label })
        assertEquals("2016-05-09", achievements.getValue("Jose's Favorite Customer").availableFrom)
        assertEquals("2016-05-09", achievements.getValue("The Phantom Philatelist").availableFrom)
        assertEquals("2016-05-09", achievements.getValue("The Deviated Cognition").availableFrom)

        // The route reaches Strength rank 3 on Jun 2. Kichijoji and Mantra Ganda
        // open on Jun 5, so incense can be bought and used in Lockdown from that
        // date even though this route waits for a later shopping-channel set.
        assertTrue(days.getValue("2016-06-02").steps.any { "Strength reaches rank 3" in it.label })
        assertTrue(days.getValue("2016-06-05").steps.any { "Kichijoji" in it.label })
        assertEquals("2016-06-05", achievements.getValue("Intensive Training").availableFrom)

        // Optimized Royal routing can unlock Jazz Jin with Justice rank 4 on Jun
        // 25; the trophy requires a separate subsequent visit. This completion
        // route schedules Justice rank 4 much later and must not leak that date.
        assertEquals("2016-06-26", achievements.getValue("A Night in Kichijoji").availableFrom)

        // Royal unlocks Ichigaya through Ryuji's Jul 3 fishing hangout. That
        // introductory visit unlocks the location but does not award Angler's
        // Debut; the first independent fishing visit can happen the next day.
        assertTrue(days.getValue("2016-07-03").steps.any { "Ryuji's invitation" in it.label })
        assertEquals("2016-07-04", achievements.getValue("Angler's Debut").availableFrom)

        // Royal's Kaitul path becomes freely explorable on Jul 12. Earlier paths
        // contain 20 stamps total and Kaitul supplies enough additional fixed/
        // random podiums on that first visit to max the cheapest cognition line.
        assertEquals("2016-07-12", achievements.getValue("The Search for Power").availableFrom)

        // Earliest Palace-route security gates these Velvet Room mechanics. The
        // completion route can secure the same routes later without changing
        // their first possible Royal trophy dates.
        assertEquals("2016-05-20", achievements.getValue("A Grand Experiment").availableFrom)
        assertEquals("2016-06-21", achievements.getValue("Accident-Prone").availableFrom)
        assertEquals("2016-07-26", achievements.getValue("Success Built on Sacrifice").availableFrom)

        // Showtime's forced Bank tutorial does not itself award the trophy, but
        // subsequent Showtime activations are eligible from Jun 21 onward.
        assertEquals("2016-06-21", achievements.getValue("It's Showtime!").availableFrom)

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
