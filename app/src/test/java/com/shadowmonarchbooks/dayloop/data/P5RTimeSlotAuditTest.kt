package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Pins the Day/Night column audit against Alyookid's original schedule table. */
class P5RTimeSlotAuditTest {

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
    fun `every p5r task has a source period and periods never move backwards`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val pack = requireNotNull(loaded.pack)
        assertEquals(
            mapOf("afternoon" to "Day", "evening" to "Night"),
            pack.slots.associate { it.id to it.label },
        )

        val steps = loaded.walkthroughs.flatMap { it.file.days }.flatMap { day ->
            val slots = day.steps.map { it.slot }
            assertFalse(null in slots, "${day.date}: every task must be assigned to Day or Night")
            val firstNight = slots.indexOf("evening")
            if (firstNight >= 0) {
                assertTrue(
                    slots.drop(firstNight).all { it == "evening" },
                    "${day.date}: source periods must stay in Day-then-Night order",
                )
            }
            day.steps
        }

        assertEquals(1_198, steps.size)
        assertEquals(841, steps.count { it.slot == "afternoon" })
        assertEquals(357, steps.count { it.slot == "evening" })
    }

    @Test
    fun `cross-period tasks remain split at the source boundary`() {
        val root = contentPacksDir() ?: return
        val days = PackLoader.load(root.resolve("p5r")).walkthroughs
            .flatMap { it.file.days }
            .associateBy { it.date }

        val april9 = days.getValue("2016-04-09").steps
        assertEquals("afternoon", april9.single { "clean it up" in it.label }.slot)
        assertEquals("evening", april9.single { "go to sleep" in it.label }.slot)

        val april17 = days.getValue("2016-04-17").steps
        assertEquals("afternoon", april17.single { "Airsoft" in it.label }.slot)
        assertEquals("evening", april17.single { "lock pick" in it.label }.slot)

        val november18 = days.getValue("2016-11-18").steps
        assertEquals("afternoon", november18.single { it.label == "Send the Calling Card" }.slot)
        assertEquals("evening", november18.single { it.label == "Save your game" }.slot)
    }
}
