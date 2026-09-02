package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RCraneGameAuditTest {

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
    fun `p5r completion route keeps all eight Royal crane prizes in order`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        data class Prize(
            val routeDate: String,
            val stockDate: String,
            val name: String,
            val sequence: Int,
        )

        val expected = listOf(
            Prize("2016-09-02", "2016-09-01", "Jack Frost Doll", 1),
            Prize("2016-09-22", "2016-09-22", "Burger-kun Doll", 2),
            Prize("2016-10-14", "2016-10-14", "Wanna-kun Doll", 3),
            Prize("2016-11-03", "2016-11-03", "Lexy Doll", 4),
            Prize("2016-11-25", "2016-11-25", "Sheep Man Doll", 5),
            Prize("2016-12-15", "2016-12-15", "Black Frost Doll", 6),
            Prize("2017-01-13", "2017-01-13", "Buchimaru Doll", 7),
            Prize("2017-01-23", "2017-01-23", "Jagao Doll", 8),
        )

        val numbered = loaded.walkthroughs
            .flatMap { it.file.days }
            .flatMap { day -> day.steps.map { day.date to it.label } }
            .filter { (_, label) -> Regex("""\([1-8]/8\)""").containsMatchIn(label) && "arcade" in label }

        assertEquals(8, numbered.size, "completion route must expose exactly eight numbered crane-game prizes")

        expected.forEach { prize ->
            val matching = days.getValue(prize.routeDate).steps.filter { step ->
                prize.name in step.label && "(${prize.sequence}/8)" in step.label
            }
            assertEquals(
                1,
                matching.size,
                "${prize.routeDate}: expected ${prize.name} as crane prize ${prize.sequence}/8",
            )
            assertTrue(
                java.time.LocalDate.parse(prize.routeDate) >= java.time.LocalDate.parse(prize.stockDate),
                "${prize.name} cannot be routed before its Royal stock date ${prize.stockDate}",
            )
        }
    }
}
