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

    // ---- Phase 15 (docs/ROADMAP-v3.md): plaque + seal geometry ----

    @Test
    fun `plaque chamfer stays bounded in dp`() {
        // The cap is 4 dp regardless of surface size.
        assertEquals(4f * 2.6f, plaqueChamfer(large, density), 0.01f, "large surface cap")
        assertEquals(4f * 2.6f, plaqueChamfer(cell, density), 0.01f, "small cell cap")
        // Tiny surfaces floor at 35% of the min side so the cut stays visible.
        val tiny = Size(10f, 10f)
        assertEquals(3.5f, plaqueChamfer(tiny, density), 0.01f, "tiny surface floor")
    }

    @Test
    fun `plaque chamfer is deterministic`() {
        assertEquals(plaqueChamfer(large, density), plaqueChamfer(large, density))
    }

    @Test
    fun `seal rim stays a wobbled disc around the center`() {
        val marker = Size(9f * 2.6f, 9f * 2.6f) // the SkinTag seal marker
        val pts = sealRim(marker, density)
        assertEquals(14, pts.size, "the stamp edge has 14 points")
        val cx = marker.width / 2f
        val cy = marker.height / 2f
        val r = marker.width / 2f
        for (p in pts) {
            val dist = kotlin.math.hypot((p.x - cx).toDouble(), (p.y - cy).toDouble()).toFloat()
            assertTrue(dist <= r * 1.0225f + 0.01f, "rim exceeds the stamp: $dist")
            assertTrue(dist >= r * 0.9775f - 0.01f, "rim collapses inward: $dist")
        }
    }

    @Test
    fun `seal rim is deterministic across calls`() {
        assertEquals(sealRim(marker(), density), sealRim(marker(), density), "seeded wobble must not wobble between frames")
    }

    private fun marker(): Size = Size(23.4f, 23.4f)
}
