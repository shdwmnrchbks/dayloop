package com.shadowmonarchbooks.dayloop.preview

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.SkinBottomBar
import com.shadowmonarchbooks.dayloop.ui.skin.SkinNavItem
import com.shadowmonarchbooks.dayloop.ui.skin.SkinRouteBadge
import com.shadowmonarchbooks.dayloop.ui.skin.SkinTopBar
import com.shadowmonarchbooks.dayloop.ui.skin.skinBackdrop
import com.shadowmonarchbooks.dayloop.ui.theme.DayloopTheme

/**
 * Stable debug-only fixture for the generic slash/ink visual language.
 * It deliberately uses preview data and generic skin tokens rather than a
 * bundled game id so architecture tests can keep the rendering engine neutral.
 */
@Preview(
    name = "Slash chrome dark",
    group = "Skin chrome",
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SlashChromeDarkPreview() {
    val pack = Phase17PreviewFixtures.coldStarts
        .first { it.skinId == "masks" && it.dark }
        .pack

    DayloopTheme(pack = pack, darkTheme = true) {
        val skin = LocalSkin.current
        val tabs = listOf(
            SkinNavItem("today", "Today", Icons.Filled.Home),
            SkinNavItem("calendar", "Calendar", Icons.Filled.DateRange),
            SkinNavItem("bonds", "Bonds", Icons.Filled.Person),
        )
        Scaffold(
            topBar = {
                SkinTopBar(
                    title = "Preview Pack",
                    canGoBack = false,
                    onBack = {},
                    onOpenSearch = {},
                    onOpenSettings = {},
                )
            },
            bottomBar = {
                SkinBottomBar(
                    items = tabs,
                    selectedRoute = "today",
                    onSelect = {},
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { padding ->
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .skinBackdrop(skin)
                    .padding(16.dp),
            ) {
                SkinRouteBadge("100% Completion Route")
                Text(
                    text = skin.cased("Tuesday, September 1", "display"),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(14.dp),
                    ) {
                        Text(
                            text = skin.cased("Next deadline", "display"),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text("Finish the current objective before the story cutoff.")
                    }
                }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shadowElevation = 0.dp,
                ) {
                    Text(
                        text = skin.cased("Selected action", "display"),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}
