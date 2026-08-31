package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.schema.AllOf
import com.shadowmonarchbooks.dayloop.pack.schema.AnyOf
import com.shadowmonarchbooks.dayloop.pack.schema.BondRankGte
import com.shadowmonarchbooks.dayloop.pack.schema.StatGte
import com.shadowmonarchbooks.dayloop.pack.schema.StoryFlag
import com.shadowmonarchbooks.dayloop.pack.schema.Weather
import com.shadowmonarchbooks.dayloop.pack.schema.Weekdays
import kotlin.test.Test
import kotlin.test.assertEquals

/** Gate descriptions (docs/PLAN.md §3.3, ROADMAP-v2.md Phase 9). */
class PackTextTest {

    private val statLabels = mapOf("knowledge" to "Knowledge")
    private val bondLabels = mapOf("t1.bond.fool" to "Fool")

    @Test
    fun `weekday gates list the days`() {
        assertEquals("on Tue/Thu", describeCondition(Weekdays(listOf("tue", "thu"))))
    }

    @Test
    fun `stat gates resolve the pack label`() {
        assertEquals(
            "Needs Knowledge 3+",
            describeCondition(StatGte("knowledge", 3), statLabels),
        )
    }

    @Test
    fun `unknown ids fall back to the raw id`() {
        assertEquals("Needs charm 2+", describeCondition(StatGte("charm", 2), statLabels))
    }

    @Test
    fun `bond gates resolve the bond label`() {
        assertEquals(
            "Needs Fool rank 5+",
            describeCondition(BondRankGte("t1.bond.fool", 5), statLabels, bondLabels),
        )
    }

    @Test
    fun `story flags stay generic and spoiler-safe`() {
        assertEquals("After the story advances", describeCondition(StoryFlag("t1.flag.x")))
    }

    @Test
    fun `weather gates name the state`() {
        assertEquals("In rain weather", describeCondition(Weather("rain")))
    }

    @Test
    fun `allOf joins with and`() {
        val text = describeCondition(
            AllOf(listOf(Weekdays(listOf("tue")), StatGte("knowledge", 2))),
            statLabels,
        )
        assertEquals("on Tue and Needs Knowledge 2+", text)
    }

    @Test
    fun `anyOf joins with or`() {
        val text = describeCondition(
            AnyOf(listOf(Weekdays(listOf("tue")), Weekdays(listOf("sun")))),
        )
        assertEquals("on Tue or on Sun", text)
    }

    @Test
    fun `nested composites are parenthesized`() {
        val text = describeCondition(
            AllOf(
                listOf(
                    AnyOf(listOf(Weekdays(listOf("tue")), Weekdays(listOf("sun")))),
                    StatGte("knowledge", 4),
                ),
            ),
            statLabels,
        )
        assertEquals("(on Tue or on Sun) and Needs Knowledge 4+", text)
    }
}
