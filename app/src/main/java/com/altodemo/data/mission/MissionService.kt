package com.altodemo.data.mission

import com.altodemo.app.util.JsonExtractor
import com.altodemo.data.model.MissionDto
import com.google.gson.Gson
import kotlinx.coroutines.delay
import javax.inject.Inject


interface MissionService {
    suspend fun fetchTheMission(): Result<MissionDto>
}
