package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RFebruaryRouteOrderAuditTest {

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
    fun `p5r February route preserves school free slot final Calling Card and boss chronology`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val months = loaded.walkthroughs.associateBy { it.month }
        val january = months.getValue("2017-01").file
        val february = months.getValue("2017-02").file
        val days = february.days.associateBy { it.date }

        assertEquals("school", days.getValue("2017-02-01").dayKind)
        assertTrue(days.getValue("2017-02-01").steps.any { "The Goodfather" in it.label })

        val feb2 = days.getValue("2017-02-02")
        assertEquals("school", feb2.dayKind)
        assertTrue(feb2.steps.first().label.startsWith("After school:"))
        assertTrue(feb2.steps.first().label.contains("Sumire"))
        assertTrue(feb2.steps.any { "Treasure route must already be secured" in it.label })
        assertTrue(feb2.steps.any { "Refuse Maruki's deal" in it.label })
        assertTrue(feb2.steps.any { "final Calling Card" in it.label })
        assertTrue(feb2.steps.any { "We're stopping Maruki" in it.label && "third awakening" in it.label })

        val feb3 = days.getValue("2017-02-03")
        assertEquals("story", feb3.dayKind)
        assertTrue(feb3.steps.any { "Maruki and Azathoth" in it.label })
        assertTrue(feb3.steps.any { "Adam Kadmon" in it.label })
        assertTrue(feb3.steps.any { "scripted survival" in it.label })

        val routeDay = january.days.single { it.date == "2017-01-26" }
        assertEquals("school", routeDay.dayKind)
        assertTrue(routeDay.steps.any { "Reach the Treasure" in it.label })

        val routeDate = january.days.indexOfFirst { it.date == "2017-01-26" }
        val cardDate = february.days.indexOfFirst { it.date == "2017-02-02" }
        val bossDate = february.days.indexOfFirst { it.date == "2017-02-03" }
        assertTrue(routeDate >= 0 && cardDate >= 0 && bossDate >= 0)
        assertTrue(cardDate < bossDate)

        val deadlines = loaded.deadlines?.deadlines?.associateBy { it.id }.orEmpty()
        assertEquals("2017-02-02", deadlines.getValue("p5r.deadline.missable.palace8-route").date)
        assertEquals("2017-02-03", deadlines.getValue("p5r.deadline.palace8").date)
    }
}
