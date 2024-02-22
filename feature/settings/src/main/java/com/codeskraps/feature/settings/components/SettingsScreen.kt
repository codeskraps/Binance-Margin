package com.codeskraps.feature.settings.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.codeskraps.core.domain.R
import com.codeskraps.feature.settings.mvi.SettingsEvent
import com.codeskraps.feature.settings.mvi.SettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    handleEvent: (SettingsEvent) -> Unit,
    navRoute: (String) -> Unit
) {

    BackHandler {
        navRoute("back")
    }

    Scaffold(
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
                title = { Text(text = "Binance Margin") }
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
            CategoryPreference(title = "General")
            DatePickerPreference(title = "Start Date", date = state.startDate, onDateChange = {
                handleEvent(SettingsEvent.UpdateDate(it))
            })
            CategoryPreference(title = "Binance Keys")
            Preference(
                title = "API Key",
                summary = state.apiKey,
                onChange = { handleEvent(SettingsEvent.UpdateApiKey(it)) }
            )
            SpacerPreference()
            Preference(
                title = "Secret Key",
                summary = state.secretKey,
                passwordVisible = false,
                onChange = { handleEvent(SettingsEvent.UpdateSecretKey(it)) }
            )
            CategoryPreference(title = "Information")
            Preference(
                title = "Binance Margin v1.0",
                summary = "Apache License Version 2.0 - 2024 - Codeskraps"
            )
        }
    }
}