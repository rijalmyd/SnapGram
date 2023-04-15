package com.rijaldev.snapgram.domain.usecase.story

import com.rijaldev.snapgram.domain.model.story.StoryUploadRequest
import com.rijaldev.snapgram.domain.repository.StoryRepository
import javax.inject.Inject

class StoryInteractor @Inject constructor(private val storyRepository: StoryRepository) : StoryUseCase {

    override fun getStories() = storyRepository.getStories()

    override fun getStoriesWithLocation() = storyRepository.getStories(1)

    override fun getDetailStory(id: String) = storyRepository.getDetailStory(id)

    override fun uploadStory(story: StoryUploadRequest) = storyRepository.uploadStory(story)
}