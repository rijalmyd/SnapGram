package com.rijaldev.snapgram.data.source.remote

import com.rijaldev.snapgram.data.source.remote.response.StoryUploadResponse
import com.rijaldev.snapgram.data.source.remote.retrofit.ApiService
import com.rijaldev.snapgram.domain.model.story.StoryUploadRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun register(name: String, email: String, password: String) =
        apiService.register(name, email, password)

    suspend fun login(email: String, password: String) =
        apiService.login(email, password)

    suspend fun getStories(token: String, page: Int? = null, size: Int? = null, location: Int = 0) =
        apiService.getStories(token, page, size, location)

    suspend fun getDetailStory(token: String, id: String) =
        apiService.getDetailStory(token, id)

    suspend fun uploadStory(token: String, story: StoryUploadRequest): StoryUploadResponse {
        val description = story.description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = story.image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            story.image.name,
            requestImageFile
        )

        var latitude: RequestBody? = null
        var longitude: RequestBody? = null
        story.location?.let {
            latitude = it.latitude.toString().toRequestBody("text/plain".toMediaType())
            longitude = it.longitude.toString().toRequestBody("text/plain".toMediaType())
        }

        return apiService.uploadStory(token, imageMultipart, description, latitude, longitude)
    }
}