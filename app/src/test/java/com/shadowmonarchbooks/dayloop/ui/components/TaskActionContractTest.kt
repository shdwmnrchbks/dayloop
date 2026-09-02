package com.shadowmonarchbooks.dayloop.ui.components

import com.shadowmonarchbooks.dayloop.progress.StepMark
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskActionContractTest {

    @Test
    fun `task rows expose only done and skip`() {
        assertEquals(listOf(StepMark.DONE, StepMark.SKIP), taskActionMarks)
    }
}
