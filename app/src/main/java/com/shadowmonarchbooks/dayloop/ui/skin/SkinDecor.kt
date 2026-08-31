package com.shadowmonarchbooks.dayloop.ui.skin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.shadowmonarchbooks.dayloop.ui.skin.SkinPainters.glassBrush
import com.shadowmonarchbooks.dayloop.ui.skin.SkinPainters.grainPath
import com.shadowmonarchbooks.dayloop.ui.skin.SkinPainters.halftonePath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Decoration layer of the Skin DSL (docs/ROADMAP-v3.md Phase 12): draws a
 * slot's declared decor art behind the content, falling back to the motif
 * family's procedural painter, then to nothing (the engine look). Skins
 * decorate — they never carry information.
 */
fun Modifier.skinDecor(slot: String, accent: Color = Unspecified): Modifier = composed {
    val skin = LocalSkin.current
    val color = if (accent.isSpecified) accent else MaterialTheme.colorScheme.primary
    val asset = skin.decor.art[slot]
    val bitmap = rememberDecorBitmap(asset)
    when {
        bitmap != null -> drawBehind {
            // Draw the declared art aspect-preserving, center-cropped to the
            // decorated area (ContentScale.Crop semantics). Naive stretching
            // turned halftone dots into ovals and warped slash bands on
            // differently-proportioned surfaces (Phase 13 polish).
            val scale = maxOf(size.width / bitmap.width, size.height / bitmap.height)
            val cropW = (size.width / scale).toInt().coerceIn(1, bitmap.width)
            val cropH = (size.height / scale).toInt().coerceIn(1, bitmap.height)
            val srcX = ((bitmap.width - cropW) / 2f).toInt()
            val srcY = ((bitmap.height - cropH) / 2f).toInt()
            drawImage(
                image = bitmap.asImageBitmap(),
                srcOffset = IntOffset(srcX, srcY),
                srcSize = IntSize(cropW, cropH),
                dstOffset = IntOffset.Zero,
                dstSize = IntSize(size.width.toInt(), size.height.toInt()),
            )
        }
        skin.decor.painter != null -> drawBehind { paintDecoration(skin.decor.painter, color, size) }
        else -> Modifier
    }
}

@Composable
private fun rememberDecorBitmap(asset: String?): Bitmap? {
    val context = LocalContext.current
    return produceState<Bitmap?>(initialValue = null, key1 = asset) {
        value = asset?.let { path ->
            withContext(Dispatchers.IO) {
                runCatching {
                    context.assets.open(path).use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }.getOrNull()
            }
        }
    }.value
}

/** Procedural painters for the motif families (closed set in [SkinPainters]). */
private fun DrawScope.paintDecoration(painter: String?, color: Color, size: Size) {
    when (painter) {
        "halftone" -> drawPath(halftonePath(size, density), color.copy(alpha = 0.10f))
        "grain" -> drawPath(grainPath(size, density), color.copy(alpha = 0.08f))
        "glass" -> drawRect(brush = glassBrush(color))
        "filigree" -> filigree(color, size)
    }
}

/** Corner curls + an inner hairline — the filigree family's frame painter. */
private fun DrawScope.filigree(color: Color, size: Size) {
    val stroke = Stroke(width = 1.dp.toPx())
    val inset = 3.dp.toPx()
    val pad = 2.dp.toPx()
    val radius = minOf(size.width, size.height) * 0.14f
    val c = color.copy(alpha = 0.45f)
    // Inner hairline frame
    drawRoundRect(
        color = c.copy(alpha = 0.35f),
        topLeft = Offset(inset, inset),
        size = Size(size.width - inset * 2, size.height - inset * 2),
        cornerRadius = CornerRadius(6.dp.toPx()),
        style = stroke,
    )
    // Corner curls: quarter arcs tucked into each corner
    drawArc(c, startAngle = 90f, sweepAngle = 90f, useCenter = false, topLeft = Offset(inset + pad, inset + pad), size = Size(radius, radius), style = stroke)
    drawArc(c, startAngle = 0f, sweepAngle = 90f, useCenter = false, topLeft = Offset(size.width - inset - pad - radius, inset + pad), size = Size(radius, radius), style = stroke)
    drawArc(c, startAngle = 270f, sweepAngle = 90f, useCenter = false, topLeft = Offset(size.width - inset - pad - radius, size.height - inset - pad - radius), size = Size(radius, radius), style = stroke)
    drawArc(c, startAngle = 180f, sweepAngle = 90f, useCenter = false, topLeft = Offset(inset + pad, size.height - inset - pad - radius), size = Size(radius, radius), style = stroke)
}
