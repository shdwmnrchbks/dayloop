package com.shadowmonarchbooks.dayloop.tools.packgen

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ArchiveSection(
    val title: String,
    val order: Int = 0,
    val sourceUrl: String = "",
    val blocks: List<Block> = emptyList(),
) {
    @Serializable
    data class Block(val type: String, val content: String? = null, val items: List<String>? = null)
}

/**
 * packgen CLI.
 *
 * usage: packgen --archive <guide-package-dir> --out <output-dir>
 *
 * Reads <archive>/sections/ (all .json files), reconstructs the day
 * timeline from date tokens, and writes <out>/p5r-candidates.json. Candidates
 * contain guide text fragments and must NEVER be committed (they land under build/).
 */
fun main(args: Array<String>) {
    var archive: Path? = null
    var out: Path? = null
    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--archive" -> { archive = Path.of(args[i + 1]); i += 2 }
            "--out" -> { out = Path.of(args[i + 1]); i += 2 }
            else -> { System.err.println("unknown argument: ${args[i]}"); kotlin.system.exitProcess(2) }
        }
    }
    val archiveDir = archive ?: run { System.err.println("missing --archive"); kotlin.system.exitProcess(2) }
    val outDir = out ?: run { System.err.println("missing --out"); kotlin.system.exitProcess(2) }
    val sectionsDir = archiveDir.resolve("sections")
    if (!Files.isDirectory(sectionsDir)) {
        System.err.println("no sections/ under $archiveDir")
        kotlin.system.exitProcess(2)
    }

    val json = Json { ignoreUnknownKeys = true }
    val sections = mutableListOf<ArchiveSection>()
    Files.list(sectionsDir).use { stream ->
        stream.filter { it.isRegularFile() && it.name.endsWith(".json") }
            .sorted()
            .forEach { file ->
                try {
                    sections += json.decodeFromString(ArchiveSection.serializer(), file.readText())
                } catch (e: Exception) {
                    System.err.println("WARN: skipped ${file.name}: ${e.message?.lineSequence()?.firstOrNull()}")
                }
            }
    }
    sections.sortBy { it.order }

    val inputs = sections.map { s ->
        val text = s.blocks.joinToString("\n") { b ->
            when (b.type) {
                "text" -> b.content ?: ""
                "list" -> b.items?.joinToString("\n") ?: ""
                else -> ""
            }
        }
        Extractor.SectionInput(title = s.title, text = text)
    }

    val (days, report) = Extractor.extract(inputs)
    val guideUrl = sections.firstOrNull()?.sourceUrl ?: ""
    val result = Candidates(guideUrl = guideUrl, days = days, report = report)

    Files.createDirectories(outDir)
    val outFile = outDir.resolve("p5r-candidates.json")
    outFile.writeText(Json { prettyPrint = true }.encodeToString(result))

    println("packgen: read ${report.sectionsRead} sections, found ${report.datesFound} distinct dated days")
    println("packgen: weekday mismatches: ${report.weekdayMismatches.size}, review-needed fragments: ${report.reviewCount}")
    if (report.weekdayMismatches.isNotEmpty()) {
        report.weekdayMismatches.take(10).forEach { println("  MISMATCH $it") }
    }
    println("packgen: wrote $outFile")
}
