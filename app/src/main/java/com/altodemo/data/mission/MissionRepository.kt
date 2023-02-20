package com.altodemo.data.mission

import com.altodemo.data.model.DriverDto
import com.altodemo.data.model.TripDto
import com.altodemo.data.model.VehicleDto
import com.altodemo.data.model.VibeDto
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    fun tripFlow(): Flow<TripDto>
    fun vehicleFlow(): Flow<VehicleDto>
    fun driverFlow(): Flow<DriverDto>
    fun selectedVibeFlow(): Flow<VibeDto>
    fun errorFlow(): Flow<Exception?>
    fun isPerformingFetchFlow(): Flow<Boolean>
    suspend fun fetchMission()
    suspend fun mutateVibe(name: String?)
    suspend fun mutateDropOffNotes(notes: String)
}