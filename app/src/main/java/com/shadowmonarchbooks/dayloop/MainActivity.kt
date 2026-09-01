package com.shadowmonarchbooks.dayloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowmonarchbooks.dayloop.data.PackStore
import com.shadowmonarchbooks.dayloop.ui.AppRoot
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.theme.DayloopTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var packStore: PackStore

    override fun onCreate(savedInstanceState: Bundle?) {
        // Phase 17a: keep the platform-owned splash visible while DataStore
        // resolves the saved pack. Compose may initialize behind it, but no
        // app frame is exposed until the active skin is known.
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        splash.setKeepOnScreenCondition { !packStore.state.value.selectionReady }

        enableEdgeToEdge()
        setContent {
            // Theme directly from PackStore instead of the ViewModel's richer
            // projection. This is the earliest resolved source of truth for
            // the selected pack and prevents an engine-theme first frame.
            val packsState by packStore.state.collectAsState()
            val vm: DayloopViewModel = hiltViewModel()
            DayloopTheme(pack = packsState.selected) {
                AppRoot(vm = vm)
            }
        }
    }
}
