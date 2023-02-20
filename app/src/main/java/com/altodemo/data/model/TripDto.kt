package com.altodemo.data.model


data class TripDto(
    val dropoff_location: LocationDto? = null,
    val estimated_arrival: String? = null,
    val estimated_fare_max: Int? = null,
    val estimated_fare_min: Int? = null,
    val notes: String? = null,
    val passengers_max: Int? = null,
    val passengers_min: Int? = null,
    val payment: String? = null,
    val pickup_location: LocationDto? = null
)