package com.rijaldev.snapgram.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rijaldev.snapgram.data.source.local.entity.RemoteKeys
import com.rijaldev.snapgram.data.source.local.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}