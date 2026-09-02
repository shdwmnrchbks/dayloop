package com.shadowmonarchbooks.dayloop.preview

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.ui.components.SkinHeader
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.SkinTopBar
import com.shadowmonarchbooks.dayloop.ui.skin.skinBackdrop
import com.shadowmonarchbooks.dayloop.ui.skin.skinDecor
import com.shadowmonarchbooks.dayloop.ui.theme.DayloopTheme

@Preview(
    name = "Search",
    group = "Slash screens",
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SlashSearchPreview() {
    val pack = Phase17PreviewFixtures.coldStarts
        .first { it.skinId == "masks" && it.dark }
        .pack

    DayloopTheme(pack = pack, darkTheme = true) {
        val skin = LocalSkin.current
        Scaffold(
            topBar = {
                SkinTopBar(
                    title = "Search",
                    canGoBack = true,
                    onBack = {},
                    onOpenSearch = {},
                    onOpenSettings = {},
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { padding ->
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .skinBackdrop(skin)
                    .padding(16.dp),
            ) {
                OutlinedTextField(
                    value = "Maruki",
                    onValueChange = {},
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                )
                SkinHeader("Confidants")
                SlashSearchPreviewRow(
                    title = "COUNCILOR — TAKUTO MARUKI",
                    snippet = "Rank 9 must be reached by 11/17 to unlock the third semester.",
                )
                SkinHeader("Deadlines")
                SlashSearchPreviewRow(
                    title = "COUNCILOR RANK 9",
                    snippet = "Required to unlock the third semester.",
                )
                SkinHeader("Activities")
                SlashSearchPreviewRow(
                    title = "READ 'THE CRAFT OF CINEMA'",
                    snippet = "Shinjuku bookstore / metro",
                )
            }
        }
    }
}

@Composable
private fun SlashSearchPreviewRow(
    title: String,
    snippet: String,
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
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.skinDecor("panel").padding(horizontal = 12.dp, vertical = 9.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(snippet, style = MaterialTheme.typography.bodySmall)
        }
    }
}
