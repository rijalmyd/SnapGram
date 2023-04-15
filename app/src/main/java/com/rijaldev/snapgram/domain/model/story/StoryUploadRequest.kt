package com.rijaldev.snapgram.domain.model.story

import android.location.Location
import java.io.File

data class StoryUploadRequest(
    val image: File,
    val description: String,
    val location: Location? = null
)
