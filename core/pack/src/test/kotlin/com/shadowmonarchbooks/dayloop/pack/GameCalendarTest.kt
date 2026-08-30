package com.shadowmonarchbooks.dayloop.pack

import com.shadowmonarchbooks.dayloop.pack.schema.CalendarRange
import com.shadowmonarchbooks.dayloop.pack.schema.WeekdayAnchor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameCalendarTest {

    @Test
    fun `real month lengths equal the real calendar`() {
        val cal = GameCalendar.of(CalendarRange("2016-04-09", "2016-05-25"))!!
        assertEquals(listOf("2016-04-09"), cal.dates.take(1))
        assertEquals("2016-04-30", cal.next("2016-04-29"))
        assertEquals("2016-05-01", cal.next("2016-04-30"))
        assertEquals("2016-04-30", cal.previous("2016-05-01"))
        assertEquals(46, cal.diffDays("2016-04-09", "2016-05-25"))
        assertEquals("sat", cal.weekdayOf("2016-04-09"))
        assertTrue("2016-05-25" in cal)
        assertFalse("2016-05-26" in cal)
        assertNull(cal.next("2016-05-25"))
    }

    @Test
    fun `game month lengths override the real calendar`() {
        // Five 30-day game months; a day above 31 is representable.
        val cal = GameCalendar.of(
            CalendarRange(
                startDate = "2100-06-02",
                endDate = "2100-10-26",
                monthLengths = listOf(30, 30, 30, 30, 30),
            ),
        )!!
        assertEquals(145, cal.size)
        assertEquals("2100-07-01", cal.next("2100-06-30"))
        assertEquals("2100-06-30", cal.previous("2100-07-01"))
        assertEquals(30, cal.daysInMonth("2100-07"))
        assertEquals(26, cal.daysInMonth("2100-10"))
        assertTrue(cal.dates.last() == "2100-10-26")
    }

    @Test
    fun `a game month longer than 31 days is representable`() {
        val cal = GameCalendar.of(
            CalendarRange(startDate = "2100-12-01", endDate = "2100-12-35", monthLengths = listOf(35)),
        )!!
        assertEquals(35, cal.size)
        assertTrue("2100-12-35" in cal)
        assertEquals("2100-12-35", cal.next("2100-12-34"))
    }

    @Test
    fun `weekday cycle resolves from the anchor`() {
        val cycle = listOf("metalsday", "idlesday", "flamesday", "watersday", "arboursday")
        val cal = GameCalendar.of(
            CalendarRange(
                startDate = "2100-06-02",
                endDate = "2100-06-30",
                monthLengths = listOf(30),
                weekdayCycle = cycle,
                weekdayAnchor = WeekdayAnchor("2100-06-02", "watersday"),
            ),
        )!!
        assertEquals("watersday", cal.weekdayOf("2100-06-02"))
        assertEquals("arboursday", cal.weekdayOf("2100-06-03"))
        assertEquals("idlesday", cal.weekdayOf("2100-06-05"))
        assertEquals("metalsday", cal.weekdayOf("2100-06-24"))
        assertEquals(3, cal.cyclePosition("2100-06-02"))
        assertEquals(0, cal.cyclePosition("2100-06-24"))
        // The cycle wraps correctly across the month boundary.
        assertEquals("flamesday", cal.weekdayOf("2100-06-06"))
    }

    @Test
    fun `malformed ranges are rejected`() {
        assertNull(GameCalendar.of(CalendarRange("bad", "2100-01-31")))
        assertNull(GameCalendar.of(CalendarRange("2100-02-01", "2100-01-01")))
        // End day beyond the game month's length.
        assertNull(GameCalendar.of(CalendarRange("2100-06-01", "2100-06-31", monthLengths = listOf(30))))
        assertNull(GameCalendar.of(CalendarRange("2100-06-01", "2100-06-30", monthLengths = listOf(0))))
        // A declared cycle needs a valid anchor inside the calendar.
        assertNull(
            GameCalendar.of(
                CalendarRange(
                    "2100-06-01", "2100-06-30", monthLengths = listOf(30),
                    weekdayCycle = listOf("a", "b"), weekdayAnchor = null,
                ),
            ),
        )
        assertNull(
            GameCalendar.of(
                CalendarRange(
                    "2100-06-01", "2100-06-30", monthLengths = listOf(30),
                    weekdayCycle = listOf("a", "b"), weekdayAnchor = WeekdayAnchor("2100-07-01", "a"),
                ),
            ),
        )
    }
}
