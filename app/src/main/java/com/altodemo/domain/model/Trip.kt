package com.altodemo.domain.model

import java.time.LocalDateTime

data class Trip(
    val notes: String,
    val payment: String,
    val eta: LocalDateTime,
    val maxPassengers: String,
    val minPassengers:String,
    val maxFare:String,
    val minFare:String,
    val pickupAddress: TripLocation,
    val dropOffLocation: TripLocation,
)
