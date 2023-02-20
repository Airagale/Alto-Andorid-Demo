package com.altodemo.domain.mission

import com.altodemo.data.mission.MissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ObserveIsMissionFetching @Inject constructor(private val missionRepository: MissionRepository) {
    operator fun invoke(): Flow<Boolean> = missionRepository
        .isPerformingFetchFlow()
        .onEach {
            it
        }
}