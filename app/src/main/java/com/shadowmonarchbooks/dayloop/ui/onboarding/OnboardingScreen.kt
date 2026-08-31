package com.shadowmonarchbooks.dayloop.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.data.formatDate
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.PackIcon
import com.shadowmonarchbooks.dayloop.ui.components.rememberAssetImage
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.packShape
import com.shadowmonarchbooks.dayloop.ui.theme.packColorScheme

/**
 * First-run game picker (docs/ROADMAP-v2.md Phase 7): a swipeable carousel of
 * cover-art cards, one per installed pack. Fully engine-neutral — titles,
 * date ranges, day counts, and the art all come from pack data; Kotlin never
 * names a game.
 *
 * The same screen is the app's single game picker (docs/ROADMAP-v3.md
 * Phase 11): when a game is already selected, Settings' Game section lands
 * here with [onCancel] wired (back arrow, current game preselected, saved
 * profile counts on the cards).
 */
@Composable
fun OnboardingScreen(
    vm: DayloopViewModel,
    onStart: () -> Unit,
    onCancel: (() -> Unit)? = null,
) {
    val state by vm.state.collectAsState()
    val packs = state.packs
    val profileCounts by vm.profileCounts.collectAsState()
    val reselecting = state.selectedSlug != null
    val pagerState = rememberPagerState(
        initialPage = packs.indexOfFirst { it.slug == state.selectedSlug }.coerceAtLeast(0),
        pageCount = { packs.size },
    )

    Column(Modifier.fillMaxSize().padding(vertical = 28.dp)) {
        Column(Modifier.padding(horizontal = 28.dp)) {
            if (reselecting && onCancel != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to settings")
                    }
                    Text(
                        text = "Choose a game",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            Text(
                text = "dayloop",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (reselecting) {
                    "Switching games never drops saves — each game keeps its own profiles and clock."
                } else {
                    "Track a calendar-JRPG run day by day — what to do now, " +
                        "and what you're about to miss. Your progress is saved per game."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(18.dp))
            // Skinned packs set the picker's display voice (ROADMAP-v3 Phase
            // 13); token-less packs keep the engine title look byte-stable.
            val skin = LocalSkin.current
            if (skin.hasSkin) {
                Text(
                    text = skin.cased("What are we tracking?", "display"),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                Text(
                    text = "What are we tracking?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 48.dp),
            pageSpacing = 14.dp,
        ) { page ->
            GameCard(
                pack = packs[page],
                selected = page == pagerState.currentPage,
                savedProfiles = profileCounts[packs[page].slug] ?: 0,
                onSelect = {
                    vm.selectPack(packs[page].slug)
                    onStart()
                },
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 4.dp),
        ) {
            repeat(packs.size) { i ->
                val active = i == pagerState.currentPage
                val dotSize by animateDpAsState(if (active) 9.dp else 6.dp, label = "dot")
                Box(
                    Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(
                            if (active) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline,
                        ),
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    pack: LoadedPack,
    selected: Boolean,
    savedProfiles: Int,
    onSelect: () -> Unit,
) {
    val cover = rememberAssetImage(pack.cardAsset)
    // Each card wears its own pack's accent (docs/ROADMAP-v2.md Phase 10),
    // not the active pack's — the carousel previews every installed skin.
    val packAccent = pack.pack.theme?.let { packColorScheme(it, dark = true).primary }
        ?: MaterialTheme.colorScheme.primary
    // …and each card wears its own pack's card silhouette (ROADMAP-v3 Phase
    // 13) — shape-only resolution, no font loading per preview card.
    val packCardShape = packShape(
        theme = pack.pack.theme,
        slot = "card",
        fallback = RoundedCornerShape(24.dp),
        density = LocalDensity.current,
    )
    Surface(
        onClick = onSelect,
        shape = packCardShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) BorderStroke(3.dp, packAccent) else null,
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(Modifier.fillMaxSize()) {
            val coverArt = cover
            if (coverArt != null) {
                Image(
                    bitmap = coverArt,
                    contentDescription = pack.pack.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.55f)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.85f),
                            ),
                        ),
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp),
                ) {
                    Text(
                        text = pack.pack.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = metaLine(pack, savedProfiles),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                    featureText(pack)?.let { features ->
                        Text(
                            text = features,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                        )
                    }
                }
            } else {
                // Art-free fallback: centered monogram tile with themed text.
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                ) {
                    PackIcon(pack.iconAsset, pack.pack.title, size = 96.dp)
                    Text(
                        text = pack.pack.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = metaLine(pack, savedProfiles),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    featureText(pack)?.let { features ->
                        Text(
                            text = features,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

private fun metaLine(pack: LoadedPack, savedProfiles: Int): String {
    val base = "${pack.sortedDates(Routes.defaultId(pack.pack)).size} authored days · ${dateRange(pack)}"
    return when (savedProfiles) {
        0 -> base
        1 -> "$base · 1 saved profile"
        else -> "$base · $savedProfiles saved profiles"
    }
}

private fun dateRange(pack: LoadedPack): String =
    "${formatDate(pack.pack.calendar.startDate, pack.calendar)} → " +
        formatDate(pack.pack.calendar.endDate, pack.calendar)

/** Engine-neutral capability summary; packs without extras get no line at all. */
private fun featureText(pack: LoadedPack): String? {
    val features = buildList {
        if (pack.pack.capabilities.answers) add("exam answers")
        if (pack.pack.routes.size > 1) add("${pack.pack.routes.size} routes")
    }
    return features.takeIf { it.isNotEmpty() }?.joinToString(" · ")
}
