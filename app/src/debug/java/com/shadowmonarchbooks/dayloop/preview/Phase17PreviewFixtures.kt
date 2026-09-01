package com.shadowmonarchbooks.dayloop.preview

import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.pack.schema.CalendarRange
import com.shadowmonarchbooks.dayloop.pack.schema.Pack
import com.shadowmonarchbooks.dayloop.pack.schema.PackTheme
import com.shadowmonarchbooks.dayloop.pack.schema.SkinShapes
import com.shadowmonarchbooks.dayloop.pack.schema.Slot
import com.shadowmonarchbooks.dayloop.pack.schema.StatDef
import com.shadowmonarchbooks.dayloop.widget.WidgetLayoutClass
import com.shadowmonarchbooks.dayloop.widget.WidgetSnapshot
import com.shadowmonarchbooks.dayloop.widget.widgetLayoutClass
import com.shadowmonarchbooks.dayloop.widget.widgetSkinSnapshot

/**
 * Stable Phase 17d inputs for the Phase 18 screenshot matrix.
 *
 * These fixtures deliberately use only generic skin DSL tokens. They contain no
 * game ids, game titles, or game-specific rendering branches; the masks/moon/
 * crown families exercise the same engine paths as the three bundled skins.
 */
data class PreviewSkinFixture(
    val id: String,
    val theme: PackTheme?,
)

data class WidgetPreviewFixture(
    val id: String,
    val skinId: String,
    val widthDp: Int,
    val heightDp: Int,
    val snapshot: WidgetSnapshot,
) {
    val layoutClass: WidgetLayoutClass
        get() = widgetLayoutClass(widthDp.toFloat(), heightDp.toFloat())
}

data class ColdStartPreviewFixture(
    val id: String,
    val skinId: String,
    val dark: Boolean,
    val pack: LoadedPack?,
)

object Phase17PreviewFixtures {
    val skins: List<PreviewSkinFixture> = listOf(
        PreviewSkinFixture(id = "engine", theme = null),
        PreviewSkinFixture(
            id = "masks",
            theme = PackTheme(
                accent = "#D81800",
                accentDark = "#D81800",
                style = "ink",
                motif = "masks",
                shapes = SkinShapes(card = "jagged", chip = "slash", header = "ribbon", frame = "cut"),
            ),
        ),
        PreviewSkinFixture(
            id = "moon",
            theme = PackTheme(
                accent = "#09134E",
                accentDark = "#1A46CE",
                style = "tonalSpot",
                motif = "moon",
                shapes = SkinShapes(chip = "diamond", header = "diamond"),
            ),
        ),
        PreviewSkinFixture(
            id = "crown",
            theme = PackTheme(
                accent = "#9E815F",
                accentDark = "#BEA52A",
                style = "expressive",
                motif = "crown",
                shapes = SkinShapes(card = "plaque", chip = "seal", header = "plaque", frame = "plaque"),
            ),
        ),
    )

    private data class WidgetSize(val id: String, val widthDp: Int, val heightDp: Int)

    private val widgetSizes = listOf(
        WidgetSize("compact", 180, 75),
        WidgetSize("standard", 250, 110),
        WidgetSize("expanded", 320, 160),
    )

    /** 4 skins × 3 supported Glance size classes = 12 stable widget fixtures. */
    val widgets: List<WidgetPreviewFixture> = skins.flatMap { skin ->
        widgetSizes.map { size ->
            WidgetPreviewFixture(
                id = "widget.${skin.id}.${size.id}",
                skinId = skin.id,
                widthDp = size.widthDp,
                heightDp = size.heightDp,
                snapshot = semanticWidgetSnapshot(skin.theme),
            )
        }
    }

    /** 4 skins × light/dark = 8 stable cold-start fixtures. */
    val coldStarts: List<ColdStartPreviewFixture> = skins.flatMap { skin ->
        listOf(false, true).map { dark ->
            ColdStartPreviewFixture(
                id = "cold-start.${skin.id}.${if (dark) "dark" else "light"}",
                skinId = skin.id,
                dark = dark,
                pack = previewPack(skin),
            )
        }
    }

    fun skin(id: String): PreviewSkinFixture = skins.first { it.id == id }

    private fun semanticWidgetSnapshot(theme: PackTheme?): WidgetSnapshot = WidgetSnapshot(
        packTitle = "Preview Pack",
        profileName = "Preview Profile",
        routeLabel = "Standard",
        dateLabel = "Tuesday, September 1",
        doneCount = 3,
        totalCount = 5,
        deadlineLabel = "Example deadline",
        deadlineDays = 2,
        skin = widgetSkinSnapshot(theme),
    )

    private fun previewPack(skin: PreviewSkinFixture): LoadedPack? {
        val theme = skin.theme ?: return null
        return LoadedPack(
            slug = "preview-${skin.id}",
            pack = Pack(
                packId = "preview-${skin.id}",
                title = "Preview Pack",
                contentVersion = 1,
                timeModel = "weekdayGrid",
                calendar = CalendarRange("2026-09-01", "2026-09-02"),
                slots = listOf(Slot("day", "Day")),
                stats = listOf(StatDef("focus", "Focus")),
                theme = theme,
            ),
        )
    }
}
