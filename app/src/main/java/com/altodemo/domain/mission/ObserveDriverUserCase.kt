package com.altodemo.domain.mission

import com.altodemo.data.mission.MissionRepository
import com.altodemo.domain.model.Driver
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveDriverUserCase @Inject constructor(private val missionRepository: MissionRepository) {
    operator fun invoke() = missionRepository.driverFlow()
        .map { dto ->
            Driver(
                bio = dto.bio!!,
                image = dto.image!!,
                name = dto.name!!,
                phone = dto.phone!!,
            )
        }
}