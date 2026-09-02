package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RAugustRouteOrderAuditTest {

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
    fun `p5r August route preserves summer freedom late story block and reusable activity links`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val august = loaded.walkthroughs.single { it.month == "2016-08" }.file
        val days = august.days.associateBy { it.date }

        august.days.filter { it.date <= "2016-08-20" }.forEach { day ->
            assertEquals("free", day.dayKind, "${day.date} remains part of the route's unrestricted summer-break block")
        }
        august.days.filter { it.date >= "2016-08-21" }.forEach { day ->
            assertEquals("story", day.dayKind, "${day.date} has mandatory daytime story before the route's evening slot")
        }

        val aug21 = days.getValue("2016-08-21")
        assertTrue(aug21.steps.first().label.contains("Medjed"))
        assertTrue(aug21.steps.first().label.contains("confined to LeBlanc"))
        assertEquals("p5r.activity.dvd.d-housewives", aug21.steps.single { "D.Housewives" in it.label }.activityRef)

        val aug22 = days.getValue("2016-08-22")
        assertTrue(aug22.steps.first().label.contains("Futaba"))
        assertTrue(aug22.steps.any { "Judgement reaches rank 3" in it.label })
        assertTrue(aug22.steps.any { "Fool reaches rank 7" in it.label })

        listOf("2016-08-23", "2016-08-24", "2016-08-25", "2016-08-26", "2016-08-27", "2016-08-28").forEach { date ->
            val context = days.getValue(date).steps.first().label
            assertTrue("Futaba socialization story" in context, "$date should surface the fixed daytime Futaba sequence")
            assertTrue("evening free time resumes" in context)
        }

        assertTrue(days.getValue("2016-08-29").steps.first().label.contains("Beach-trip story"))
        assertTrue(days.getValue("2016-08-30").steps.first().label.contains("Team homework story"))
        val aug31 = days.getValue("2016-08-31")
        assertTrue(aug31.steps.first().label.contains("Akihabara"))
        assertTrue(aug31.steps.first().label.contains("confined to LeBlanc"))
        assertTrue(aug31.steps.any { "Hermit reaches rank 1" in it.label })
        assertTrue(aug31.steps.any { "Fool reaches rank 8" in it.label })
        assertEquals("p5r.activity.dvd.d-housewives", aug31.steps.single { "D.Housewives" in it.label }.activityRef)

        val aojiru = "p5r.activity.drink.fruit-drink"
        mapOf(
            "2016-08-07" to mapOf("proficiency" to 2),
            "2016-08-14" to mapOf("guts" to 2),
            "2016-08-28" to mapOf("kindness" to 2),
        ).forEach { (date, expectedGain) ->
            val step = days.getValue(date).steps.single { "Sunday drink" in it.label }
            assertEquals(aojiru, step.activityRef, "$date must stay linked to the reusable Aojiru activity")
            assertEquals(expectedGain, step.statGains)
        }

        assertTrue(days.getValue("2016-08-16").steps.any { "Knowledge reaches rank 5" in it.label })
    }
}
