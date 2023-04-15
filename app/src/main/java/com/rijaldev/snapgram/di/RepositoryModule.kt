package com.rijaldev.snapgram.di

import com.rijaldev.snapgram.data.repository.AuthRepositoryImpl
import com.rijaldev.snapgram.data.repository.StoryRepositoryImpl
import com.rijaldev.snapgram.domain.repository.AuthRepository
import com.rijaldev.snapgram.domain.repository.StoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun provideStoryRepository(storyRepositoryImpl: StoryRepositoryImpl): StoryRepository
}