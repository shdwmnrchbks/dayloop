package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class P5RSootyScaleArmorAuditTest {

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
    fun `p5r third semester route treats Dragon Scale Scarf as the desired random laundry result`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        val farm = days.getValue("2017-01-12").steps.single { "Sooty Scale Armor" in it.label }.label
        assertTrue("Ravenous Dragon" in farm)
        assertTrue("laundry can roll" in farm)
        assertTrue("Dragon Scale Scarf" in farm)
        assertFalse("washes into Morgana's best armor" in farm)

        val wash = days.getValue("2017-01-14").steps.single { "Sooty Scale Armor" in it.label }.label
        assertTrue("Save first" in wash)
        assertTrue("Kawakami" in wash)
        assertTrue("reload until" in wash)
        assertTrue("Dragon Scale Scarf" in wash)
    }
}
