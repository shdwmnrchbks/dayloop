package com.shadowmonarchbooks.dayloop.launcher

/** Pixel placement for a small pack motif over the Dayloop-owned shortcut icon. */
data class LauncherBadgePlacement(
    val left: Int,
    val top: Int,
    val size: Int,
    val plateRadius: Float,
)

/**
 * Keep the pack motif subordinate to the Dayloop identity: ~38% of the icon,
 * inset from the lower-right edge with a slightly larger circular backing.
 */
fun launcherBadgePlacement(iconSize: Int): LauncherBadgePlacement {
    val badgeSize = (iconSize * 0.38f).toInt().coerceAtLeast(1)
    val inset = (iconSize * 0.055f).toInt().coerceAtLeast(1)
    return LauncherBadgePlacement(
        left = iconSize - badgeSize - inset,
        top = iconSize - badgeSize - inset,
        size = badgeSize,
        plateRadius = badgeSize * 0.58f,
    )
}
