package com.krayapp.buffercompanion.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import com.krayapp.buffercompanion.R

class MainWidgetProvider : AppWidgetProvider() {
    companion object {
        val WIDGET_COPY_ACTION = "com.krayapp.buffercompanion.WIDGET_COPY_ACTION"
        val OPEN_ACTIVITY_ACTION = "com.krayapp.buffercompanion.OPEN_ACTIVITY"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            WIDGET_COPY_ACTION -> {
                val text = intent.getStringExtra(WIDGET_COPY_ACTION)
                copy(context, text ?: "")
            }
            OPEN_ACTIVITY_ACTION -> context.startActivity(context.packageManager.getLaunchIntentForPackage(context.packageName))
        }
    }

    private fun copy(context: Context, text: String) {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        manager.setPrimaryClip(clip)
    }


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val serviceIntent = Intent(context, BufferRemoteService::class.java)

        val views = RemoteViews(context.packageName, R.layout.layout_widget).apply {
            setRemoteAdapter(R.id.listView, serviceIntent)
            setPendingIntentTemplate(
                R.id.listView,
                Intent(context, MainWidgetProvider::class.java).run {
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        this,
                        FLAG_UPDATE_CURRENT or FLAG_MUTABLE
                    )
                })
        }


        Handler(Looper.myLooper()!!).postDelayed({
            appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetIds,
                R.id.listView
            )
        }, 3000)

        appWidgetManager.updateAppWidget(appWidgetIds, views)
    }

}