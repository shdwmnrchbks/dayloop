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
    fun `p5r Sunday Aojiru keeps Royal location timing points and purchase rotation`() {
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

        val purchases = p5r.walkthroughs
            .flatMap { it.file.days }
            .flatMap { day -> day.steps.map { step -> day.date to step } }
            .filter { (_, step) -> step.activityRef == activity.id }

        assertTrue(purchases.isNotEmpty(), "The completion route should exercise the audited Aojiru activity")

        val rotation = listOf("charm", "proficiency", "guts", "kindness", "knowledge")
        purchases.forEachIndexed { index, (date, step) ->
            assertEquals(DayOfWeek.SUNDAY, LocalDate.parse(date).dayOfWeek, "$date Aojiru purchase must be on Sunday")
            assertTrue(step.label.contains("Underground Walkway"), "$date should keep the audited Shibuya location visible")
            assertEquals(
                mapOf(rotation[index % rotation.size] to 2),
                step.statGains,
                "$date must advance the fixed Royal Aojiru rotation only after a purchase",
            )
        }
    }
}
