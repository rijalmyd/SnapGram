package com.rijaldev.snapgram.presentation.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.rijaldev.snapgram.domain.usecase.story.StoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val storyUseCase: StoryUseCase) : ViewModel() {

    private val storyId = MutableLiveData<String>()

    fun setStoryId(id: String) {
        storyId.value = id
    }

    val detailStory by lazy {
        storyId.switchMap {
            storyUseCase.getDetailStory(it).asLiveData()
        }
    }
}