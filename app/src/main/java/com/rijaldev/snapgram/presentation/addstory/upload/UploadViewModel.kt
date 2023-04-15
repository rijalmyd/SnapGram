package com.rijaldev.snapgram.presentation.addstory.upload

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.story.StoryUpload
import com.rijaldev.snapgram.domain.model.story.StoryUploadRequest
import com.rijaldev.snapgram.domain.usecase.story.StoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(private val storyUseCase: StoryUseCase) : ViewModel() {

    private val _uploadStoryResult = MutableLiveData<Result<StoryUpload>>()
    val uploadStoryResult: LiveData<Result<StoryUpload>>
        get() = _uploadStoryResult

    fun uploadStory(image: File, description: String, location: Location? = null) = viewModelScope.launch {
        val storyRequest = StoryUploadRequest(image, description, location)
        storyUseCase.uploadStory(storyRequest).collect { result ->
            _uploadStoryResult.value = result
        }
    }
}