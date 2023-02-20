package com.altodemo.domain.mission

import com.altodemo.data.mission.MissionRepository
import javax.inject.Inject

class FetchMissionUseCase @Inject constructor(private val missionRepository: MissionRepository) {
    suspend operator fun invoke() {
        missionRepository.fetchMission()
    }
}