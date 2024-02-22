package com.codeskraps.feature.settings.mvi

sealed interface SettingsEvent {
    data class UpdateApiKey(val apiKey: String) : SettingsEvent
    data class UpdateSecretKey(val secretKey: String) : SettingsEvent
    data class UpdateDate(val date: Long) : SettingsEvent
}