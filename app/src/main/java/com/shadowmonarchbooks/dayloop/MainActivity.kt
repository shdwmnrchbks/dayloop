package com.shadowmonarchbooks.dayloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowmonarchbooks.dayloop.ui.AppRoot
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.theme.DayloopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // The active pack's `theme` recolors and skins the whole app
            // (ROADMAP-v2 Phase 10, ROADMAP-v3 Phase 12): switching packs in
            // Settings re-skins in place, no restart. Packs without a theme
            // get the engine look.
            val vm: DayloopViewModel = hiltViewModel()
            val state by vm.state.collectAsState()
            DayloopTheme(pack = state.selected) {
                AppRoot(vm = vm)
            }
        }
    }
}
