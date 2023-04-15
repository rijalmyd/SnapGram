package com.rijaldev.snapgram.domain.usecase.widget

import com.rijaldev.snapgram.domain.repository.StoryRepository
import javax.inject.Inject

class WidgetStoryInteractor @Inject constructor(private val storyRepository: StoryRepository) : WidgetStoryUseCase {

    override fun getStories() = storyRepository.getStoriesForWidget()
}