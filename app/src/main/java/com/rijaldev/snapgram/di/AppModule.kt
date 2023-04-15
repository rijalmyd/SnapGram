package com.rijaldev.snapgram.di

import com.rijaldev.snapgram.domain.usecase.auth.AuthInteractor
import com.rijaldev.snapgram.domain.usecase.auth.AuthUseCase
import com.rijaldev.snapgram.domain.usecase.story.StoryInteractor
import com.rijaldev.snapgram.domain.usecase.story.StoryUseCase
import com.rijaldev.snapgram.domain.usecase.widget.WidgetStoryInteractor
import com.rijaldev.snapgram.domain.usecase.widget.WidgetStoryUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun provideAuthUseCase(authInteractor: AuthInteractor): AuthUseCase

    @Binds
    @Singleton
    abstract fun provideStoryUseCase(storyInteractor: StoryInteractor): StoryUseCase

    @Binds
    @Singleton
    abstract fun provideWidgetStoryUseCase(widgetStoryInteractor: WidgetStoryInteractor): WidgetStoryUseCase
}