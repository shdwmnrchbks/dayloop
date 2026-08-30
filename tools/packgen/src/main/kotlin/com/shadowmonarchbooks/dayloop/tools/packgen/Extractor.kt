package com.shadowmonarchbooks.dayloop.tools.packgen

/**
 * Pure extraction logic: reconstruct the day timeline from date tokens
 * regardless of section boundaries (the archive has misplaced blocks), clean
 * archiver artifacts, and flag anything suspicious for human review.
 */
object Extractor {

    /** e.g. "04/24 Sun". Weekday token is validated against the real calendar later. */
    val DATE_TOKEN = Regex("""\b(\d{2})/(\d{2})\s+(Mon|Tue|Wed|Thu|Fri|Sat|Sun)\b""")

    /** Markers of archiver HTML corruption inside text. */
    val ARTIFACT = Regex(
        """[a-z]/\?imageurl=|rcontent\.com|class=\\"|sharedFilePreview|https?://|%2F|<img|</div|\.jpg|\.png""",
        RegexOption.IGNORE_CASE,
    )

    val WEEKDAY_OF_TOKEN = mapOf(
        "Mon" to "mon", "Tue" to "tue", "Wed" to "wed", "Thu" to "thu",
        "Fri" to "fri", "Sat" to "sat", "Sun" to "sun",
    )

    // UTF-8 punctuation misdecoded as CP1252 shows up as sequences starting
    // with U+00E2 U+20AC. Handle the known pairings explicitly, then catch
    // leftovers deterministically. Written as escapes so the source itself is
    // byte-stable.
    private const val E2 = "\u00E2\u20AC"

    /** Guide months Apr..Dec are 2016; Jan..Mar are 2017. */
    fun yearForMonth(month: Int): Int = if (month in 4..12) 2016 else 2017

    fun normalize(raw: String): String {
        var s = raw
        s = s.replace(E2 + "\u0153", "\"")   // opening “
        s = s.replace(E2 + "\u009C", "\"")   // opening “ variant
        s = s.replace(E2 + "\u009D", "\"")   // closing ”
        s = s.replace(E2 + "\u201C", "\"")   // “ variant
        s = s.replace(E2 + "\u201D", "\"")   // ” variant
        s = s.replace(E2 + "\u2019", "'")    // closing ’
        s = s.replace(E2 + "\u2122", "'")    // ’ variant
        s = s.replace(E2, "\"")              // leftover bare sequence
        return s.replace("\r", " ").replace(Regex("\\s+"), " ").trim()
    }

    /**
     * Attempts to salvage a fragment. Returns (kept, review):
     *  - clean fragment → (cleaned, null)
     *  - fragment with a cuttable tail → (prefix, original)
     *  - hopeless fragment → (null, original)
     */
    fun cleanFragment(raw: String): Pair<String?, String?> {
        val s = normalize(raw)
        if (s.isEmpty()) return null to null
        val m = ARTIFACT.find(s)
        if (m == null) return s to null
        val prefix = s.substring(0, m.range.first).trim(' ', '-', ',', ';', '"', '\'', '>')
        val prefixLooksReal = prefix.length >= 8 &&
            prefix.count { it.isLetter() || it == ' ' } > prefix.length * 2 / 3
        return if (prefixLooksReal) prefix to s else null to s
    }

    /** Splits a day's raw run into fragments on semicolons and newlines. */
    fun splitFragments(run: String): List<String> =
        run.split(';', '\n').map { it.trim() }.filter { it.isNotEmpty() }

    /** A section as consumed by [extract]: display title + concatenated content. */
    data class SectionInput(val title: String, val text: String)

    /**
     * Walks all section text in [sections] (already in document order), finds
     * date tokens, and merges fragments per date. Order of [sections] does not
     * matter for correctness of dates — output is sorted by date.
     */
    fun extract(sections: List<SectionInput>): Pair<List<CandidateDay>, ExtractionReport> {
        data class Acc(val sources: MutableSet<String>, val fragments: MutableList<String>, val review: MutableList<String>)

        val byDate = sortedMapOf<String, Acc>()
        val mismatches = mutableListOf<String>()
        var reviewCount = 0

        fun acc(date: String): Acc = byDate.getOrPut(date) { Acc(mutableSetOf(), mutableListOf(), mutableListOf()) }

        sections.forEach { section ->
            val matches = DATE_TOKEN.findAll(section.text).toList()
            matches.forEachIndexed { i, m ->
                val mm = m.groupValues[1].toInt()
                val dd = m.groupValues[2].toInt()
                val weekday = WEEKDAY_OF_TOKEN[m.groupValues[3]]
                    ?: error("unreachable weekday token ${m.groupValues[3]}")
                val date = "%04d-%02d-%02d".format(yearForMonth(mm), mm, dd)

                val realWeekday = Cal.weekdayOf(date)
                if (realWeekday != null && realWeekday != weekday) {
                    mismatches += "$date token says $weekday, real calendar says $realWeekday"
                }

                val start = m.range.last + 1
                val end = if (i + 1 < matches.size) matches[i + 1].range.first else section.text.length
                val a = acc(date)
                a.sources += section.title
                splitFragments(section.text.substring(start, end)).forEach { raw ->
                    val (kept, review) = cleanFragment(raw)
                    if (kept != null && kept !in a.fragments) a.fragments += kept
                    if (review != null) {
                        reviewCount++
                        if (review !in a.review) a.review += review
                    }
                }
            }
        }

        val days = byDate.map { (date, a) ->
            CandidateDay(
                date = date,
                weekday = Cal.weekdayOf(date) ?: "",
                sources = a.sources.toList(),
                fragments = a.fragments.toList(),
                review = a.review.toList(),
            )
        }
        val conflicts = byDate.filter { it.value.sources.size > 1 }.map { (date, a) ->
            "$date appears in sections: ${a.sources.joinToString()}"
        }
        val report = ExtractionReport(
            sectionsRead = sections.size,
            datesFound = days.size,
            weekdayMismatches = mismatches,
            duplicateTokenConflicts = conflicts,
            reviewCount = reviewCount,
        )
        return days to report
    }
}
