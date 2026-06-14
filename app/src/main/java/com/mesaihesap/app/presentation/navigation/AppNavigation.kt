package com.mesaihesap.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.mesaihesap.app.presentation.calendar.CalendarScreen
import com.mesaihesap.app.presentation.calendar.DayDetailScreen
import com.mesaihesap.app.presentation.history.HistoryScreen
import com.mesaihesap.app.presentation.home.HomeScreen
import com.mesaihesap.app.presentation.settings.SalarySettingsScreen
import com.mesaihesap.app.presentation.settings.SettingsScreen
import com.mesaihesap.app.presentation.statistics.StatisticsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Statistics : Screen("statistics")
    object History : Screen("history")
    object Settings : Screen("settings")
    object DayDetail : Screen("day_detail/{date}") {
        fun createRoute(date: String) = "day_detail/$date"
    }
    object SalarySettings : Screen("salary_settings")
}

data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Ana Sayfa", Icons.Default.Home),
    BottomNavItem(Screen.Calendar, "Takvim", Icons.Default.CalendarMonth),
    BottomNavItem(Screen.Statistics, "İstatistik", Icons.Default.BarChart),
    BottomNavItem(Screen.History, "Geçmiş", Icons.Default.History),
    BottomNavItem(Screen.Settings, "Ayarlar", Icons.Default.Settings),
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { it.screen.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onNavigate = { navController.navigate(it) })
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(onDayClick = { date ->
                    navController.navigate(Screen.DayDetail.createRoute(date))
                })
            }
            composable(
                route = Screen.DayDetail.route,
                arguments = listOf(navArgument("date") { type = NavType.StringType })
            ) { backStackEntry ->
                DayDetailScreen(
                    date = backStackEntry.arguments?.getString("date") ?: "",
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Statistics.route) { StatisticsScreen() }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Settings.route) {
                SettingsScreen(onSalarySettingsClick = { navController.navigate(Screen.SalarySettings.route) })
            }
            composable(Screen.SalarySettings.route) {
                SalarySettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
