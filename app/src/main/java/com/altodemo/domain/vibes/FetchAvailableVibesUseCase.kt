package com.altodemo.domain.vibes

import com.altodemo.data.vibes.VibesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchAvailableVibesUseCase @Inject constructor(private val vibesRepository: VibesRepository,) {
    suspend operator fun invoke(){
        withContext(Dispatchers.IO){
            vibesRepository.fetchAvailableVibes()
        }
    }
}