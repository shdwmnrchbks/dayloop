package com.shadowmonarchbooks.dayloop.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.android.AndroidEntryPoint

private val Ink = Color(0xFF14171F)
private val InkSoft = Color(0xFF1D2230)
private val TextPrimary = Color(0xFFF2F4F8)
private val TextSecondary = Color(0xFF9AA3B2)
private val Amber = Color(0xFFFFC857)
private val Sky = Color(0xFF6FA8FF)
private val Danger = Color(0xFFFF8A80)

/**
 * Home-screen widget (docs/PLAN.md Phase 5 / §6.3): the in-game clock, today's
 * progress, and the next deadline — always visible, never a push notification.
 */
class DayloopWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val snapshotter = dagger.hilt.android.EntryPointAccessors
            .fromApplication(context.applicationContext, WidgetEntryPoint::class.java)
            .snapshotter()
        val snapshot = snapshotter.snapshot()
        provideContent { Content(snapshot) }
    }

    @Composable
    private fun Content(snapshot: WidgetSnapshot) {
        Box(
            modifier = GlanceModifier.fillMaxSize().background(Ink).padding(12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            when {
                snapshot.isEmpty -> Text(
                    "dayloop",
                    style = TextStyle(color = ColorProvider(TextSecondary), fontSize = 13.sp),
                )
                else -> Column(modifier = GlanceModifier.fillMaxSize()) {
                    Row(modifier = GlanceModifier.fillMaxWidth()) {
                        Text(
                            snapshot.packTitle.orEmpty(),
                            style = TextStyle(color = ColorProvider(TextSecondary), fontSize = 12.sp),
                            modifier = GlanceModifier.defaultWeight(),
                        )
                        snapshot.routeLabel?.let {
                            Text(
                                it,
                                style = TextStyle(color = ColorProvider(Sky), fontSize = 11.sp),
                            )
                        }
                    }
                    Text(
                        snapshot.dateLabel.orEmpty(),
                        style = TextStyle(
                            color = ColorProvider(TextPrimary),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    if (snapshot.totalCount > 0) {
                        Text(
                            "${snapshot.doneCount} of ${snapshot.totalCount} done today",
                            style = TextStyle(color = ColorProvider(TextSecondary), fontSize = 12.sp),
                        )
                    }
                    snapshot.deadlineLabel?.let { label ->
                        val days = snapshot.deadlineDays
                        val urgency = if (days != null && days <= 3) Danger else Amber
                        Box(
                            modifier = GlanceModifier.fillMaxWidth().background(InkSoft).padding(horizontal = 8.dp, vertical = 6.dp),
                        ) {
                            Column {
                                Text(
                                    when {
                                        days == null -> ""
                                        days < 0 -> "Past deadline"
                                        days == 0L -> "Due today"
                                        else -> "In $days day(s)"
                                    },
                                    style = TextStyle(color = ColorProvider(urgency), fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                )
                                Text(
                                    label,
                                    style = TextStyle(color = ColorProvider(TextPrimary), fontSize = 12.sp),
                                    maxLines = 2,
                                )
                            }
                        }
                    }
                    snapshot.profileName?.let {
                        Text(
                            it,
                            style = TextStyle(color = ColorProvider(TextSecondary), fontSize = 10.sp),
                        )
                    }
                }
            }
        }
    }
}

@AndroidEntryPoint
class DayloopWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DayloopWidget()
}
