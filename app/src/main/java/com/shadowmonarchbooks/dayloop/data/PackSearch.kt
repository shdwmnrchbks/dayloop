package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.schema.Activity
import com.shadowmonarchbooks.dayloop.pack.schema.AnswerSheet
import com.shadowmonarchbooks.dayloop.pack.schema.Bond
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.Day

/**
 * Engine-neutral search over one pack's content (docs/PLAN.md Phase 5).
 * Pure substring matching — packs are small enough that no index is needed,
 * and the function stays unit-testable on the JVM.
 */

data class DayHit(val date: String, val snippet: String)
data class BondHit(val bondId: String, val label: String, val snippet: String)
data class ActivityHit(val activityId: String, val label: String)
data class DeadlineHit(val deadlineId: String, val label: String)
data class AnswerHit(val date: String, val label: String, val snippet: String)

data class SearchHits(
    val days: List<DayHit> = emptyList(),
    val bonds: List<BondHit> = emptyList(),
    val activities: List<ActivityHit> = emptyList(),
    val deadlines: List<DeadlineHit> = emptyList(),
    val answers: List<AnswerHit> = emptyList(),
) {
    val total: Int get() = days.size + bonds.size + activities.size + deadlines.size + answers.size
    val isEmpty: Boolean get() = total == 0
}

fun searchPack(
    query: String,
    days: Map<String, Day>,
    bonds: List<Bond>,
    activities: Map<String, Activity>,
    deadlines: List<Deadline>,
    answersByDate: Map<String, AnswerSheet>,
    perGroupLimit: Int = 6,
): SearchHits {
    val q = query.trim().lowercase()
    if (q.isEmpty()) return SearchHits()

    fun contains(haystack: String?): Boolean = haystack != null && q in haystack.lowercase()

    val dayHits = days.entries
        .sortedBy { it.key }
        .mapNotNull { (date, day) ->
            when {
                q in date.lowercase() -> DayHit(date, day.notes ?: day.steps.firstOrNull()?.label ?: date)
                else -> {
                    val step = day.steps.firstOrNull { contains(it.label) }
                    when {
                        step != null -> DayHit(date, step.label)
                        contains(day.notes) -> DayHit(date, day.notes.orEmpty())
                        else -> null
                    }
                }
            }
        }
        .take(perGroupLimit)

    val bondHits = bonds.mapNotNull { bond ->
        when {
            contains(bond.label) || contains(bond.characterLabel) ->
                BondHit(bond.id, bond.label, bond.characterLabel ?: bond.label)
            else -> {
                val rank = bond.ranks.firstOrNull { contains(it.notes) || contains(it.location) }
                if (rank != null) {
                    BondHit(bond.id, bond.label, rank.notes ?: rank.location.orEmpty())
                } else {
                    null
                }
            }
        }
    }.take(perGroupLimit)

    val activityHits = activities.values
        .sortedBy { it.id }
        .filter { contains(it.label) || contains(it.notes) }
        .take(perGroupLimit)
        .map { ActivityHit(it.id, it.label) }

    val deadlineHits = deadlines
        .sortedBy { deadlineStart(it) ?: "9999" }
        .filter { contains(it.label) }
        .take(perGroupLimit)
        .map { DeadlineHit(it.id, it.label) }

    val answerHits = answersByDate.values
        .sortedBy { it.date }
        .mapNotNull { sheet ->
            when {
                contains(sheet.label) || q in sheet.date -> AnswerHit(sheet.date, sheet.label, sheet.answers.firstOrNull().orEmpty())
                else -> sheet.answers.firstOrNull { contains(it) }?.let { AnswerHit(sheet.date, sheet.label, it) }
            }
        }
        .take(perGroupLimit)

    return SearchHits(dayHits, bondHits, activityHits, deadlineHits, answerHits)
}
