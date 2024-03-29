package com.codeskraps.binance.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codeskraps.feature.symbol.SymbolViewModel
import com.codeskraps.feature.symbol.components.SymbolScreen
import com.codeskraps.core.domain.navigation.Screen
import com.codeskraps.binance.ui.components.AccountTradeScreen
import com.codeskraps.binance.ui.components.SetUpScreen
import com.codeskraps.binance.ui.mvi.MainActivityEvent
import com.codeskraps.core.domain.theme.BinanceTheme
import com.codeskraps.feature.settings.SettingsViewModel
import com.codeskraps.feature.settings.components.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BinanceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val mainActivityViewModel = hiltViewModel<MainActivityViewModel>()
                    val mainActivityState by mainActivityViewModel.state.collectAsStateWithLifecycle()

                    LifecycleResumeEffect(Unit) {
                        mainActivityViewModel.state.handleEvent(MainActivityEvent.Resume)
                        onPauseOrDispose {}
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Screen.AccountTrade.route
                    ) {
                        if (mainActivityState.hasApiKey) {
                            composable(Screen.AccountTrade.route) {
                                AccountTradeScreen {
                                    navController.navigate(it)
                                }
                            }
                        } else {
                            composable(Screen.SetUp.route) {
                                SetUpScreen {
                                    navController.navigate(it.route)
                                }
                            }
                        }

                        composable(
                            route = Screen.Symbol.route
                        ) {
                            val symbolViewModel = hiltViewModel<SymbolViewModel>()
                            val state by symbolViewModel.state.collectAsStateWithLifecycle()

                            SymbolScreen(
                                state = state,
                                handleEvent = symbolViewModel.state::handleEvent
                            )
                        }

                        composable(
                            route = Screen.Setting.route
                        ) {
                            val settingsViewModel = hiltViewModel<SettingsViewModel>()
                            val state by settingsViewModel.state.collectAsStateWithLifecycle()

                            SettingsScreen(
                                state = state,
                                handleEvent = settingsViewModel.state::handleEvent
                            ) {
                                mainActivityViewModel.state.handleEvent(MainActivityEvent.CheckApiKey)
                                navController.navigateUp()
                            }
                        }
                    }
                }
            }
        }
    }
}