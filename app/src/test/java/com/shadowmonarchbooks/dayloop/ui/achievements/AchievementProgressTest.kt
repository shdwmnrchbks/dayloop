package com.shadowmonarchbooks.dayloop.ui.achievements

import com.shadowmonarchbooks.dayloop.pack.schema.MediaItem
import com.shadowmonarchbooks.dayloop.pack.schema.MediaKinds
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AchievementProgressTest {

    @Test
    fun `date anchored achievement becomes due on its date`() {
        val item = achievement(dates = listOf("2016-05-10"))

        assertFalse(achievementIsDue(item, "2016-05-09"))
        assertTrue(achievementIsDue(item, "2016-05-10"))
        assertTrue(achievementIsDue(item, "2016-05-11"))
    }

    @Test
    fun `month anchored achievement becomes due when that game month is reached`() {
        val item = achievement(months = listOf("2016-07"))

        assertFalse(achievementIsDue(item, "2016-06-30"))
        assertTrue(achievementIsDue(item, "2016-07-01"))
        assertTrue(achievementIsDue(item, "2016-08-01"))
    }

    @Test
    fun `unanchored achievement is available immediately`() {
        assertTrue(achievementIsDue(achievement(), "2016-04-09"))
    }

    private fun achievement(
        months: List<String> = emptyList(),
        dates: List<String> = emptyList(),
    ) = MediaItem(
        id = "fixture.media.achievement.test",
        file = "images/test.png",
        kind = MediaKinds.ACHIEVEMENT,
        title = "Test achievement",
        months = months,
        dates = dates,
    )
}
