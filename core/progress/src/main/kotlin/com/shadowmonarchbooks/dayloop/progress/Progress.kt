package com.shadowmonarchbooks.dayloop.progress

import java.time.LocalDate

/**
 * Engine-neutral progress semantics (docs/PLAN.md Phase 3): tri-state step
 * marks, the End-Day in-game clock, and orphan detection. No game vocabulary
 * and no Android — persistence layers (Room) map their rows onto these
 * structures, and the UI renders their results.
 */

/** Tri-state walkthrough mark (docs/PLAN.md §6.1: Done / Skip / Later). */
enum class StepMark { DONE, SKIP, LATER }

/**
 * Stable location of a step inside a pack: its authored ISO date and the
 * 0-based step index within that day. Persisted marks are keyed by this pair;
 * when content edits invalidate it, [ProgressLogic.orphans] surfaces the
 * strays instead of silently dropping them (docs/PLAN.md §3.6).
 */
data class StepKey(val date: String, val index: Int) {
    override fun toString(): String = "$date#$index"

    companion object {
        /** Inverse of [toString]; null when [raw] is not a valid key. */
        fun parse(raw: String): StepKey? {
            val sep = raw.lastIndexOf('#')
            if (sep <= 0) return null
            val index = raw.substring(sep + 1).toIntOrNull() ?: return null
            if (index < 0) return null
            return StepKey(raw.substring(0, sep), index)
        }
    }
}

/**
 * Inclusive calendar span the pack's game runs on — the engine mirror of
 * pack.json's `calendar` object.
 */
data class CalendarSpan(
    val startDate: String,
    val endDate: String,
    /** Dates inside the span the player cannot act on (story-only, travel). */
    val nonPlayableDates: Set<String> = emptySet(),
) {
    fun playable(iso: String): Boolean = iso !in nonPlayableDates
}

/**
 * End-Day clock semantics: the clock sits on playable dates only. Advancing
 * jumps over non-playable dates (the game plays those itself) and clamps at
 * the calendar bounds; a null result means "no further day in this pack".
 */
object Clock {

    fun parseOrNull(iso: String): LocalDate? = runCatching { LocalDate.parse(iso) }.getOrNull()

    /** Next playable date strictly after [iso], or null when none remains. */
    fun next(span: CalendarSpan, iso: String): String? = step(span, iso, forward = true)

    /** Previous playable date strictly before [iso], or null when none remains. */
    fun previous(span: CalendarSpan, iso: String): String? = step(span, iso, forward = false)

    /** Reset position for a fresh or reset profile: the pack's first day. */
    fun start(span: CalendarSpan): String = span.startDate

    private fun step(span: CalendarSpan, iso: String, forward: Boolean): String? {
        val start = parseOrNull(span.startDate) ?: return null
        val end = parseOrNull(span.endDate) ?: return null
        var current = parseOrNull(iso) ?: return null
        while (true) {
            current = if (forward) current.plusDays(1) else current.minusDays(1)
            if (forward && current > end) return null
            if (!forward && current < start) return null
            val candidate = current.toString()
            if (span.playable(candidate)) return candidate
        }
    }
}

/** Per-day tally for progress summaries. */
data class DayProgress(val done: Int, val skipped: Int, val deferred: Int, val total: Int) {
    val settled: Int get() = done + skipped
}

/**
 * Pure transformations over the mark map. [ProgressLogic] never touches
 * storage; callers own read-modify-write of the persisted state.
 */
object ProgressLogic {

    /**
     * Toggle semantics: applying the mark a step already carries clears it
     * (tapping "Done" on a done step un-dones it). Immutably returns the
     * updated map.
     */
    fun withMark(
        states: Map<StepKey, StepMark>,
        key: StepKey,
        mark: StepMark,
    ): Map<StepKey, StepMark> = if (states[key] == mark) states - key else states + (key to mark)

    /** Explicitly clear a mark (same as toggling whatever it currently has). */
    fun withoutMark(states: Map<StepKey, StepMark>, key: StepKey): Map<StepKey, StepMark> =
        states - key

    /**
     * Marks deferred on days *before* [beforeIso] — the carried-over queue.
     * Sorted oldest-first so the earliest promise is honoured first.
     */
    fun carriedOver(states: Map<StepKey, StepMark>, beforeIso: String): List<StepKey> =
        states.asSequence()
            .filter { (_, mark) -> mark == StepMark.LATER }
            .map { (key, _) -> key }
            .filter { it.date < beforeIso }
            .sortedWith(compareBy({ it.date }, { it.index }))
            .toList()

    /** Tally of marks recorded for [date]; [total] is the authored step count. */
    fun dayProgress(states: Map<StepKey, StepMark>, date: String, total: Int): DayProgress {
        var done = 0
        var skipped = 0
        var deferred = 0
        for ((key, mark) in states) {
            if (key.date != date) continue
            when (mark) {
                StepMark.DONE -> done++
                StepMark.SKIP -> skipped++
                StepMark.LATER -> deferred++
            }
        }
        return DayProgress(done, skipped, deferred, total)
    }

    /**
     * Saved marks that no longer resolve against current content: their day
     * is no longer authored, or the day lost steps so the index fell off.
     * [stepCounts] maps an authored date to its step count. Orphans are
     * surfaced for review, never dropped silently (docs/PLAN.md §3.6).
     */
    fun orphans(states: Map<StepKey, StepMark>, stepCounts: Map<String, Int>): Set<StepKey> =
        states.keys.filterTo(mutableSetOf()) { key ->
            (stepCounts[key.date] ?: 0) <= key.index
        }
}
