package com.shadowmonarchbooks.dayloop.ui.skin

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Phase 13 polish (v0.9.1): silhouette primitives must stay crisp at any
 * surface size. The first Phantom skin scaled tooth depth as a percentage of
 * the surface, which stretched the jagged highlight on the large carousel
 * card; these tests pin the bounded-geometry contract on the vertex math.
 */
class SkinShapeTest {

    private val density = Density(2.6f)
    private val large = Size(1056f, 1440f) // carousel card @2.6px/dp
    private val cell = Size(117f, 117f) // calendar day cell @2.6px/dp

    @Test
    fun `jagged teeth stay bounded on large surfaces`() {
        val pts = jaggedVertices(large, density)
        val maxDepthPx = 7f * 2.6f // the depth cap
        val maxY = pts.maxOf { it.y }
        val minX = pts.minOf { it.x }
        val maxX = pts.maxOf { it.x }
        // Teeth extend outward by at most the depth cap (jitter <= 1.4x).
        assertTrue(maxY <= large.height + maxDepthPx * 1.4f + 0.5f, "stretched bottom teeth: $maxY")
        assertTrue(minX >= -maxDepthPx * 1.4f - 0.5f, "stretched left teeth: $minX")
        assertTrue(maxX <= large.width + maxDepthPx * 1.4f + 0.5f, "stretched right teeth: $maxX")
    }

    @Test
    fun `jagged teeth stay visible on small surfaces`() {
        val pts = jaggedVertices(cell, density)
        val minY = pts.minOf { it.y }
        // The depth floor keeps small bursts (calendar clock date) visibly jagged.
        assertTrue(
            minY <= -2.5f * 2.6f * 0.6f + 0.5f,
            "teeth vanished on the small cell: minY=$minY",
        )
    }

    @Test
    fun `shapes are deterministic across calls`() {
        val a = jaggedVertices(large, density)
        val b = jaggedVertices(large, density)
        assertEquals(a, b, "seeded jitter must not wobble between frames")
    }

    @Test
    fun `teeth pitch stays tight across surface sizes`() {
        // On the large card the old percentage-based shape drew ~41dp teeth;
        // the pitch must now stay near the absolute 14dp target.
        val pts = jaggedVertices(large, density)
        val topPts = pts.filter { it.y <= 0f }.map { it.x }.sorted()
        assertTrue(topPts.size >= 8, "expected many teeth on a wide edge, got ${topPts.size}")
        val spans = topPts.zipWithNext { a, b -> b - a }.filter { it > 0f }
        val avgSpan = spans.average().toFloat()
        val expected = 14f * 2.6f
        assertTrue(avgSpan <= expected * 1.6f, "teeth too coarse on large card: $avgSpan px")
    }
}
