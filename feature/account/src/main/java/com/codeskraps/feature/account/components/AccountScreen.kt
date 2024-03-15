package com.codeskraps.feature.account.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codeskraps.core.domain.R
import com.codeskraps.core.domain.components.ObserveAsEvents
import com.codeskraps.core.domain.navigation.Screen
import com.codeskraps.feature.account.mvi.AccountAction
import com.codeskraps.feature.account.mvi.AccountEvent
import com.codeskraps.feature.account.mvi.AccountState
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    modifier: Modifier,
    state: AccountState,
    handleEvent: (AccountEvent) -> Unit,
    action: Flow<AccountAction>,
    navRoute: (String) -> Unit
) {
    LifecycleResumeEffect(Unit) {
        handleEvent(AccountEvent.Resume)
        onPauseOrDispose {
            handleEvent(AccountEvent.Pause)
        }
    }

    ObserveAsEvents(flow = action) { onAction ->
        when (onAction) {
            is AccountAction.OpenSymbol -> {
                navRoute(Screen.Symbol.createRoute(onAction.symbol, onAction.entry))
            }
        }
    }

    val configuration = LocalConfiguration.current

    val scrollBehavior = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState()
        )

        Configuration.ORIENTATION_PORTRAIT -> TopAppBarDefaults.enterAlwaysScrollBehavior(
            rememberTopAppBarState()
        )

        else -> TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    }

    val scaffoldModifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)

    Scaffold(
        modifier = scaffoldModifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(5.dp),
                        painter = painterResource(id = R.mipmap.ic_launcher),
                        contentDescription = ""
                    )
                },
                title = { Text(text = "Binance Margin") },
                actions = {
                    if (state.isLoading) CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { navRoute(Screen.Setting.route) }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            ) {
                when (configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        PortraitColumn(state = state, handleEvent = handleEvent)
                    }

                    Configuration.ORIENTATION_LANDSCAPE -> {
                        LandscapeColumn(state = state, handleEvent = handleEvent)
                    }

                    else -> {
                    }
                }
            }
        }
    }
}