package com.codeskraps.binance.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.codeskraps.binance.R
import com.codeskraps.core.domain.navigation.Screen
import com.codeskraps.feature.account.AccountViewModel
import com.codeskraps.feature.account.components.AccountScreen
import com.codeskraps.feature.pnl.PnLViewModel
import com.codeskraps.feature.pnl.components.FinishTradesScreen
import com.codeskraps.feature.trades.TradeViewModel
import com.codeskraps.feature.trades.components.TradeScreen
import com.codeskraps.feature.watchlist.WatchlistViewModel
import com.codeskraps.feature.watchlist.components.WatchlistScreen

@Composable
fun AccountTradeScreen(
    navRoute: (String) -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val navController = rememberNavController()

    val accViewModel = hiltViewModel<AccountViewModel>()
    val watchlistViewModel = hiltViewModel<WatchlistViewModel>()
    val pnlViewModel = hiltViewModel<PnLViewModel>()
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
                        if (selectedTabIndex != 0) {
                            selectedTabIndex = 0
                            navController.navigate(Screen.Account.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = false
                                }
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
                        if (selectedTabIndex != 1) {
                            selectedTabIndex = 1
                            navController.navigate(Screen.Watchlist.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = false
                                }
                            }
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_favorite),
                            contentDescription = null
                        )
                    })
                NavigationBarItem(
                    selected = selectedTabIndex == 2,
                    onClick = {
                        if (selectedTabIndex != 2) {
                            selectedTabIndex = 2
                            navController.navigate(Screen.PnL.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = false
                                }
                            }
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pnl),
                            contentDescription = null
                        )
                    })
                NavigationBarItem(
                    selected = selectedTabIndex == 3,
                    onClick = {
                        if (selectedTabIndex != 3) {
                            selectedTabIndex = 3
                            navController.navigate(Screen.Trade.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = false
                                }
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
                    action = accViewModel.action,
                    handleEvent = accViewModel.state::handleEvent
                ) { route ->
                    val args = route.split("/")
                    when (args[0]) {
                        Screen.Setting.route -> navRoute(Screen.Setting.route)
                        Screen.Symbol.route.split("/")[0] -> navRoute(
                            Screen.Symbol.createRoute(args[1], args[2].toDouble())
                        )

                        else -> {}
                    }
                }
            }
            composable(
                route = Screen.Watchlist.route
            ) {
                val state by watchlistViewModel.state.collectAsStateWithLifecycle()

                WatchlistScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = state,
                    handleEvent = watchlistViewModel.state::handleEvent,
                ) { route ->
                    navRoute(route)
                }
            }
            composable(
                route = Screen.PnL.route
            ) {
                val state by pnlViewModel.state.collectAsStateWithLifecycle()

                FinishTradesScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = state,
                    handleEvent = pnlViewModel.state::handleEvent
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