package com.altodemo.data.di

import com.altodemo.app.util.JsonExtractor
import com.altodemo.data.mission.MissionRepository
import com.altodemo.data.mission.MissionRepositoryImpl
import com.altodemo.data.mission.MissionService
import com.altodemo.data.mission.MissionServiceImpl
import com.altodemo.data.vibes.VibesRepository
import com.altodemo.data.vibes.VibesRepositoryImpl
import com.altodemo.data.vibes.VibesService
import com.altodemo.data.vibes.VibesServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModules {

    @Provides
    @Singleton
    fun provideMissionService(
        jsonExtractor: JsonExtractor
    ): MissionService {
        return MissionServiceImpl(
            jsonExtractor = jsonExtractor
        )
    }


    @Provides
    @Singleton
    fun provideVibesService(
        jsonExtractor: JsonExtractor
    ): VibesService {
        return VibesServiceImpl(
            jsonExtractor = jsonExtractor
        )
    }


    @Provides
    @Singleton
    fun provideMissionRepo(missionService: MissionService): MissionRepository {
        return MissionRepositoryImpl(missionService = missionService)
    }

    @Provides
    @Singleton
    fun provideVibesRepo(vibesService: VibesService): VibesRepository {
        return VibesRepositoryImpl(vibesService = vibesService)
    }


}