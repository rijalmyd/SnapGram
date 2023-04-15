package com.rijaldev.snapgram.presentation.addstory.upload.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class ImageResult(
    val imageFile: File?,
    val imageUri: Uri? = null,
    val imageBitmap: Bitmap? = null,
    val isFromCamera: Boolean = true
) : Parcelable
