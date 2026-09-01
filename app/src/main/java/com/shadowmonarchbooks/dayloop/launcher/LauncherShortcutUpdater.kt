package com.shadowmonarchbooks.dayloop.launcher

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.shadowmonarchbooks.dayloop.MainActivity
import com.shadowmonarchbooks.dayloop.R
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.data.PackStore
import com.shadowmonarchbooks.dayloop.data.launcherBadgeAsset
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Phase 17c launcher parity.
 *
 * Android's primary adaptive launcher icon is a compiled resource, so arbitrary
 * pack assets cannot safely replace it at runtime. Where Android explicitly
 * permits runtime icons — dynamic launcher shortcuts — Dayloop keeps its own
 * icon as the base and composites the optional pack `launcherBadge` motif over
 * it. Packs without a badge publish no extra shortcut, preserving the current
 * launcher treatment exactly.
 */
@Singleton
class LauncherShortcutUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
    private val store: PackStore,
) {
    private val started = AtomicBoolean(false)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        if (!started.compareAndSet(false, true)) return
        scope.launch {
            store.state
                .filter { it.selectionReady }
                .map { it.selected }
                .distinctUntilChangedBy { it?.slug to it?.launcherBadgeAsset }
                .collect { pack ->
                    // Launcher implementations differ. Any platform/launcher
                    // failure must degrade to the unchanged primary app icon,
                    // never escape this app-lifetime background sync.
                    runCatching { sync(pack) }
                }
        }
    }

    private fun sync(pack: LoadedPack?) {
        val badgeAsset = pack?.launcherBadgeAsset
        if (pack == null || badgeAsset == null) {
            ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(SHORTCUT_ID))
            return
        }
        val icon = compositeIcon(badgeAsset)
        if (icon == null) {
            // Packlint should make this unreachable for bundled packs. Runtime
            // still fails closed to the unchanged Dayloop launcher treatment.
            ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(SHORTCUT_ID))
            return
        }
        val shortcut = ShortcutInfoCompat.Builder(context, SHORTCUT_ID)
            .setShortLabel(context.getString(R.string.app_name))
            .setLongLabel("${context.getString(R.string.app_name)} · ${pack.pack.title}")
            .setIcon(IconCompat.createWithBitmap(icon))
            .setIntent(
                Intent(context, MainActivity::class.java)
                    .setAction(Intent.ACTION_VIEW),
            )
            .setRank(0)
            .build()
        ShortcutManagerCompat.addDynamicShortcuts(context, listOf(shortcut))
    }

    private fun compositeIcon(badgeAsset: String): Bitmap? = runCatching {
        val size = ICON_SIZE_PX
        val baseDrawable = context.packageManager.getApplicationIcon(context.packageName)
        val base = baseDrawable.toBitmap(size, size, Bitmap.Config.ARGB_8888)
        val badge = context.assets.open(badgeAsset).use { BitmapFactory.decodeStream(it) }
            ?: return null

        val result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawBitmap(base, 0f, 0f, null)

        val placement = launcherBadgePlacement(size)
        val centerX = placement.left + placement.size / 2f
        val centerY = placement.top + placement.size / 2f
        val plate = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 238
        }
        canvas.drawCircle(centerX, centerY, placement.plateRadius, plate)

        val padding = (placement.size * 0.10f).toInt()
        val dst = Rect(
            placement.left + padding,
            placement.top + padding,
            placement.left + placement.size - padding,
            placement.top + placement.size - padding,
        )
        canvas.drawBitmap(badge, null, dst, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        result
    }.getOrNull()

    private companion object {
        const val SHORTCUT_ID = "dayloop-active-pack"
        const val ICON_SIZE_PX = 192
    }
}
