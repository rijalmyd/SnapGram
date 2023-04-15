package com.rijaldev.snapgram.presentation.main.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.story.Story
import com.rijaldev.snapgram.domain.usecase.story.StoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(private val storyUseCase: StoryUseCase) : ViewModel() {

    private val _stories = MutableLiveData<Result<List<Story>>>()
    val stories: LiveData<Result<List<Story>>>
        get() = _stories

    fun getStoryWithLocation() = viewModelScope.launch {
        storyUseCase.getStoriesWithLocation().collect { result ->
            _stories.value = result
        }
    }
}