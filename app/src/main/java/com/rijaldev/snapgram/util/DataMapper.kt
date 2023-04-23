package com.rijaldev.snapgram.util

import androidx.paging.PagingData
import androidx.paging.map
import com.rijaldev.snapgram.data.source.local.entity.StoryEntity
import com.rijaldev.snapgram.data.source.remote.response.LoginResponse
import com.rijaldev.snapgram.data.source.remote.response.RegisterResponse
import com.rijaldev.snapgram.data.source.remote.response.StoryItem
import com.rijaldev.snapgram.data.source.remote.response.StoryUploadResponse
import com.rijaldev.snapgram.domain.model.auth.Login
import com.rijaldev.snapgram.domain.model.auth.Register
import com.rijaldev.snapgram.domain.model.story.Story
import com.rijaldev.snapgram.domain.model.story.StoryUpload

fun RegisterResponse?.toRegisterDomain() = Register(this?.error, this?.message)

fun LoginResponse?.toLoginDomain() = Login(this?.loginResult?.name, this?.loginResult?.userId, this?.loginResult?.token)

fun List<StoryItem>.toStoryDomain() = map {
    Story(it.id, it.name, it.photoUrl, it.createdAt, it.description, it.lat, it.lon)
}

fun StoryItem.toStoryDomain() = Story(id, name, photoUrl, createdAt, description, lat, lon)

fun StoryUploadResponse.toStoryUploadDomain() = StoryUpload(error, message)

fun List<StoryItem>.toStoryEntity() = map {
    StoryEntity(it.id, it.name, it.photoUrl, it.createdAt, it.description, it.lat, it.lon)
}

fun toStoryDomain(story: PagingData<StoryEntity>) = story.map {
    Story(it.id, it.name, it.photoUrl, it.createdAt, it.description, it.lat, it.lon)
}