package com.shadowmonarchbooks.dayloop.preview

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shadowmonarchbooks.dayloop.StartupShell
import com.shadowmonarchbooks.dayloop.ui.theme.DayloopTheme

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@Preview(
    name = "Light",
    group = "Phase 17 cold start",
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark",
    group = "Phase 17 cold start",
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
private annotation class Phase17LightDarkPreview

@Composable
private fun ColdStartFixture(skinId: String) {
    val pack = Phase17PreviewFixtures.coldStarts.first { it.skinId == skinId }.pack
    DayloopTheme(pack = pack) {
        StartupShell()
    }
}

@Phase17LightDarkPreview
@Composable
private fun EngineColdStartPreview() = ColdStartFixture("engine")

@Phase17LightDarkPreview
@Composable
private fun MasksColdStartPreview() = ColdStartFixture("masks")

@Phase17LightDarkPreview
@Composable
private fun MoonColdStartPreview() = ColdStartFixture("moon")

@Phase17LightDarkPreview
@Composable
private fun CrownColdStartPreview() = ColdStartFixture("crown")
