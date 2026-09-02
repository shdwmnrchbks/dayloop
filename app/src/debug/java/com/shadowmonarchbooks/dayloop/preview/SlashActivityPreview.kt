package com.shadowmonarchbooks.dayloop.preview

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.ui.components.SkinHeader
import com.shadowmonarchbooks.dayloop.ui.components.SkinTag
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.SkinTopBar
import com.shadowmonarchbooks.dayloop.ui.skin.skinBackdrop
import com.shadowmonarchbooks.dayloop.ui.skin.skinDecor
import com.shadowmonarchbooks.dayloop.ui.theme.DayloopTheme

@Preview(
    name = "Activities",
    group = "Slash screens",
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SlashActivitiesPreview() {
    val pack = Phase17PreviewFixtures.coldStarts
        .first { it.skinId == "masks" && it.dark }
        .pack

    DayloopTheme(pack = pack, darkTheme = true) {
        val skin = LocalSkin.current
        Scaffold(
            topBar = {
                SkinTopBar(
                    title = "Activities",
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
                SkinHeader("Reusable activities")
                SlashActivityPreviewRow(
                    kind = "Dvd",
                    title = "WATCH '31'",
                    metadata = "Attic TV — Guts +3",
                    note = "Royal-only Scarlet rental inventory from 8/1",
                )
                SlashActivityPreviewRow(
                    kind = "Videogame",
                    title = "PLAY 'FEATHERMAN SEEKER'",
                    metadata = "Attic TV — Knowledge +3",
                    note = "Royal-only · Akihabara retro game shop",
                )
                SlashActivityPreviewRow(
                    kind = "Book",
                    title = "READ 'THE CRAFT OF CINEMA'",
                    metadata = "Shinjuku bookstore / metro",
                    note = "Movies and DVDs gain +2 additional points",
                )
            }
        }
    }
}

@Composable
private fun SlashActivityPreviewRow(
    kind: String,
    title: String,
    metadata: String,
    note: String,
) {
    val skin = LocalSkin.current
    Surface(
        shape = skin.shapes.card,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.skinDecor("panel").padding(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SkinTag(
                    text = kind,
                    container = MaterialTheme.colorScheme.secondaryContainer,
                    content = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
            }
            Text(metadata, style = MaterialTheme.typography.labelMedium)
            Text(note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}
