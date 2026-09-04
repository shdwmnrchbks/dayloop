package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetaphorHeartsAsOneAuditTest {

    private data class RankEight(
        val bondId: String,
        val date: String,
        val phrase: String,
    )

    private val rankEights = linkedMapOf(
        "metaphor.event.follower-strohl-rank8" to RankEight("metaphor.bond.strohl", "2100-08-07", "Join Strohl visiting his parents"),
        "metaphor.event.follower-hulkenberg-rank8" to RankEight("metaphor.bond.hulkenberg", "2100-08-22", "Hulkenberg's resolve"),
        "metaphor.event.follower-heismay-rank8" to RankEight("metaphor.bond.heismay", "2100-08-23", "sit with Heismay"),
        "metaphor.event.follower-maria-rank8" to RankEight("metaphor.bond.maria", "2100-09-03", "Answer Maria's summons at the inn"),
        "metaphor.event.follower-junah-rank8" to RankEight("metaphor.bond.junah", "2100-09-22", "Visit Myrtus with Junah"),
        "metaphor.event.follower-gallica-rank8" to RankEight("metaphor.bond.gallica", "2100-09-24", "Gallica reaches Follower rank 8"),
        "metaphor.event.follower-brigitta-rank8" to RankEight("metaphor.bond.brigitta", "2100-09-26", "catch up with Brigitta"),
        "metaphor.event.follower-bardon-rank8" to RankEight("metaphor.bond.bardon", "2100-09-27", "Visit the recovered Bardon"),
        "metaphor.event.follower-catherina-rank8" to RankEight("metaphor.bond.catherina", "2100-09-27", "Hear Catherina's announcement"),
        "metaphor.event.follower-more-rank8" to RankEight("metaphor.bond.more", "2100-09-30", "More reaches Follower rank 8"),
        "metaphor.event.follower-eupha-rank8" to RankEight("metaphor.bond.eupha", "2100-10-03", "Hear Eupha out"),
        "metaphor.event.follower-basilio-rank8" to RankEight("metaphor.bond.basilio", "2100-10-04", "raises Basilio to Follower rank 8"),
        "metaphor.event.follower-alonzo-rank8" to RankEight("metaphor.bond.alonzo", "2100-10-04", "close out the Faker bond"),
        "metaphor.event.follower-neuras-rank8" to RankEight("metaphor.bond.neuras", "2100-10-05", "Follower rank 8 for Neuras"),
    )

    @Test
    fun `Hearts as One tracks exactly all fourteen Follower rank eights`() {
        val achievement = heartsAsOne()
        assertEquals(AchievementTrackingTypes.ALL_EVENTS, achievement.tracking.type)
        assertEquals("2100-08-07", achievement.availableFrom)
        assertEquals("2100-10-05", achievement.expectedBy)
        assertEquals(rankEights.keys.toList(), achievement.tracking.events)
        assertEquals(14, achievement.tracking.events.distinct().size)
    }

    @Test
    fun `every Hearts as One event resolves to one authored rank eight step`() {
        val loaded = loadMetaphor()
        val events = assertNotNull(loaded.achievements).events.associateBy { it.id }

        rankEights.forEach { (eventId, expected) ->
            val event = assertNotNull(events[eventId], eventId)
            assertEquals(expected.date, event.date, eventId)
            assertEquals(expected.phrase, event.labelContains, eventId)
            assertEquals(
                1,
                day(expected.date).steps.count { it.label.contains(expected.phrase, ignoreCase = true) },
                "$eventId should resolve to exactly one ${expected.date} walkthrough step",
            )
        }
    }

    @Test
    fun `Follower catalog rank eight route dates agree with Hearts as One`() {
        val bonds = assertNotNull(loadMetaphor().bonds).bonds.associateBy { it.id }
        assertEquals(rankEights.values.map { it.bondId }.toSet(), bonds.keys)

        rankEights.values.forEach { expected ->
            val bond = assertNotNull(bonds[expected.bondId], expected.bondId)
            val rankEight = assertNotNull(bond.ranks.singleOrNull { it.rank == 8 }, "${expected.bondId} rank 8")
            val routeDate = rankEight.scheduledFor ?: rankEight.availableFrom
            assertEquals(expected.date, routeDate, expected.bondId)
        }
    }

    @Test
    fun `Neuras is the final authored Hearts as One milestone`() {
        val achievement = heartsAsOne()
        assertEquals("2100-10-05", achievement.expectedBy)
        assertEquals("metaphor.event.follower-neuras-rank8", achievement.tracking.events.last())
        assertTrue(day("2100-10-05").steps.any {
            it.label.contains("Follower rank 8 for Neuras", ignoreCase = true)
        })
    }

    private fun heartsAsOne() = assertNotNull(loadMetaphor().achievements)
        .achievements
        .single { it.id == "metaphor.achievement.hearts-as-one" }

    private fun day(date: String) = assertNotNull(
        loadMetaphor().walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .firstOrNull { it.date == date },
        date,
    )

    private fun loadMetaphor() = PackLoader.load(metaphorDir()).also { loaded ->
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
    }

    private fun metaphorDir(): Path {
        val candidates = listOf(
            Path.of("content", "packs", "metaphor"),
            Path.of("..", "..", "content", "packs", "metaphor"),
        )
        return candidates.firstOrNull { Files.isDirectory(it) }
            ?: error("Cannot locate content/packs/metaphor from ${Path.of("").toAbsolutePath()}")
    }
}
