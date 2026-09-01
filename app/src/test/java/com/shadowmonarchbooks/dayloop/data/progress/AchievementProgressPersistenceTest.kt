package com.shadowmonarchbooks.dayloop.data.progress

import kotlin.test.Test
import kotlin.test.assertEquals

class AchievementProgressPersistenceTest {

    @Test
    fun `counter entries decode by achievement id`() {
        assertEquals(
            mapOf(
                "p3r.achievement.fools-journey" to 4,
                "p3r.achievement.dream-work" to 37,
            ),
            decodeAchievementCounts(
                setOf(
                    "p3r.achievement.fools-journey=4",
                    "p3r.achievement.dream-work=37",
                ),
            ),
        )
    }

    @Test
    fun `malformed and zero counter entries are ignored`() {
        assertEquals(
            emptyMap(),
            decodeAchievementCounts(
                setOf(
                    "missing-count=",
                    "missing-separator",
                    "zero=0",
                    "negative=-3",
                    "text=nope",
                ),
            ),
        )
    }

    @Test
    fun `checklist entries group checked items by achievement`() {
        assertEquals(
            mapOf(
                "p3r.achievement.strength-of-hearts" to setOf("yukari", "shinjiro"),
                "fixture.achievement" to setOf("one"),
            ),
            decodeAchievementChecklist(
                setOf(
                    "p3r.achievement.strength-of-hearts=yukari",
                    "p3r.achievement.strength-of-hearts=shinjiro",
                    "fixture.achievement=one",
                ),
            ),
        )
    }

    @Test
    fun `malformed checklist entries are ignored`() {
        assertEquals(
            emptyMap(),
            decodeAchievementChecklist(
                setOf(
                    "missing-item=",
                    "missing-separator",
                    "=missing-achievement",
                ),
            ),
        )
    }
}
