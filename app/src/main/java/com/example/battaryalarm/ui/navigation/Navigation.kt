package com.example.battaryalarm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.battaryalarm.ui.screens.CreateAlarmScreen
import com.example.battaryalarm.ui.screens.MainScreen
import com.example.battaryalarm.ui.viewmodel.AlarmViewModel

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object CreateAlarm : Screen("create_alarm")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: AlarmViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateAlarm.route)
                }
            )
        }

        composable(Screen.CreateAlarm.route) {
            CreateAlarmScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
