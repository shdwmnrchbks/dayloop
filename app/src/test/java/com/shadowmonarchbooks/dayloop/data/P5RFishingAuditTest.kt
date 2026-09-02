package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `p5r fishing route keeps Royal Guardian to Kingpin progression and bait semantics`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }
        fun step(date: String, marker: String) = days.getValue(date).steps.single { marker in it.label }

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

        assertTrue("2016-12-16" < "2017-01-16", "The completion route must catch the Guardian before attempting the Royal-only Kingpin")
    }
}
