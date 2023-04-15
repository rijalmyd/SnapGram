package com.rijaldev.snapgram.domain.repository

import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.story.Story
import com.rijaldev.snapgram.domain.model.story.StoryUpload
import com.rijaldev.snapgram.domain.model.story.StoryUploadRequest
import kotlinx.coroutines.flow.Flow

interface StoryRepository {
    fun getStories(location: Int = 0): Flow<Result<List<Story>>>

    fun getStoriesForWidget(): Flow<List<Story>>

    fun getDetailStory(id: String): Flow<Result<Story>>

    fun uploadStory(story: StoryUploadRequest): Flow<Result<StoryUpload>>
}