package com.shadowmonarchbooks.dayloop.tools.pack

import com.shadowmonarchbooks.dayloop.pack.PackLoader
import com.shadowmonarchbooks.dayloop.pack.schema.Routes
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertTrue

class MetaphorFullQuestCoverageAuditTest {

    @Test
    fun `quest - More's Task - Foreword and Prologue`() = assertQuest("More's Task: Foreword and Prologue")

    @Test
    fun `quest - More's Task Chapter One - Ordeal`() = assertQuest("More's Task Chapter One: Ordeal")

    @Test
    fun `quest - Pagan's Dilemma`() = assertQuest("Pagan's Dilemma")

    @Test
    fun `quest - Help the Hushed Honeybee`() = assertQuest("Help the Hushed Honeybee")

    @Test
    fun `quest - A Friend in Need`() = assertQuest("A Friend in Need")

    @Test
    fun `quest - A Bullish Embargo`() = assertQuest("A Bullish Embargo")

    @Test
    fun `quest - Man's Not-So Best Friend`() = assertQuest("Man's Not-So Best Friend")

    @Test
    fun `quest - Promising Returns`() = assertQuest("Promising Returns")

    @Test
    fun `quest - Providing a Spark`() = assertQuest("Providing a Spark")

    @Test
    fun `quest - The Old Castle Town Kidnapper`() = assertQuest("The Old Castle Town Kidnapper")

    @Test
    fun `quest - A Dagger, a Ring, and a Rake`() = assertQuest("A Dagger, a Ring, and a Rake")

    @Test
    fun `quest - Hatching a Plan`() = assertQuest("Hatching a Plan")

    @Test
    fun `quest - The New King of the Imps`() = assertQuest("The New King of the Imps")

    @Test
    fun `quest - Relic Search - Inventor's Bequest`() = assertQuest("Relic Search: Inventor's Bequest")

    @Test
    fun `quest - More's Task Chapter Two - Solitude`() = assertQuest("More's Task Chapter Two: Solitude")

    @Test
    fun `quest - More's Task Chapter Three - Drifting`() = assertQuest("More's Task Chapter Three: Drifting")

    @Test
    fun `quest - A Noble's Legacy`() = assertQuest("A Noble's Legacy")

    @Test
    fun `quest - A Haunted Heirloom`() = assertQuest("A Haunted Heirloom")

    @Test
    fun `quest - Skullduggery`() = assertQuest("Skullduggery")

    @Test
    fun `quest - The Queen of Cuisine - Heart`() = assertQuest("The Queen of Cuisine: Heart")

    @Test
    fun `quest - More's Task Chapter Four - Turmoil`() = assertQuest("More's Task Chapter Four: Turmoil")

    @Test
    fun `quest - Relic Search - Dregs of Destiny`() = assertQuest("Relic Search: Dregs of Destiny")

    @Test
    fun `quest - Soldier's Solace`() = assertQuest("Soldier's Solace")

    @Test
    fun `quest - Deeds and Diversions`() = assertQuest("Deeds and Diversions")

    @Test
    fun `quest - Efflorescent Youth`() = assertQuest("Efflorescent Youth")

    @Test
    fun `quest - Dental Distress`() = assertQuest("Dental Distress")

    @Test
    fun `quest - Obtain Sergeant Xanth's Key`() = assertQuest("Obtain Sergeant Xanth's Key")

    @Test
    fun `quest - The Chalice vs The Brew`() = assertQuest("The Chalice vs. The Brew")

    @Test
    fun `quest - Superior Scrimshaw`() = assertQuest("Superior Scrimshaw")

    @Test
    fun `quest - Grieving Ghost of the Goblet`() = assertQuest("Grieving Ghost of the Goblet")

    @Test
    fun `quest - Defeat the Coliseum Monster`() = assertQuest("Defeat the Coliseum Monster")

    @Test
    fun `quest - Relic Search - Youth's Folly`() = assertQuest("Relic Search: Youth's Folly")

    @Test
    fun `quest - More's Task Chapter Five - Resolve`() = assertQuest("More's Task Chapter Five: Resolve")

    @Test
    fun `quest - The Right to Rule`() = assertQuest("The Right to Rule")

    @Test
    fun `quest - The Price of Hope`() = assertQuest("The Price of Hope")

    @Test
    fun `quest - Peak Curiosity`() = assertQuest("Peak Curiosity")

    @Test
    fun `quest - The Greater One-Eyed Scoundrel`() = assertQuest("The Greater One-Eyed Scoundrel")

    @Test
    fun `quest - A Guiding Gift`() = assertQuest("A Guiding Gift")

    @Test
    fun `quest - Save the Mourning Snakes`() = assertQuest("Save the Mourning Snakes")

    @Test
    fun `quest - The Trial of Malnova`() = assertQuest("The Trial of Malnova")

    @Test
    fun `quest - Relic Search - Engineer's Destiny`() = assertQuest("Relic Search: Engineer's Destiny")

    @Test
    fun `quest - More's Task Chapter Six - The End`() = assertQuest("More's Task Chapter Six: The End")

    @Test
    fun `quest - Wayward Shepherd`() = assertQuest("Wayward Shepherd")

    @Test
    fun `quest - Relic Search - Bitter Memories`() = assertQuest("Relic Search: Bitter Memories")

    @Test
    fun `quest - The Fiend in the Frozen Forest`() = assertQuest("The Fiend in the Frozen Forest")

    @Test
    fun `quest - Proof of Power`() = assertQuest("Proof of Power")

    @Test
    fun `quest - Warmth in Winter`() = assertQuest("Warmth in Winter")

    @Test
    fun `quest - Defeat Milo`() = assertQuest("Defeat Milo")

    @Test
    fun `quest - A Rake's Last Wish`() = assertQuest("A Rake's Last Wish")

    @Test
    fun `quest - The Queen of Cuisine - Soul`() = assertQuest("The Queen of Cuisine: Soul")

    @Test
    fun `quest - The Edge of Glory`() = assertQuest("The Edge of Glory")

    @Test
    fun `quest - The Incarnate in the Woods`() = assertQuest("The Incarnate in the Woods")

    @Test
    fun `quest - The Apostles of the Apocalypse`() = assertQuest("The Apostles of the Apocalypse")

    @Test
    fun `quest - The Cockatrice in the Clouds`() = assertQuest("The Cockatrice in the Clouds")

    @Test
    fun `quest - Trial of the Dragon - Bygone Legacy`() = assertQuest("Trial of the Dragon: Bygone Legacy")

    @Test
    fun `quest - Deliver Hot Spring Water`() = assertQuest("Deliver Hot Spring Water")

    @Test
    fun `quest - Trial of the Dragon - Heroes' Rest`() = assertQuest("Trial of the Dragon: Heroes' Rest")

    @Test
    fun `quest - Trial of the Dragon - Mad Mischief`() = assertQuest("Trial of the Dragon: Mad Mischief")

    @Test
    fun `quest - A Brother's Mercy`() = assertQuest("A Brother's Mercy")

    @Test
    fun `quest - Relic Search - A Dream's Origin`() = assertQuest("Relic Search: A Dream's Origin")

    @Test
    fun `quest - Petty Thief`() = assertQuest("Petty Thief")

    @Test
    fun `quest - Trial of the Dragon - Essence of Power`() = assertQuest("Trial of the Dragon: Essence of Power")

    @Test
    fun `quest - Become Champion of the Coliseum`() = assertQuest("Become Champion of the Coliseum")

    @Test
    fun `quest - one alternate Charadrius key quest`() {
        val routeText = routeText()
        val alternates = listOf(
            "Obtain Maintenance Chief Ceiba's Key",
            "Obtain Master Sergeant Glechom's Key",
        )
        assertTrue(
            alternates.any { routeText.contains(it.normalized()) },
            "Missing both alternate Charadrius key quests",
        )
    }

    private fun assertQuest(title: String) {
        assertTrue(
            routeText().contains(title.normalized()),
            "Missing required side quest: $title",
        )
    }

    private fun routeText() = loadMetaphor().walkthroughs
        .filter { it.routeId == Routes.DEFAULT }
        .flatMap { it.file.days }
        .flatMap { it.steps }
        .joinToString("\n") { it.label }
        .normalized()

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
