package com.shadowmonarchbooks.dayloop.pack.schema

import kotlinx.serialization.Serializable

/**
 * answers.json — structured answer sheets (docs/PLAN.md Phase 5).
 *
 * Facts only: which answers the game accepts on which in-game date. The
 * walkthrough keeps the step ("Answer the class question"); the sheet holds
 * the answers so the UI can render them as data instead of guide prose.
 */
@Serializable
data class AnswersFile(val answers: List<AnswerSheet>)

@Serializable
data class AnswerSheet(
    /** Immutable, pack-prefixed, e.g. "p5r.answers.exam.2016-05-11". */
    val id: String,
    /** In-game date the questions are asked (ISO). */
    val date: String,
    /** exam | classQuestion */
    val kind: String,
    /** Short display label in our own words, e.g. "May midterms — day 1". */
    val label: String,
    /** Accepted answers, in asking order; alternates are separate entries. */
    val answers: List<String>,
    /** Optional reference into deadlines.json for exam sheets. */
    val deadlineRef: String? = null,
)
