package com.shadowmonarchbooks.dayloop.ui.today

import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.pack.schema.Step
import com.shadowmonarchbooks.dayloop.progress.StepMark
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TodayPresentationTest {
    @Test
    fun `today removes only the heist instruction from deadline labels`() {
        assertEquals(
            "Second palace story deadline",
            todayDeadlineLabel("Second palace story deadline — finish the heist beforehand"),
        )
        assertEquals("Exam results", todayDeadlineLabel("Exam results"))
    }

    @Test
    fun `day scene changes only after every task in the first slot is done`() {
        val day = Day(
            date = "2016-04-10",
            weekday = "sun",
            steps = listOf(
                Step("Day one", slot = "afternoon"),
                Step("Day two", slot = "afternoon"),
                Step("Night", slot = "evening"),
            ),
        )
        val marks = mutableListOf(StepMark.DONE, StepMark.SKIP, StepMark.SKIP)
        assertFalse(areSlotTasksDone(day, "afternoon", marks::get))
        marks[1] = StepMark.DONE
        assertTrue(areSlotTasksDone(day, "afternoon", marks::get))
        assertFalse(areSlotTasksDone(day, "evening", marks::get))
        assertFalse(areSlotTasksDone(day, null, marks::get))
    }
}
