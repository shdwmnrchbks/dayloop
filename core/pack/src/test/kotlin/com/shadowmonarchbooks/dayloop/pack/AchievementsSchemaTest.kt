package com.shadowmonarchbooks.dayloop.pack

import com.shadowmonarchbooks.dayloop.pack.schema.AchievementTrackingTypes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AchievementsSchemaTest {

    @Test
    fun `achievement catalog decodes rules and semantic anchors`() {
        val decoded = PackLoader.decodeAchievements(
            """
            {
              "achievements": [
                {
                  "id": "fixture.gourmand",
                  "title": "Gourmand",
                  "expectedBy": "2009-06-28",
                  "tracking": { "type": "event", "event": "fixture.event.gourmand" }
                }
              ],
              "events": [
                {
                  "id": "fixture.event.gourmand",
                  "date": "2009-06-28",
                  "labelContains": "Seafood Full Course"
                }
              ]
            }
            """.trimIndent(),
        )

        assertNotNull(decoded)
        assertEquals(1, decoded.achievements.size)
        assertEquals(AchievementTrackingTypes.EVENT, decoded.achievements.single().tracking.type)
        assertEquals("fixture.event.gourmand", decoded.achievements.single().tracking.event)
        assertEquals("Seafood Full Course", decoded.events.single().labelContains)
    }
}
