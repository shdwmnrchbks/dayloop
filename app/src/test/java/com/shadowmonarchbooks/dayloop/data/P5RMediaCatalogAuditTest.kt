package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.MediaKinds
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RMediaCatalogAuditTest {

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
    fun `p5r media separates trophy art from source-specific guide graphics`() {
        val root = contentPacksDir() ?: return
        val p5r = PackLoader.load(root.resolve("p5r"))
        assertEquals(emptyList(), p5r.parseIssues)

        val media = p5r.media?.media.orEmpty()
        val trophyArt = media.filter { it.kind == MediaKinds.ACHIEVEMENT }
        val guideGraphics = media.filterNot { it.kind == MediaKinds.ACHIEVEMENT }
        val authoredMonths = p5r.walkthroughs.mapTo(linkedSetOf()) { it.month }

        assertEquals(53, media.size)
        assertEquals(50, trophyArt.size, "the imported P5R guide archive contributes 50 trophy images")
        assertEquals(
            setOf(
                "p5r.media.month-opener",
                "p5r.media.marker-schedule",
                "p5r.media.marker-deadline",
            ),
            guideGraphics.mapTo(linkedSetOf()) { it.id },
            "all non-trophy P5R media should remain the three source-specific guide graphics",
        )
        assertEquals(1, guideGraphics.count { it.kind == MediaKinds.MONTH })
        assertEquals(2, guideGraphics.count { it.kind == MediaKinds.SECTION })

        guideGraphics.forEach { item ->
            assertTrue(
                item.caption.orEmpty().contains("guide", ignoreCase = true),
                "${item.id}: non-game artwork must stay explicitly identified as guide/source-specific presentation",
            )
            assertTrue(item.months.isNotEmpty(), "${item.id}: guide placement must remain anchored to at least one authored month")
            assertTrue(
                item.months.all { it in authoredMonths },
                "${item.id}: guide placement month falls outside authored P5R walkthrough coverage",
            )
            assertTrue(item.dates.isEmpty(), "${item.id}: reusable guide chrome should not masquerade as a gameplay-date fact")
            assertTrue(item.bonds.isEmpty(), "${item.id}: reusable guide chrome should not masquerade as a Confidant fact")
        }
    }
}
