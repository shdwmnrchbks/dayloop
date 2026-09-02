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

class P5RAojiruAuditTest {

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
    fun `p5r Sunday Aojiru keeps Royal location timing points rotation and active modifiers`() {
        val root = contentPacksDir() ?: return
        val p5r = PackLoader.load(root.resolve("p5r"))
        assertEquals(emptyList(), p5r.parseIssues)

        val activity = p5r.activities?.activities.orEmpty()
            .single { it.id == "p5r.activity.drink.fruit-drink" }

        assertEquals("drink", activity.kind)
        assertEquals("Shibuya Underground Walkway", activity.location)
        assertTrue(activity.notes.orEmpty().contains("Sundays only"))
        assertTrue(activity.notes.orEmpty().contains("does not consume a time slot"))
        assertTrue(activity.notes.orEmpty().contains("+2 points"))
        assertTrue(activity.notes.orEmpty().contains("same stat repeats on later Sundays until purchased"))

        // Audit every authored route purchase by its reusable reference when present
        // or by the visible action text. The first route entry says "Sunday fruit
        // drink" while later entries shorten that to "Sunday drink", and older
        // imports did not tag every row with activityRef.
        val purchases = p5r.walkthroughs
            .flatMap { it.file.days }
            .flatMap { day -> day.steps.map { step -> day.date to step } }
            .filter { (_, step) ->
                step.activityRef == activity.id ||
                    step.label.contains("Sunday drink") ||
                    step.label.contains("Sunday fruit drink")
            }

        assertTrue(purchases.isNotEmpty(), "The completion route should exercise the audited Aojiru activity")

        val rotation = listOf("charm", "proficiency", "guts", "kindness", "knowledge")
        purchases.forEachIndexed { index, (date, step) ->
            assertEquals(DayOfWeek.SUNDAY, LocalDate.parse(date).dayOfWeek, "$date Aojiru purchase must be on Sunday")
            assertTrue(step.label.contains("Underground Walkway"), "$date should keep the audited Shibuya location visible")

            val expectedStat = rotation[index % rotation.size]
            val expectedPoints = if (step.label.contains("Luck Reading active")) 3 else 2
            assertEquals(
                mapOf(expectedStat to expectedPoints),
                step.statGains,
                "$date must advance the fixed Royal Aojiru rotation only after a purchase and include any active Luck Reading modifier",
            )

            step.activityRef?.let { ref ->
                assertEquals(activity.id, ref, "$date must not link an Aojiru purchase to the wrong reusable activity")
            }
        }
    }
}
