package com.shadowmonarchbooks.dayloop.ui.theme

import androidx.compose.ui.graphics.Color
import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.PackTheme
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.fileSize
import kotlin.io.path.name
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Phase 10 (docs/ROADMAP-v2.md): the pack's declared theme drives a full
 * Material 3 scheme for both modes, the bundled packs ship valid themes and
 * art slots, and switching packs actually switches the accent. Runs only
 * where the repo checkout is present (no-ops otherwise).
 */
class PackThemeTest {

    /** The repo's content/packs directory, or null when not running in a checkout. */
    private fun contentPacksDir(): Path? {
        var dir: Path? = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        repeat(5) {
            val candidate = dir?.resolve("content")?.resolve("packs")
            if (candidate != null && candidate.isDirectory()) return candidate
            dir = dir?.parent
        }
        return null
    }

    private fun loadPacks(): List<Pair<String, Path>> {
        val root = contentPacksDir() ?: return emptyList()
        val packs = Files.list(root).use { stream ->
            stream.filter { it.isDirectory() }.sorted()
                .map { it.name to it }.toList()
        }
        assertTrue(packs.isNotEmpty(), "no packs found under $root")
        return packs
    }

    private fun themeOf(slug: String): PackTheme {
        val root = contentPacksDir() ?: error("no content checkout")
        val pack = PackLoader.load(root.resolve(slug)).pack
        assertNotNull(pack, "$slug must decode")
        val theme = pack.theme
        assertNotNull(theme, "$slug must declare a theme (Phase 10)")
        return theme
    }

    @Test
    fun `every bundled pack declares a parseable theme with both seeds`() {
        listOf("p5r", "p3r", "metaphor").forEach { slug ->
            val theme = themeOf(slug)
            assertNotNull(PackTheme.parseHexColor(theme.accent ?: ""), "$slug light accent")
            assertNotNull(PackTheme.parseHexColor(theme.accentDark ?: ""), "$slug dark accent")
            assertNotNull(theme.seedArgb(dark = false), "$slug light seed")
            assertNotNull(theme.seedArgb(dark = true), "$slug dark seed")
            assertTrue("card" in theme.art, "$slug must declare the onboarding card art slot")
            assertTrue("icon" in theme.art, "$slug must declare the icon art slot")
        }
    }

    @Test
    fun `every declared art slot resolves to a real image file`() {
        loadPacks().forEach { (slug, dir) ->
            val pack = PackLoader.load(dir).pack ?: return@forEach
            val theme = pack.theme ?: return@forEach
            theme.art.forEach { (slot, rel) ->
                val file = dir.resolve(rel)
                assertTrue(file.isRegularFile(), "$slug: theme.art['$slot'] missing file $rel")
                assertTrue(
                    rel.substringAfterLast('.').lowercase() in setOf("png", "jpg", "jpeg", "webp"),
                    "$slug: theme.art['$slot'] must be an image: $rel",
                )
            }
        }
    }

    @Test
    fun `pack themes build full dark and light schemes`() {
        listOf("p5r", "p3r", "metaphor").forEach { slug ->
            val theme = themeOf(slug)
            listOf(true, false).forEach { dark ->
                val scheme = packColorScheme(theme, dark)
                assertTrue(scheme.primary != scheme.error, "$slug dark=$dark primary collides with error")
                assertTrue(scheme.primary != scheme.surface, "$slug dark=$dark primary collides with surface")
                assertTrue(scheme.onSurface != scheme.surface, "$slug dark=$dark text unreadable")
            }
        }
    }

    @Test
    fun `on-primary pairs hold readable contrast in both modes`() {
        listOf("p5r", "p3r", "metaphor").forEach { slug ->
            val theme = themeOf(slug)
            listOf(true, false).forEach { dark ->
                val scheme = packColorScheme(theme, dark)
                val ratio = contrastRatio(scheme.primary, scheme.onPrimary)
                assertTrue(ratio >= 3.0, "$slug dark=$dark primary/onPrimary contrast $ratio < 3.0")
            }
        }
    }

    @Test
    fun `switching packs switches the accent`() {
        val primaries = listOf("p5r", "p3r", "metaphor").map { slug ->
            packColorScheme(themeOf(slug), dark = true).primary
        }
        assertEquals(3, primaries.map { it.value }.distinct().size, "all three packs must recolor: $primaries")
    }

    @Test
    fun `theme without colors falls back to the engine skin`() {
        val fallback = packColorScheme(PackTheme(), dark = true)
        assertEquals(Color(0xFFE8B84B), fallback.primary, "engine lantern primary expected")
    }

    // ---- Phase 13 (docs/ROADMAP-v3.md): the Phantom skin data ----

    @Test
    fun `p5r declares the phantom skin data`() {
        val theme = themeOf("p5r")
        assertEquals("masks", theme.motif)
        assertNotNull(theme.shapes, "p5r must declare shapes")
        assertEquals("jagged", theme.shapes?.card)
        assertEquals("slash", theme.shapes?.chip)
        assertEquals("ribbon", theme.shapes?.header)
        assertEquals("cut", theme.shapes?.frame)
        assertEquals("slash", theme.motion, "p5r motion token")
        val display = assertNotNull(theme.typography?.display, "p5r must declare a display font role")
        assertEquals("art/fonts/display.ttf", display.file)
        assertEquals("upper", display.case, "display case token")
        assertTrue(display.italic, "display must be italic")
        assertTrue("header" in theme.decor, "p5r decor header slot")
        assertTrue("divider" in theme.decor, "p5r decor divider slot")
    }

    @Test
    fun `p5r bundles its display font and license`() {
        val root = contentPacksDir() ?: error("no content checkout")
        val dir = root.resolve("p5r")
        val font = dir.resolve("art/fonts/display.ttf")
        assertTrue(font.isRegularFile(), "bundled display font missing")
        assertTrue(font.fileSize() <= 2L * 1024 * 1024, "font exceeds the 2 MB cap")
        assertTrue(font.fileSize() > 10_000, "font suspiciously small — truncated download?")
        val head = Files.readAllBytes(font).take(4)
        // TTF magic 00 01 00 00 — a real TrueType file, not a stray download.
        assertEquals(listOf<Byte>(0, 1, 0, 0), head, "display.ttf must start with the TTF magic")
        assertTrue(dir.resolve("art/fonts/OFL.txt").isRegularFile(), "OFL license must ship beside the font")
    }

    @Test
    fun `p5r decor art files exist on disk`() {
        val root = contentPacksDir() ?: error("no content checkout")
        val dir = root.resolve("p5r")
        val theme = themeOf("p5r")
        theme.decor.forEach { (slot, rel) ->
            assertTrue(dir.resolve(rel).isRegularFile(), "p5r theme.decor['$slot'] missing file $rel")
        }
    }

    @Test
    fun `p3r and metaphor stay token-less for skin isolation`() {
        // ROADMAP-v3 Phase 13 acceptance: the other packs must render
        // byte-identical to the engine look — no v3 skin layers declared.
        listOf("p3r", "metaphor").forEach { slug ->
            val theme = themeOf(slug)
            assertEquals(null, theme.shapes, "$slug must not declare shapes yet")
            assertEquals(null, theme.motion, "$slug must not declare motion yet")
            assertEquals(null, theme.typography, "$slug must not declare typography yet")
            assertTrue(theme.decor.isEmpty(), "$slug must not declare decor yet")
        }
    }

    // ---- contrast helper (WCAG relative luminance) ----

    private fun linearChannel(v: Double): Double =
        if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)

    private fun luminance(c: Color): Double =
        0.2126 * linearChannel(c.red.toDouble()) +
            0.7152 * linearChannel(c.green.toDouble()) +
            0.0722 * linearChannel(c.blue.toDouble())

    private fun contrastRatio(a: Color, b: Color): Double {
        val la = luminance(a) + 0.05
        val lb = luminance(b) + 0.05
        return maxOf(la, lb) / minOf(la, lb)
    }
}
