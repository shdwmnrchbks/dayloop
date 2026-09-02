package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class P5RNovemberAuditTest {

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
    fun `p5r November route uses audited points and does not misstate the Royal gate`() {
        val root = contentPacksDir() ?: return
        val p5r = PackLoader.load(root.resolve("p5r"))
        assertEquals(emptyList(), p5r.parseIssues)

        val days = p5r.walkthroughs.flatMap { it.file.days }
            .filter { it.date.startsWith("2016-11") }

        fun step(date: String, text: String) =
            days.first { it.date == date }.steps.first { it.label.contains(text) }

        fun gain(date: String, text: String): Map<String, Int> =
            step(date, text).statGains

        assertEquals(mapOf("guts" to 5), gain("2016-11-02", "Hero with a Bow"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-02", "class question"))
        assertFalse(step("2016-11-02", "Justice reaches rank 7").label.contains("required for the third semester", ignoreCase = true))
        assertEquals(emptyMap(), step("2016-11-03", "Moon reaches rank 7").statGains)
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-04", "class question"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-04", "Reward"))
        assertFalse(step("2016-11-04", "Justice reaches rank 8").label.contains("required for the third semester", ignoreCase = true))
        assertEquals(mapOf("knowledge" to 7), gain("2016-11-05", "Heroic Revelations"))
        assertEquals(mapOf("charm" to 5), gain("2016-11-05", "Dressed in Ashes"))
        assertEquals(1, days.first { it.date == "2016-11-05" }.steps.count { it.label.contains("Strength reaches rank 9") })
        assertEquals(mapOf("proficiency" to 2), gain("2016-11-06", "Sunday drink"))
        assertEquals(mapOf("kindness" to 5), gain("2016-11-07", "Tower reaches rank 8"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-08", "class question"))
        assertEquals(mapOf("proficiency" to 3), gain("2016-11-08", "darts with Akechi"))
        assertEquals(mapOf("proficiency" to 7), gain("2016-11-09", "Admission Impossible"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-10", "class question"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-10", "TV game show"))
        assertEquals(mapOf("kindness" to 7), gain("2016-11-11", "Call Me Chief"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-12", "class question"))
        assertEquals(mapOf("guts" to 2), gain("2016-11-13", "Sunday drink"))
        assertEquals(mapOf("guts" to 7), gain("2016-11-13", "Pach Saw"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-14", "class question"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-14", "Bashing"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-15", "class question"))
        assertEquals(mapOf("kindness" to 5), gain("2016-11-15", "Mega Fertilizer"))
        assertEquals(mapOf("charm" to 7), gain("2016-11-16", "Reckless Casanova"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-17", "class question"))
        assertEquals(mapOf("knowledge" to 3), gain("2016-11-18", "Featherman Seeker"))
        assertEquals(mapOf("knowledge" to 3), gain("2016-11-22", "Featherman Seeker"))
        assertEquals(mapOf("knowledge" to 3), gain("2016-11-23", "Featherman Seeker"))
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-24", "TV game show"))
        assertEquals(mapOf("charm" to 3), gain("2016-11-24", "Punch Ouch"))
        assertEquals(mapOf("kindness" to 5), gain("2016-11-26", "Tower reaches rank 9"))
        assertEquals(mapOf("proficiency" to 5), gain("2016-11-26", "Hanged Man reaches rank 8"))
        assertEquals(mapOf("kindness" to 2), gain("2016-11-27", "Sunday drink"))
        assertEquals(emptyMap(), step("2016-11-27", "Hanged Man reaches rank 9").statGains)
        assertEquals(mapOf("knowledge" to 2), gain("2016-11-28", "Charisma"))
        assertEquals(mapOf("kindness" to 7), gain("2016-11-28", "Over the Pigeon's Nest"))

        assertTrue(step("2016-11-09", "Admission Impossible").label.contains("Craft of Cinema bonus active"))
        assertTrue(step("2016-11-13", "Pach Saw").label.contains("Craft of Cinema bonus active"))
        assertTrue(step("2016-11-28", "Over the Pigeon's Nest").label.contains("Craft of Cinema bonus active"))
    }
}
