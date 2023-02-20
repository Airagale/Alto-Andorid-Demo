package com.altodemo.app.vibe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.altodemo.app.util.BaseViewModel
import com.altodemo.domain.mission.MutateVibeUseCase
import com.altodemo.domain.mission.ObserveSelectedVibeUseCase
import com.altodemo.domain.vibes.ObserveAvailableVibes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeVibeViewModel @Inject constructor(
    private val mutateVibe: MutateVibeUseCase,
    private val availableVibes: ObserveAvailableVibes,
    private val selectedVibe: ObserveSelectedVibeUseCase,
) : BaseViewModel() {


    var uiState by mutableStateOf(ChangeVibeUiState())
    private set

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            /**
             * The below delay is to simulate loading
             * states only.
             */
            delay(1500L)
            combine(
                selectedVibe(),
                availableVibes()
            ) { selectedVibe, availableVibes ->
                uiState.copy(
                    selectedVibe = selectedVibe,
                    availableVibes = availableVibes,
                    isLoading = selectedVibe.isBlank() || availableVibes.isEmpty(),
                )
            }.collect { incomingUiState ->
                uiState = incomingUiState
            }
        }
    }

    fun processMutateVibe(vibe: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            mutateVibe(vibe)
            /**
             * The below delay and ui state manipulation is to simulate loading
             * states only.
             */
            delay(200L)
            uiState = uiState.copy(isLoading = true)
            delay(800L)
            processExit()
        }
    }

    fun processExit() {
        navigator.navigateUp()
    }
}