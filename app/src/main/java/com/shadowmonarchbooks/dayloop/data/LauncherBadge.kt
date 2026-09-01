package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.theme.launcherBadgePath

/** Asset path consumed by the Phase 17c Dayloop-owned launcher shortcut treatment. */
val LoadedPack.launcherBadgeAsset: String?
    get() = pack.theme?.launcherBadgePath()?.let { "$slug/$it" }
