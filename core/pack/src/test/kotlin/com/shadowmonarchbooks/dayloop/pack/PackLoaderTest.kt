package com.shadowmonarchbooks.dayloop.pack

import com.shadowmonarchbooks.dayloop.pack.schema.Capabilities
import com.shadowmonarchbooks.dayloop.pack.schema.CalendarRange
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.pack.schema.Labels
import com.shadowmonarchbooks.dayloop.pack.schema.Pack
import com.shadowmonarchbooks.dayloop.pack.schema.Slot
import com.shadowmonarchbooks.dayloop.pack.schema.StatDef
import com.shadowmonarchbooks.dayloop.pack.schema.Step
import com.shadowmonarchbooks.dayloop.pack.schema.WalkthroughFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Decoding contract shared by the lint tool and the Android app. */
class PackLoaderTest {

    private val packJson = """
        {
          "packId": "t1",
          "title": "Test Game",
          "contentVersion": 1,
          "timeModel": "weekdayGrid",
          "calendar": { "startDate": "2016-04-09", "endDate": "2016-04-10" },
          "slots": [ { "id": "afternoon", "label": "Afternoon" } ],
          "stats": [ { "id": "knowledge", "label": "Knowledge" } ],
          "capabilities": { "exams": true },
          "labels": { "bond": "Confidant", "stat": "Social Stat" }
        }
    """.trimIndent()

    @Test
    fun `decodePack parses a valid pack`() {
        val pack = PackLoader.decodePack(packJson)
        assertNotNull(pack)
        assertEquals("t1", pack.packId)
        assertEquals("Confidant", pack.labels.bond)
        assertTrue(pack.capabilities.exams)
    }

    @Test
    fun `decodePack returns null on garbage instead of throwing`() {
        assertNull(PackLoader.decodePack("{ not json"))
        assertNull(PackLoader.decodePack("""{"packId": 42}"""))
    }

    @Test
    fun `decodeError reports the offending file`() {
        val error = PackLoader.decodeError("{ not json", "pack.json", Pack.serializer())
        assertNotNull(error)
        assertTrue("pack.json" in error)
    }

    @Test
    fun `decodeWalkthrough round-trips days`() {
        val wt = WalkthroughFile(
            month = "2016-04",
            days = listOf(Day("2016-04-09", "sat", "story", steps = listOf(Step("Arrive in town")))),
        )
        val text = PackLoader.json.encodeToString(WalkthroughFile.serializer(), wt)
        val parsed = PackLoader.decodeWalkthrough(text)
        assertNotNull(parsed)
        assertEquals(1, parsed.days.size)
        assertEquals("Arrive in town", parsed.days.first().steps.first().label)
    }

    @Test
    fun `unknown keys are ignored`() {
        val text = packJson.replace("\"exams\": true", "\"exams\": true, \"futureFlag\": true")
        assertNotNull(PackLoader.decodePack(text))
    }

    @Test
    fun `calendar helpers agree with the real calendar`() {
        assertEquals("sat", Cal.weekdayOf("2016-04-09"))
        assertEquals("thu", Cal.weekdayOf("2016-05-12"))
        assertNull(Cal.parseDate("2016-13-40"))
        assertEquals(2, Cal.datesBetween("2016-04-09", "2016-04-10").size)
        assertEquals(emptyList(), Cal.datesBetween("2016-04-10", "2016-04-09"))
    }

    @Test
    fun `pack with defaults only needs identity and calendar`() {
        val minimal = PackLoader.decodePack(
            """
            {
              "packId": "t2",
              "title": "Minimal",
              "contentVersion": 1,
              "timeModel": "dayCounter",
              "calendar": { "startDate": "2100-06-01", "endDate": "2100-06-07" },
              "slots": [ { "id": "day", "label": "Day" } ],
              "stats": [ { "id": "courage", "label": "Courage" } ]
            }
            """.trimIndent(),
        )
        assertNotNull(minimal)
        assertEquals(Capabilities(), minimal.capabilities)
        assertEquals(Labels(), minimal.labels)
        assertNotNull(CalendarRange(startDate = "2100-06-01", endDate = "2100-06-07"))
        assertEquals(1, minimal.slots.size)
        assertEquals(StatDef("courage", "Courage"), minimal.stats.first())
    }
}
