package com.altodemo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.altodemo.app.mission.MissionView
import com.altodemo.app.ui.theme.AltoDemoTheme
import com.altodemo.app.util.AltoDemoApplication
import com.altodemo.app.navigation.Navigation
import com.altodemo.app.navigation.NavigationEvent
import com.altodemo.app.navigation.NavigationEventReceiver
import com.altodemo.app.vibe.ChangeVibeView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationReceiver: NavigationEventReceiver

    private val altoDemoApplication by lazy { application as AltoDemoApplication }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        altoDemoApplication.setFileExtractionCallback(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val navController = rememberNavController()
            AltoDemoTheme {
                Surface {
                    CollectNavigationEvents(navController = navController)
                    NavHost(
                        navController = navController,
                        startDestination = Navigation.Mission
                    ) {
                        composable(Navigation.Mission) { MissionView() }
                        composable(Navigation.ChangeVibe) { ChangeVibeView() }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        altoDemoApplication.clearFileExtractionCallback()
    }

    @Composable
    private fun CollectNavigationEvents(navController: NavHostController) {
        val scope = rememberCoroutineScope()
        LaunchedEffect(key1 = Unit) {
            scope.launch {
                navigationReceiver().collect { event ->
                    when (event) {
                        is NavigationEvent.PopUpTo ->
                            navController.popBackStack(
                                route = event.destination,
                                inclusive = false
                            )
                        is NavigationEvent.Navigate ->
                            navController.navigate(
                                route = event.destination
                            )
                        is NavigationEvent.NavigateUp ->
                            navController.popBackStack()
                    }
                }
            }
        }
    }
}
