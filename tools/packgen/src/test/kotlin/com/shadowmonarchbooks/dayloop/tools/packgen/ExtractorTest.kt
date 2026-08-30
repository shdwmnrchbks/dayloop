package com.shadowmonarchbooks.dayloop.tools.packgen

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExtractorTest {

    @Test
    fun `year inference maps game months correctly`() {
        assertEquals(2016, Extractor.yearForMonth(4))
        assertEquals(2016, Extractor.yearForMonth(12))
        assertEquals(2017, Extractor.yearForMonth(1))
        assertEquals(2017, Extractor.yearForMonth(2))
    }

    @Test
    fun `artifact tail is cut and kept when prefix looks real`() {
        val (kept, review) = Extractor.cleanFragment(
            "Talk with the Twins for Strengthg/?imageurl=https%3A%2F%2Fimages.steamusercontent.com%2Fugc"
        )
        assertEquals("Talk with the Twins for Strength", kept)
        assertTrue(review != null && "imageurl" in review)
    }

    @Test
    fun `hopeless artifact fragment goes to review only`() {
        val (kept, review) = Extractor.cleanFragment(
            "Kindnesrcontent.com/ugc/1907863287663336978/BDFF/\\\" class=\\\"sharedFilePreviewImage floatLeft\\\"> Accident-Prone"
        )
        assertEquals(null, kept)
        assertTrue(review != null && "rcontent.com" in review)
    }

    @Test
    fun `clean fragment passes through with whitespace normalized`() {
        val (kept, review) = Extractor.cleanFragment("  Study   in the  library. Knowledge +1  ")
        assertEquals("Study in the library. Knowledge +1", kept)
        assertEquals(null, review)
    }

    @Test
    fun `mojibake quotes are normalized`() {
        val input = "Morgana says \u00E2\u20AC\u0153an efficient use of materials\u00E2\u20AC"
        val (kept, _) = Extractor.cleanFragment(input)
        assertEquals("Morgana says \"an efficient use of materials\"", kept)
    }

    @Test
    fun `out-of-order sections still produce a sorted merged timeline`() {
        val later = Extractor.SectionInput(
            title = "May 24-26 (misplaced)",
            text = "05/24 Tue Send the Calling Card; 05/25 Wed Steal the Treasure",
        )
        val earlier = Extractor.SectionInput(
            title = "May 23",
            text = "05/23 Mon Infiltrate; Sometime soon we will need 100000 yen",
        )
        val (days, report) = Extractor.extract(listOf(later, earlier))
        assertEquals(listOf("2016-05-23", "2016-05-24", "2016-05-25"), days.map { it.date })
        assertEquals(3, report.datesFound)
    }

    @Test
    fun `same date across sections merges sources and dedupes fragments`() {
        val a = Extractor.SectionInput(title = "Sec A", text = "05/23 Mon Infiltrate the museum; Buy items")
        val b = Extractor.SectionInput(title = "Sec B", text = "05/23 Mon Infiltrate the museum; Rank 2")
        val (days, report) = Extractor.extract(listOf(a, b))
        assertEquals(1, days.size)
        assertEquals(listOf("Sec A", "Sec B"), days[0].sources)
        assertEquals(listOf("Infiltrate the museum", "Buy items", "Rank 2"), days[0].fragments)
        assertEquals(1, report.duplicateTokenConflicts.size)
    }

    @Test
    fun `weekday mismatch is reported`() {
        // 2016-04-12 was a Tuesday; claim Wednesday.
        val a = Extractor.SectionInput(title = "Sec", text = "04/12 Wed Answer the question")
        val (days, report) = Extractor.extract(listOf(a))
        assertEquals("tue", days[0].weekday) // day keeps the REAL weekday
        assertEquals(1, report.weekdayMismatches.size)
        assertTrue("token says wed" in report.weekdayMismatches[0])
    }
}
