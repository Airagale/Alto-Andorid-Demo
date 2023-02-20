package com.altodemo.data.vibes

import com.altodemo.app.util.JsonExtractor
import com.altodemo.data.model.VibesDto
import com.google.gson.Gson
import timber.log.Timber
import javax.inject.Inject

class VibesServiceImpl @Inject constructor(private val jsonExtractor: JsonExtractor) : VibesService {
    private val gson = Gson()

    override suspend fun fetchVibes(): Result<VibesDto> {
        return try {
            Timber.i("Extracting JSON")
            val json = jsonExtractor.extract(VIBE_JSON_FILE)
            Result.success(
                gson.fromJson(json,VibesDto::class.java)
            )
        } catch (e: Exception) {
            Timber.i("JSON Extraction Failed")
            Result.failure(e)
        }
    }

    companion object {
        private const val VIBE_JSON_FILE = "vibes.json"
    }
}