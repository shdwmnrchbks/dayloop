package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RBathhouseAuditTest {

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
    fun `p5r Getting the Vapors uses the first confirmed Royal opportunity without inventing a route deadline`() {
        val root = contentPacksDir() ?: return
        val loaded = PackLoader.load(root.resolve("p5r"))
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())

        val achievement = loaded.achievements?.achievements.orEmpty()
            .single { it.title == "Getting the Vapors" }
        val description = achievement.description.orEmpty()

        assertEquals("2016-05-19", achievement.availableFrom)
        assertTrue(description.contains("bathhouse", ignoreCase = true))
        assertTrue(description.contains("rainy", ignoreCase = true))
        assertTrue(description.contains("snowy", ignoreCase = true))
        assertEquals(AchievementTrackingTypes.EVENT, achievement.tracking.type)
        assertEquals("2016-06-21", achievement.expectedBy)

        assertTrue(
            loaded.deadlines?.deadlines.orEmpty().none { it.label.contains("Getting the Vapors", ignoreCase = true) },
            "An RNG/weather-dependent trophy is not a universal deadline",
        )
    }
}
