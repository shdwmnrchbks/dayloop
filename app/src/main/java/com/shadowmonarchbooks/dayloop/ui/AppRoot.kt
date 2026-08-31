package com.shadowmonarchbooks.dayloop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.ui.activities.ActivitiesScreen
import com.shadowmonarchbooks.dayloop.ui.activities.ActivityDetailScreen
import com.shadowmonarchbooks.dayloop.ui.answers.AnswersScreen
import com.shadowmonarchbooks.dayloop.ui.bonds.BondDetailScreen
import com.shadowmonarchbooks.dayloop.ui.bonds.BondsScreen
import com.shadowmonarchbooks.dayloop.ui.day.DayScreen
import com.shadowmonarchbooks.dayloop.ui.deadlines.DeadlinesScreen
import com.shadowmonarchbooks.dayloop.ui.media.MediaScreen
import com.shadowmonarchbooks.dayloop.ui.month.MonthScreen
import com.shadowmonarchbooks.dayloop.ui.onboarding.OnboardingScreen
import com.shadowmonarchbooks.dayloop.ui.search.SearchScreen
import com.shadowmonarchbooks.dayloop.ui.settings.SettingsScreen
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkin
import com.shadowmonarchbooks.dayloop.ui.skin.LocalSkinFx
import com.shadowmonarchbooks.dayloop.ui.skin.navMotion
import com.shadowmonarchbooks.dayloop.ui.skin.rememberAnimationsDisabled
import com.shadowmonarchbooks.dayloop.ui.skin.skinDecor
import com.shadowmonarchbooks.dayloop.ui.today.TodayScreen

/** Every top-level destination stays registered, whatever the active pack ships. */
private val TopLevelRoutes = setOf("today", "calendar", "bonds", "deadlines", "answers")

/** One bottom-bar entry; the list is derived from the active pack (Phase 8). */
private data class TopTab(val route: String, val label: String, val icon: ImageVector)

/**
 * Tabs the active pack earns (docs/ROADMAP-v2.md Phase 8): Bonds/Deadlines
 * appear when the pack ships their files, the Answers tab only when the pack
 * declares `capabilities.answers` (packlint guarantees the data exists).
 * Tab count and order are pack data, never hardcoded; a null pack falls back
 * to the full set so navigation never strands.
 */
private fun topLevelTabs(pack: LoadedPack?): List<TopTab> = buildList {
    add(TopTab("today", "Today", Icons.Filled.Home))
    add(TopTab("calendar", "Calendar", Icons.Filled.DateRange))
    if (pack == null || pack.hasBondsFile) {
        add(TopTab("bonds", pack?.pack?.labels?.bond?.let { it + "s" } ?: "Bonds", Icons.Filled.Person))
    }
    if (pack == null || pack.hasDeadlinesFile) {
        add(TopTab("deadlines", "Deadlines", Icons.Filled.Warning))
    }
    if (pack == null || pack.pack.capabilities.answers) {
        add(TopTab("answers", "Answers", Icons.Filled.Info))
    }
}

@Composable
fun AppRoot(vm: DayloopViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()

    // Cold start: wait for the persisted pack selection instead of flashing
    // the onboarding grid at returning users (docs/ROADMAP-v2.md Phase 7).
    if (!state.selectionReady) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route
    val pack = state.selected

    // Fixed for the session once selection is ready: installs with no persisted
    // choice start on the picker, everyone else lands on Today.
    val startDestination = remember(state.selectionReady) {
        if (state.selectedSlug == null) "onboarding" else "today"
    }

    // The feedback layer (docs/ROADMAP-v3.md Phase 16) is app-wide: mark
    // buttons, End-Day, and the perfect-day splash reach it through
    // [LocalSkinFx]; the widget never touches it, so it can never sound there.
    CompositionLocalProvider(LocalSkinFx provides vm.skinFx) {
        Scaffold(
        topBar = {
            if (route != "onboarding") {
                DayloopTopBar(
                    title = pack?.pack?.title ?: "dayloop",
                    canGoBack = route != null && route !in TopLevelRoutes,
                    onBack = { nav.popBackStack() },
                    onOpenSearch = { nav.navigate("search") { launchSingleTop = true } },
                    onOpenSettings = { nav.navigate("settings") { launchSingleTop = true } },
                )
            }
        },
        bottomBar = {
            if (route in TopLevelRoutes) {
                NavigationBar {
                    topLevelTabs(pack).forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = { nav.navigate(tab.route) { launchSingleTop = true } },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        // Skin motion grammar (docs/ROADMAP-v3.md Phase 12): the pack's
        // `theme.motion` token drives screen transitions; null = engine
        // defaults; the system remove-animations setting collapses everything.
        val skin = LocalSkin.current
        val animationsDisabled = rememberAnimationsDisabled()
        val motion = remember(skin, animationsDisabled) { skin.navMotion(animationsDisabled) }
        NavHost(
            navController = nav,
            startDestination = startDestination,
            modifier = Modifier.padding(padding),
            enterTransition = { motion.enter },
            exitTransition = { motion.exit },
            popEnterTransition = { motion.popEnter },
            popExitTransition = { motion.popExit },
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    vm = vm,
                    onStart = {
                        nav.navigate("today") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    },
                    // Re-entered from Settings (a game is already active):
                    // the back arrow pops back instead of stranding the user.
                    onCancel = if (state.selectedSlug != null) {
                        { nav.popBackStack() }
                    } else {
                        null
                    },
                )
            }
            composable("today") {
                TodayScreen(
                    vm = vm,
                    onOpenDay = { date -> nav.navigate("day/$date") },
                    onOpenCalendar = { nav.navigate("calendar") { launchSingleTop = true } },
                    onOpenSettings = { nav.navigate("settings") { launchSingleTop = true } },
                    onOpenActivities = { nav.navigate("activities") { launchSingleTop = true } },
                    // The Today answer-sheet card taps through to the full
                    // Answers tab (exam answers now surface on the tracker).
                    onOpenAnswers = { nav.navigate("answers") { launchSingleTop = true } },
                )
            }
            composable("day/{date}") { entry ->
                DayScreen(
                    date = entry.arguments?.getString("date").orEmpty(),
                    vm = vm,
                    onOpenDay = { next ->
                        // Browse authored days in place: replace the current
                        // day entry so Back returns to the originating tab.
                        nav.navigate("day/$next") {
                            popUpTo("day/{date}") { inclusive = true }
                        }
                    },
                    onOpenAnswers = { nav.navigate("answers") { launchSingleTop = true } },
                    onOpenActivity = { ref -> nav.navigate("activity/$ref") },
                )
            }
            composable("calendar") {
                MonthScreen(vm = vm, onOpenDay = { date -> nav.navigate("day/$date") })
            }
            composable("bonds") {
                BondsScreen(
                    pack = pack,
                    onOpenBond = { id -> nav.navigate("bond/$id") },
                )
            }
            composable("bond/{bondId}") { entry ->
                BondDetailScreen(
                    bondId = entry.arguments?.getString("bondId").orEmpty(),
                    pack = pack,
                )
            }
            composable("deadlines") {
                DeadlinesScreen(vm = vm)
            }
            composable("answers") {
                AnswersScreen(vm = vm, onOpenDay = { date -> nav.navigate("day/$date") })
            }
            composable("activities") {
                ActivitiesScreen(
                    vm = vm,
                    onOpenActivity = { id -> nav.navigate("activity/$id") },
                )
            }
            composable("activity/{activityId}") { entry ->
                ActivityDetailScreen(
                    activityId = entry.arguments?.getString("activityId").orEmpty(),
                    vm = vm,
                )
            }
            composable("search") {
                SearchScreen(
                    vm = vm,
                    onOpenDay = { date -> nav.navigate("day/$date") },
                    onOpenBond = { id -> nav.navigate("bond/$id") },
                    onOpenActivity = { id -> nav.navigate("activity/$id") },
                    onOpenDeadlines = { nav.navigate("deadlines") { launchSingleTop = true } },
                )
            }
            composable("settings") {
                SettingsScreen(
                    vm = vm,
                    // One game picker in the whole app: Settings redirects to
                    // the first-run carousel (docs/ROADMAP-v3.md Phase 11).
                    onSwitchGame = { nav.navigate("onboarding") { launchSingleTop = true } },
                    onOpenMedia = { nav.navigate("media") { launchSingleTop = true } },
                )
            }
            composable("media") {
                MediaScreen(vm = vm)
            }
        }
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayloopTopBar(
    title: String,
    canGoBack: Boolean,
    onBack: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    // Engine look preserved: the bar keeps its surface fill (drawn on the
    // modifier so the skin's header decoration can sit behind it).
    TopAppBar(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .skinDecor("header"),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        navigationIcon = {
            if (canGoBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        title = {
            Text(title, style = MaterialTheme.typography.titleMedium)
        },
        actions = {
            IconButton(onClick = onOpenSearch) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        },
    )
}
