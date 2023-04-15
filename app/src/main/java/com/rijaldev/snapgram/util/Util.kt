package com.rijaldev.snapgram.util

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import com.rijaldev.snapgram.R
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

fun String?.generateToken() = "Bearer $this"

fun HttpException.getErrorMessage(): String {
    val message = response()?.errorBody()?.string().toString()
    return JSONObject(message).getString("message")
}

fun String?.getTimeAgoFormat(): String {
    if (this.isNullOrEmpty()) return "Unknown"

    val format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val sdf = SimpleDateFormat(format, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }

    val pastTime = sdf.parse(this)?.time ?: return "Unknown"
    val diff = System.currentTimeMillis() - pastTime

    val oneMin = 60_000L
    val oneHour = 60 * oneMin
    val oneDay = 24 * oneHour
    val oneMonth = 30 * oneDay
    val oneYear = 365 * oneDay

    return when {
        diff >= oneYear -> "${diff / oneYear} years ago"
        diff >= oneMonth -> "${diff / oneMonth} months ago"
        diff >= oneDay -> "${diff / oneDay} days ago"
        diff >= oneHour -> "${diff / oneHour} hours ago"
        diff >= oneMin -> "${diff / oneMin} min ago"
        else -> "Just now"
    }
}

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStamp.jpg")
}

fun createCustomTempFile(context: Context): File {
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun Uri.toFile(context: Context): File {
    val contentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    val inputStream = contentResolver.openInputStream(this) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun File.toBitmap(): Bitmap {
    return BitmapFactory.decodeFile(this.path)
}

fun Bitmap.rotateBitmap(isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(
            this,
            0,
            0,
            this.width,
            this.height,
            matrix,
            true
        )
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, this.width / 2f, this.height / 2f)
        Bitmap.createBitmap(
            this,
            0,
            0,
            this.width,
            this.height,
            matrix,
            true
        )
    }
}