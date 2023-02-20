package com.altodemo.data.mission

import com.altodemo.data.model.DriverDto
import com.altodemo.data.model.TripDto
import com.altodemo.data.model.VehicleDto
import com.altodemo.data.model.VibeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MissionRepositoryImpl @Inject constructor(private val missionService: MissionService) : MissionRepository {

    private val _isFetching = MutableStateFlow(false)
    override fun isPerformingFetchFlow(): Flow<Boolean> = _isFetching

    private val _errorFlow = MutableStateFlow<Exception?>(null)
    override fun errorFlow(): Flow<Exception?> = _errorFlow

    private val _trip = MutableStateFlow<TripDto?>(null)
    override fun tripFlow(): Flow<TripDto> = _trip.filterNotNull()

    private val _driver = MutableStateFlow<DriverDto?>(null)
    override fun driverFlow(): Flow<DriverDto> = _driver.filterNotNull()

    private val _vehicle = MutableStateFlow<VehicleDto?>(null)
    override fun vehicleFlow(): Flow<VehicleDto> = _vehicle.filterNotNull()

    private val _selectedVibe = MutableStateFlow<VibeDto?>(null)
    override fun selectedVibeFlow(): Flow<VibeDto> = _selectedVibe.filterNotNull() // Good vibes only

    override suspend fun mutateVibe(name: String?) {
        _selectedVibe.value = name?.let { VibeDto(name = name) }
    }

    override suspend fun mutateDropOffNotes(notes: String) {
        _trip.value = _trip.value?.copy(notes = notes)
    }

    override suspend fun fetchMission() {
        withContext(Dispatchers.IO) {
            _isFetching.value = true
            val result = missionService.fetchTheMission()
            result.getOrNull()?.let { mission ->
                Timber.i("Repo Running Success Logic")
                _errorFlow.value = null
                _trip.value = mission.trip
                _driver.value = mission.driver
                _vehicle.value = mission.vehicle
                _selectedVibe.value = mission.vibe
            } ?: run {
                _errorFlow.value = result.exceptionOrNull()?.let { Exception(it) }
                    ?: Exception("There was a problem resolving the result.")

                Timber.i("Repo Running Error Logic ${_errorFlow.value}")
            }
            _isFetching.value = false
        }
    }

}