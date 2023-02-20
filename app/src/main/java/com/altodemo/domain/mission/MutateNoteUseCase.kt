package com.altodemo.domain.mission

import com.altodemo.data.mission.MissionRepository
import javax.inject.Inject

class MutateNoteUseCase @Inject constructor(private val repository: MissionRepository) {

    suspend operator fun invoke(notes:String){
        repository.mutateDropOffNotes(notes)
    }
}