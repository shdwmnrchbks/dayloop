package com.shadowmonarchbooks.dayloop

import android.app.Application
import com.shadowmonarchbooks.dayloop.launcher.LauncherShortcutUpdater
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DayloopApplication : Application() {
    @Inject lateinit var launcherShortcutUpdater: LauncherShortcutUpdater

    override fun onCreate() {
        super.onCreate()
        launcherShortcutUpdater.start()
    }
}
