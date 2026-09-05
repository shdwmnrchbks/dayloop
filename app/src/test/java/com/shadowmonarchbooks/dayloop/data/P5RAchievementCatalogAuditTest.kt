package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.GameCalendar
import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import com.shadowmonarchbooks.dayloop.pack.schema.MediaKinds
import com.shadowmonarchbooks.dayloop.ui.media.anchorText
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
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
        val trophyMediaIds = media
            .filter { it.kind == MediaKinds.ACHIEVEMENT }
            .mapTo(linkedSetOf()) { it.id }
        val legacyTrophyIds = trophyMediaIds.filterTo(linkedSetOf()) { it in byId }
        val packDef = p5r.pack ?: return
        val calendar = GameCalendar.of(packDef.calendar) ?: return

        assertEquals(53, achievements.size)
        assertEquals(53, byId.size, "Royal trophy ids must be unique")
        assertEquals(53, trophyMediaIds.size, "every Royal trophy should have its official Steam icon")
        assertEquals(50, legacyTrophyIds.size, "existing media-backed trophy ids must remain stable for saved progress")
        legacyTrophyIds.forEach { id ->
            assertEquals(
                mediaById.getValue(id).title,
                byId.getValue(id).title,
                "$id must keep the same visible trophy identity when migrating from media fallback to achievements.json",
            )
        }

        val achievementsPreviouslyMissingArt = mapOf(
            "p5r.achievement.its-showtime" to ("It's Showtime!" to "p5r.media.achievement.its-showtime"),
            "p5r.achievement.accident-prone" to ("Accident-Prone" to "p5r.media.achievement.accident-prone"),
            "p5r.achievement.master-of-akihabara" to ("Master of Akihabara" to "p5r.media.achievement.master-of-akihabara"),
        )
        achievementsPreviouslyMissingArt.forEach { (id, identity) ->
            val (title, mediaRef) = identity
            val achievement = byId.getValue(id)
            assertEquals(title, achievement.title)
            assertEquals(mediaRef, achievement.iconMediaRef, "$title should resolve its official Steam icon")
        }

        assertEquals("2016-06-21", byId.getValue("p5r.achievement.its-showtime").availableFrom)
        assertEquals("2016-06-21", byId.getValue("p5r.achievement.accident-prone").availableFrom)
        assertEquals("2016-08-31", byId.getValue("p5r.achievement.master-of-akihabara").availableFrom)

        achievements.forEach { achievement ->
            assertTrue(
                achievement.tracking.type != AchievementTrackingTypes.MANUAL,
                "${achievement.title} should use an audited route rule instead of month-end manual tracking",
            )
            assertTrue(!achievement.expectedBy.isNullOrBlank(), "${achievement.title} needs a route checkpoint")
            achievement.availableFrom?.let { date ->
                assertTrue(date in calendar, "${achievement.title}: availableFrom $date is outside the Royal calendar")
            }
            achievement.expectedBy?.let { date ->
                assertTrue(date in calendar, "${achievement.title}: expectedBy $date is outside the Royal calendar")
            }
            achievement.iconMediaRef?.let { ref ->
                assertTrue(ref in mediaById, "${achievement.title}: iconMediaRef '$ref' does not resolve")
                assertEquals(achievement.title, mediaById.getValue(ref).title, "icon refs must preserve the visible trophy identity")
            }
        }

        val loadedPack = LoadedPack(
            slug = "p5r",
            pack = packDef,
            media = media,
            achievements = achievements,
        )
        val trophyArt = media.first { it.kind == MediaKinds.ACHIEVEMENT && it.months.isNotEmpty() }
        assertTrue(
            anchorText(loadedPack, trophyArt).startsWith("guide placement months:"),
            "achievement art month anchors must be presented as guide placement, not trophy unlock timing",
        )
        val monthArt = media.first { it.kind == MediaKinds.MONTH && it.months.isNotEmpty() }
        assertTrue(
            anchorText(loadedPack, monthArt).startsWith("months:"),
            "ordinary media anchors should keep their neutral placement label",
        )

        assertEquals(53, achievements.count { it.iconMediaRef != null })
        assertEquals(0, achievements.count { it.iconMediaRef == null })
        assertTrue(p5r.achievements?.events.orEmpty().size >= 40)
    }
}
