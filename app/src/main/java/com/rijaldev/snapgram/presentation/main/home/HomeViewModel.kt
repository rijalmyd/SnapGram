package com.rijaldev.snapgram.presentation.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.rijaldev.snapgram.domain.usecase.story.StoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val storyUseCase: StoryUseCase) : ViewModel() {

    val stories by lazy {
        storyUseCase.getStories().cachedIn(viewModelScope).asLiveData()
    }
}