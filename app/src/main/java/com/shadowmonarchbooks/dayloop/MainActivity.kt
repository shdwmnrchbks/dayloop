package com.shadowmonarchbooks.dayloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.shadowmonarchbooks.dayloop.ui.AppRoot
import com.shadowmonarchbooks.dayloop.ui.theme.DayloopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DayloopTheme {
                AppRoot()
            }
        }
    }
}
