package com.altodemo.data.model

data class MissionDto(
    val driver: DriverDto? = null,
    val trip: TripDto? = null,
    val vehicle: VehicleDto? = null,
    val vibe: VibeDto? = null
)