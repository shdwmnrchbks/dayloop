package com.shadowmonarchbooks.dayloop.progress

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StepKeyTest {

    @Test
    fun `round-trips through string form`() {
        val key = StepKey("2016-04-12", 3)
        assertEquals(key, StepKey.parse(key.toString()))
    }

    @Test
    fun `rejects malformed strings`() {
        assertNull(StepKey.parse("2016-04-12"))
        assertNull(StepKey.parse("#3"))
        assertNull(StepKey.parse("2016-04-12#x"))
        assertNull(StepKey.parse("2016-04-12#-1"))
    }
}

class ClockTest {

    private val span = CalendarSpan(
        startDate = "2100-06-01",
        endDate = "2100-06-30",
        nonPlayableDates = setOf("2100-06-14", "2100-06-15"),
    )

    @Test
    fun `next advances one day`() {
        assertEquals("2100-06-02", Clock.next(span, "2100-06-01"))
    }

    @Test
    fun `next skips non-playable dates`() {
        assertEquals("2100-06-16", Clock.next(span, "2100-06-13"))
    }

    @Test
    fun `next returns null past the end of the calendar`() {
        assertNull(Clock.next(span, "2100-06-30"))
        assertNull(Clock.next(span, "2100-07-02"))
    }

    @Test
    fun `previous steps back and skips non-playable dates`() {
        assertEquals("2100-06-13", Clock.previous(span, "2100-06-16"))
        assertEquals("2100-06-29", Clock.previous(span, "2100-06-30"))
    }

    @Test
    fun `previous returns null before the start of the calendar`() {
        assertNull(Clock.previous(span, "2100-06-01"))
    }

    @Test
    fun `start is the calendar start date`() {
        assertEquals("2100-06-01", Clock.start(span))
    }

    @Test
    fun `null results on malformed bounds or dates`() {
        assertNull(Clock.next(CalendarSpan("bad", "bad"), "2100-06-01"))
        assertNull(Clock.next(span, "not-a-date"))
    }
}

class ProgressLogicTest {

    private val d1s0 = StepKey("2016-04-09", 0)
    private val d1s1 = StepKey("2016-04-09", 1)
    private val d2s0 = StepKey("2016-04-10", 0)

    @Test
    fun `applying a mark stores it`() {
        val states = ProgressLogic.withMark(emptyMap(), d1s0, StepMark.DONE)
        assertEquals(StepMark.DONE, states[d1s0])
    }

    @Test
    fun `applying the same mark again clears it`() {
        val states = ProgressLogic.withMark(emptyMap(), d1s0, StepMark.DONE)
        assertTrue(ProgressLogic.withMark(states, d1s0, StepMark.DONE).isEmpty())
    }

    @Test
    fun `switching marks replaces the old one`() {
        val states = ProgressLogic.withMark(emptyMap(), d1s0, StepMark.DONE)
        val switched = ProgressLogic.withMark(states, d1s0, StepMark.LATER)
        assertEquals(StepMark.LATER, switched[d1s0])
    }

    @Test
    fun `withoutMark removes only the targeted key`() {
        val states = mapOf(d1s0 to StepMark.DONE, d2s0 to StepMark.SKIP)
        val cleared = ProgressLogic.withoutMark(states, d1s0)
        assertFalse(d1s0 in cleared)
        assertEquals(StepMark.SKIP, cleared[d2s0])
    }

    @Test
    fun `carriedOver collects LATER marks from earlier days, oldest first`() {
        val states = mapOf(
            d1s1 to StepMark.LATER, // Apr 9, later index
            d2s0 to StepMark.LATER, // Apr 10
            d1s0 to StepMark.DONE,  // done, not carried
            StepKey("2016-04-11", 0) to StepMark.LATER, // not before the cutoff
        )
        val carried = ProgressLogic.carriedOver(states, "2016-04-11")
        assertEquals(listOf(d1s1, d2s0), carried)
    }

    @Test
    fun `dayProgress tallies by mark`() {
        val states = mapOf(
            d1s0 to StepMark.DONE,
            d1s1 to StepMark.SKIP,
            d2s0 to StepMark.LATER,
        )
        assertEquals(DayProgress(done = 1, skipped = 1, deferred = 0, total = 3), ProgressLogic.dayProgress(states, "2016-04-09", total = 3))
        assertEquals(DayProgress(done = 0, skipped = 0, deferred = 1, total = 2), ProgressLogic.dayProgress(states, "2016-04-10", total = 2))
    }

    @Test
    fun `orphans flag unknown dates and fallen-off indices`() {
        val states = mapOf(
            d1s0 to StepMark.DONE,                       // still resolves (2 steps that day)
            StepKey("2016-04-09", 5) to StepMark.DONE,   // index fell off the day
            StepKey("2015-01-01", 0) to StepMark.SKIP,   // day no longer authored
            d2s0 to StepMark.LATER,                      // still resolves (1 step that day)
        )
        val stepCounts = mapOf("2016-04-09" to 2, "2016-04-10" to 1)
        assertEquals(setOf(StepKey("2016-04-09", 5), StepKey("2015-01-01", 0)), ProgressLogic.orphans(states, stepCounts))
    }

    @Test
    fun `orphans is empty when everything resolves`() {
        val states = mapOf(d1s0 to StepMark.DONE, d2s0 to StepMark.SKIP)
        val stepCounts = mapOf("2016-04-09" to 2, "2016-04-10" to 1)
        assertTrue(ProgressLogic.orphans(states, stepCounts).isEmpty())
    }
}
