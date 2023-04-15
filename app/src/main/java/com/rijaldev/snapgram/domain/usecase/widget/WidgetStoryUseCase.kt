package com.rijaldev.snapgram.domain.usecase.widget

import com.rijaldev.snapgram.domain.model.story.Story
import kotlinx.coroutines.flow.Flow

interface WidgetStoryUseCase {
    fun getStories(): Flow<List<Story>>
}