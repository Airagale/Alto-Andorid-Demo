package com.altodemo.app.util

import android.app.Activity
import android.app.Application
import com.altodemo.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.nio.charset.Charset
import javax.inject.Inject

@HiltAndroidApp
class AltoDemoApplication : Application() {

    @Inject
    lateinit var fileExtractorProvider: FileExtractorProvider

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }

    fun setFileExtractionCallback(activity: Activity) {
        fileExtractorProvider.performExtraction { jsonFileName ->
            val result: String? = try {
                val inputStream = activity.assets.open(jsonFileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, Charset.defaultCharset())
            } catch (e: Exception) {
                null
            }
            return@performExtraction result
        }
    }

    fun clearFileExtractionCallback() {
        fileExtractorProvider.clearExtractionMethod()
    }
}