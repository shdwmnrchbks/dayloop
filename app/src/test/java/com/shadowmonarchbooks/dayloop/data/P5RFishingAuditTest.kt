package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class P5RFishingAuditTest {

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
    fun `p5r fishing route keeps Royal unlock and Guardian to Kingpin progression semantics`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val allDays = loaded.walkthroughs.flatMap { it.file.days }
        val days = allDays.associateBy { it.date }
        fun step(date: String, marker: String) = days.getValue(date).steps.single { marker in it.label }

        // Royal's Ryuji fishing hangout is Jul 3. It unlocks Ichigaya, but the
        // introductory hangout itself does not award Angler's Debut; an
        // independent visit can first earn the trophy from Jul 4 onward.
        assertTrue(days.getValue("2016-07-03").steps.any { "Ryuji's invitation" in it.label })
        val angler = loaded.achievements?.achievements.orEmpty().single { it.title == "Angler's Debut" }
        assertEquals("2016-07-04", angler.availableFrom)
        assertNull(angler.expectedBy, "the completion route deliberately fishes later and must not turn that choice into a deadline")

        val firstFishing = step("2016-12-05", "fish at Ichigaya")
        assertEquals(mapOf("proficiency" to 2), firstFishing.statGains)

        val essence = loaded.activities?.activities.orEmpty()
            .single { it.id == "p5r.activity.book.essence-of-fishing" }
        assertTrue(essence.location.orEmpty().contains("Shinjuku bookstore"))
        assertTrue(essence.notes.orEmpty().contains("after fishing at Ichigaya"))
        assertTrue(essence.notes.orEmpty().contains("Third Eye"))
        assertTrue(essence.notes.orEmpty().contains("Prize Tag"))
        assertEquals("p5r.activity.book.essence-of-fishing", step("2016-12-09", "Essence of Fishing").activityRef)

        val prep = step("2016-12-13", "fish at Ichigaya")
        assertTrue("Third Eye" in prep.label)
        assertTrue("Suspicious Boilie" in prep.label)
        assertEquals(mapOf("proficiency" to 2), prep.statGains)
        assertTrue(step("2016-12-13", "Hi-Tech Rod").label.contains("Underground Mall sports shop"))

        val guardian = step("2016-12-16", "Ichigaya Guardian")
        assertTrue("Suspicious Boilie" in guardian.label)
        assertTrue("gold glow" in guardian.label)
        assertTrue("reload on a miss" in guardian.label)
        assertEquals(mapOf("proficiency" to 2), guardian.statGains)

        val kingpin = step("2017-01-16", "Ichigaya Kingpin")
        assertTrue("snow warning" in kingpin.label.lowercase())
        assertTrue("first cast" in kingpin.label.lowercase())
        assertTrue("Suspicious Boilie" in kingpin.label)
        assertTrue("reload on a miss" in kingpin.label)
        assertEquals(mapOf("proficiency" to 2), kingpin.statGains)

        val guardianIndex = allDays.indexOfFirst { it.date == "2016-12-16" && it.steps.any { step -> "Ichigaya Guardian" in step.label } }
        val kingpinIndex = allDays.indexOfFirst { it.date == "2017-01-16" && it.steps.any { step -> "Ichigaya Kingpin" in step.label } }
        assertTrue(guardianIndex >= 0 && kingpinIndex > guardianIndex, "The completion route must catch the Guardian before attempting the Royal-only Kingpin")
    }
}
