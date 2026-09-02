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

class P5RGunCustomizationAuditTest {

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
    fun `p5r gun customization route respects Guts four Hanged rank one and return visit ordering`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        val gutsFour = days.getValue("2016-07-18").steps.single { "Guts reaches rank 4" in it.label }
        assertEquals(mapOf("guts" to 3), gutsFour.statGains)

        val hanged = loaded.bonds?.bonds.orEmpty().single { it.label == "Hanged Man" }
        val rank1 = hanged.ranks.single { it.rank == 1 }
        assertEquals("2016-08-10", rank1.scheduledFor)
        assertTrue(rank1.gates != null, "Hanged rank 1 should retain its authored social-stat gate")
        assertTrue(days.getValue("2016-08-10").steps.any { "Hanged Man reaches rank 1" in it.label })

        val customization = days.getValue("2016-08-11").steps.single { "Customize a gun" in it.label }
        assertTrue(customization.label.contains("Iwai", ignoreCase = true))
        assertTrue("2016-08-10" < "2016-08-11")

        val trophy = loaded.achievements?.achievements.orEmpty()
            .single { it.title == "Professional Modification" }
        assertEquals(AchievementTrackingTypes.MANUAL, trophy.tracking.type)
        assertNull(trophy.expectedBy, "gun customization is player-state gated, not a route deadline")
    }
}
