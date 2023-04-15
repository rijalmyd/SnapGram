package com.rijaldev.snapgram.presentation.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.rijaldev.snapgram.domain.usecase.widget.WidgetStoryUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StackWidgetService : RemoteViewsService() {

    @Inject
    lateinit var widgetStoryUseCase: WidgetStoryUseCase

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory =
        StackRemoteFactory(this.applicationContext, widgetStoryUseCase)
}
