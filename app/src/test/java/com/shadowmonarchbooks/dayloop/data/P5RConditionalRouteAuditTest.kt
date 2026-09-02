package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class P5RConditionalRouteAuditTest {

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
    fun `p5r romance decisions remain choices rather than automatic relationships`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        val romanceDecisionSteps = listOf(
            "2016-06-24" to "Lovers reaches rank 9",
            "2016-07-06" to "Temperance reaches rank 9",
            "2016-08-20" to "Death reaches rank 9",
            "2016-09-23" to "Devil reaches rank 9",
            "2016-09-28" to "Star reaches rank 9",
            "2016-11-12" to "Hermit reaches rank 9",
            "2016-11-15" to "Priestess reaches rank 9",
            "2016-11-17" to "Fortune reaches rank 9",
            "2016-12-12" to "Empress reaches rank 9",
            "2017-01-23" to "Faith reaches rank 9",
        )

        romanceDecisionSteps.forEach { (date, marker) ->
            val label = days.getValue(date).steps.single { marker in it.label }.label
            assertTrue(
                label.contains("romance choice", ignoreCase = true),
                "$date $marker must tell the player romance is an explicit decision",
            )
        }

        val ann = days.getValue("2016-06-24").steps.single { "Lovers reaches rank 9" in it.label }.label
        assertTrue(ann.contains("completion route chooses romance", ignoreCase = true))
        assertFalse(ann.contains("becomes a relationship", ignoreCase = true))
    }

    @Test
    fun `p5r completion route does not present Royal DVD cleanup as a return deadline`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        val finalRental = days.getValue("2016-10-14").steps.single { "rent 'Tee'" in it.label }.label
        assertTrue(finalRental.contains("Completion-route final rental"))
        assertTrue(finalRental.contains("no return deadline"))
        assertFalse(finalRental.contains("last DVD rental of the game"))

        val cleanup = days.getValue("2016-10-23").steps.single { "return the DVDs" in it.label }.label
        assertTrue(cleanup.contains("Completion-route cleanup"))
        assertTrue(cleanup.contains("no return deadline"))
        assertTrue(cleanup.contains("this route makes no more rentals"))
        assertFalse(cleanup.contains("no more rentals from here on"))
    }

    @Test
    fun `p5r weather and rng dependent bonuses stay visibly conditional`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        val rainyBath = days.getValue("2016-06-21").steps.single { "bathhouse" in it.label }.label
        assertTrue(rainyBath.contains("during the rain", ignoreCase = true))

        val maidCafe = days.getValue("2016-09-24").steps.single { "Sincere Omelette" in it.label }.label
        assertTrue(maidCafe.contains("reload if she is flawless", ignoreCase = true))

        val snowFishing = days.getValue("2017-01-16").steps.single { "Ichigaya" in it.label }.label
        assertTrue(snowFishing.contains("snow warning", ignoreCase = true))
        assertTrue(snowFishing.contains("reload on a miss", ignoreCase = true))
    }
}
