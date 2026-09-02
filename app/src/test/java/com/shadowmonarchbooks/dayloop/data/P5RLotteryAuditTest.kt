package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class P5RLotteryAuditTest {

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
    fun `p5r Easy Money route warns that the lottery is RNG and Summer Mammoth does not qualify`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val july1 = loaded.walkthroughs
            .flatMap { it.file.days }
            .single { it.date == "2016-07-01" }
        val lottery = july1.steps.single { "Easy Money" in it.label }

        assertTrue("Shibuya Station Square" in lottery.label)
        assertTrue("RNG" in lottery.label)
        assertTrue("Summer Mammoth" in lottery.label)
        assertTrue("do not count" in lottery.label)
        assertTrue("qualifying tickets" in lottery.label)
        assertTrue("check each result" in lottery.label)

        val achievement = loaded.achievements?.achievements.orEmpty()
            .single { it.title == "Easy Money" }
        assertTrue(achievement.description.orEmpty().contains("qualifying lottery prize", ignoreCase = true))
        assertEquals("2016-04-25", achievement.availableFrom, "Apr 25 is the first possible result from an ordinary Apr 18 ticket")
        assertEquals(AchievementTrackingTypes.MANUAL, achievement.tracking.type)
        assertNull(achievement.expectedBy, "Easy Money is RNG and the completion route must not claim a guaranteed completion date")
    }
}
