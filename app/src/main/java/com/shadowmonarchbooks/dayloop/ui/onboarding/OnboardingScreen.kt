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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.data.formatDate
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.components.PackIcon
import com.shadowmonarchbooks.dayloop.ui.components.rememberAssetImage

/**
 * First-run game picker (docs/ROADMAP-v2.md Phase 7): a swipeable carousel of
 * cover-art cards, one per installed pack. Fully engine-neutral — titles,
 * date ranges, day counts, and the art all come from pack data; Kotlin never
 * names a game.
 */
@Composable
fun OnboardingScreen(
    vm: DayloopViewModel,
    onStart: () -> Unit,
) {
    val state by vm.state.collectAsState()
    val packs = state.packs
    val pagerState = rememberPagerState(pageCount = { packs.size })

    Column(Modifier.fillMaxSize().padding(vertical = 28.dp)) {
        Column(Modifier.padding(horizontal = 28.dp)) {
            Text(
                text = "dayloop",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Track a calendar-JRPG run day by day — what to do now, " +
                    "and what you're about to miss. Your progress is saved per game.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "What are we tracking?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
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
    onSelect: () -> Unit,
) {
    val cover = rememberAssetImage(pack.cardAsset)
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
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
                        text = metaLine(pack),
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
                        text = metaLine(pack),
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

private fun metaLine(pack: LoadedPack): String =
    "${pack.sortedDates(Routes.defaultId(pack.pack)).size} authored days · ${dateRange(pack)}"

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
