package com.shadowmonarchbooks.dayloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowmonarchbooks.dayloop.data.PackStore
import com.shadowmonarchbooks.dayloop.ui.AppRoot
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.skin.skinDecor
import com.shadowmonarchbooks.dayloop.ui.startupContentReady
import com.shadowmonarchbooks.dayloop.ui.theme.DayloopTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var packStore: PackStore

    /** Once the first matching UI projection renders, later pack switches use the normal in-app path. */
    private var startupCommitted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Phase 17a: keep the platform-owned splash visible while DataStore
        // resolves the saved pack. Compose may initialize behind it, but no
        // app frame is exposed until the active skin is known.
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        splash.setKeepOnScreenCondition { !packStore.state.value.selectionReady }

        enableEdgeToEdge()
        setContent {
            // Theme directly from PackStore during the initial handoff. This is
            // the earliest resolved source of truth for the selected pack and
            // prevents an engine-theme first frame. Once startup commits, the
            // normal ViewModel projection owns in-session pack changes again.
            val packsState by packStore.state.collectAsState()
            val vm: DayloopViewModel = hiltViewModel()
            val uiState by vm.state.collectAsState()
            val contentReady = startupContentReady(
                packSelectionReady = packsState.selectionReady,
                packSelectedSlug = packsState.selectedSlug,
                uiSelectionReady = uiState.selectionReady,
                uiSelectedSlug = uiState.selectedSlug,
            )
            if (contentReady) startupCommitted = true
            val themePack = if (startupCommitted) uiState.selected else packsState.selected

            DayloopTheme(pack = themePack) {
                if (startupCommitted) {
                    AppRoot(vm = vm)
                } else {
                    StartupShell()
                }
            }
        }
    }
}

/** Phase 17a shell exposed internally so Phase 17d/18 can render the exact startup surface. */
@Composable
internal fun StartupShell() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .skinDecor("panel"),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
