package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RTrainingFacilityAuditTest {

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
    fun `p5r temple and gym cleanup steps remain late route choices after their real unlocks`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val allDays = loaded.walkthroughs.flatMap { it.file.days }
        val days = allDays.associateBy { it.date }

        val templeSteps = allDays.flatMap { day ->
            day.steps.filter { "Kichijoji temple" in it.label }.map { day.date to it }
        }
        assertEquals(1, templeSteps.size, "The completion route intentionally uses the temple only once")
        assertEquals("2017-01-30", templeSteps.single().first)
        assertTrue("raise max SP" in templeSteps.single().second.label)
        assertTrue(templeSteps.single().second.statGains.isEmpty(), "max-SP growth is not a social-stat reward")

        val templeTrophy = loaded.achievements?.achievements.orEmpty()
            .single { it.title == "A Serene Experience" }
        assertEquals("2016-06-06", templeTrophy.availableFrom)

        val chariot = loaded.bonds?.bonds.orEmpty().single { it.label == "Chariot" }
        val rank5 = chariot.ranks.single { it.rank == 5 }
        assertEquals("2016-06-04", rank5.scheduledFor)
        assertTrue(days.getValue("2016-06-04").steps.any { "Chariot reaches rank 5" in it.label })

        val optionalGym = days.getValue("2017-01-31").steps.single { "Protein Lovers Gym" in it.label }
        assertTrue("optional" in optionalGym.label.lowercase())
        assertTrue("training" in optionalGym.label.lowercase())
    }
}
