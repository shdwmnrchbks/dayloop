package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.LintIssue
import com.shadowmonarchbooks.dayloop.pack.schema.PackTheme
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.writeBytes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LauncherBadgeLintTest {
    private fun tempDir() = Files.createTempDirectory("launcher-badge-lint")

    private fun packWithBadge(path: String = "art/launcher-badge.png") = Fixture.skinPack().let { pack ->
        pack.copy(theme = pack.theme!!.copy(art = pack.theme!!.art + ("launcherBadge" to path)))
    }

    private fun writeFixture(path: String = "art/launcher-badge.png"): Path {
        val pack = packWithBadge(path)
        val dir = tempDir()
        Fixture.writePack(dir, pack = pack)
        Fixture.writeSkinArt(dir, pack.theme!!)
        return dir
    }

    private fun writePng(path: Path, width: Int, height: Int) {
        Files.createDirectories(path.parent)
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()
        g.color = java.awt.Color(0x22, 0x44, 0x88, 0xFF)
        g.fillOval(4, 4, width - 8, height - 8)
        g.dispose()
        ImageIO.write(image, "png", path.toFile())
    }

    @Test
    fun `valid square launcher badge lints clean`() {
        val dir = writeFixture()
        writePng(dir.resolve("art/launcher-badge.png"), 96, 96)
        val errors = PackLintAll.run(dir, false).filter { it.severity == LintIssue.Severity.ERROR }
        assertEquals(emptyList(), errors, errors.toString())
    }

    @Test
    fun `missing launcher badge fails packlint`() {
        val dir = writeFixture()
        Files.delete(dir.resolve("art/launcher-badge.png"))
        val errors = PackLintAll.run(dir, false).filter { it.severity == LintIssue.Severity.ERROR }
        assertTrue(errors.any { "theme.art['launcherBadge'] file not found" in it.message }, errors.toString())
    }

    @Test
    fun `launcher badge must be a square png in the badge size budget`() {
        val dir = writeFixture()
        writePng(dir.resolve("art/launcher-badge.png"), 96, 64)
        var errors = PackLintAll.run(dir, false).filter { it.severity == LintIssue.Severity.ERROR }
        assertTrue(errors.any { "must be square" in it.message }, errors.toString())

        writePng(dir.resolve("art/launcher-badge.png"), 32, 32)
        errors = PackLintAll.run(dir, false).filter { it.severity == LintIssue.Severity.ERROR }
        assertTrue(errors.any { "dimensions must be 48–256px" in it.message }, errors.toString())
    }

    @Test
    fun `launcher badge rejects non-png references`() {
        val dir = writeFixture("art/launcher-badge.jpg")
        dir.resolve("art/launcher-badge.jpg").writeBytes(byteArrayOf(1, 2, 3))
        val errors = PackLintAll.run(dir, false).filter { it.severity == LintIssue.Severity.ERROR }
        assertTrue(errors.any { "must be a [png] file" in it.message }, errors.toString())
    }
}
