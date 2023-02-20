package com.altodemo.data.mission

import com.altodemo.app.util.JsonExtractor
import com.altodemo.data.model.MissionDto
import com.google.gson.Gson
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject


class MissionServiceImpl @Inject constructor(
    private val jsonExtractor: JsonExtractor
) : MissionService {

    private val gson = Gson()

    override suspend fun fetchTheMission(): Result<MissionDto> {
        /**
         * The below delay is to simulate loading
         * states only.
         */
        delay(MOCK_REMOTE_FETCH_DELAY)
        return try {
            Timber.i("Extracting JSON")
            val json = jsonExtractor.extract(MISSION_JSON_FILE)
            Result.success(
                gson.fromJson(json, MissionDto::class.java)
            )
        } catch (e: Exception) {
            Timber.i("JSON Extraction Failed")
            Result.failure(e)
        }
    }

    companion object {
        private const val MISSION_JSON_FILE = "mission.json"
        private const val MOCK_REMOTE_FETCH_DELAY = 3000L
    }
}