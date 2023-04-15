package com.rijaldev.snapgram.presentation.main.home

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
class HomeViewModel @Inject constructor(private val storyUseCase: StoryUseCase) : ViewModel() {

    private val _stories = MutableLiveData<Result<List<Story>?>>()
    val stories: MutableLiveData<Result<List<Story>?>> get() = _stories

    fun getStories() = viewModelScope.launch {
        storyUseCase.getStories().collect { result ->
            _stories.value = result
        }
    }
}