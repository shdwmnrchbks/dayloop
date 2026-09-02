package com.shadowmonarchbooks.dayloop.ui

import com.shadowmonarchbooks.dayloop.progress.StepKey
import com.shadowmonarchbooks.dayloop.progress.StepMark
import kotlin.test.Test
import kotlin.test.assertEquals

class EndDayTaskMarkingTest {

    @Test
    fun `end day auto skips only unchecked tasks`() {
        val date = "2016-04-09"
        val marks = mapOf(
            StepKey(date, 0) to StepMark.DONE,
            StepKey(date, 2) to StepMark.SKIP,
            StepKey(date, 4) to StepMark.LATER,
        )

        assertEquals(
            listOf(StepKey(date, 1), StepKey(date, 3), StepKey(date, 5)),
            tasksToAutoSkip(date, taskCount = 6, marks = marks),
        )
    }

    @Test
    fun `end day does not invent tasks for empty or invalid counts`() {
        assertEquals(emptyList(), tasksToAutoSkip("2016-04-09", 0, emptyMap()))
        assertEquals(emptyList(), tasksToAutoSkip("2016-04-09", -2, emptyMap()))
    }
}
