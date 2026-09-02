package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RTwinsOutingAuditTest {

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
    fun `p5r completion route keeps all thirteen Royal warden and Lavenza outings in a legal order`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        data class Outing(
            val routeDate: String,
            val sequence: Int,
            val name: String,
            val availableFrom: String,
            val availableUntil: String,
        )

        val expected = listOf(
            Outing("2016-06-06", 1, "Big Bang Burger", "2016-06-06", "2016-12-19"),
            Outing("2016-06-07", 2, "Shibuya Movie Theater", "2016-06-07", "2016-12-19"),
            Outing("2016-06-16", 3, "Protein Lovers Gym", "2016-06-15", "2016-12-19"),
            Outing("2016-07-02", 4, "Kanda Church", "2016-06-25", "2016-12-19"),
            Outing("2016-08-03", 5, "Shinagawa Aquarium", "2016-07-26", "2016-12-19"),
            Outing("2016-09-05", 6, "Asakusa Skytree", "2016-07-26", "2016-12-19"),
            Outing("2016-09-25", 7, "Akihabara Maid Cafe", "2016-09-19", "2016-12-19"),
            Outing("2016-09-27", 8, "Miura Beach", "2016-09-02", "2016-09-29"),
            Outing("2016-10-16", 9, "Destinyland", "2016-10-01", "2016-12-19"),
            Outing("2016-10-23", 10, "Ueno Museum", "2016-10-01", "2016-11-03"),
            Outing("2016-11-25", 11, "Cafe LeBlanc", "2016-11-25", "2016-12-19"),
            Outing("2016-12-03", 12, "Shibuya Underground Mall", "2016-12-01", "2016-12-09"),
            Outing("2017-01-13", 13, "Lavenza / protagonist room", "2017-01-13", "2017-01-13"),
        )

        val daySteps = loaded.walkthroughs
            .flatMap { it.file.days }
            .flatMap { day -> day.steps.map { step -> day.date to step.label } }

        val numbered = daySteps.filter { (_, label) ->
            Regex("""\((?:[1-9]|1[0-3])/13\)""").containsMatchIn(label)
        }
        assertEquals(13, numbered.size, "completion route must expose exactly thirteen numbered Twins/Lavenza outings")

        expected.forEach { outing ->
            val marker = "(${outing.sequence}/13)"
            val matches = daySteps.filter { (date, label) -> date == outing.routeDate && marker in label }
            assertEquals(
                1,
                matches.size,
                "${outing.routeDate}: expected ${outing.name} as outing ${outing.sequence}/13",
            )

            val routeDate = LocalDate.parse(outing.routeDate)
            assertTrue(
                routeDate >= LocalDate.parse(outing.availableFrom),
                "${outing.name} is routed before its Royal availability window",
            )
            assertTrue(
                routeDate <= LocalDate.parse(outing.availableUntil),
                "${outing.name} is routed after its Royal availability window",
            )
        }

        assertTrue(
            expected.map { LocalDate.parse(it.routeDate) }.zipWithNext().all { (a, b) -> a < b },
            "the source-specific 1/13 through 13/13 route order must remain chronological",
        )
    }
}
