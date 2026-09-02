package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RPartTimeJobAuditTest {

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
    fun `p5r beef bowl route keeps job rewards and Yoshida unlock order separate from global job availability`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }
        fun step(date: String, marker: String) = days.getValue(date).steps.single { marker in it.label }

        val firstShift = step("2016-05-18", "Beef Bowl Shop")
        assertTrue("Apply" in firstShift.label || "apply" in firstShift.label)
        assertEquals(mapOf("proficiency" to 3), firstShift.statGains)

        val secondShift = step("2016-05-21", "Beef Bowl Shop")
        assertTrue("every order right" in secondShift.label)
        assertEquals(mapOf("proficiency" to 5), secondShift.statGains)

        val sun = loaded.bonds?.bonds.orEmpty().single { it.label == "Sun" }
        val rank1 = sun.ranks.single { it.rank == 1 }
        assertEquals("2016-05-26", rank1.scheduledFor)
        assertTrue(rank1.notes.orEmpty().contains("beef-bowl night job twice"))
        assertTrue(days.getValue("2016-05-26").steps.any { "Sun reaches rank 1" in it.label })

        // Punch That Clock uses the first possible part-time-job date, while the
        // completion route deliberately waits until May for its Beef Bowl shifts.
        val punchThatClock = loaded.achievements?.achievements.orEmpty()
            .single { it.title == "Punch That Clock!" }
        assertEquals("2016-04-18", punchThatClock.availableFrom)
    }
}
