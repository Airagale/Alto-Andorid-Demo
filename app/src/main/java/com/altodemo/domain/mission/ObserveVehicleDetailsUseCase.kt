package com.altodemo.domain.mission

import com.altodemo.data.mission.MissionRepository
import com.altodemo.domain.model.Vehicle
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveVehicleDetailsUseCase @Inject constructor(private val missionRepository: MissionRepository) {
    operator fun invoke() = missionRepository.vehicleFlow()
        .map { dto ->
            Vehicle(
                color = dto.color!!,
                image = dto.image!!,
                license = dto.license!!,
                make = dto.make!!,
            )
        }
}