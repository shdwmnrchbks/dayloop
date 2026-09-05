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
        val confidantBackgrounds = media.filter { it.kind == MediaKinds.BANNER }
        val tarotBackdrops = media.filter { it.kind == MediaKinds.BACKDROP }
        val guideGraphics = media.filter {
            it.kind != MediaKinds.ACHIEVEMENT &&
                it.kind != MediaKinds.BANNER &&
                it.kind != MediaKinds.BACKDROP
        }
        val authoredMonths = p5r.walkthroughs.mapTo(linkedSetOf()) { it.month }
        val achievementIconRefs = p5r.achievements?.achievements.orEmpty()
            .mapNotNullTo(linkedSetOf()) { it.iconMediaRef }

        assertEquals(101, media.size)
        assertEquals(53, trophyArt.size, "every Royal trophy has its official Steam achievement image")
        assertEquals(23, confidantBackgrounds.size, "the supplied Confidants artwork contributes 23 backgrounds")
        assertEquals(24, tarotBackdrops.size, "all supplied Confidant tarot variants are declared")
        assertEquals(
            setOf("p5r.media.month-opener"),
            guideGraphics.mapTo(linkedSetOf()) { it.id },
            "the month opener should be the only remaining non-trophy guide graphic",
        )
        assertEquals(1, guideGraphics.count { it.kind == MediaKinds.MONTH })
        assertEquals(0, guideGraphics.count { it.kind == MediaKinds.SECTION })
        assertEquals(49, trophyArt.count { it.months.isNotEmpty() })
        assertTrue(
            trophyArt.all { it.id in achievementIconRefs },
            "every trophy image must resolve from an achievement icon reference",
        )

        assertEquals(23, confidantBackgrounds.flatMap { it.bonds }.distinct().size)
        confidantBackgrounds.forEach { item ->
            assertEquals(1, item.bonds.size, "${item.id}: each background must target one Confidant")
            assertTrue(item.file.startsWith("images/confidant"), "${item.id}: unexpected Confidant asset path")
            assertTrue(item.months.isEmpty(), "${item.id}: Confidant art must not masquerade as month art")
            assertTrue(item.dates.isEmpty(), "${item.id}: Confidant art must not masquerade as date art")
        }

        assertEquals(23, tarotBackdrops.flatMap { it.bonds }.distinct().size)
        tarotBackdrops.forEach { item ->
            assertEquals(1, item.bonds.size, "${item.id}: each tarot backdrop must target one Confidant")
            assertTrue(item.file.startsWith("images/tarot/"), "${item.id}: unexpected tarot asset path")
            assertTrue(item.months.isEmpty(), "${item.id}: tarot art must not masquerade as month art")
            assertTrue(item.dates.isEmpty(), "${item.id}: tarot art must not masquerade as date art")
        }
        val faithBackdrops = tarotBackdrops.filter { it.bonds == listOf("p5r.bond.faith") }
        assertEquals(2, faithBackdrops.size)
        assertEquals(
            setOf(0 to 5, 6 to 10),
            faithBackdrops.mapTo(linkedSetOf()) {
                requireNotNull(it.minBondRank) to requireNotNull(it.maxBondRank)
            },
            "Faith must show its base tarot before progression and switch after rank 5",
        )

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
