package com.shadowmonarchbooks.dayloop.ui.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.pack.schema.MediaItem
import com.shadowmonarchbooks.dayloop.pack.schema.MediaKinds
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState
import com.shadowmonarchbooks.dayloop.ui.components.MediaImage

/**
 * The pack's bundled graphics, every declared media.json item grouped by
 * serving kind (docs/ROADMAP-v3.md Phase 11). Anchored items state where they
 * surface; unanchored ones are labelled gallery-only.
 */
@Composable
fun MediaScreen(
    vm: DayloopViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    val pack = state.selected ?: run {
        EmptyState("No pack selected.")
        return
    }
    if (pack.media.isEmpty()) {
        EmptyState("This pack ships no graphics.")
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        pack.mediaByKind().forEach { (kind, items) ->
            item(key = "kind-$kind") {
                Text(
                    text = kindLabel(kind, items.size, pack),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            items(items, key = { it.id }) { item ->
                MediaGalleryRow(pack = pack, item = item)
            }
        }
    }
}

@Composable
private fun MediaGalleryRow(pack: LoadedPack, item: MediaItem) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(10.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                MediaImage(
                    assetPath = pack.assetOf(item),
                    title = item.title,
                    size = 64.dp,
                    modifier = Modifier.padding(6.dp),
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                item.caption?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = anchorText(pack, item),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

/** Where this item surfaces, in pack vocabulary ("June 2016", "Lovers", "gallery only"). */
private fun anchorText(pack: LoadedPack, item: MediaItem): String {
    val anchors = buildList {
        if (item.months.isNotEmpty()) add("months: ${item.months.joinToString()}")
        if (item.dates.isNotEmpty()) add("days: ${item.dates.joinToString()}")
        item.bonds.forEach { bondId ->
            pack.bonds.firstOrNull { it.id == bondId }?.let { add(it.label) }
        }
    }
    return if (anchors.isEmpty()) "gallery only" else anchors.joinToString(" · ")
}

private fun kindLabel(kind: String, count: Int, pack: LoadedPack): String {
    val noun = when (kind) {
        MediaKinds.ACHIEVEMENT -> "Achievements"
        MediaKinds.MONTH -> "Month art"
        MediaKinds.SECTION -> "Section markers"
        MediaKinds.DAY -> "Day markers"
        MediaKinds.PORTRAIT -> "Portraits"
        MediaKinds.BANNER -> "Banners"
        MediaKinds.GUIDE -> "Guide art"
        else -> kind.replaceFirstChar { it.uppercase() }
    }
    return "$noun · $count"
}
