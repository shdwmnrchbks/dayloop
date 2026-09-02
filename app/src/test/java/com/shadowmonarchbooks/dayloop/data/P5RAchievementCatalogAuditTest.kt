package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.GameCalendar
import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import com.shadowmonarchbooks.dayloop.pack.schema.MediaKinds
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class P5RAchievementCatalogAuditTest {

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
    fun `p5r exposes all 53 Royal trophies without breaking legacy progress ids`() {
        val root = contentPacksDir() ?: return
        val p5r = PackLoader.load(root.resolve("p5r"))
        assertEquals(emptyList(), p5r.parseIssues)

        val achievements = p5r.achievements?.achievements.orEmpty()
        val byId = achievements.associateBy { it.id }
        val media = p5r.media?.media.orEmpty()
        val mediaById = media.associateBy { it.id }
        val legacyTrophyIds = media
            .filter { it.kind == MediaKinds.ACHIEVEMENT }
            .mapTo(linkedSetOf()) { it.id }
        val calendar = GameCalendar.of(p5r.pack?.calendar ?: return) ?: return

        assertEquals(53, achievements.size)
        assertEquals(53, byId.size, "Royal trophy ids must be unique")
        assertEquals(50, legacyTrophyIds.size, "the imported guide art covers 50 of Royal's 53 trophies")
        assertTrue(byId.keys.containsAll(legacyTrophyIds), "legacy media trophy ids must remain achievement ids so existing checked state survives")

        val addedWithoutGuideArt = mapOf(
            "p5r.achievement.its-showtime" to "It's Showtime!",
            "p5r.achievement.accident-prone" to "Accident-Prone",
            "p5r.achievement.master-of-akihabara" to "Master of Akihabara",
        )
        addedWithoutGuideArt.forEach { (id, title) ->
            val achievement = byId.getValue(id)
            assertEquals(title, achievement.title)
            assertNull(achievement.iconMediaRef, "$title should use the built-in no-image fallback until matching art is bundled")
        }

        assertEquals("2016-06-20", byId.getValue("p5r.achievement.its-showtime").availableFrom)
        assertEquals("2016-06-21", byId.getValue("p5r.achievement.accident-prone").availableFrom)
        assertEquals("2016-08-31", byId.getValue("p5r.achievement.master-of-akihabara").availableFrom)

        achievements.forEach { achievement ->
            assertEquals(
                AchievementTrackingTypes.MANUAL,
                achievement.tracking.type,
                "P5R trophy migration intentionally stays manual until a trophy has a separately audited deterministic rule",
            )
            achievement.availableFrom?.let { date ->
                assertTrue(date in calendar, "${achievement.title}: availableFrom $date is outside the Royal calendar")
            }
            achievement.expectedBy?.let { date ->
                assertTrue(date in calendar, "${achievement.title}: expectedBy $date is outside the Royal calendar")
            }
            achievement.iconMediaRef?.let { ref ->
                assertTrue(ref in mediaById, "${achievement.title}: iconMediaRef '$ref' does not resolve")
                assertEquals(achievement.id, ref, "legacy trophy ids and icon refs should stay aligned for progress compatibility")
            }
        }

        assertEquals(50, achievements.count { it.iconMediaRef != null })
        assertEquals(3, achievements.count { it.iconMediaRef == null })
    }
}
