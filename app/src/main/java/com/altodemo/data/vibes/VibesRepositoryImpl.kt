package com.altodemo.data.vibes

import com.altodemo.data.model.Vibe
import com.altodemo.data.model.VibeDto
import com.altodemo.data.model.VibesDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class VibesRepositoryImpl @Inject constructor(
    private val vibesService: VibesService,
) : VibesRepository {

    private val _error = MutableStateFlow<Exception?>(null)
    override fun errorFlow(): Flow<Exception?> = _error

    private val _availableVibes = MutableStateFlow<VibesDto?>(null)
    override fun availableVibesFlow(): Flow<VibesDto> = _availableVibes.filterNotNull()

    override suspend fun fetchAvailableVibes() {
        val result = vibesService.fetchVibes()
        result.getOrNull()?.let { dto ->
            _availableVibes.value = dto
        } ?: run {
            _error.value = result.exceptionOrNull()?.let {
                Exception(it)
            } ?: Exception("Failed to retrieve vibes")
        }
    }
}