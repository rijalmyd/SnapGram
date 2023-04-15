package com.rijaldev.snapgram.infrastructure.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rijaldev.snapgram.presentation.widget.StoryAppWidget

class WidgetWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            StoryAppWidget.notifyDataSetChanged(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}