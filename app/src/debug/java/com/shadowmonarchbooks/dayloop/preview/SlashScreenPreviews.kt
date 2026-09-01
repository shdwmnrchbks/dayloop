package com.shadowmonarchbooks.dayloop.preview

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.ui.components.AnswerKindChip
import com.shadowmonarchbooks.dayloop.ui.components.SkinHeader
import com.shadowmonarchbooks.dayloop.ui.components.SkinTag
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.SkinBottomBar
import com.shadowmonarchbooks.dayloop.ui.skin.SkinNavItem
import com.shadowmonarchbooks.dayloop.ui.skin.SkinRouteBadge
import com.shadowmonarchbooks.dayloop.ui.skin.SkinTopBar
import com.shadowmonarchbooks.dayloop.ui.skin.skinBackdrop
import com.shadowmonarchbooks.dayloop.ui.theme.DayloopTheme

private const val SlashPreviewGroup = "Slash screens"

@Preview(
    name = "Today",
    group = SlashPreviewGroup,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SlashTodayPreview() {
    SlashPreviewFrame(title = "Today", selectedRoute = "today") {
        SkinRouteBadge("100% Completion Route")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SkinHeader("Tuesday, September 1", Modifier.weight(1f, fill = false))
            SkinTag(
                text = "School",
                container = MaterialTheme.colorScheme.primary,
                content = MaterialTheme.colorScheme.onPrimary,
            )
        }
        SlashCard {
            Text(
                text = LocalSkin.current.cased("Next deadline", "display"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text("Secure the route before the story cutoff.")
            SkinTag(
                text = "IN 2 D",
                container = MaterialTheme.colorScheme.primary,
                content = MaterialTheme.colorScheme.onPrimary,
            )
        }
        AnswerKindChip("classQuestion")
        SlashStep(number = "01", title = "Answer the class question", meta = "Knowledge +2", done = true)
        SlashStep(number = "02", title = "Meet the next Confidant", meta = "Afternoon")
        SlashStep(number = "03", title = "Spoiler — tap to reveal", meta = "Story step")
    }
}

@Preview(
    name = "Calendar",
    group = SlashPreviewGroup,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SlashCalendarPreview() {
    SlashPreviewFrame(title = "Calendar", selectedRoute = "calendar") {
        SkinHeader("September 2016")
        Row(Modifier.fillMaxWidth()) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { label ->
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(label, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
        val days = (1..28).toList().chunked(7)
        days.forEachIndexed { rowIndex, row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEach { day ->
                    SlashCalendarCell(
                        day = day,
                        selected = day == 14,
                        deadline = day == 6 || day == 21,
                        story = (day + rowIndex) % 5 == 0,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        SkinTag(
            text = "RED MARK = DEADLINE",
            container = MaterialTheme.colorScheme.primary,
            content = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Preview(
    name = "Confidants",
    group = SlashPreviewGroup,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SlashConfidantsPreview() {
    SlashPreviewFrame(title = "Confidants", selectedRoute = "bonds") {
        SkinHeader("Confidants")
        SlashListCard("COUNCILOR", "Rank 9 · route date Oct 7", "THIRD-SEMESTER GATE")
        SlashListCard("JUSTICE", "Rank 8 · route date Nov 4", "OPTIONAL ROYAL CONTENT")
        SlashListCard("FAITH", "Rank 5 · route date Jul 28", "THIRD-SEMESTER RANKS")
        SlashListCard("HANGED MAN", "Rank 10 · route date Dec 1", "MAX")
    }
}

@Preview(
    name = "Deadlines",
    group = SlashPreviewGroup,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SlashDeadlinesPreview() {
    SlashPreviewFrame(title = "Deadlines", selectedRoute = "today") {
        SkinHeader("Deadlines")
        SlashDeadlineCard("PALACE STORY DEADLINE", "October 11", "IN 3 D", urgent = true)
        SlashDeadlineCard("COUNCILOR RANK 9", "November 17", "IN 40 D")
        SlashDeadlineCard("FAITH RANK 5", "December 22", "IN 75 D")
        SlashDeadlineCard("COMPLETION ROUTE TARGET", "Finish remaining books", "ROUTE")
    }
}

@Preview(
    name = "Settings",
    group = SlashPreviewGroup,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun SlashSettingsPreview() {
    SlashPreviewFrame(title = "Settings", selectedRoute = "settings") {
        SkinHeader("Settings")
        SlashSettingRow("ACTIVE ROUTE", "100% Completion Route")
        SlashSettingRow("PACK THEME", "Ink · masks · slash")
        SlashSettingToggle("SKIN MOTION", "Use slash transitions", checked = true)
        SlashSettingToggle("SKIN SOUNDS", "Play pack interaction sounds", checked = false)
        SlashSettingRow("CONTENT VERSION", "7")
    }
}

@Composable
private fun SlashPreviewFrame(
    title: String,
    selectedRoute: String,
    content: @Composable Column.() -> Unit,
) {
    val pack = Phase17PreviewFixtures.coldStarts
        .first { it.skinId == "masks" && it.dark }
        .pack

    DayloopTheme(pack = pack, darkTheme = true) {
        val skin = LocalSkin.current
        val tabs = listOf(
            SkinNavItem("today", "Today", Icons.Filled.Home),
            SkinNavItem("calendar", "Calendar", Icons.Filled.DateRange),
            SkinNavItem("bonds", "Bonds", Icons.Filled.Person),
            SkinNavItem("settings", "Settings", Icons.Filled.Settings),
        )
        Scaffold(
            topBar = {
                SkinTopBar(
                    title = title,
                    canGoBack = false,
                    onBack = {},
                    onOpenSearch = {},
                    onOpenSettings = {},
                )
            },
            bottomBar = {
                SkinBottomBar(
                    items = tabs,
                    selectedRoute = selectedRoute,
                    onSelect = {},
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun SlashCard(content: @Composable Column.() -> Unit) {
    val skin = LocalSkin.current
    Surface(
        shape = skin.shapes.card,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(14.dp),
            content = content,
        )
    }
}

@Composable
private fun SlashStep(number: String, title: String, meta: String, done: Boolean = false) {
    val skin = LocalSkin.current
    SlashCard {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = skin.shapes.chip,
                color = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            ) {
                Text(
                    number,
                    color = if (done) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(meta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun SlashCalendarCell(
    day: Int,
    selected: Boolean,
    deadline: Boolean,
    story: Boolean,
    modifier: Modifier = Modifier,
) {
    val skin = LocalSkin.current
    Surface(
        shape = skin.shapes.card,
        color = when {
            selected -> MaterialTheme.colorScheme.primary
            story -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.background
        },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)),
        shadowElevation = 0.dp,
        modifier = modifier.aspectRatio(1f),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                day.toString(),
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                fontWeight = if (selected) FontWeight.Bold else null,
            )
            if (deadline) {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                        .size(9.dp)
                        .background(MaterialTheme.colorScheme.primary, skin.shapes.chip),
                )
            }
        }
    }
}

@Composable
private fun SlashListCard(title: String, subtitle: String, tag: String) {
    SlashCard {
        Text(
            LocalSkin.current.cased(title, "display"),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        SkinTag(
            text = tag,
            container = MaterialTheme.colorScheme.primary,
            content = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun SlashDeadlineCard(title: String, date: String, countdown: String, urgent: Boolean = false) {
    SlashCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f)) {
                Text(
                    LocalSkin.current.cased(title, "display"),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(date, style = MaterialTheme.typography.bodyMedium)
            }
            SkinTag(
                text = countdown,
                container = if (urgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                content = if (urgent) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun SlashSettingRow(label: String, value: String) {
    SlashCard {
        Text(
            LocalSkin.current.cased(label, "display"),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun SlashSettingToggle(label: String, description: String, checked: Boolean) {
    SlashCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) {
                Text(
                    LocalSkin.current.cased(label, "display"),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(description, style = MaterialTheme.typography.bodyMedium)
            }
            Switch(checked = checked, onCheckedChange = null)
        }
    }
}
