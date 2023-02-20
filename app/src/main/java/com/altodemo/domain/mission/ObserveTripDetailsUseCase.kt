package com.altodemo.domain.mission

import com.altodemo.data.mission.MissionRepository
import com.altodemo.data.model.LocationDto
import com.altodemo.domain.model.TripLocation
import com.altodemo.domain.model.Trip
import kotlinx.coroutines.flow.map
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME
import java.util.*
import javax.inject.Inject

class ObserveTripDetailsUseCase @Inject constructor(private val missionRepository: MissionRepository) {
    operator fun invoke() = missionRepository
        .tripFlow()
        .map { dto ->
            Trip(
                payment = dto.payment!!,
                maxPassengers = dto.passengers_max!!.toString(),
                minPassengers = dto.passengers_min!!.toString(),
                eta = formatEta(dto.estimated_arrival!!),
                pickupAddress = tripLocationFromDto(dto.pickup_location!!),
                dropOffLocation = tripLocationFromDto(dto.dropoff_location!!),
                maxFare = formatSimplifiedFare(dto.estimated_fare_max!!),
                minFare = formatSimplifiedFare(dto.estimated_fare_min!!),
                notes = dto.notes.orEmpty(),
            )
        }


    private fun formatSimplifiedFare(value: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        format.maximumFractionDigits = 0
        val incomingValueString = value.toString()
        val simplifiedValueInt = try {
            incomingValueString.subSequence(0, 2).toString()
        } catch (e: Exception) {
            MIN_FARE_STRING
        }.toInt()
        return format.format(simplifiedValueInt)
    }

    private fun formatEta(eta: String): LocalDateTime {
        return LocalDateTime.parse(
            eta as CharSequence,
            RFC_1123_DATE_TIME
        )
    }

    private fun tripLocationFromDto(dto: LocationDto): TripLocation {
        return TripLocation(
            city = dto.city!!,
            state = dto.state!!,
            streetLine1 = dto.street_line1!!,
            zipcode = dto.zipcode!!,
            name = dto.name.orEmpty(),
            streetLine2 = dto.street_line2.orEmpty(),
        )
    }


    companion object {
        private const val MIN_FARE_STRING = "1"
    }
}