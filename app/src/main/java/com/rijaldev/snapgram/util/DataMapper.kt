package com.rijaldev.snapgram.util

import com.rijaldev.snapgram.data.remote.response.LoginResponse
import com.rijaldev.snapgram.data.remote.response.RegisterResponse
import com.rijaldev.snapgram.data.remote.response.StoryItem
import com.rijaldev.snapgram.data.remote.response.StoryUploadResponse
import com.rijaldev.snapgram.domain.model.auth.Login
import com.rijaldev.snapgram.domain.model.auth.Register
import com.rijaldev.snapgram.domain.model.story.Story
import com.rijaldev.snapgram.domain.model.story.StoryUpload

fun RegisterResponse?.toDomainRegister() = Register(this?.error, this?.message)

fun LoginResponse?.toDomainLogin() = Login(this?.loginResult?.name, this?.loginResult?.userId, this?.loginResult?.token)

fun List<StoryItem>.toDomainStory() = map {
    Story(it.id, it.name, it.photoUrl, it.createdAt, it.description, it.lat, it.lon)
}

fun StoryItem.toDomainStory() = Story(id, name, photoUrl, createdAt, description, lat, lon)

fun StoryUploadResponse.toDomainStoryUpload() = StoryUpload(error, message)