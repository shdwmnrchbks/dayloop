package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RHomeShoppingAuditTest {

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
    fun `p5r authored Home Shopping reminders stay on real Royal broadcasts with audited set identities`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val shoppingSteps = loaded.walkthroughs
            .flatMap { it.file.days }
            .flatMap { day -> day.steps.map { step -> day.date to step.label } }
            .filter { (_, label) -> label.contains("Shopping Program") }
            .associate { it }

        val expectedDates = setOf(
            "2016-04-24",
            "2016-05-08",
            "2016-05-15",
            "2016-05-22",
            "2016-05-29",
            "2016-06-19",
            "2016-06-26",
            "2016-07-03",
            "2016-08-07",
            "2016-08-14",
            "2016-09-25",
            "2016-10-02",
            "2016-11-06",
            "2016-11-13",
            "2016-11-27",
            "2016-12-11",
            "2017-01-15",
            "2017-01-22",
        )
        assertEquals(expectedDates, shoppingSteps.keys, "The authored route should only present Home Shopping reminders on verified Royal broadcast dates")
        shoppingSteps.keys.forEach { date ->
            assertEquals(DayOfWeek.SUNDAY, LocalDate.parse(date).dayOfWeek, "$date Home Shopping reminder must fall on Sunday")
        }

        fun require(date: String, vararg fragments: String) {
            val label = shoppingSteps.getValue(date)
            fragments.forEach { fragment ->
                assertTrue(fragment in label, "$date Home Shopping label must retain '$fragment': $label")
            }
        }

        // Apr 24 is intentionally only a reminder in this completion route; the
        // route does not claim either optional set is required.
        require("2016-04-24", "TV Shopping Program")

        require("2016-05-08", "Bio Nutrients Set")
        require("2016-05-15", "Allergy Relief Pack", "20 Wide Eye Drops")
        require("2016-05-22", "Muscle Plus Set", "Outdoors Kit")
        require("2016-05-29", "Folding Screen Set")
        require("2016-06-19", "Supportive Gift Set", "Busy Revival Set", "3 revivals", "10 single-target heals")
        require("2016-06-26", "Dark Power Set", "2 Black Rocks", "Black Robe", "Cursed Tools Set", "10 Straw Dolls", "3 Curse items")
        require("2016-07-03", "Calm Mind Set")
        require("2016-08-07", "Phantom Thief Set", "Heroic Set")
        require("2016-08-14", "Drink Set", "Floral Gift Set")
        require("2016-09-25", "Phantom Thief", "Phantom Wafer", "Calling Postcard")
        require("2016-10-02", "Pumpkin Ghost", "Haunted Repel")
        require("2016-11-06", "Sturdy Ointment", "Inner Muscle")
        require("2016-11-13", "Instant Spray", "Meditative")
        require("2016-11-27", "Yaki-Imo", "Limited Sweets")
        require("2016-12-11", "Super Detox", "Fancy Magatama")
        require("2017-01-15", "Lucky Worker Bag", "Lucky Muscle")
        require("2017-01-22", "Talisman", "Sweet Delight")

        // The route deliberately skips optional May 1 and Jan 29 broadcasts;
        // omission from a completion-route reminder list is not a false claim
        // that those Royal broadcasts do not exist.
        assertTrue("2016-05-01" !in shoppingSteps)
        assertTrue("2017-01-29" !in shoppingSteps)
    }
}
