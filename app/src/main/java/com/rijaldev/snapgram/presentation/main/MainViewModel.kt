package com.rijaldev.snapgram.presentation.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rijaldev.snapgram.domain.model.story.Story
import com.rijaldev.snapgram.domain.usecase.auth.AuthUseCase
import com.rijaldev.snapgram.domain.usecase.story.StoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val storyUseCase: StoryUseCase,
) : ViewModel() {

    val stories = MutableLiveData<PagingData<Story>>()

    init {
        refreshStories()
    }

    fun refreshStories() = viewModelScope.launch {
        storyUseCase.getStories().cachedIn(viewModelScope).collect { story ->
            stories.value = story
        }
    }

    fun signOut() = viewModelScope.launch {
        authUseCase.deleteCredential()
    }
}