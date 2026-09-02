package com.shadowmonarchbooks.dayloop.ui.achievements

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MonthlyAchievementChecklistTest {

    @Test
    fun `monthly checklist appears only on the final authored day`() {
        val dates = listOf("2016-04-09", "2016-04-29", "2016-04-30", "2016-05-01")

        assertFalse(isLastAuthoredDayOfMonth("2016-04-29", dates))
        assertTrue(isLastAuthoredDayOfMonth("2016-04-30", dates))
        assertTrue(isLastAuthoredDayOfMonth("2016-05-01", dates))
    }
}
