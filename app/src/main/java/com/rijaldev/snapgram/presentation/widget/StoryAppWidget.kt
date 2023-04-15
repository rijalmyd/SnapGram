package com.rijaldev.snapgram.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.rijaldev.snapgram.R
import com.rijaldev.snapgram.infrastructure.workmanager.WidgetWorker
import com.rijaldev.snapgram.presentation.detail.DetailActivity
import com.rijaldev.snapgram.util.Constants.STORY_WORKER
import java.util.concurrent.TimeUnit

class StoryAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action != null) {
            if (intent.action == CLICK_ACTION) {
                val storyId = intent.getStringExtra(EXTRA_ITEM)
                val mIntent = Intent(context, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_ID, storyId)
                }
                val pendingIntent = PendingIntent.getActivity(
                    context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                pendingIntent.send()
            }
        }
    }

    override fun onEnabled(context: Context) {
        val worker = WorkManager.getInstance(context)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<WidgetWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        worker.enqueueUniquePeriodicWork(
            STORY_WORKER,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest
        )
    }

    override fun onDisabled(context: Context) {
        val worker = WorkManager.getInstance(context)
        worker.cancelUniqueWork(STORY_WORKER)
    }

    companion object {
        private const val CLICK_ACTION = "com.rijaldev.snapgram.CLICK_ACTION"
        const val EXTRA_ITEM = "story_item"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val intent = Intent(context, StackWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_INTENT_SCHEME).toUri()
            }

            val views = RemoteViews(context.packageName, R.layout.story_app_widget).apply {
                setRemoteAdapter(R.id.stack_view, intent)
                setEmptyView(R.id.stack_view, R.id.empty_view)
            }

            val clickIntent = Intent(context, StoryAppWidget::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                action = CLICK_ACTION
            }
            val clickPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                clickIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                } else 0
            )
            views.setPendingIntentTemplate(R.id.stack_view, clickPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun notifyDataSetChanged(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context.applicationContext, StoryAppWidget::class.java)
            )
            val intent = Intent(context, StoryAppWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(intent)
        }
    }
}