package com.shadowmonarchbooks.dayloop.ui.today

import kotlin.test.Test
import kotlin.test.assertEquals

class TodayPresentationTest {
    @Test
    fun `today removes only the heist instruction from deadline labels`() {
        assertEquals(
            "Second palace story deadline",
            todayDeadlineLabel("Second palace story deadline — finish the heist beforehand"),
        )
        assertEquals("Exam results", todayDeadlineLabel("Exam results"))
    }
}
