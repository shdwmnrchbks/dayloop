package com.shadowmonarchbooks.dayloop.pack

import kotlinx.serialization.json.Json
import com.shadowmonarchbooks.dayloop.pack.schema.ActivitiesFile
import com.shadowmonarchbooks.dayloop.pack.schema.AnswersFile
import com.shadowmonarchbooks.dayloop.pack.schema.BondsFile
import com.shadowmonarchbooks.dayloop.pack.schema.DeadlinesFile
import com.shadowmonarchbooks.dayloop.pack.schema.MediaFile
import com.shadowmonarchbooks.dayloop.pack.schema.Pack
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import com.shadowmonarchbooks.dayloop.pack.schema.WalkthroughFile
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlinx.serialization.SerializationException

data class LintIssue(val severity: Severity, val location: String, val message: String) {
    enum class Severity { ERROR, WARN }
}

/**
 * One walkthrough month file: which route it belongs to, and where it lives.
 * The default route's files sit at the top level of the walkthrough folder;
 * additional routes get a subdirectory each, walkthrough/<routeId>/<month>.json.
 */
data class LoadedWalkthrough(
    val routeId: String,
    val month: String,
    /** Location label for lint output, relative to the pack dir. */
    val location: String,
    val file: WalkthroughFile,
)

class PackLoadResult(
    val pack: Pack?,
    val bonds: BondsFile?,
    val activities: ActivitiesFile?,
    val deadlines: DeadlinesFile?,
    /** Parsed walkthrough month files across all routes (docs/PLAN.md Phase 5). */
    val walkthroughs: List<LoadedWalkthrough>,
    val answers: AnswersFile?,
    /** Graphic manifest (docs/ROADMAP-v3.md Phase 11); null when the pack ships none. */
    val media: MediaFile?,
    val parseIssues: List<LintIssue>,
)

/**
 * Loads and JSON-decodes a pack directory; structural rules live in PackLint.
 *
 * Two entry points share one JSON configuration:
 *  - [load] reads a filesystem directory (lint tooling).
 *  - [decodeX] functions parse pre-read text (Android assets).
 */
object PackLoader {

    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    // --- String-based decoding (e.g. Android assets read the text, then call these) ---

    fun decodePack(jsonText: String): Pack? =
        runCatching { json.decodeFromString(Pack.serializer(), jsonText) }.getOrNull()

    fun decodeBonds(jsonText: String): BondsFile? =
        runCatching { json.decodeFromString(BondsFile.serializer(), jsonText) }.getOrNull()

    fun decodeActivities(jsonText: String): ActivitiesFile? =
        runCatching { json.decodeFromString(ActivitiesFile.serializer(), jsonText) }.getOrNull()

    fun decodeDeadlines(jsonText: String): DeadlinesFile? =
        runCatching { json.decodeFromString(DeadlinesFile.serializer(), jsonText) }.getOrNull()

    fun decodeAnswers(jsonText: String): AnswersFile? =
        runCatching { json.decodeFromString(AnswersFile.serializer(), jsonText) }.getOrNull()

    fun decodeMedia(jsonText: String): MediaFile? =
        runCatching { json.decodeFromString(MediaFile.serializer(), jsonText) }.getOrNull()

    fun decodeWalkthrough(jsonText: String): WalkthroughFile? =
        runCatching { json.decodeFromString(WalkthroughFile.serializer(), jsonText) }.getOrNull()

    /** Returns the first-line error message if [jsonText] fails to decode as [what]; null when valid. */
    fun <T> decodeError(jsonText: String, what: String, deserializer: kotlinx.serialization.DeserializationStrategy<T>): String? =
        try {
            json.decodeFromString(deserializer, jsonText)
            null
        } catch (e: SerializationException) {
            "invalid JSON structure in $what: ${e.message?.lineSequence()?.firstOrNull()}"
        } catch (e: IllegalArgumentException) {
            "invalid JSON in $what: ${e.message?.lineSequence()?.firstOrNull()}"
        }

    // --- Filesystem-based loading (lint tooling) ---

    fun load(packDir: Path): PackLoadResult {
        val issues = mutableListOf<LintIssue>()

        fun decode(file: Path?, what: String): Pair<String?, List<LintIssue>> {
            if (file == null || !file.isRegularFile()) return null to emptyList()
            return try {
                file.readText() to emptyList()
            } catch (e: Exception) {
                null to listOf(
                    LintIssue(LintIssue.Severity.ERROR, what, "cannot read ${file.fileName}: ${e.message}")
                )
            }
        }

        fun <T> parse(jsonText: String?, file: Path?, what: String, deserializer: kotlinx.serialization.DeserializationStrategy<T>): T? {
            if (jsonText == null || file == null) return null
            val error = decodeError(jsonText, what, deserializer)
            if (error != null) {
                issues += LintIssue(LintIssue.Severity.ERROR, what, error)
                return null
            }
            return json.decodeFromString(deserializer, jsonText)
        }

        val packFile = packDir.resolve("pack.json")
        val packJson = decode(packFile, "pack.json")
        issues += packJson.second
        val pack = parse(packJson.first, packFile, "pack.json", Pack.serializer())

        val bondsFile = packDir.resolve("confidants.json")
        val bondsJson = decode(bondsFile, "confidants.json")
        issues += bondsJson.second
        val bonds = parse(bondsJson.first, bondsFile, "confidants.json", BondsFile.serializer())

        val activitiesFile = packDir.resolve("activities.json")
        val activitiesJson = decode(activitiesFile, "activities.json")
        issues += activitiesJson.second
        val activities = parse(activitiesJson.first, activitiesFile, "activities.json", ActivitiesFile.serializer())

        val deadlinesFile = packDir.resolve("deadlines.json")
        val deadlinesJson = decode(deadlinesFile, "deadlines.json")
        issues += deadlinesJson.second
        val deadlines = parse(deadlinesJson.first, deadlinesFile, "deadlines.json", DeadlinesFile.serializer())

        val answersFile = packDir.resolve("answers.json")
        val answersJson = decode(answersFile, "answers.json")
        issues += answersJson.second
        val answers = parse(answersJson.first, answersFile, "answers.json", AnswersFile.serializer())

        val mediaFile = packDir.resolve("media.json")
        val mediaJson = decode(mediaFile, "media.json")
        issues += mediaJson.second
        val media = parse(mediaJson.first, mediaFile, "media.json", MediaFile.serializer())

        val walkthroughs = mutableListOf<LoadedWalkthrough>()
        fun parseWalkthrough(file: Path, routeId: String, monthKey: String, location: String) {
            val j = decode(file, location)
            issues += j.second
            val parsed = parse(j.first, file, location, WalkthroughFile.serializer())
            if (parsed != null) walkthroughs += LoadedWalkthrough(routeId, monthKey, location, parsed)
        }

        val wtDir = packDir.resolve("walkthrough")
        if (wtDir.isDirectory()) {
            Files.list(wtDir).use { top ->
                top.sorted().forEach { entry ->
                    when {
                        entry.isRegularFile() && entry.extension == "json" -> {
                            val monthKey = entry.name.removeSuffix(".json")
                            parseWalkthrough(entry, Routes.DEFAULT, monthKey, "walkthrough/$monthKey.json")
                        }
                        // Additional routes live one level deep: walkthrough/<routeId>/<month>.json
                        entry.isDirectory() -> {
                            val routeId = entry.name
                            Files.list(entry).use { inner ->
                                inner.filter { it.isRegularFile() && it.extension == "json" }.sorted().forEach { file ->
                                    val monthKey = file.name.removeSuffix(".json")
                                    parseWalkthrough(file, routeId, monthKey, "walkthrough/$routeId/$monthKey.json")
                                }
                            }
                        }
                    }
                }
            }
        }

        return PackLoadResult(pack, bonds, activities, deadlines, walkthroughs, answers, media, issues)
    }
}
