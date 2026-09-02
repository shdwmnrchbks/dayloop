package com.shadowmonarchbooks.dayloop.preview

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.ui.components.SkinHeader
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.SkinTopBar
import com.shadowmonarchbooks.dayloop.ui.skin.skinBackdrop
import com.shadowmonarchbooks.dayloop.ui.skin.skinDecor
import com.shadowmonarchbooks.dayloop.ui.theme.DayloopTheme

@Preview(
    name = "Achievements",
    group = "Slash screens",
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SlashAchievementsPreview() {
    val pack = Phase17PreviewFixtures.coldStarts
        .first { it.skinId == "masks" && it.dark }
        .pack

    DayloopTheme(pack = pack, darkTheme = true) {
        val skin = LocalSkin.current
        Scaffold(
            topBar = {
                SkinTopBar(
                    title = "Achievements",
                    canGoBack = false,
                    onBack = {},
                    onOpenSearch = {},
                    onOpenSettings = {},
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { padding ->
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .skinBackdrop(skin)
                    .padding(16.dp),
            ) {
                SkinHeader("Royal trophies")
                Surface(
                    shape = skin.shapes.card,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.skinDecor("panel").padding(14.dp),
                    ) {
                        Text(
                            text = "28 / 53 EARNED",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            text = "13 available · 12 upcoming",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
                SlashAchievementPreviewRow(
                    title = "IT'S SHOWTIME!",
                    description = "Perform a Showtime attack.",
                    status = "Manual tracking",
                    checked = true,
                )
                SlashAchievementPreviewRow(
                    title = "ACCIDENT-PRONE",
                    description = "Perform an execution during a Fusion Alarm.",
                    status = "Available",
                    checked = false,
                )
                SlashAchievementPreviewRow(
                    title = "MASTER OF AKIHABARA",
                    description = "Order the Maid Café special menu after 20 stamps.",
                    status = "Expected by Dec 22",
                    checked = false,
                )
            }
        }
    }
}

@Composable
private fun SlashAchievementPreviewRow(
    title: String,
    description: String,
    status: String,
    checked: Boolean,
) {
    val skin = LocalSkin.current
    Surface(
        shape = skin.shapes.card,
        color = if (checked) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.skinDecor("panel").padding(10.dp),
        ) {
            Surface(
                shape = skin.shapes.chip,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.size(56.dp),
            ) {
                Text(
                    text = "★",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall)
                Text(status, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
            Checkbox(checked = checked, onCheckedChange = {})
        }
    }
}
