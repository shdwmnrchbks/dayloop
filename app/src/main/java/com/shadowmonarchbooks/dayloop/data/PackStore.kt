package com.shadowmonarchbooks.dayloop.data

import android.content.Context
import android.content.res.AssetManager
import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Activity
import com.shadowmonarchbooks.dayloop.pack.schema.AnswerSheet
import com.shadowmonarchbooks.dayloop.pack.schema.Bond
import com.shadowmonarchbooks.dayloop.pack.schema.Deadline
import com.shadowmonarchbooks.dayloop.pack.schema.Day
import com.shadowmonarchbooks.dayloop.pack.schema.Pack
import com.shadowmonarchbooks.dayloop.pack.schema.RouteDef
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * One pack loaded from assets/<slug>/ — engine-neutral, fully label-driven.
 * Walkthrough days are keyed per route (docs/PLAN.md Phase 5): the pack's
 * declared routes, or the single implicit default route.
 */
data class LoadedPack(
    val slug: String,
    val pack: Pack,
    val bonds: List<Bond> = emptyList(),
    val deadlines: List<Deadline> = emptyList(),
    val activities: Map<String, Activity> = emptyMap(),
    val answersByDate: Map<String, AnswerSheet> = emptyMap(),
    /** routeId -> (ISO date -> authored day). */
    val daysByRoute: Map<String, Map<String, Day>> = emptyMap(),
) {
    /** Routes available for this pack, declared or implicit default. */
    val routes: List<RouteDef> by lazy { Routes.effective(pack) }

    fun day(routeId: String, date: String): Day? = daysByRoute[routeId]?.get(date)

    fun sortedDates(routeId: String): List<String> =
        daysByRoute[routeId]?.keys?.sorted().orEmpty()

    fun authoredMonths(routeId: String): List<String> =
        daysByRoute[routeId]?.keys?.map { it.take(7) }?.distinct()?.sorted().orEmpty()

    fun routeLabel(routeId: String): String =
        routes.firstOrNull { it.id == routeId }?.label ?: routeId
}

data class PacksState(
    val packs: List<LoadedPack> = emptyList(),
    val selectedSlug: String? = null,
    // The in-game clock moved to the persisted profile in Phase 3; this state
    // stays a read-only registry of loaded packs + selection.
) {
    val selected: LoadedPack? get() = packs.firstOrNull { it.slug == selectedSlug }
}

/**
 * Loads every bundled pack from assets and exposes pack selection. Asset
 * layout: content/packs/<slug>/... (see app build.gradle). Progress lives in
 * data/progress (Room + DataStore).
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
                val answers = if ("answers.json" in files) {
                    PackLoader.decodeAnswers(readAsset(assets, "$slug/answers.json"))
                        ?.answers?.associateBy { it.date } ?: emptyMap()
                } else {
                    emptyMap()
                }
                // walkthrough/*.json is the default route; walkthrough/<routeId>/*.json
                // are additional declared routes (docs/PLAN.md Phase 5).
                val daysByRoute = mutableMapOf<String, Map<String, Day>>()
                if ("walkthrough" in files) {
                    fun daysOf(routePath: String): Map<String, Day> = buildMap {
                        for (name in assets.list(routePath).orEmpty().sorted()) {
                            if (!name.endsWith(".json")) continue
                            val wt = PackLoader.decodeWalkthrough(readAsset(assets, "$routePath/$name"))
                            if (wt != null) {
                                for (day in wt.days) put(day.date, day)
                            }
                        }
                    }
                    daysByRoute[Routes.DEFAULT] = daysOf("$slug/walkthrough")
                    for (name in assets.list("$slug/walkthrough").orEmpty().sorted()) {
                        val inner = assets.list("$slug/walkthrough/$name").orEmpty()
                        if (inner.isNotEmpty()) {
                            daysByRoute[name] = daysOf("$slug/walkthrough/$name")
                        }
                    }
                }
                result += LoadedPack(slug, pack, bonds, deadlines, activities, answers, daysByRoute)
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
        _state.value = s.copy(selectedSlug = slug)
    }
}

private fun readAsset(assets: AssetManager, path: String): String =
    assets.open(path).bufferedReader().use { it.readText() }
