package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RTraderSakaiAuditTest {

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
    fun `p5r completion route keeps all 16 audited Trader Sakai exchanges`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        data class Trade(
            val date: String,
            val requested: String,
            val reward: String,
            val sequence: Int,
        )

        val expected = listOf(
            Trade("2016-06-12", "Imported Protein", "Decorative Whip", 1),
            Trade("2016-06-26", "Yakisoba Pan", "Black Robe", 2),
            Trade("2016-07-12", "Soothing Soba", "Koedo Sword", 3),
            Trade("2016-07-26", "MRE Ration", "Factorization Guide", 4),
            Trade("2016-08-07", "Exorcism Water", "Model Gun", 5),
            Trade("2016-08-23", "Melon Pan", "Old Man's Fist", 6),
            Trade("2016-09-04", "Phantom Wafer", "Strength Up Ofuda", 7),
            Trade("2016-09-19", "Thief Mask", "Magic Up Ofuda", 8),
            Trade("2016-10-02", "Calling Postcard", "Strawberry Daifuku", 9),
            Trade("2016-10-16", "Gear Girimehkala", "Hot Blooded Sword", 10),
            Trade("2016-10-30", "Mystery Stew", "Angel Badge", 11),
            Trade("2016-11-13", "Moon Dango", "Kintaro Axe", 12),
            Trade("2016-11-27", "Legendary Yaki-imo", "Empowering Ofuda", 13),
            Trade("2016-12-11", "Angel Tart", "Fervent Bat", 14),
            Trade("2017-01-13", "Special Chimaki", "Strength Belt", 15),
            Trade("2017-01-22", "Supernova Burger", "Old Man's Elixir", 16),
        )

        assertEquals(16, expected.size)
        expected.forEach { trade ->
            val matching = days.getValue(trade.date).steps.filter { step ->
                val normalized = step.label.replace("-", " ")
                val requested = trade.requested.replace("-", " ")
                val reward = trade.reward.replace("-", " ")
                val hasSequence = "(${trade.sequence}/16)" in normalized ||
                    "(trade ${trade.sequence}/16)" in normalized
                requested in normalized && reward in normalized && hasSequence
            }
            assertEquals(
                1,
                matching.size,
                "${trade.date}: expected exactly one ${trade.requested} -> ${trade.reward} Trader Sakai step (${trade.sequence}/16)",
            )
        }
    }
}
