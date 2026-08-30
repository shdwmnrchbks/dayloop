package com.shadowmonarchbooks.dayloop.tools.pack

import kotlinx.serialization.json.Json
import com.shadowmonarchbooks.dayloop.tools.pack.schema.ActivitiesFile
import com.shadowmonarchbooks.dayloop.tools.pack.schema.BondsFile
import com.shadowmonarchbooks.dayloop.tools.pack.schema.DeadlinesFile
import com.shadowmonarchbooks.dayloop.tools.pack.schema.Pack
import com.shadowmonarchbooks.dayloop.tools.pack.schema.WalkthroughFile
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

class PackLoadResult(
    val pack: Pack?,
    val bonds: BondsFile?,
    val activities: ActivitiesFile?,
    val deadlines: DeadlinesFile?,
    /** month (e.g. "2016-04") -> parsed walkthrough file. */
    val walkthroughs: Map<String, WalkthroughFile>,
    val parseIssues: List<LintIssue>,
)

/** Loads and JSON-decodes a pack directory; structural rules live in PackLint. */
object PackLoader {

    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

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
            return try {
                json.decodeFromString(deserializer, jsonText)
            } catch (e: SerializationException) {
                issues += LintIssue(LintIssue.Severity.ERROR, what, "invalid JSON structure in ${file.fileName}: ${e.message?.lineSequence()?.firstOrNull()}")
                null
            } catch (e: IllegalArgumentException) {
                issues += LintIssue(LintIssue.Severity.ERROR, what, "invalid JSON in ${file.fileName}: ${e.message?.lineSequence()?.firstOrNull()}")
                null
            }
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

        val walkthroughs = mutableMapOf<String, WalkthroughFile>()
        val wtDir = packDir.resolve("walkthrough")
        if (wtDir.isDirectory()) {
            Files.list(wtDir).use { stream ->
                stream.filter { it.isRegularFile() && it.extension == "json" }.sorted().forEach { file ->
                    val monthKey = file.name.removeSuffix(".json")
                    val j = decode(file, "walkthrough/$monthKey.json")
                    issues += j.second
                    val parsed = parse(j.first, file, "walkthrough/$monthKey.json", WalkthroughFile.serializer())
                    if (parsed != null) walkthroughs[monthKey] = parsed
                }
            }
        }

        return PackLoadResult(pack, bonds, activities, deadlines, walkthroughs, issues)
    }
}
