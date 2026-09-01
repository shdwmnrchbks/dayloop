package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.LintIssue
import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.theme.LauncherBadgeRules
import com.shadowmonarchbooks.dayloop.pack.theme.launcherBadgePath
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.isRegularFile

/** Phase 17c validation for the reserved `theme.art["launcherBadge"]` slot. */
object LauncherBadgeLint {
    fun run(packDir: Path): List<LintIssue> {
        val theme = PackLoader.load(packDir).pack?.theme ?: return emptyList()
        val rel = theme.launcherBadgePath() ?: return emptyList()
        val what = "theme.art['${LauncherBadgeRules.SLOT}']"
        val target = packDir.resolve(rel)
        val extension = rel.substringAfterLast('.').lowercase()
        val issues = mutableListOf<LintIssue>()

        when {
            rel.contains('\\') || rel.startsWith('/') || rel.split('/').contains("..") ->
                issues += err("$what must be a pack-relative path: '$rel'")
            !target.isRegularFile() ->
                issues += err("$what file not found: $rel")
            extension !in LauncherBadgeRules.EXTENSIONS ->
                issues += err("$what must be a ${LauncherBadgeRules.EXTENSIONS} file: $rel")
            else -> {
                val image = runCatching { ImageIO.read(target.toFile()) }.getOrNull()
                if (image == null) {
                    issues += err("$what is not a decodable PNG: $rel")
                } else {
                    if (image.width != image.height) {
                        issues += err("$what must be square; found ${image.width}x${image.height}: $rel")
                    }
                    if (image.width !in LauncherBadgeRules.MIN_PX..LauncherBadgeRules.MAX_PX ||
                        image.height !in LauncherBadgeRules.MIN_PX..LauncherBadgeRules.MAX_PX
                    ) {
                        issues += err(
                            "$what dimensions must be ${LauncherBadgeRules.MIN_PX}–${LauncherBadgeRules.MAX_PX}px; " +
                                "found ${image.width}x${image.height}: $rel",
                        )
                    }
                    if (Files.size(target) > MAX_BYTES) {
                        issues += err("$what exceeds ${MAX_BYTES / 1024} KB: $rel")
                    }
                }
            }
        }
        return issues
    }

    private fun err(message: String) = LintIssue(LintIssue.Severity.ERROR, "pack.json", message)

    private const val MAX_BYTES = 128L * 1024
}
