package com.shadowmonarchbooks.dayloop.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shadowmonarchbooks.dayloop.ui.bonds.BondDetailScreen
import com.shadowmonarchbooks.dayloop.ui.bonds.BondsScreen
import com.shadowmonarchbooks.dayloop.ui.day.DayScreen
import com.shadowmonarchbooks.dayloop.ui.deadlines.DeadlinesScreen
import com.shadowmonarchbooks.dayloop.ui.month.MonthScreen
import com.shadowmonarchbooks.dayloop.ui.today.TodayScreen

private val TopLevelRoutes = setOf("today", "calendar", "bonds", "deadlines")

@Composable
fun AppRoot(vm: DayloopViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route
    val pack = state.selected

    Scaffold(
        topBar = {
            DayloopTopBar(
                title = pack?.pack?.title ?: "dayloop",
                packs = state.packs.map { it.slug to it.pack.title },
                selectedSlug = state.selectedSlug,
                onSelect = vm::selectPack,
                canGoBack = route != null && route !in TopLevelRoutes,
                onBack = { nav.popBackStack() },
            )
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
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "today",
            modifier = Modifier.padding(padding),
        ) {
            composable("today") {
                TodayScreen(
                    vm = vm,
                    onOpenDay = { date -> nav.navigate("day/$date") },
                    onOpenCalendar = { nav.navigate("calendar") { launchSingleTop = true } },
                )
            }
            composable("day/{date}") { entry ->
                DayScreen(
                    date = entry.arguments?.getString("date").orEmpty(),
                    vm = vm,
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayloopTopBar(
    title: String,
    packs: List<Pair<String, String>>,
    selectedSlug: String?,
    onSelect: (String) -> Unit,
    canGoBack: Boolean,
    onBack: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    TopAppBar(
        navigationIcon = {
            if (canGoBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        title = {
            if (packs.size > 1) {
                Box {
                    TextButton(onClick = { menuOpen = true }) {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Switch pack")
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        packs.forEach { (slug, packTitle) ->
                            DropdownMenuItem(
                                text = { Text(if (slug == selectedSlug) "✓ $packTitle" else packTitle) },
                                onClick = {
                                    onSelect(slug)
                                    menuOpen = false
                                },
                            )
                        }
                    }
                }
            } else {
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
        },
    )
}
