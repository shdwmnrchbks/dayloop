package com.shadowmonarchbooks.dayloop.preview

import com.shadowmonarchbooks.dayloop.widget.WidgetLayoutClass
import com.shadowmonarchbooks.dayloop.widget.WidgetTreatment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class Phase17PreviewFixturesTest {
    @Test
    fun `fixture ids pin the Phase 18 widget matrix`() {
        assertEquals(
            listOf(
                "widget.engine.compact",
                "widget.engine.standard",
                "widget.engine.expanded",
                "widget.masks.compact",
                "widget.masks.standard",
                "widget.masks.expanded",
                "widget.moon.compact",
                "widget.moon.standard",
                "widget.moon.expanded",
                "widget.crown.compact",
                "widget.crown.standard",
                "widget.crown.expanded",
            ),
            Phase17PreviewFixtures.widgets.map { it.id },
        )
    }

    @Test
    fun `widget fixtures cover all skins and supported size classes`() {
        assertEquals(12, Phase17PreviewFixtures.widgets.size)
        Phase17PreviewFixtures.skins.forEach { skin ->
            val cases = Phase17PreviewFixtures.widgets.filter { it.skinId == skin.id }
            assertEquals(
                setOf(WidgetLayoutClass.COMPACT, WidgetLayoutClass.STANDARD, WidgetLayoutClass.EXPANDED),
                cases.map { it.layoutClass }.toSet(),
            )
        }
    }

    @Test
    fun `widget fixtures change chrome but keep semantic payload identical`() {
        val signatures = Phase17PreviewFixtures.widgets.map { fixture ->
            fixture.snapshot.run {
                listOf(
                    packTitle,
                    profileName,
                    routeLabel,
                    dateLabel,
                    doneCount,
                    totalCount,
                    deadlineLabel,
                    deadlineDays,
                )
            }
        }.toSet()
        assertEquals(1, signatures.size)
    }

    @Test
    fun `generic skin families hit the four Phase 17 widget treatments`() {
        val treatmentBySkin = Phase17PreviewFixtures.widgets
            .groupBy { it.skinId }
            .mapValues { (_, fixtures) -> fixtures.first().snapshot.skin.treatment }
        assertEquals(WidgetTreatment.ENGINE, treatmentBySkin["engine"])
        assertEquals(WidgetTreatment.ANGULAR, treatmentBySkin["masks"])
        assertEquals(WidgetTreatment.GLASS, treatmentBySkin["moon"])
        assertEquals(WidgetTreatment.FRAMED, treatmentBySkin["crown"])
    }

    @Test
    fun `cold start fixtures pin four skins in both light and dark modes`() {
        assertEquals(8, Phase17PreviewFixtures.coldStarts.size)
        Phase17PreviewFixtures.skins.forEach { skin ->
            val cases = Phase17PreviewFixtures.coldStarts.filter { it.skinId == skin.id }
            assertEquals(setOf(false, true), cases.map { it.dark }.toSet())
        }
        assertNull(Phase17PreviewFixtures.coldStarts.first { it.skinId == "engine" }.pack)
        Phase17PreviewFixtures.coldStarts
            .filterNot { it.skinId == "engine" }
            .forEach { assertNotNull(it.pack) }
    }

    @Test
    fun `cold start fixture ids are stable for screenshot baselines`() {
        assertEquals(
            listOf(
                "cold-start.engine.light",
                "cold-start.engine.dark",
                "cold-start.masks.light",
                "cold-start.masks.dark",
                "cold-start.moon.light",
                "cold-start.moon.dark",
                "cold-start.crown.light",
                "cold-start.crown.dark",
            ),
            Phase17PreviewFixtures.coldStarts.map { it.id },
        )
    }
}
