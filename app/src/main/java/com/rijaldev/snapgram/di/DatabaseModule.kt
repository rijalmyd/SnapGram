package com.rijaldev.snapgram.di

import android.content.Context
import androidx.room.Room
import com.rijaldev.snapgram.data.source.local.room.StoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStoryDatabase(@ApplicationContext context: Context): StoryDatabase =
        Room.databaseBuilder(
            context,
            StoryDatabase::class.java,
            "story"
        ).fallbackToDestructiveMigration().build()
}