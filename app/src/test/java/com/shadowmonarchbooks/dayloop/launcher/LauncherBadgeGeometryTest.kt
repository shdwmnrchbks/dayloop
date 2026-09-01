package com.shadowmonarchbooks.dayloop.launcher

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LauncherBadgeGeometryTest {
    @Test
    fun `badge stays subordinate and inside the Dayloop icon`() {
        val placement = launcherBadgePlacement(192)
        assertTrue(placement.size in 70..74)
        assertTrue(placement.left > 100)
        assertEquals(placement.left, placement.top)
        assertTrue(placement.left + placement.size < 192)
        assertTrue(placement.plateRadius > placement.size / 2f)
    }

    @Test
    fun `badge placement scales with icon size`() {
        val small = launcherBadgePlacement(96)
        val large = launcherBadgePlacement(192)
        assertTrue(large.size >= small.size * 2 - 1)
        assertTrue(large.left > small.left)
    }
}
