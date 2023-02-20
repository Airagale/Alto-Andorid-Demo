package com.altodemo.data.vibes

import com.altodemo.data.model.VibesDto

interface VibesService {
    suspend fun fetchVibes(): Result<VibesDto>
}