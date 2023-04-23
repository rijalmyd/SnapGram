package com.rijaldev.snapgram.data.repository

import androidx.paging.*
import com.rijaldev.snapgram.data.paging.StoryRemoteMediator
import com.rijaldev.snapgram.data.source.local.datastore.UserPreferences
import com.rijaldev.snapgram.data.source.local.room.StoryDatabase
import com.rijaldev.snapgram.data.source.remote.RemoteDataSource
import com.rijaldev.snapgram.data.source.remote.retrofit.ApiService
import com.rijaldev.snapgram.domain.common.Result
import com.rijaldev.snapgram.domain.model.story.StoryUploadRequest
import com.rijaldev.snapgram.domain.repository.StoryRepository
import com.rijaldev.snapgram.util.generateToken
import com.rijaldev.snapgram.util.toStoryDomain
import com.rijaldev.snapgram.util.toStoryUploadDomain
import com.rijaldev.snapgram.util.wrapEspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val userPreferences: UserPreferences,
) : StoryRepository {

    override fun getStories() =
        @OptIn(ExperimentalPagingApi::class)
        Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            },
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, userPreferences)
        ).flow.map(::toStoryDomain)

    override fun getStoriesWithLocation() = flow {
        emit(Result.Loading())
        try {
            val token = userPreferences.getToken().first()
            val response = remoteDataSource.getStories(token.generateToken(), location = 1)
            val result = response.listStory.toStoryDomain()
            emit(Result.Success(result))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)

    override fun getStoriesForWidget() = flow {
        try {
            val token = userPreferences.getToken().first()
            val response = remoteDataSource.getStories(token.generateToken())
            emit(response.listStory.toStoryDomain())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    override fun getDetailStory(id: String) = flow {
        emit(Result.Loading())
        try {
            val token = userPreferences.getToken().first()
            val response = remoteDataSource.getDetailStory(token.generateToken(), id)
            val result = response.story.toStoryDomain()
            emit(Result.Success(result))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }.flowOn(Dispatchers.IO)

    override fun uploadStory(story: StoryUploadRequest) = flow {
        emit(Result.Loading())
        wrapEspressoIdlingResource {
            try {
                val token = userPreferences.getToken().first()
                val response = remoteDataSource.uploadStory(token.generateToken(), story)
                val result = response.toStoryUploadDomain()
                emit(Result.Success(result))
            } catch (e: Exception) {
                emit(Result.Error(e.message))
            }
        }
    }.flowOn(Dispatchers.IO)
}