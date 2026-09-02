package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RActivityCatalogAuditTest {

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
    fun `p5r Royal movie catalog keeps audited names theaters and base points`() {
        val root = contentPacksDir() ?: return
        val p5r = PackLoader.load(root.resolve("p5r"))
        assertEquals(emptyList(), p5r.parseIssues)

        val movies = p5r.activities?.activities.orEmpty()
            .filter { it.id.startsWith("p5r.activity.movie.") }
            .associateBy { it.id }

        data class ExpectedMovie(
            val id: String,
            val label: String,
            val stat: String,
            val location: String,
        )

        val expected = listOf(
            ExpectedMovie("p5r.activity.movie.tanktop-millionaire", "Watch 'Tanktop Millionaire'", "guts", "Central Street movie theater"),
            ExpectedMovie("p5r.activity.movie.cake-knight-rises", "Watch 'The Cake Knight Rises'", "kindness", "Central Street movie theater"),
            ExpectedMovie("p5r.activity.movie.love-possibly", "Watch 'Love Possibly'", "charm", "Central Street movie theater"),
            ExpectedMovie("p5r.activity.movie.le-miserable", "Watch 'Le Miserable'", "kindness", "Central Street movie theater"),
            ExpectedMovie("p5r.activity.movie.admission-impossible", "Watch 'Admission Impossible'", "proficiency", "Central Street movie theater"),
            ExpectedMovie("p5r.activity.movie.clean-hard", "Watch 'Clean Hard'", "kindness", "Central Street movie theater"),
            ExpectedMovie("p5r.activity.movie.finding-beemo", "Watch 'Finding Beemo'", "charm", "Central Street movie theater"),
            ExpectedMovie("p5r.activity.movie.like-a-dragon", "Watch 'Like a Dragon'", "guts", "Shinjuku movie theater"),
            ExpectedMovie("p5r.activity.movie.saraemon", "Watch 'Saraemon'", "knowledge", "Shinjuku movie theater"),
            ExpectedMovie("p5r.activity.movie.duhvengers", "Watch 'Duh-vengers'", "kindness", "Shinjuku movie theater"),
            ExpectedMovie("p5r.activity.movie.pach-saw", "Watch 'Pach-Saw'", "guts", "Shinjuku movie theater"),
            ExpectedMovie("p5r.activity.movie.bite-club", "Watch 'Bite Club'", "guts", "Shinjuku movie theater"),
            ExpectedMovie("p5r.activity.movie.showtime-redemption", "Watch 'Showtime Redemption'", "charm", "Yongen-Jaya movie theater"),
            ExpectedMovie("p5r.activity.movie.back-to-the-ninja", "Watch 'Back to the Ninja'", "knowledge", "Yongen-Jaya movie theater"),
            ExpectedMovie("p5r.activity.movie.over-the-pigeons-nest", "Watch 'Over the Pigeon's Nest'", "kindness", "Yongen-Jaya movie theater"),
            ExpectedMovie("p5r.activity.movie.merry-christmess", "Watch 'Merry Christmess'", "guts", "Yongen-Jaya movie theater"),
            ExpectedMovie("p5r.activity.movie.march-of-the-lambs", "Watch 'March of the Lambs'", "proficiency", "Yongen-Jaya movie theater"),
            ExpectedMovie("p5r.activity.movie.the-goodfather", "Watch 'The Goodfather'", "kindness", "Yongen-Jaya movie theater"),
        )

        assertEquals(expected.size, movies.size, "P5R should expose the complete 18-film Royal theater catalog")
        expected.forEach { movie ->
            val actual = movies.getValue(movie.id)
            assertEquals(movie.label, actual.label, movie.id)
            assertEquals(movie.location, actual.location, movie.id)
            assertEquals(mapOf(movie.stat to 5), actual.statGains, "${movie.id} must store the 5-point first-viewing base before Craft of Cinema")
        }

        assertTrue(movies.getValue("p5r.activity.movie.love-possibly").notes.orEmpty().contains("7/17 Ann"))
        assertTrue(movies.getValue("p5r.activity.movie.like-a-dragon").notes.orEmpty().contains("7/28 Makoto"))
        assertTrue(movies.getValue("p5r.activity.movie.back-to-the-ninja").notes.orEmpty().contains("10/2 Futaba"))
        assertTrue(movies.getValue("p5r.activity.movie.pach-saw").notes.orEmpty().contains("11/13 Haru"))
    }
}
