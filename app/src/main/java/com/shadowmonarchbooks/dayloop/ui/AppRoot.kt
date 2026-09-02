package com.shadowmonarchbooks.dayloop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shadowmonarchbooks.dayloop.data.LoadedPack
import com.shadowmonarchbooks.dayloop.ui.achievements.AchievementsScreen
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
private val TopLevelRoutes = setOf("today", "calendar", "achievements", "bonds", "deadlines", "answers")

/** One bottom-bar entry; the list is derived from the active pack (Phase 8). */
private data class TopTab(val route: String, val label: String, val icon: ImageVector)

/**
 * Tabs the active pack earns. Achievements is a first-class tracker for every
 * pack; packs without achievement data show an explicit empty state rather
 * than falling back to the old Activities browser. Activities stay reachable
 * through authored step references and Search, where they have context.
 */
private fun topLevelTabs(pack: LoadedPack?): List<TopTab> = buildList {
    add(TopTab("today", "Today", Icons.Filled.Home))
    add(TopTab("calendar", "Calendar", Icons.Filled.DateRange))
    add(TopTab("achievements", "Achievements", Icons.Filled.Star))
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
    val tabs = remember(pack) { topLevelTabs(pack) }
    val skin = LocalSkin.current

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
                    title = bannerTitle(route, tabs, pack),
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
                    tabs.forEach { tab ->
                        val selected = route == tab.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = { nav.navigate(tab.route) { launchSingleTop = true } },
                            icon = { TopTabIcon(tab, selected) },
                            label = null,
                            alwaysShowLabel = false,
                            colors = if (skin.motif == "masks") {
                                NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                            } else {
                                NavigationBarItemDefaults.colors()
                            },
                        )
                    }
                }
            }
        },
    ) { padding ->
        // Skin motion grammar (docs/ROADMAP-v3.md Phase 12): the pack's
        // `theme.motion` token drives screen transitions; null = engine
        // defaults; the system remove-animations setting collapses everything.
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
                    onOpenAchievements = { nav.navigate("achievements") { launchSingleTop = true } },
                    onOpenActivity = { ref -> nav.navigate("activity/$ref") },
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
            composable("achievements") {
                AchievementsScreen(vm = vm)
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
            // Activities are still a valid detail/browse destination for search
            // and authored activity references, but no longer occupy top-level UI.
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

@Composable
private fun TopTabIcon(tab: TopTab, selected: Boolean) {
    val skin = LocalSkin.current
    if (skin.motif == "masks" && selected) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, skin.shapes.chip)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                tab.icon,
                contentDescription = tab.label,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    } else {
        Icon(tab.icon, contentDescription = tab.label)
    }
}

/**
 * The header identifies the current place while pack artwork supplies the
 * visual identity. Top-level names moved here from the icon-only navigation
 * bar; detail destinations get an equally useful, engine-neutral title.
 */
private fun bannerTitle(route: String?, tabs: List<TopTab>, pack: LoadedPack?): String {
    tabs.firstOrNull { it.route == route }?.let { return it.label }
    return when (route?.substringBefore('/')) {
        "day" -> "Day"
        "bond" -> pack?.pack?.labels?.bond ?: "Bond"
        "activity", "activities" -> "Activities"
        "search" -> "Search"
        "settings" -> "Settings"
        "media" -> "Media"
        else -> pack?.pack?.title ?: "dayloop"
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
    val skin = LocalSkin.current
    val cutlineHeader = skin.motif == "masks"
    // Keep system information on a calm surface instead of extending the
    // pack's decorated banner behind the status-bar icons.
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars),
        )
        TopAppBar(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .skinDecor("header"),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                navigationIconContentColor = if (cutlineHeader) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                titleContentColor = if (cutlineHeader) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            ),
            windowInsets = WindowInsets(0, 0, 0, 0),
            navigationIcon = {
                if (canGoBack) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            },
            title = {
                Text(
                    text = if (cutlineHeader) skin.cased(title, "display") else title,
                    style = if (cutlineHeader) {
                        MaterialTheme.typography.displaySmall.copy(fontSize = 25.sp, lineHeight = 28.sp)
                    } else {
                        MaterialTheme.typography.titleMedium
                    },
                    maxLines = 1,
                )
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
}
