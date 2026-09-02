package com.shadowmonarchbooks.dayloop.pack.theme

import com.shadowmonarchbooks.dayloop.pack.schema.SkinFont
import com.shadowmonarchbooks.dayloop.pack.schema.SkinShapes
import com.shadowmonarchbooks.dayloop.pack.schema.SkinTypography
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Pins the skin DSL's resolution semantics (docs/ROADMAP-v3.md Phase 12):
 * explicit token > motif family default > engine look, per layer.
 */
class SkinTokensTest {

    @Test
    fun `closed sets contain the documented tokens`() {
        assertEquals(setOf("masks", "moon", "crown"), SkinTokens.MOTIFS)
        assertEquals(
            setOf("jagged", "slash", "cut", "ribbon", "diamond", "plaque", "seal"),
            SkinTokens.SHAPES,
        )
        assertEquals(setOf("slash", "fade", "flip", "none"), SkinTokens.MOTIONS)
        assertEquals(setOf("tap", "advance", "complete"), SkinTokens.SFX_SLOTS)
        assertEquals(setOf("ogg"), SkinTokens.SFX_EXTENSIONS)
        assertEquals(100L * 1024, SkinTokens.MAX_SFX_BYTES)
        assertEquals(setOf("upper"), SkinTokens.FONT_CASES)
        assertEquals(setOf("header", "panel", "divider"), SkinTokens.DECOR_SLOTS)
        assertEquals(setOf("cutline", "halftone", "grain", "glass", "filigree"), SkinTokens.DECOR_PAINTERS)
    }

    @Test
    fun `explicit shape tokens beat the motif family`() {
        val shapes = SkinShapes(card = "cut")
        assertEquals("cut", SkinTokens.resolveShape(shapes, "masks", "card"))
    }

    @Test
    fun `plaque and seal tokens resolve in every slot`() {
        // ROADMAP-v3 Phase 15: the royal vocabulary rides the same closed set.
        for (slot in SkinTokens.SHAPE_SLOTS) {
            val shapes = SkinShapes(card = "plaque", chip = "seal", header = "plaque", frame = "plaque")
            val expected = if (slot == "chip") "seal" else "plaque"
            assertEquals(expected, SkinTokens.resolveShape(shapes, "crown", slot), "$slot")
        }
    }

    @Test
    fun `masks family fills unspecified slots`() {
        assertEquals("cut", SkinTokens.resolveShape(null, "masks", "card"))
        assertEquals("slash", SkinTokens.resolveShape(null, "masks", "chip"))
        assertEquals("slash", SkinTokens.resolveShape(null, "masks", "header"))
        assertNull(SkinTokens.resolveShape(null, "masks", "frame"))
    }

    @Test
    fun `painter-driven families declare no silhouettes`() {
        for (slot in SkinTokens.SHAPE_SLOTS) {
            assertNull(SkinTokens.familyShape("moon", slot), "moon/$slot")
            assertNull(SkinTokens.familyShape("crown", slot), "crown/$slot")
        }
    }

    @Test
    fun `no motif and no tokens resolve to the engine look`() {
        for (slot in SkinTokens.SHAPE_SLOTS) {
            assertNull(SkinTokens.resolveShape(null, null, slot))
        }
    }

    @Test
    fun `motif maps to its painter family`() {
        assertEquals("cutline", SkinTokens.painterForMotif("masks"))
        assertEquals("glass", SkinTokens.painterForMotif("moon"))
        assertEquals("filigree", SkinTokens.painterForMotif("crown"))
        assertNull(SkinTokens.painterForMotif("unknown"))
        assertNull(SkinTokens.painterForMotif(null))
    }

    @Test
    fun `motion resolves explicitly only`() {
        assertEquals("slash", SkinTokens.resolveMotion("slash"))
        assertNull(SkinTokens.resolveMotion(null))
        // Motifs never set motion — checked via a full theme below.
    }

    @Test
    fun `case transform applies only the upper token`() {
        val upper = SkinFont(file = "f.ttf", case = "upper")
        assertEquals("TAKE YOUR TIME", SkinTokens.applyCase("take your time", upper))
        assertEquals("Take your time", SkinTokens.applyCase("Take your time", SkinFont(file = "f.ttf")))
        assertEquals("mixed", SkinTokens.applyCase("mixed", null))
    }

    @Test
    fun `role coverage follows declared roles`() {
        val typography = SkinTypography(display = SkinFont(file = "d.ttf"))
        assertTrue(SkinTokens.rolesOf(typography, "display"))
        assertTrue(!SkinTokens.rolesOf(typography, "title"))
        assertTrue(!SkinTokens.rolesOf(SkinTypography(), "body"))
    }
}
