package com.shadowmonarchbooks.dayloop.tools.packgen

import kotlinx.serialization.Serializable

/** One extracted day: raw facts awaiting human curation. */
@Serializable
data class CandidateDay(
    val date: String,
    val weekday: String,
    /** Section titles the day's fragments came from (misplaced blocks may span sections). */
    val sources: List<String>,
    /** Cleaned step fragments in document order, deduplicated. */
    val fragments: List<String>,
    /** Fragments containing archive artifacts — human review required. */
    val review: List<String>,
)

@Serializable
data class ExtractionReport(
    val sectionsRead: Int,
    val datesFound: Int,
    val weekdayMismatches: List<String>,
    val duplicateTokenConflicts: List<String>,
    val reviewCount: Int,
)

@Serializable
data class Candidates(
    val guideUrl: String,
    val days: List<CandidateDay>,
    val report: ExtractionReport,
)
