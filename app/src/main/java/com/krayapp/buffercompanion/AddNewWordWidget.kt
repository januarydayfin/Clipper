package com.krayapp.buffercompanion

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class AddNewWordWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val views = RemoteViews(context.packageName, R.layout.add_new_word_widget).apply {
            setOnClickPendingIntent(
                R.id.addRoot,
                PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(context, BufferWidgetReceiver::class.java).apply {
                        putExtra(
                            BufferWidgetReceiver.WIDGET_PASTE_ACTION,
                            "getFromClipboard(context)"
                        )
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
        appWidgetManager.updateAppWidget(appWidgetIds, views)
    }
}