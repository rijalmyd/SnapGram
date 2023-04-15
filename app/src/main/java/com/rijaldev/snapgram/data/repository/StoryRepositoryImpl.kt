package com.rijaldev.snapgram.data.repository

import com.rijaldev.snapgram.data.local.datastore.UserPreferences
import com.rijaldev.snapgram.data.remote.RemoteDataSource
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.story.StoryUploadRequest
import com.rijaldev.snapgram.domain.repository.StoryRepository
import com.rijaldev.snapgram.util.generateToken
import com.rijaldev.snapgram.util.toDomainStory
import com.rijaldev.snapgram.util.toDomainStoryUpload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val userPreferences: UserPreferences,
) : StoryRepository {

    override fun getStories() = flow {
        emit(Result.Loading())
        try {
            val token = userPreferences.getToken().first()
            val response = remoteDataSource.getStories(token.generateToken())
            val result = response.listStory.toDomainStory()
            emit(Result.Success(result))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)

    override fun getStoriesForWidget() = flow {
        try {
            val token = userPreferences.getToken().first()
            val response = remoteDataSource.getStories(token.generateToken())
            emit(response.listStory.toDomainStory())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    override fun getDetailStory(id: String) = flow {
        emit(Result.Loading())
        try {
            val token = userPreferences.getToken().first()
            val response = remoteDataSource.getDetailStory(token.generateToken(), id)
            val result = response.story.toDomainStory()
            emit(Result.Success(result))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)

    override fun uploadStory(story: StoryUploadRequest) = flow {
        emit(Result.Loading())
        try {
            val token = userPreferences.getToken().first()
            val response = remoteDataSource.uploadStory(token.generateToken(), story)
            val result = response.toDomainStoryUpload()
            emit(Result.Success(result))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)
}