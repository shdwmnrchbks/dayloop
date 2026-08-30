package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.schema.Activity
import com.shadowmonarchbooks.dayloop.pack.schema.ActivitiesFile
import com.shadowmonarchbooks.dayloop.pack.schema.AnswerSheet
import com.shadowmonarchbooks.dayloop.pack.schema.Bond
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.pack.schema.RankStep
import com.shadowmonarchbooks.dayloop.pack.schema.Step
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Search over pack content (docs/PLAN.md Phase 5). */
class PackSearchTest {

    private val days = mapOf(
        "2016-05-11" to Day(
            "2016-05-11", "wed", "exam",
            steps = listOf(Step("Exams day 1")),
        ),
        "2016-05-12" to Day(
            "2016-05-12", "thu", "exam",
            steps = listOf(Step("Study with the team")),
            notes = "Rooftop lunch",
        ),
    )
    private val bonds = listOf(
        Bond(
            "t1.bond.fool", "Fool",
            ranks = listOf(RankStep(rank = 1, notes = "Meet at the courtyard")),
        ),
    )
    private val activities = mapOf(
        "t1.activity.dvd.wraith" to Activity("t1.activity.dvd.wraith", "Rent 'Wraith'", "dvd"),
    )
    private val deadlines = listOf(
        Deadline("t1.deadline.exams", "May midterms", "exam", date = "2016-05-11"),
    )
    private val answers = mapOf(
        "2016-05-11" to AnswerSheet(
            "t1.answers.exam.2016-05-11", "2016-05-11", "exam", "May midterms — day 1",
            listOf("Minamoto no Yoshitsune"),
        ),
    )

    @Test
    fun `empty query returns nothing`() {
        val hits = searchPack("  ", days, bonds, activities, deadlines, answers)
        assertTrue(hits.isEmpty)
    }

    @Test
    fun `step labels match days case-insensitively`() {
        val hits = searchPack("study", days, bonds, activities, deadlines, answers)
        assertEquals(listOf("2016-05-12"), hits.days.map { it.date })
    }

    @Test
    fun `day notes match`() {
        val hits = searchPack("rooftop", days, bonds, activities, deadlines, answers)
        assertEquals(listOf("2016-05-12"), hits.days.map { it.date })
    }

    @Test
    fun `bonds match on rank notes`() {
        val hits = searchPack("courtyard", days, bonds, activities, deadlines, answers)
        assertEquals(listOf("t1.bond.fool"), hits.bonds.map { it.bondId })
    }

    @Test
    fun `deadlines and answers match`() {
        val hits = searchPack("midterms", days, bonds, activities, deadlines, answers)
        assertEquals(listOf("t1.deadline.exams"), hits.deadlines.map { it.deadlineId })
        assertEquals(listOf("2016-05-11"), hits.answers.map { it.date })
    }

    @Test
    fun `answer text matches and lands on its day group`() {
        val hits = searchPack("yoshitsune", days, bonds, activities, deadlines, answers)
        assertTrue(hits.answers.isNotEmpty())
        assertTrue(hits.days.isEmpty())
    }

    @Test
    fun `results are capped per group`() {
        val many = (1..20).associate {
            ("2016-05-%02d".format(it)) to Day(
                "2016-05-%02d".format(it), "sat", "free",
                steps = listOf(Step("Needle point $it")),
            )
        }
        val hits = searchPack("needle", many, bonds, activities, deadlines, answers, perGroupLimit = 6)
        assertEquals(6, hits.days.size)
    }
}
