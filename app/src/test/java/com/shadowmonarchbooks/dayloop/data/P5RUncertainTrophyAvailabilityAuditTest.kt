package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RUncertainTrophyAvailabilityAuditTest {

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
    fun `p5r state branch trophies keep universal availability separate from route checkpoints`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val achievements = loaded.achievements?.achievements.orEmpty().associateBy { it.title }
        val intentionallyUndated = setOf(
            "The Phenomenal Phantom Thief",
            "The Path Chosen",
            "A Most Studious Disguise",
            "Pure Perfection",
            "My Closest Partner",
            "True Confidence",
            "Unsurpassed Rebel",
            "Professional Modification",
        )

        assertEquals(intentionallyUndated, achievements.values.filter { it.availableFrom == null }.mapTo(linkedSetOf()) { it.title })
        intentionallyUndated.forEach { title ->
            val achievement = achievements.getValue(title)
            assertEquals(null, achievement.availableFrom, "$title depends on player state, route branch, or progression rather than one universal date")
            assertTrue(achievement.tracking.type != AchievementTrackingTypes.MANUAL)
            assertTrue(!achievement.expectedBy.isNullOrBlank(), "$title should still have an audited route checkpoint")
        }

        // Keep a few nearby exact anchors explicit so this policy cannot be
        // misread as removing useful dates from deterministic Royal mechanics.
        assertEquals("2016-04-28", achievements.getValue("Tokyo Tourist").availableFrom)
        assertEquals("2016-05-19", achievements.getValue("Getting the Vapors").availableFrom)
        assertEquals("2016-07-04", achievements.getValue("Angler's Debut").availableFrom)
        assertEquals("2017-01-10", achievements.getValue("Awakening the Phantom Thieves").availableFrom)
    }
}
