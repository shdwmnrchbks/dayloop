package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertTrue

class MetaphorRouteResidualAuditTest {

    @Test
    fun `June route keeps source-critical follower and beetle details`() {
        val days = standardDays()

        val june5 = days.getValue("2100-06-05").steps
        assertTrue(june5.any { "missable Gold Beetle" in it.label })

        val june12 = days.getValue("2100-06-12").steps
        assertTrue(june12.any { "A Friend in Need" in it.label && "Catherina" in it.label && "Follower rank 1" in it.label })
        assertTrue(june12.any { "Foreword and Prologue" in it.label && "Follower rank 2" in it.label })

        val june27 = days.getValue("2100-06-27").steps.single { it.activityRef == "metaphor.activity.gold-beetles" }
        assertTrue("cupboard only contains a Medicinal Herb" in june27.label)
        assertTrue("Gold Beetles" in june27.label)

        val june29 = days.getValue("2100-06-29").steps
        assertTrue(june29.any { "Imp's Den" in it.label && "Gold Beetle" in it.label && it.activityRef == "metaphor.activity.gold-beetles" })
    }

    @Test
    fun `July route does not invent a free day and preserves completion-critical sweeps`() {
        val days = standardDays()

        val july7 = days.getValue("2100-07-07").steps
        assertTrue(july7.any { "Practical Pidgeon Parcel" in it.label })
        assertTrue(july7.any { "Man-Eater's Grotto" in it.label && "Gold Beetle" in it.label && "lost relic" in it.label })

        val july8 = days.getValue("2100-07-08").steps
        assertTrue(july8.any { "Hatching a Plan" in it.label && "Practical Pidgeon Parcel" in it.label })

        assertTrue("2100-07-21" !in days, "Hayate's authored schedule skips July 21; do not move July 23 errands onto it")

        val july20 = days.getValue("2100-07-20").steps
        assertTrue(july20.any { "all five route Gold Beetles" in it.label && it.activityRef == "metaphor.activity.gold-beetles" })

        val july23 = days.getValue("2100-07-23").steps
        assertTrue(july23.any { "Efflorescent Youth" in it.label && "Dental Distress" in it.label && "Superior Scrimshaw" in it.label })
        assertTrue(july23.any { "fourth market stall" in it.label && "Gold Beetle" in it.label })
        assertTrue(july23.any { "Rainbow Flower" in it.label && "Efflorescent Youth" in it.label && it.statGains["tolerance"] == 10 })
        assertTrue(july23.any { "MesmerEyes" in it.label && "hay bales" in it.label && "Bardon" in it.label })

        val july25 = days.getValue("2100-07-25").steps
        assertTrue(july25.any { "Sergeant Xanth" in it.label && "Maintenance Chief Ceiba" in it.label && "Master Sergeant Glechom" in it.label })
        assertTrue(july25.any { "only one corridor branch is needed" in it.label && "Help Anyone in Need" in it.label })

        assertTrue(days.getValue("2100-07-15").steps.any { "Skullduggery" in it.label && "Mortaskulls" in it.label })
        assertTrue(days.getValue("2100-07-16").steps.any { "A Haunted Heirloom" in it.label && "necklace" in it.label })
    }

    @Test
    fun `August route keeps timed requests and completion beetles visible`() {
        val days = standardDays()

        val august4 = days.getValue("2100-08-04").steps
        assertTrue(august4.any { "Toothbrush of Hygienia" in it.label && "Gold Beetle" in it.label })

        val august19 = days.getValue("2100-08-19").steps
        assertTrue(august19.any { "Peak Curiosity" in it.label && "The Price of Hope" in it.label })
        assertTrue(august19.any { "Greater One-Eyed Scoundrel" in it.label && "Gold Beetle" in it.label && "Comfort Concoctions" in it.label })
        assertTrue(august19.any { "A Guiding Gift" in it.label && "Save the Mourning Snakes" in it.label && "The Price of Hope" in it.label })
        assertTrue(august19.any { "three Polar Stones" in it.label && "A Guiding Gift" in it.label })

        val august20 = days.getValue("2100-08-20").steps
        assertTrue(august20.any { "A Guiding Gift" in it.label && "three Polar Stones" in it.label && "Gold Beetle" in it.label })

        val august27 = days.getValue("2100-08-27").steps
        assertTrue(august27.any { "Save the Mourning Snakes" in it.label && "Gold Beetle" in it.label })

        val august28 = days.getValue("2100-08-28").steps
        assertTrue(august28.any { "Peak Curiosity" in it.label })
    }

    @Test
    fun `September route preserves final request setup and correct virtue naming`() {
        val days = standardDays()

        val september13 = days.getValue("2100-09-13").steps
        assertTrue(september13.any { "Rudolf" in it.label && "Proof of Power" in it.label })
        assertTrue(september13.any { "Tainted Threads" in it.label && "Tail Bait" in it.label && "Fiend in the Frozen Forest" in it.label })

        val september14 = days.getValue("2100-09-14").steps
        assertTrue(september14.any { "Malva" in it.label && "fifth town" in it.label && "Globetrotter" in it.label })

        val september18 = days.getValue("2100-09-18").steps
        assertTrue(september18.any { "Rusty Greatsword" in it.label && "The Edge of Glory" in it.label && "Gold Beetle" in it.label })
        assertTrue(september18.any { "Cursed Love Ballad" in it.label })

        val september20 = days.getValue("2100-09-20").steps
        assertTrue(september20.any { "Ligno" in it.label && "equipment shop" in it.label })

        val september26 = days.getValue("2100-09-26").steps
        assertTrue(september26.any { "Incarnate in the Woods" in it.label && "Cockatrice in the Clouds" in it.label && "Apostles of the Apocalypse" in it.label })
        assertTrue(september26.any { "Trial of the Dragon: Mad Mischief" in it.label })
        assertTrue(september26.any { "Trial of the Dragon: Heroes' Rest" in it.label })
        assertTrue(september26.any { "Trial of the Dragon: Bygone Legacy" in it.label && "Deliver Hot Spring Water" in it.label })

        val september30 = days.getValue("2100-09-30").steps
        assertTrue(september30.any { "Wisdom reaches rank 5 (Sagacious)" in it.label })
        assertTrue(september30.none { "Sagatious" in it.label })
    }

    private fun standardDays() = loadMetaphor().walkthroughs
        .filter { it.routeId == "standard" }
        .flatMap { it.file.days }
        .associateBy { it.date }

    private fun loadMetaphor() = PackLoader.load(metaphorDir()).also { loaded ->
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
    }

    private fun metaphorDir(): Path {
        val candidates = listOf(
            Path.of("content", "packs", "metaphor"),
            Path.of("..", "..", "content", "packs", "metaphor"),
        )
        return candidates.firstOrNull { Files.isDirectory(it) }
            ?: error("Cannot locate content/packs/metaphor from ${Path.of("").toAbsolutePath()}")
    }
}
