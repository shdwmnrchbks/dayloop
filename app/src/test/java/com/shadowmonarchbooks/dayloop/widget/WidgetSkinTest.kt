package com.shadowmonarchbooks.dayloop.widget

import com.shadowmonarchbooks.dayloop.pack.schema.PackTheme
import com.shadowmonarchbooks.dayloop.pack.schema.SkinShapes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class WidgetSkinTest {

    @Test
    fun `theme-less widget keeps engine treatment and palette`() {
        val skin = widgetSkinSnapshot(null)
        assertEquals(WidgetTreatment.ENGINE, skin.treatment)
        assertEquals(0xFF14171F.toInt(), skin.palette.backgroundArgb)
        assertEquals(0xFFFFC857.toInt(), skin.palette.primaryArgb)
    }

    @Test
    fun `motif families resolve to Glance-safe treatments`() {
        assertEquals(WidgetTreatment.ANGULAR, widgetTreatment(PackTheme(motif = "masks")))
        assertEquals(WidgetTreatment.GLASS, widgetTreatment(PackTheme(motif = "moon")))
        assertEquals(WidgetTreatment.FRAMED, widgetTreatment(PackTheme(motif = "crown")))
    }

    @Test
    fun `explicit shapes resolve without any pack identity`() {
        assertEquals(
            WidgetTreatment.ANGULAR,
            widgetTreatment(PackTheme(shapes = SkinShapes(header = "ribbon"))),
        )
        assertEquals(
            WidgetTreatment.FRAMED,
            widgetTreatment(PackTheme(shapes = SkinShapes(frame = "plaque"))),
        )
    }

    @Test
    fun `color-only theme keeps engine chrome but inherits pack palette`() {
        val skin = widgetSkinSnapshot(PackTheme(accent = "#336699", accentDark = "#6699CC"))
        assertEquals(WidgetTreatment.ENGINE, skin.treatment)
        assertNotEquals(WidgetPalette().primaryArgb, skin.palette.primaryArgb)
    }

    @Test
    fun `responsive sizes cover compact standard and expanded layouts`() {
        assertEquals(WidgetLayoutClass.COMPACT, widgetLayoutClass(180f, 75f))
        assertEquals(WidgetLayoutClass.STANDARD, widgetLayoutClass(250f, 110f))
        assertEquals(WidgetLayoutClass.EXPANDED, widgetLayoutClass(320f, 160f))
    }

    @Test
    fun `either narrow width or short height forces compact layout`() {
        assertEquals(WidgetLayoutClass.COMPACT, widgetLayoutClass(210f, 160f))
        assertEquals(WidgetLayoutClass.COMPACT, widgetLayoutClass(320f, 90f))
    }
}
