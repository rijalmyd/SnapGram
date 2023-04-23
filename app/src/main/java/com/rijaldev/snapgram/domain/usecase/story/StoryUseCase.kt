package com.rijaldev.snapgram.domain.usecase.story

import androidx.paging.PagingData
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.story.Story
import com.rijaldev.snapgram.domain.model.story.StoryUpload
import com.rijaldev.snapgram.domain.model.story.StoryUploadRequest
import kotlinx.coroutines.flow.Flow

interface StoryUseCase {
    fun getStories(): Flow<PagingData<Story>>

    fun getStoriesWithLocation(): Flow<Result<List<Story>>>

    fun getDetailStory(id: String): Flow<Result<Story>>

    fun uploadStory(story: StoryUploadRequest): Flow<Result<StoryUpload>>
}