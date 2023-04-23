package com.rijaldev.snapgram.util

import com.rijaldev.snapgram.domain.model.story.Story

object DataDummy {
    fun generateDummyStoryEntity(): List<Story> {
        return (0..100).map {
            Story(
                it.toString(),
                "name $it",
                "photo $it",
                "createdAt $it",
                "description $it",
                null,
                null,
            )
        }
    }
}