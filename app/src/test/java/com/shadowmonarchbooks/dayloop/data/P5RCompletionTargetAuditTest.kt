package com.shadowmonarchbooks.dayloop.data

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class P5RCompletionTargetAuditTest {

    private fun contentPacksDir(): Path? {
        var dir: Path? = Paths.get(System.getProperty("user.dir")).toAbsolutePath()
        repeat(5) {
            val candidate = dir?.resolve("content")?.resolve("packs")
            if (candidate != null && candidate.isDirectory()) return candidate
            dir = dir?.parent
        }
        return null
    }

    @Test
    fun `p5r full book catalog remains a route target and not the Bookworm threshold`() {
        val root = contentPacksDir() ?: return
        val p5r = PackLoader.load(root.resolve("p5r"))
        assertEquals(emptyList(), p5r.parseIssues)

        val target = p5r.deadlines?.deadlines.orEmpty()
            .single { it.id == "p5r.deadline.missable.books-complete" }

        assertEquals("routeTarget", target.kind)
        assertEquals("2017-01-25", target.date)
        assertTrue(target.label.contains("full 46-book catalog"))
        assertTrue(target.label.contains("Bookworm award requires 40"))

        val jan25 = p5r.walkthroughs
            .flatMap { it.file.days }
            .single { it.date == "2017-01-25" }
        val completionStep = jan25.steps.single { it.label.contains("Read every remaining book") }

        assertEquals("p5r.activity.book.chinese-sweets", completionStep.activityRef)
        assertTrue(completionStep.label.contains("all city locations are complete"))
    }
}
