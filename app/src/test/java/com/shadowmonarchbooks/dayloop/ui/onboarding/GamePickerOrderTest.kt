package com.shadowmonarchbooks.dayloop.ui.onboarding

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals

class GamePickerOrderTest {
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
    fun `bundled packs declare requested picker order`() {
        val root = contentPacksDir() ?: return
        val ordered = listOf("metaphor", "p3r", "p5r")
            .map { slug -> PackLoader.decodePack(root.resolve(slug).resolve("pack.json").readText())!! }
            .sortedBy { it.pickerOrder }
            .map { it.packId }

        assertEquals(listOf("p5r", "p3r", "metaphor"), ordered)
    }
}
