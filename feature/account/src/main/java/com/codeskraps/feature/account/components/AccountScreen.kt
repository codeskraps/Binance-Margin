package com.codeskraps.feature.account.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.codeskraps.core.domain.components.BinanceScaffold
import com.codeskraps.core.domain.components.ObserveAsEvents
import com.codeskraps.core.domain.navigation.Screen
import com.codeskraps.feature.account.mvi.AccountAction
import com.codeskraps.feature.account.mvi.AccountEvent
import com.codeskraps.feature.account.mvi.AccountState
import kotlinx.coroutines.flow.Flow

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

    BinanceScaffold(
        modifier = modifier,
        title = {
            Column {
                Text(text = "Binance Margin")
                Text(text = "UTC ${state.utcTime}", fontSize = MaterialTheme.typography.bodySmall.fontSize)
            }
        },
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
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp)
        ) {
            val configuration = LocalConfiguration.current
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