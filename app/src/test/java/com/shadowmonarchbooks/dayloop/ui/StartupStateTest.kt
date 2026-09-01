package com.shadowmonarchbooks.dayloop.ui

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StartupStateTest {

    @Test
    fun `returning user waits until ui projection matches resolved pack`() {
        assertFalse(
            startupContentReady(
                packSelectionReady = true,
                packSelectedSlug = "saved-pack",
                uiSelectionReady = true,
                uiSelectedSlug = null,
            ),
        )
        assertTrue(
            startupContentReady(
                packSelectionReady = true,
                packSelectedSlug = "saved-pack",
                uiSelectionReady = true,
                uiSelectedSlug = "saved-pack",
            ),
        )
    }

    @Test
    fun `fresh install may show onboarding once selection lookup is complete`() {
        assertTrue(
            startupContentReady(
                packSelectionReady = true,
                packSelectedSlug = null,
                uiSelectionReady = true,
                uiSelectedSlug = null,
            ),
        )
    }

    @Test
    fun `startup never renders before persisted selection lookup completes`() {
        assertFalse(
            startupContentReady(
                packSelectionReady = false,
                packSelectedSlug = null,
                uiSelectionReady = true,
                uiSelectedSlug = null,
            ),
        )
    }

    @Test
    fun `stale ui pack cannot render during a selection handoff`() {
        assertFalse(
            startupContentReady(
                packSelectionReady = true,
                packSelectedSlug = "new-pack",
                uiSelectionReady = true,
                uiSelectedSlug = "old-pack",
            ),
        )
    }
}
