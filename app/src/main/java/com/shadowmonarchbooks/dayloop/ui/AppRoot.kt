package com.shadowmonarchbooks.dayloop.ui

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shadowmonarchbooks.dayloop.ui.answers.AnswersScreen
import com.shadowmonarchbooks.dayloop.ui.bonds.BondDetailScreen
import com.shadowmonarchbooks.dayloop.ui.bonds.BondsScreen
import com.shadowmonarchbooks.dayloop.ui.day.DayScreen
import com.shadowmonarchbooks.dayloop.ui.deadlines.DeadlinesScreen
import com.shadowmonarchbooks.dayloop.ui.month.MonthScreen
import com.shadowmonarchbooks.dayloop.ui.onboarding.OnboardingScreen
import com.shadowmonarchbooks.dayloop.ui.search.SearchScreen
import com.shadowmonarchbooks.dayloop.ui.settings.SettingsScreen
import com.shadowmonarchbooks.dayloop.ui.today.TodayScreen

private val TopLevelRoutes = setOf("today", "calendar", "bonds", "deadlines", "answers")

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
                    NavigationBarItem(
                        selected = route == "today",
                        onClick = { nav.navigate("today") { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text("Today") },
                    )
                    NavigationBarItem(
                        selected = route == "calendar",
                        onClick = { nav.navigate("calendar") { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                        label = { Text("Calendar") },
                    )
                    NavigationBarItem(
                        selected = route == "bonds",
                        onClick = { nav.navigate("bonds") { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        label = { Text(pack?.pack?.labels?.bond?.let { it + "s" } ?: "Bonds") },
                    )
                    NavigationBarItem(
                        selected = route == "deadlines",
                        onClick = { nav.navigate("deadlines") { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Warning, contentDescription = null) },
                        label = { Text("Deadlines") },
                    )
                    NavigationBarItem(
                        selected = route == "answers",
                        onClick = { nav.navigate("answers") { launchSingleTop = true } },
                        icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                        label = { Text("Answers") },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = startDestination,
            modifier = Modifier.padding(padding),
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    vm = vm,
                    onStart = {
                        nav.navigate("today") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    },
                )
            }
            composable("today") {
                TodayScreen(
                    vm = vm,
                    onOpenDay = { date -> nav.navigate("day/$date") },
                    onOpenCalendar = { nav.navigate("calendar") { launchSingleTop = true } },
                    onOpenSettings = { nav.navigate("settings") { launchSingleTop = true } },
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
            composable("search") {
                SearchScreen(
                    vm = vm,
                    onOpenDay = { date -> nav.navigate("day/$date") },
                    onOpenBond = { id -> nav.navigate("bond/$id") },
                )
            }
            composable("settings") {
                SettingsScreen(vm = vm)
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
    TopAppBar(
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
