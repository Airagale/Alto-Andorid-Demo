package com.altodemo.app.mission

import androidx.compose.ui.unit.Dp
import com.altodemo.domain.model.Driver
import com.altodemo.domain.model.Trip
import com.altodemo.domain.model.Vehicle

data class MissionUiState(
    val driver: Driver? = null,
    val trip: Trip? = null,
    val selectedVibe: String? = null,
    val vehicle: Vehicle? = null,
    val formattedEta: String? = null,
    val appBarHeight: Dp? = null,
    val activeIndex: Int? = null,
    val isVibeChangeAvailable: Boolean = false,
    val isEditingNotes: Boolean = false,
) {

    fun isLoading(): Boolean {
        return !isDataAvailable()
                && appBarHeight == null
    }

    fun isDataAvailable(): Boolean {
        return driver != null
                && trip != null
                && selectedVibe != null
                && vehicle != null
                && formattedEta != null
    }
}
