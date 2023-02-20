package com.altodemo.domain.model

import com.altodemo.data.model.LocationDto

data class TripLocation(
    val city: String,
    val name: String,
    val state: String,
    val streetLine1: String,
    val streetLine2: String,
    val zipcode: String
) {
    fun formatted(): String {
        // This type of format is okay as we can't translate strings from an API.
        return StringBuilder()
            .apply {
                if (name.isNotBlank()) {
                    append(name + "\n")
                }
                append(streetLine1 + "\n")
                if (streetLine2.isNotBlank()) {
                    append(name + "\n")
                }
                append("$city, $state $zipcode")
            }.toString()
    }
}