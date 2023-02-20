package com.altodemo.app.util

class FileExtractorProvider : JsonExtractor {
    private var callback: ((jsonFileName: String) -> String?)? = null
    fun performExtraction(callback: (jsonFileName: String) -> String?) {
        this.callback = callback
    }

    fun clearExtractionMethod() {
        this.callback = null
    }

    override fun extract(jsonFileName: String): String? {
        return this.callback?.invoke(jsonFileName)
    }
}

interface JsonExtractor {
    fun extract(jsonFileName: String): String?
}