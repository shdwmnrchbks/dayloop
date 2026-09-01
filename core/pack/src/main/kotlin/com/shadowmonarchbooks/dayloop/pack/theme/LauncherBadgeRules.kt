package com.shadowmonarchbooks.dayloop.pack.theme

import com.shadowmonarchbooks.dayloop.pack.schema.PackTheme

/** Phase 17c contract for the optional Dayloop launcher/shortcut motif badge. */
object LauncherBadgeRules {
    const val SLOT = "launcherBadge"
    val EXTENSIONS = setOf("png")
    const val MIN_PX = 48
    const val MAX_PX = 256
}

/** Pack-relative launcher badge path, or null when the pack keeps the engine launcher treatment. */
fun PackTheme.launcherBadgePath(): String? = art[LauncherBadgeRules.SLOT]
