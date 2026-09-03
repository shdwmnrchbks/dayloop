package com.shadowmonarchbooks.dayloop.ui.bonds

import com.shadowmonarchbooks.dayloop.pack.schema.Bond
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.progress.StepKey
import com.shadowmonarchbooks.dayloop.progress.StepMark

/**
 * Highest relationship rank proven by a DONE walkthrough task. Packs already
 * author rank milestones in task labels (for example, "Chariot reaches rank
 * 5"), so progress remains profile-scoped without a second mutable counter.
 */
internal fun completedBondRank(
    bond: Bond,
    days: Map<String, Day>,
    marks: Map<StepKey, StepMark>,
): Int {
    val rankPattern = Regex(
        pattern = "\\b${Regex.escape(bond.label)}(?:\\s+arcana)?\\b.*?\\b(?:reaches|advances to)\\s+rank\\s+(\\d+)\\b",
        option = RegexOption.IGNORE_CASE,
    )
    val highestAuthoredRank = bond.ranks.maxOfOrNull { it.rank } ?: return 0

    return days.values.maxOfOrNull { day ->
        day.steps.mapIndexedNotNull { index, step ->
            if (marks[StepKey(day.date, index)] != StepMark.DONE) return@mapIndexedNotNull null
            rankPattern.find(step.label)?.groupValues?.get(1)?.toIntOrNull()
        }.maxOrNull() ?: 0
    }?.coerceAtMost(highestAuthoredRank) ?: 0
}
