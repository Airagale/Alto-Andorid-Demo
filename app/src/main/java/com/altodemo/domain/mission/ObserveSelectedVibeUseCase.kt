package com.altodemo.domain.mission

import com.altodemo.data.mission.MissionRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveSelectedVibeUseCase @Inject constructor(
    private val missionRepository: MissionRepository,
) {
    operator fun invoke() = missionRepository.selectedVibeFlow().map { it.name!! }

}