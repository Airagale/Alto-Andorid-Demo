package com.altodemo.app.mission

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewModelScope
import com.altodemo.app.navigation.Navigation
import com.altodemo.app.util.BaseViewModel
import com.altodemo.domain.mission.*
import com.altodemo.domain.mission.ObserveSelectedVibeUseCase
import com.altodemo.domain.vibes.FetchAvailableVibesUseCase
import com.altodemo.domain.vibes.ObserveAvailableVibes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor(
    driver: ObserveDriverUserCase,
    trip: ObserveTripDetailsUseCase,
    vehicle: ObserveVehicleDetailsUseCase,
    availableVibes: ObserveAvailableVibes,
    selectedVibe: ObserveSelectedVibeUseCase,
    private val mutateNotes: MutateNoteUseCase,
    private val fetchMission: FetchMissionUseCase,
    private val fetchAvailableVibes: FetchAvailableVibesUseCase,
) : BaseViewModel() {

    var uiState: MissionUiState by mutableStateOf(MissionUiState())
        private set
    private val etaFormatter = DateTimeFormatter.ofPattern("h:mm a")

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            combine(
                driver(),
                trip(),
                selectedVibe(),
                availableVibes(),
                vehicle(),
            ) { driver, trip, selectedVibe, availableVibes, vehicleDetails ->
                uiState.copy(
                    driver = driver,
                    trip = trip,
                    selectedVibe = selectedVibe,
                    vehicle = vehicleDetails,
                    formattedEta = etaFormatter.format(trip.eta),
                    isVibeChangeAvailable = availableVibes.isNotEmpty()
                )
            }
                .filterNotNull()
                .collect { incomingState ->
                    uiState = incomingState
                }
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            fetchMission()
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            fetchAvailableVibes()
        }
    }

    fun processTrackAppBarHeight(height: Dp) {
        uiState = uiState.copy(appBarHeight = height)
    }

    fun processSaveDropOffNotes() {
        uiState = uiState.copy(isEditingNotes = false)
    }

    fun processStartEditNotes() {
        uiState = uiState.copy(isEditingNotes = true)
    }

    fun processNavigateChangeVibe() {
        navigator.navigationToDestination(Navigation.ChangeVibe)
    }

    fun processMutateDropOffNotes(notes: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            mutateNotes(notes)
        }
    }


}