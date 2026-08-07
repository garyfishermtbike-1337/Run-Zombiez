package com.rangerdie.runzombiez

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rangerdie.runzombiez.ui.MissionViewModel
import com.rangerdie.runzombiez.ui.screens.HelpScreen
import com.rangerdie.runzombiez.ui.screens.HomeScreen
import com.rangerdie.runzombiez.ui.screens.MissionScreen
import com.rangerdie.runzombiez.ui.theme.RunZombiezTheme

private const val ROUTE_HOME = "home"
private const val ROUTE_MISSION = "mission"
private const val ROUTE_HELP = "help"

/** First bundled mission for the MVP; more asset files can be added under assets/missions/. */
private const val FIRST_MISSION_ASSET = "outbreak_signal.json"

class MainActivity : ComponentActivity() {

    private val viewModel: MissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RunZombiezTheme {
                val navController = rememberNavController()
                RunZombiezNavHost(navController, viewModel)
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun RunZombiezNavHost(navController: NavHostController, viewModel: MissionViewModel) {
    val missionState by viewModel.missionState.collectAsState()

    NavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeScreen(
                onStartMission = {
                    viewModel.startMission(FIRST_MISSION_ASSET)
                    navController.navigate(ROUTE_MISSION)
                },
                onDemo = {
                    viewModel.startDemo()
                    navController.navigate(ROUTE_MISSION)
                },
                onStop = { viewModel.stopMission() },
                onHelp = { navController.navigate(ROUTE_HELP) }
            )
        }
        composable(ROUTE_MISSION) {
            MissionScreen(
                state = missionState,
                onStop = {
                    viewModel.stopMission()
                    navController.popBackStack(ROUTE_HOME, inclusive = false)
                },
                onReturnHome = {
                    navController.popBackStack(ROUTE_HOME, inclusive = false)
                }
            )
        }
        composable(ROUTE_HELP) {
            HelpScreen(onBack = { navController.popBackStack() })
        }
    }
}
