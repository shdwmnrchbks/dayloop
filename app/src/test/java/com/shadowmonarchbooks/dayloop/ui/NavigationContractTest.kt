package com.shadowmonarchbooks.dayloop.ui

import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.pack.schema.CalendarRange
import com.shadowmonarchbooks.dayloop.pack.schema.Capabilities
import com.shadowmonarchbooks.dayloop.pack.schema.Pack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NavigationContractTest {

    @Test
    fun `deadlines remain a detail destination rather than a bottom tab`() {
        assertFalse("deadlines" in TopLevelRoutes)
        assertFalse(topLevelTabs(null).any { it.route == "deadlines" })
    }

    @Test
    fun `Mementos requests replace Answers only for capable packs`() {
        val p5r = LoadedPack(
            slug = "fixture",
            pack = Pack(
                packId = "fixture",
                title = "Fixture",
                contentVersion = 1,
                timeModel = "weekdayGrid",
                calendar = CalendarRange("2016-04-01", "2016-04-30"),
                slots = emptyList(),
                stats = emptyList(),
                capabilities = Capabilities(answers = true, mementosRequests = true),
            ),
        )

        val routes = topLevelTabs(p5r).map { it.route }
        assertEquals(1, routes.count { it == "mementos" })
        assertFalse("answers" in routes)
    }
}
