package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertTrue

class MetaphorFullQuestCoverageAuditTest {

    @Test
    fun `100 percent route exposes every required side quest`() {
        val routeText = loadMetaphor().walkthroughs
            .filter { it.routeId == Routes.DEFAULT }
            .flatMap { it.file.days }
            .flatMap { it.steps }
            .joinToString("\n") { it.label }
            .normalized()

        val requiredSideQuests = listOf(
            "More's Task: Foreword and Prologue",
            "More's Task Chapter One: Ordeal",
            "Pagan's Dilemma",
            "Help the Hushed Honeybee",
            "A Friend in Need",
            "Bullish Embargo",
            "Man's Not-So Best Friend",
            "Promising Returns",
            "Providing a Spark",
            "The Old Castle Town Kidnapper",
            "A Dagger, a Ring, and a Rake",
            "Hatching a Plan",
            "The New King of the Imps",
            "Relic Search: Inventor's Bequest",
            "More's Task Chapter Two: Solitude",
            "More's Task Chapter Three: Drifting",
            "A Noble's Legacy",
            "A Haunted Heirloom",
            "Skullduggery",
            "The Queen of Cuisine: Heart",
            "More's Task Chapter Four: Turmoil",
            "Relic Search: Dregs of Destiny",
            "Soldier's Solace",
            "Deeds and Diversions",
            "Efflorescent Youth",
            "Dental Distress",
            "Obtain Sergeant Xanth's Key",
            "The Chalice vs. The Brew",
            "Superior Scrimshaw",
            "Grieving Ghost of the Goblet",
            "Defeat the Coliseum Monster",
            "Relic Search: Youth's Folly",
            "More's Task Chapter Five: Resolve",
            "The Right to Rule",
            "The Price of Hope",
            "Peak Curiosity",
            "The Greater One-Eyed Scoundrel",
            "A Guiding Gift",
            "Save the Mourning Snakes",
            "The Trial of Malnova",
            "Relic Search: Engineer's Destiny",
            "More's Task Chapter Six: The End",
            "Wayward Shepherd",
            "Relic Search: Bitter Memories",
            "The Fiend in the Frozen Forest",
            "Proof of Power",
            "Warmth in Winter",
            "Defeat Milo",
            "A Rake's Last Wish",
            "The Queen of Cuisine: Soul",
            "The Edge of Glory",
            "The Incarnate in the Woods",
            "The Apostles of the Apocalypse",
            "The Cockatrice in the Clouds",
            "Trial of the Dragon: Bygone Legacy",
            "Deliver Hot Spring Water",
            "Trial of the Dragon: Heroes' Rest",
            "Trial of the Dragon: Mad Mischief",
            "A Brother's Mercy",
            "Relic Search: A Dream's Origin",
            "Petty Thief",
            "Trial of the Dragon: Essence of Power",
            "Become Champion of the Coliseum",
        )

        val missing = requiredSideQuests.filterNot { routeText.contains(it.normalized()) }
        assertTrue(
            missing.isEmpty(),
            "The 100% route does not explicitly expose these required side quests: ${missing.joinToString()}",
        )

        // Charadrius awards completion credit for Xanth plus either Ceiba or Glechom.
        // Only one of the latter pair is needed for the 75/76 achievement requirement.
        val alternateKeyQuests = listOf(
            "Obtain Maintenance Chief Ceiba's Key",
            "Obtain Master Sergeant Glechom's Key",
        )
        assertTrue(
            alternateKeyQuests.any { routeText.contains(it.normalized()) },
            "The route must explicitly expose at least one of the two alternate Charadrius key quests",
        )
    }

    private fun String.normalized() =
        replace('’', '\'')
            .replace('‘', '\'')
            .replace('–', '-')
            .replace('—', '-')
            .lowercase()

    private fun loadMetaphor() = PackLoader.load(metaphorDir()).also { loaded ->
        assertTrue(loaded.parseIssues.isEmpty(), loaded.parseIssues.joinToString())
    }

    private fun metaphorDir(): Path {
        val candidates = listOf(
            Path.of("content", "packs", "metaphor"),
            Path.of("..", "..", "content", "packs", "metaphor"),
        )
        return candidates.firstOrNull { Files.isDirectory(it) }
            ?: error("Cannot locate content/packs/metaphor from ${Path.of("").toAbsolutePath()}")
    }
}
