package com.shadowmonarchbooks.dayloop.ui.components

import com.shadowmonarchbooks.dayloop.pack.schema.Step
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskGroupingTest {

    @Test
    fun `tasks group by authored time slot without changing progress indexes`() {
        val groups = groupTasksBySlot(
            listOf(
                Step(label = "First", slot = "afternoon"),
                Step(label = "Second", slot = "afternoon"),
                Step(label = "Third", slot = "night"),
            ),
        )

        assertEquals(listOf("afternoon", "night"), groups.map { it.slotId })
        assertEquals(listOf(0, 1), groups[0].tasks.map { it.index })
        assertEquals(listOf(2), groups[1].tasks.map { it.index })
    }

    @Test
    fun `untagged tasks stay in a truthful any-time group`() {
        val groups = groupTasksBySlot(listOf(Step("One"), Step("Two")))

        assertEquals(1, groups.size)
        assertEquals(null, groups.single().slotId)
        assertEquals(listOf(0, 1), groups.single().tasks.map { it.index })
    }
}
