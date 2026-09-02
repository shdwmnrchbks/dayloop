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

    @Test
    fun `p5r Royal DVD catalog keeps audited inventory waves points and modifiers`() {
        val root = contentPacksDir() ?: return
        val p5r = PackLoader.load(root.resolve("p5r"))
        assertEquals(emptyList(), p5r.parseIssues)

        val dvds = p5r.activities?.activities.orEmpty()
            .filter { it.kind == "dvd" }
            .associateBy { it.id }

        data class ExpectedDvd(
            val id: String,
            val label: String,
            val stat: String,
            val inventoryNote: String,
        )

        val expected = listOf(
            ExpectedDvd("p5r.activity.dvd.bubbly-hills", "Watch 'Bubbly Hills, 90210'", "charm", "Initial Scarlet rental inventory"),
            ExpectedDvd("p5r.activity.dvd.wraith", "Watch 'Wraith'", "kindness", "Initial Scarlet rental inventory"),
            ExpectedDvd("p5r.activity.dvd.guy-mcver", "Watch 'Guy McVer'", "proficiency", "Initial Scarlet rental inventory"),
            ExpectedDvd("p5r.activity.dvd.x-folders", "Watch 'The X Folders'", "guts", "Initial Scarlet rental inventory"),
            ExpectedDvd("p5r.activity.dvd.not-so-hot-betsy", "Watch 'Not-so-hot Betsy'", "charm", "from 6/1"),
            ExpectedDvd("p5r.activity.dvd.icu", "Watch 'ICU'", "kindness", "from 6/1"),
            ExpectedDvd("p5r.activity.dvd.jail-break", "Watch 'Jail Break'", "proficiency", "from 6/1"),
            ExpectedDvd("p5r.activity.dvd.the-running-dead", "Watch 'The Running Dead'", "guts", "from 6/1"),
            ExpectedDvd("p5r.activity.dvd.d-housewives", "Watch 'D Housewives'", "charm", "from 8/1"),
            ExpectedDvd("p5r.activity.dvd.mouse-md", "Watch 'Mouse MD'", "kindness", "from 8/1"),
            ExpectedDvd("p5r.activity.dvd.31", "Watch '31'", "guts", "from 8/1"),
            ExpectedDvd("p5r.activity.dvd.tee", "Watch 'Tee'", "proficiency", "from 8/1"),
        )

        assertEquals(12, dvds.size)
        expected.forEach { dvd ->
            val actual = dvds.getValue(dvd.id)
            assertEquals(dvd.label, actual.label, dvd.id)
            assertEquals("Attic TV", actual.location, dvd.id)
            assertEquals(mapOf(dvd.stat to 3), actual.statGains, "${dvd.id} must store Royal's +3 hidden-point base per viewing")
            val notes = actual.notes.orEmpty()
            assertTrue(dvd.inventoryNote in notes, "${dvd.id}: missing audited inventory wave")
            assertTrue("Two viewings" in notes, "${dvd.id}: Royal DVDs must state the two-viewing rule")
            assertTrue("no return deadline" in notes, "${dvd.id}: Royal rental subscription must not imply a return window")
            assertTrue("Craft of Cinema adds +2" in notes, "${dvd.id}: active movie/DVD modifier must remain explicit")
        }
    }

    @Test
    fun `p5r Royal retro games keep audited stages acquisition and point rewards`() {
        val root = contentPacksDir() ?: return
        val p5r = PackLoader.load(root.resolve("p5r"))
        assertEquals(emptyList(), p5r.parseIssues)

        val games = p5r.activities?.activities.orEmpty()
            .filter { it.kind == "videoGame" }
            .associateBy { it.id }

        data class ExpectedGame(
            val id: String,
            val label: String,
            val stat: String,
            val stages: String,
            val acquisition: String,
        )

        val expected = listOf(
            ExpectedGame("p5r.activity.videoGame.star-forneus", "Play 'Star Forneus'", "guts", "Three-stage", "Retro Game Set"),
            ExpectedGame("p5r.activity.videoGame.gambla-goemon", "Play 'Gambla Goemon'", "charm", "Two-stage", "from 7/26"),
            ExpectedGame("p5r.activity.videoGame.punch-ouch", "Play 'Punch Ouch'", "charm", "Three-stage", "Akihabara retro game shop"),
            ExpectedGame("p5r.activity.videoGame.featherman-seeker", "Play 'Featherman Seeker'", "knowledge", "three-stage", "Akihabara retro game shop"),
            ExpectedGame("p5r.activity.videoGame.train-of-life", "Play 'Train of Life'", "kindness", "Three-stage", "Akihabara retro game shop"),
            ExpectedGame("p5r.activity.videoGame.power-intuition", "Play 'Power Intuition'", "guts", "Three-stage", "Akihabara retro game shop"),
            ExpectedGame("p5r.activity.videoGame.golfer-sarutahiko", "Play 'Golfer Sarutahiko'", "proficiency", "Three-stage", "Akihabara retro game shop"),
        )

        assertEquals(7, games.size)
        expected.forEach { game ->
            val actual = games.getValue(game.id)
            assertEquals(game.label, actual.label, game.id)
            assertEquals("Attic TV", actual.location, game.id)
            assertEquals(mapOf(game.stat to 3), actual.statGains, "${game.id} must store the successful-clear +3 hidden-point reward")
            val notes = actual.notes.orEmpty()
            assertTrue(game.stages in notes, "${game.id}: missing audited stage count")
            assertTrue(game.acquisition in notes, "${game.id}: missing audited acquisition source")
            assertTrue("Game Secrets enables a cheat" in notes, "${game.id}: Game Secrets should affect difficulty, not point value")
        }
    }
}
