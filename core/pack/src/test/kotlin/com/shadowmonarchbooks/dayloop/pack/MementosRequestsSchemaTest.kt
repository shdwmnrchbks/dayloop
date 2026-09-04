package com.shadowmonarchbooks.dayloop.pack

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MementosRequestsSchemaTest {

    @Test
    fun `request catalog decodes exact completion event`() {
        val decoded = PackLoader.decodeMementosRequests(
            """
            {
              "requests": [
                {
                  "id": "fixture.request.bully",
                  "title": "A Bully",
                  "receivedOn": "2016-05-09",
                  "expectedBy": "2016-06-02",
                  "target": "Shadow Bully",
                  "location": "Aiyatsbus: Area 2",
                  "reward": "Protein",
                  "completionEvent": "fixture.event.bully"
                }
              ],
              "events": [
                {
                  "id": "fixture.event.bully",
                  "date": "2016-06-02",
                  "labelContains": "Complete request: A Bully"
                }
              ]
            }
            """.trimIndent(),
        )

        assertNotNull(decoded)
        assertEquals("fixture.event.bully", decoded.requests.single().completionEvent)
        assertEquals("Complete request: A Bully", decoded.events.single().labelContains)
    }
}
