package com.shadowmonarchbooks.dayloop.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Pushes fresh snapshots into the home-screen widget whenever the app state
 * changes (docs/PLAN.md Phase 5). Bursts of state changes coalesce into one
 * update; the widget also re-reads everything on its own [provideGlance]
 * passes, so this is only about keeping it current while the app is alive.
 */
@Singleton
class WidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val pending = MutableStateFlow(0)

    init {
        scope.launch {
            pending.collectLatest {
                delay(250)
                runCatching { DayloopWidget().updateAll(context) }
            }
        }
    }

    fun requestPush() {
        pending.value += 1
    }
}
