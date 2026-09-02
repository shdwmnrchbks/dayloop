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
    name = "Media",
    group = "Slash screens",
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SlashMediaPreview() {
    val pack = Phase17PreviewFixtures.coldStarts
        .first { it.skinId == "masks" && it.dark }
        .pack

    DayloopTheme(pack = pack, darkTheme = true) {
        val skin = LocalSkin.current
        Scaffold(
            topBar = {
                SkinTopBar(
                    title = "Pack media",
                    canGoBack = true,
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
                SkinHeader("Achievements · 50")
                SlashMediaPreviewRow(
                    title = "CASTLE OF LUST: SEIZED",
                    caption = "Imported Royal trophy artwork",
                    anchor = "guide placement months: 2016-04",
                )
                SlashMediaPreviewRow(
                    title = "THE PHENOMENAL PHANTOM THIEF",
                    caption = "Imported Royal trophy artwork",
                    anchor = "guide placement months: 2017-01, 2017-02",
                )
                SkinHeader("Month art · 1")
                SlashMediaPreviewRow(
                    title = "MONTH OPENER",
                    caption = "Graphic used by the imported guide schedule.",
                    anchor = "months: 2016-04, 2016-05, 2016-06…",
                )
            }
        }
    }
}

@Composable
private fun SlashMediaPreviewRow(
    title: String,
    caption: String,
    anchor: String,
) {
    val skin = LocalSkin.current
    Surface(
        shape = skin.shapes.card,
        color = MaterialTheme.colorScheme.background,
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
                modifier = Modifier.size(64.dp),
            ) {
                Text(
                    text = "★",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(caption, style = MaterialTheme.typography.bodySmall)
                Text(anchor, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
