package com.codeskraps.feature.settings.components

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.codeskraps.core.domain.components.BinanceScaffold
import com.codeskraps.feature.settings.mvi.SettingsEvent
import com.codeskraps.feature.settings.mvi.SettingsState

@Composable
fun SettingsScreen(
    state: SettingsState,
    handleEvent: (SettingsEvent) -> Unit,
    navRoute: (String) -> Unit
) {

    BackHandler {
        navRoute("back")
    }

    BinanceScaffold(
        title = { Text(text = "Binance Margin") }
    ) {
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