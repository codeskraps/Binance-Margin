package com.codeskraps.binance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codeskraps.feature.account.AccountViewModel
import com.codeskraps.feature.account.components.AccountScreen
import com.codeskraps.feature.trades.TradeViewModel
import com.codeskraps.feature.trades.components.TradeScreen
import com.codeskraps.binance.ui.theme.BinanceTheme
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
                    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
                    val navController = rememberNavController()

                    val accViewModel = hiltViewModel<AccountViewModel>()
                    val tradeViewModel = hiltViewModel<TradeViewModel>()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier.height(50.dp),
                            ) {
                                NavigationBarItem(
                                    selected = selectedTabIndex == 0,
                                    onClick = {
                                        selectedTabIndex = 0
                                        navController.navigate(Screen.Account.route) {
                                            popUpTo(navController.graph.id) {
                                                inclusive = false
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_account),
                                            contentDescription = null
                                        )
                                    })
                                NavigationBarItem(
                                    selected = selectedTabIndex == 1,
                                    onClick = {
                                        selectedTabIndex = 1
                                        navController.navigate(Screen.Trade.route) {
                                            popUpTo(navController.graph.id) {
                                                inclusive = false
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_trade),
                                            contentDescription = null
                                        )
                                    })
                            }
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Account.route
                        ) {
                            composable(
                                route = Screen.Account.route
                            ) {

                                val state by accViewModel.state.collectAsStateWithLifecycle()

                                AccountScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues),
                                    state = state,
                                    handleEvent = accViewModel.state::handleEvent
                                )
                            }
                            composable(
                                route = Screen.Trade.route
                            ) {
                                val state by tradeViewModel.state.collectAsStateWithLifecycle()

                                TradeScreen(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues),
                                    state = state,
                                    handleEvent = tradeViewModel.state::handleEvent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}