package com.shadowmonarchbooks.dayloop.ui.bonds

import com.shadowmonarchbooks.dayloop.pack.schema.Bond
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.pack.schema.MediaItem
import com.shadowmonarchbooks.dayloop.pack.schema.MediaKinds
import com.shadowmonarchbooks.dayloop.pack.schema.RankStep
import com.shadowmonarchbooks.dayloop.pack.schema.Step
import com.shadowmonarchbooks.dayloop.progress.StepKey
import com.shadowmonarchbooks.dayloop.progress.StepMark
import kotlin.test.Test
import kotlin.test.assertEquals

class BondProgressTest {

    private val chariot = Bond(
        id = "p5r.bond.chariot",
        label = "Chariot",
        ranks = (1..10).map { RankStep(rank = it) },
    )
    private val day = Day(
        date = "2016-06-04",
        weekday = "sat",
        steps = listOf(
            Step("Hang out with Ryuji — Chariot reaches rank 5"),
            Step("Hang out with Ann — Lovers reaches rank 4"),
        ),
    )

    @Test
    fun `done matching task advances bond rank`() {
        assertEquals(
            5,
            completedBondRank(
                bond = chariot,
                days = mapOf(day.date to day),
                marks = mapOf(StepKey(day.date, 0) to StepMark.DONE),
            ),
        )
    }

    @Test
    fun `unchecked skipped and unrelated tasks do not advance bond rank`() {
        assertEquals(
            0,
            completedBondRank(
                bond = chariot,
                days = mapOf(day.date to day),
                marks = mapOf(
                    StepKey(day.date, 0) to StepMark.SKIP,
                    StepKey(day.date, 1) to StepMark.DONE,
                ),
            ),
        )
    }

    @Test
    fun `rank-aware bond backdrop switches at rank six`() {
        val media = listOf(
            MediaItem(
                id = "p5r.media.tarot.faith",
                file = "images/tarot/Faith.png",
                kind = MediaKinds.BACKDROP,
                title = "Faith Tarot (Ranks 0–5)",
                bonds = listOf("p5r.bond.faith"),
                minBondRank = 0,
                maxBondRank = 5,
            ),
            MediaItem(
                id = "p5r.media.tarot.faith-rank-6",
                file = "images/tarot/Faith_Rank_6.png",
                kind = MediaKinds.BACKDROP,
                title = "Faith Tarot (Ranks 6–10)",
                bonds = listOf("p5r.bond.faith"),
                minBondRank = 6,
                maxBondRank = 10,
            ),
        )

        assertEquals("p5r.media.tarot.faith", selectBondBackdrop(media, completedRank = 0)?.id)
        assertEquals("p5r.media.tarot.faith", selectBondBackdrop(media, completedRank = 5)?.id)
        assertEquals("p5r.media.tarot.faith-rank-6", selectBondBackdrop(media, completedRank = 6)?.id)
    }
}
