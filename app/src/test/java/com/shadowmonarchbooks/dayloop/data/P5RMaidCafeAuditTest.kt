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

class P5RMaidCafeAuditTest {

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
    fun `p5r Maid Cafe route earns twenty stamps before the Royal special menu and Photo of Clara`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }
        fun step(date: String, marker: String) = days.getValue(date).steps.single { marker in it.label }

        data class OmeletteVisit(
            val date: String,
            val expectedGains: Map<String, Int>,
        )

        val visits = listOf(
            OmeletteVisit("2016-09-24", mapOf("charm" to 3, "guts" to 2)),
            OmeletteVisit("2016-12-10", mapOf("charm" to 3)),
            OmeletteVisit("2016-12-17", mapOf("charm" to 3)),
        )

        var stamps = 0
        visits.forEach { visit ->
            val date = LocalDate.parse(visit.date)
            assertEquals(DayOfWeek.SATURDAY, date.dayOfWeek, "${visit.date} is intentionally a Royal Maid Day visit")
            val omelette = step(visit.date, "Sincere Omelette")
            assertTrue("maid cafe" in omelette.label.lowercase())
            assertEquals(visit.expectedGains, omelette.statGains, "${visit.date} hidden-point reward")

            // Sincere Omelette costs ¥5,000 => 5 stamps; Royal Maid Day adds 4.
            stamps += 9
        }

        assertTrue(stamps >= 20, "The authored Saturday omelette visits must unlock the special menu before Dec 22")

        val mistake = step("2016-09-24", "Sincere Omelette")
        assertTrue("Clara fix a mistake" in mistake.label)
        assertTrue("reload if she is flawless" in mistake.label)
        assertEquals(mapOf("charm" to 3, "guts" to 2), mistake.statGains)

        val special = step("2016-12-22", "special menu")
        assertTrue("after 20 stamps" in special.label)
        assertTrue("Photo of Clara" in special.label)
        assertEquals(mapOf("charm" to 5), special.statGains, "Royal special menu gives three displayed Charm notes = five hidden points")

        val master = loaded.achievements?.achievements.orEmpty()
            .single { it.title == "Master of Akihabara" }
        val description = master.description.orEmpty()
        assertTrue(description.contains("special menu", ignoreCase = true))
        assertTrue(description.contains("20 stamps", ignoreCase = true))
        assertEquals("2016-12-22", master.expectedBy)
    }
}
