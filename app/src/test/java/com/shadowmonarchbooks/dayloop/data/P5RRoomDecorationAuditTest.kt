package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RRoomDecorationAuditTest {

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
    fun `p5r completion route keeps all 20 confidant room decorations in order`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
        val days = loaded.walkthroughs.flatMap { it.file.days }.associateBy { it.date }

        data class Decoration(
            val date: String,
            val name: String,
            val sequence: Int,
        )

        val expected = listOf(
            Decoration("2016-07-06", "Choco Fountain", 1),
            Decoration("2016-07-08", "Idol Poster", 2),
            Decoration("2016-10-15", "Swan Boat", 3),
            Decoration("2016-10-30", "Balloons", 4),
            Decoration("2016-11-01", "Sushi Teacup", 5),
            Decoration("2016-11-06", "Kumade", 6),
            Decoration("2016-11-12", "King Piece", 7),
            Decoration("2016-11-16", "Ramen Bowl", 8),
            Decoration("2016-11-30", "Hero Figure", 9),
            Decoration("2016-12-02", "Night Pennant", 10),
            Decoration("2016-12-04", "Star Stickers", 11),
            Decoration("2016-12-07", "I <3 Tokyo Shirt", 12),
            Decoration("2016-12-08", "Skytree Lamp", 13),
            Decoration("2016-12-13", "Featherman Dolls", 14),
            Decoration("2016-12-14", "Sea Slug Doll", 15),
            Decoration("2016-12-15", "Gi-Nyant Doll", 16),
            Decoration("2016-12-17", "Hamaya", 17),
            Decoration("2016-12-22", "Nude Statue", 18),
            Decoration("2017-01-24", "Giant Spatula", 19),
            Decoration("2017-01-29", "Shumai Cushion", 20),
        )

        val numberedSteps = loaded.walkthroughs
            .flatMap { it.file.days }
            .flatMap { day -> day.steps.map { day.date to it.label } }
            .filter { (_, label) -> "room decoration" in label }

        assertEquals(20, expected.size)
        assertEquals(20, numberedSteps.size, "completion route must expose exactly 20 numbered Confidant room decorations")

        expected.forEach { decoration ->
            val matches = days.getValue(decoration.date).steps.filter { step ->
                decoration.name in step.label &&
                    "(room decoration ${decoration.sequence}/20)" in step.label
            }
            assertEquals(
                1,
                matches.size,
                "${decoration.date}: expected ${decoration.name} as room decoration ${decoration.sequence}/20",
            )
        }

        val route = loaded.pack?.routes?.singleOrNull() ?: error("P5R should expose one completion route")
        assertTrue(route.description.orEmpty().contains("chooses romance", ignoreCase = true))
        assertTrue(route.description.orEmpty().contains("friendship is valid gameplay", ignoreCase = true))

        val nightPennant = days.getValue("2016-12-02").steps.single { "Night Pennant" in it.label }.label
        val skytreeLamp = days.getValue("2016-12-08").steps.single { "Skytree Lamp" in it.label }.label
        assertTrue("room decoration 10/20" in nightPennant)
        assertTrue("room decoration 13/20" in skytreeLamp)
    }
}
