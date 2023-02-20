package com.altodemo.domain.vibes

import com.altodemo.data.vibes.VibesRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveAvailableVibes @Inject constructor(
    private val vibesRepository: VibesRepository
) {

    operator fun invoke() = vibesRepository.availableVibesFlow()
        .map { dto ->
            dto.vibes?.mapNotNull { vibe ->
                vibe?.name
            }
        }.filterNotNull()
}