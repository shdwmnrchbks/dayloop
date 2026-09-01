package com.shadowmonarchbooks.dayloop.ui

/**
 * True only when the persisted pack selection and the richer UI projection
 * agree on the active pack. Phase 17a uses this to keep the first app frame
 * behind a skin-aware loading shell instead of briefly routing to onboarding.
 */
internal fun startupContentReady(
    packSelectionReady: Boolean,
    packSelectedSlug: String?,
    uiSelectionReady: Boolean,
    uiSelectedSlug: String?,
): Boolean =
    packSelectionReady &&
        uiSelectionReady &&
        packSelectedSlug == uiSelectedSlug
