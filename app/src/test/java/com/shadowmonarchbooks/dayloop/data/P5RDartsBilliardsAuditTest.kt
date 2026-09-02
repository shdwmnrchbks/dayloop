package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RDartsBilliardsAuditTest {

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
    fun `p5r route keeps audited darts Baton Pass progression and billiards Technical sequence`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }
        fun step(date: String, marker: String) = days.getValue(date).steps.single { marker in it.label }

        // Darts: the source route uses the tutorial session for Ryuji rank 2,
        // then two later sessions to push pairs of teammates directly to rank 3.
        val ryuji = step("2016-06-05", "Play darts with Ryuji")
        assertTrue("Penguin Sniper Lounge" in ryuji.label)
        assertTrue("Baton Pass reaches rank 2" in ryuji.label)

        val june7 = step("2016-06-07", "Get two teammates to Baton Pass rank 3")
        assertTrue("bull's-eyes add Proficiency" in june7.label)
        assertEquals(mapOf("proficiency" to 3), june7.statGains)
        assertTrue("Just play like normal" in step("2016-06-07", "play darts").label)
        assertTrue("Pretend like you are painting" in step("2016-06-07", "play darts").label)

        val june26 = step("2016-06-26", "play darts")
        assertTrue("Penguin Sniper Lounge" in june26.label)
        assertTrue("Take a deep breath" in june26.label)
        assertTrue("two more teammates reach Baton Pass rank 3" in june26.label)
        assertEquals(mapOf("proficiency" to 3), june26.statGains)

        // Technique-book metadata remains generic and does not hard-code the
        // source route's exact purchase date as a universal unlock date.
        val books = loaded.activities?.activities.orEmpty().associateBy { it.id }
        val learnProDarts = books.getValue("p5r.activity.book.learn-pro-darts")
        assertTrue(learnProDarts.location.orEmpty().contains("Shinjuku bookstore"))
        assertTrue(learnProDarts.notes.orEmpty().contains("after playing darts"))
        assertTrue(learnProDarts.notes.orEmpty().contains("Third Eye"))

        val expert = books.getValue("p5r.activity.book.expert-billiards")
        assertTrue(expert.location.orEmpty().contains("Underground Mall sports store"))
        assertTrue(expert.notes.orEmpty().contains("after playing billiards"))
        assertTrue(expert.notes.orEmpty().contains("Technical Rank"))

        val magician = books.getValue("p5r.activity.book.billiards-magician")
        assertTrue(magician.notes.orEmpty().contains("Technical Rank 3"))
        assertTrue(magician.notes.orEmpty().contains("Massé Shot"))
        assertTrue(magician.notes.orEmpty().contains("Technical Rank 4"))

        // Billiards route order: first authored session, technique purchases,
        // book, Jump Shot / rank 3, Billiards Magician, Massé / rank 4.
        assertTrue("Penguin Sniper Lounge" in step("2016-08-17", "play billiards").label)
        val shopping = step("2016-08-18", "Expert Billiards")
        assertTrue("Jump Cue" in shopping.label)
        assertTrue("Underground Mall" in shopping.label)
        assertEquals("p5r.activity.book.expert-billiards", step("2016-08-19", "Expert Billiards").activityRef)
        assertTrue("Technical" in step("2016-08-26", "play billiards").label)
        assertTrue("rank 3" in step("2016-09-05", "play billiards").label)
        assertTrue("Billiards Magician" in step("2016-09-05", "Receive 'Billiards Magician'").label)
        assertEquals("p5r.activity.book.billiards-magician", step("2016-09-23", "Billiards Magician").activityRef)
        assertTrue("rank 4" in step("2016-10-14", "Play billiards").label)
    }
}
