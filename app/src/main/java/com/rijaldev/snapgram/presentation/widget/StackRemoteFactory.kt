package com.rijaldev.snapgram.presentation.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.domain.model.story.Story
import com.rijaldev.snapgram.domain.usecase.widget.WidgetStoryUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class StackRemoteFactory(
    private val context: Context,
    private val widgetStoryUseCase: WidgetStoryUseCase,
) : RemoteViewsService.RemoteViewsFactory {

    private val storiesBitmap = arrayListOf<Bitmap>()
    private val stories = arrayListOf<Story>()

    override fun onCreate() {}

    override fun onDataSetChanged() = runBlocking {
        try {
            val result = widgetStoryUseCase.getStories().first()
            val bitmap = result.map {
                Glide.with(context)
                    .asBitmap()
                    .load(it.photoUrl)
                    .override(256, 256)
                    .submit()
                    .get()
            }

            storiesBitmap.clear()
            stories.clear()
            storiesBitmap.addAll(bitmap)
            stories.addAll(result)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        StoryAppWidget.notifyDataSetChanged(context)
    }

    override fun onDestroy() {}

    override fun getCount(): Int = stories.size

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.item_widget).apply {
            setImageViewBitmap(R.id.iv_story, storiesBitmap[position])
        }
        val extras = bundleOf(
            StoryAppWidget.EXTRA_ITEM to stories[position].id
        )
        val fillInIntent = Intent().apply {
            putExtras(extras)
        }

        remoteViews.setOnClickFillInIntent(R.id.iv_story, fillInIntent)
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}