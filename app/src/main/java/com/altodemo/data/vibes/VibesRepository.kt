package com.altodemo.data.vibes

import com.altodemo.data.model.VibesDto
import kotlinx.coroutines.flow.Flow

interface VibesRepository {
    fun errorFlow(): Flow<Exception?>
    fun availableVibesFlow(): Flow<VibesDto>

    suspend fun fetchAvailableVibes()
}