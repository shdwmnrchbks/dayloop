package com.shadowmonarchbooks.dayloop.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.data.PackStore
import com.shadowmonarchbooks.dayloop.data.progress.PackSeed
import com.shadowmonarchbooks.dayloop.data.progress.ProgressRepository
import com.shadowmonarchbooks.dayloop.data.progress.ProfileEntity
import com.shadowmonarchbooks.dayloop.data.progress.StepStateEntity
import com.shadowmonarchbooks.dayloop.progress.CalendarSpan
import com.shadowmonarchbooks.dayloop.progress.Clock
import com.shadowmonarchbooks.dayloop.progress.ProgressLogic
import com.shadowmonarchbooks.dayloop.progress.StepKey
import com.shadowmonarchbooks.dayloop.progress.StepMark
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** UI projection of a Room profile row. */
data class ProfileUi(
    val id: Long,
    val packId: String,
    val name: String,
    val clockDate: String,
    val contentVersion: Int,
)

/**
 * Everything the screens render: the pack registry merged with the active
 * profile's progress. `currentDate` is the persisted in-game clock (End-Day),
 * falling back to the first authored day until a profile exists.
 */
data class DayloopUiState(
    val packs: List<LoadedPack> = emptyList(),
    val selectedSlug: String? = null,
    val profiles: List<ProfileUi> = emptyList(),
    val activeProfile: ProfileUi? = null,
    val marks: Map<StepKey, StepMark> = emptyMap(),
    /** Saved marks whose (date, index) no longer resolves in current content. */
    val orphans: Set<StepKey> = emptySet(),
) {
    val selected: LoadedPack? get() = packs.firstOrNull { it.slug == selectedSlug }

    val currentDate: String?
        get() = activeProfile?.clockDate ?: selected?.sortedDates?.firstOrNull()

    val calendarSpan: CalendarSpan?
        get() = selected?.pack?.calendar?.let {
            CalendarSpan(it.startDate, it.endDate, it.nonPlayableDates.toSet())
        }

    /** End-Day availability: false at the end of the pack's calendar. */
    fun hasNextDay(): Boolean {
        val date = currentDate ?: return false
        return calendarSpan?.let { Clock.next(it, date) } != null
    }

    /** Reroll availability: false at the start of the pack's calendar. */
    fun hasPreviousDay(): Boolean {
        val date = currentDate ?: return false
        return calendarSpan?.let { Clock.previous(it, date) } != null
    }

    fun markAt(date: String, index: Int): StepMark? = marks[StepKey(date, index)]
}

private fun ProfileEntity.toUi() = ProfileUi(id, packId, name, clockDate, contentVersion)

/**
 * Merges the read-only pack registry (PackStore) with persisted progress
 * (ProgressRepository) into one StateFlow; all mutations go through the
 * repository.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DayloopViewModel @Inject constructor(
    private val store: PackStore,
    private val repo: ProgressRepository,
) : ViewModel() {

    val state: StateFlow<DayloopUiState> = store.state
        .flatMapLatest { packs ->
            val slug = packs.selectedSlug
            val pack = packs.packs.firstOrNull { it.slug == slug }
            if (slug == null || pack == null) {
                flowOf(DayloopUiState(packs = packs.packs))
            } else {
                combine(
                    repo.profilesFor(slug),
                    repo.activeProfileId(slug),
                ) { profiles, activeId -> profiles to activeId }
                    .flatMapLatest { (profiles, activeId) ->
                        val active = profiles.firstOrNull { it.id == activeId }
                            ?: profiles.firstOrNull()
                        val marksFlow = active?.let { repo.marksFor(it.id) }
                            ?: flowOf(emptyList())
                        marksFlow.map { rows ->
                            buildUiState(packs.packs, slug, pack, profiles, active, rows)
                        }
                    }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DayloopUiState())

    init {
        // First-run bootstrap: one profile per installed pack, valid active pointer.
        viewModelScope.launch {
            repo.ensureProfiles(
                store.state.value.packs.map { loaded ->
                    PackSeed(loaded.slug, loaded.pack.contentVersion, spanOf(loaded))
                },
            )
        }
    }

    // ---- Selection ----

    fun selectPack(slug: String) = store.select(slug)

    // ---- End-Day clock ----

    fun endDay() = withActivePack { id, seed -> repo.endDay(id, seed) }

    fun rerollDay() = withActivePack { id, seed -> repo.rerollDay(id, seed) }

    // ---- Step marks ----

    /** Toggle: applying the mark a step already carries clears it. */
    fun toggleMark(date: String, index: Int, mark: StepMark) = withActiveProfile { id ->
        val current = state.value.marks[StepKey(date, index)]
        repo.setMark(id, StepKey(date, index), if (current == mark) null else mark)
    }

    fun discardOrphans() = withActiveProfile { id ->
        repo.discardOrphans(id, state.value.orphans)
    }

    // ---- Profiles ----

    fun createProfile(name: String) = withSelectedSeed { seed ->
        repo.createProfile(seed, name.ifBlank { "Profile" })
    }

    fun renameProfile(id: Long, name: String) = viewModelScope.launch {
        repo.renameProfile(id, name.ifBlank { "Profile" })
    }

    fun switchProfile(id: Long) {
        val slug = state.value.selectedSlug ?: return
        viewModelScope.launch { repo.selectProfile(slug, id) }
    }

    fun deleteProfile(id: Long) = withSelectedSeed { seed ->
        repo.deleteProfile(id, seed)
    }

    fun resetProfile() = withActivePack { id, seed -> repo.resetProfile(id, seed) }

    // ---- Helpers ----

    private fun buildUiState(
        packs: List<LoadedPack>,
        slug: String,
        pack: LoadedPack,
        profiles: List<ProfileEntity>,
        active: ProfileEntity?,
        rows: List<StepStateEntity>,
    ): DayloopUiState {
        val marks = rows.mapNotNull { row ->
            StepMark.entries.firstOrNull { it.name == row.mark }
                ?.let { StepKey(row.date, row.stepIndex) to it }
        }.toMap()
        val stepCounts = pack.daysByDate.mapValues { (_, day) -> day.steps.size }
        return DayloopUiState(
            packs = packs,
            selectedSlug = slug,
            profiles = profiles.map { it.toUi() },
            activeProfile = active?.toUi(),
            marks = marks,
            orphans = if (active != null) ProgressLogic.orphans(marks, stepCounts) else emptySet(),
        )
    }

    private fun spanOf(pack: LoadedPack): CalendarSpan = with(pack.pack.calendar) {
        CalendarSpan(startDate, endDate, nonPlayableDates.toSet())
    }

    private fun withSelectedSeed(block: suspend (PackSeed) -> Unit) {
        val pack = state.value.selected ?: return
        viewModelScope.launch { block(PackSeed(pack.slug, pack.pack.contentVersion, spanOf(pack))) }
    }

    private fun withActiveProfile(block: suspend (Long) -> Unit) {
        val profile = state.value.activeProfile ?: return
        viewModelScope.launch { block(profile.id) }
    }

    private fun withActivePack(block: suspend (Long, PackSeed) -> Unit) {
        val pack = state.value.selected ?: return
        val profile = state.value.activeProfile ?: return
        val seed = PackSeed(pack.slug, pack.pack.contentVersion, spanOf(pack))
        viewModelScope.launch { block(profile.id, seed) }
    }
}
