package com.codeskraps.feature.settings.mvi

data class SettingsState(
    val isLoading: Boolean,
    val apiKey: String,
    val secretKey: String,
    val startDate: Long,
) {
    companion object {
        val initial = SettingsState(
            isLoading = false,
            apiKey = "",
            secretKey = "",
            startDate = 0L
        )
    }
}
