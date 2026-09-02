package com.shadowmonarchbooks.dayloop.ui

import kotlin.test.Test
import kotlin.test.assertFalse

class NavigationContractTest {

    @Test
    fun `deadlines remain a detail destination rather than a bottom tab`() {
        assertFalse("deadlines" in TopLevelRoutes)
        assertFalse(topLevelTabs(null).any { it.route == "deadlines" })
    }
}
