package com.altodemo.app.di

import com.altodemo.app.navigation.NavigationDelegate
import com.altodemo.app.navigation.NavigationEventReceiver
import com.altodemo.app.navigation.NavigationEventHandler
import com.altodemo.app.util.FileExtractorProvider
import com.altodemo.app.util.JsonExtractor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModules {

    @Binds
    @Singleton
    abstract fun provideFileExtractor(fileExtractorProvider: FileExtractorProvider): JsonExtractor

    companion object {
        @Provides
        @Singleton
        fun provideFileExtractorProvider(): FileExtractorProvider {
            return FileExtractorProvider()
        }

        @Provides
        @Singleton
        fun provideNavDelegate(): NavigationDelegate {
            return NavigationDelegate()
        }

        @Provides
        @Singleton
        fun provideNavigationEventReceiver(delegate: NavigationDelegate): NavigationEventReceiver {
            return delegate
        }

        @Provides
        @Singleton
        fun provideNavigationEventHandler(delegate: NavigationDelegate): NavigationEventHandler {
            return delegate
        }

    }
}