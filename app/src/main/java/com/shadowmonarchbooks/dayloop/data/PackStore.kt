package com.shadowmonarchbooks.dayloop.data

import android.content.Context
import android.content.res.AssetManager
import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Activity
import com.shadowmonarchbooks.dayloop.pack.schema.Bond
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.pack.schema.Pack
import com.shadowmonarchbooks.dayloop.pack.schema.WalkthroughFile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** One pack loaded from assets/<slug>/ — engine-neutral, fully label-driven. */
data class LoadedPack(
    val slug: String,
    val pack: Pack,
    val bonds: List<Bond> = emptyList(),
    val deadlines: List<Deadline> = emptyList(),
    val activities: Map<String, Activity> = emptyMap(),
    val walkthroughs: Map<String, WalkthroughFile> = emptyMap(),
) {
    val daysByDate: Map<String, Day> by lazy {
        walkthroughs.values.flatMap { it.days }.associateBy { it.date }
    }
    val sortedDates: List<String> by lazy { daysByDate.keys.sorted() }
    val authoredMonths: List<String> by lazy { walkthroughs.keys.sorted() }

    fun day(date: String): Day? = daysByDate[date]
}

data class PacksState(
    val packs: List<LoadedPack> = emptyList(),
    val selectedSlug: String? = null,
    /** In-memory in-game "today" — read-only phase; persistence arrives in Phase 3. */
    val currentDate: String? = null,
) {
    val selected: LoadedPack? get() = packs.firstOrNull { it.slug == selectedSlug }
}

/**
 * Loads every bundled pack from assets and exposes selection + the read-only
 * in-game clock. Asset layout: content/packs/<slug>/... (see app build.gradle).
 */
@Singleton
class PackStore @Inject constructor(@ApplicationContext context: Context) {

    private val _state = MutableStateFlow(PacksState())
    val state: StateFlow<PacksState> = _state.asStateFlow()

    init {
        val packs = loadAll(context)
        val first = packs.firstOrNull { it.slug == "p5r" } ?: packs.firstOrNull()
        _state.value = PacksState(
            packs = packs,
            selectedSlug = first?.slug,
            currentDate = first?.sortedDates?.firstOrNull(),
        )
    }

    private fun loadAll(context: Context): List<LoadedPack> {
        val assets = context.assets
        val result = mutableListOf<LoadedPack>()
        for (slug in assets.list("").orEmpty().sorted()) {
            val files = assets.list(slug).orEmpty().toSet()
            if ("pack.json" !in files) continue
            try {
                val pack = PackLoader.decodePack(readAsset(assets, "$slug/pack.json")) ?: continue
                val bonds = if ("confidants.json" in files) {
                    PackLoader.decodeBonds(readAsset(assets, "$slug/confidants.json"))?.bonds.orEmpty()
                } else {
                    emptyList()
                }
                val deadlines = if ("deadlines.json" in files) {
                    PackLoader.decodeDeadlines(readAsset(assets, "$slug/deadlines.json"))?.deadlines.orEmpty()
                } else {
                    emptyList()
                }
                val activities = if ("activities.json" in files) {
                    PackLoader.decodeActivities(readAsset(assets, "$slug/activities.json"))
                        ?.activities?.associateBy { it.id } ?: emptyMap()
                } else {
                    emptyMap()
                }
                val walkthroughs = buildMap {
                    if ("walkthrough" in files) {
                        for (name in assets.list("$slug/walkthrough").orEmpty().sorted()) {
                            if (!name.endsWith(".json")) continue
                            val wt = PackLoader.decodeWalkthrough(readAsset(assets, "$slug/walkthrough/$name"))
                            if (wt != null) put(wt.month, wt)
                        }
                    }
                }
                result += LoadedPack(slug, pack, bonds, deadlines, activities, walkthroughs)
            } catch (_: Exception) {
                // A broken pack must never take the app down; lint guards content quality.
                continue
            }
        }
        return result
    }

    fun select(slug: String) {
        val s = _state.value
        if (s.selectedSlug == slug) return
        val pack = s.packs.firstOrNull { it.slug == slug }
        _state.value = s.copy(selectedSlug = slug, currentDate = pack?.sortedDates?.firstOrNull())
    }

    /** Read-only day browsing; the End-Day clock persists in Phase 3. */
    fun moveCurrent(delta: Int) {
        val s = _state.value
        val dates = s.selected?.sortedDates ?: return
        val idx = dates.indexOf(s.currentDate)
        if (idx == -1) return
        _state.value = s.copy(currentDate = dates[(idx + delta).coerceIn(dates.indices)])
    }
}

private fun readAsset(assets: AssetManager, path: String): String =
    assets.open(path).bufferedReader().use { it.readText() }
