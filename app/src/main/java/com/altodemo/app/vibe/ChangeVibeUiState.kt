package com.altodemo.app.vibe

import com.altodemo.domain.vibes.ObserveAvailableVibes

data class ChangeVibeUiState(
    val isLoading: Boolean = true,
    val selectedVibe: String = "",
    val availableVibes: List<String> = listOf(),
    val error: String? = null
) {
}