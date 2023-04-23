package com.rijaldev.snapgram.domain.model.story

data class Story(
    val id: String,
    val name: String?,
    val photoUrl: String?,
    val createdAt: String?,
    val description: String?,
    val lat: Double?,
    val lon: Double?,
)
