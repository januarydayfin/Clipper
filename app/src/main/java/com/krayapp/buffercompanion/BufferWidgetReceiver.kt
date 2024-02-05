package com.krayapp.buffercompanion

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteCollectionItems
import android.widget.Toast

class BufferWidgetReceiver : AppWidgetProvider() {
    companion object {
        val WIDGET_COPY_ACTION = "WIDGET_COPY_ACTION"
        val WIDGET_PASTE_ACTION = "WIDGET_PASTE_ACTION"
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val prefs = PreferenceManager(context)

        if (intent.hasExtra(WIDGET_COPY_ACTION)) {
            val text = intent.getStringExtra(WIDGET_COPY_ACTION)
            copy(context, text ?: "")
        }
        if (intent.hasExtra(WIDGET_PASTE_ACTION)) {
            Toast.makeText(context, getFromClipboard(context), Toast.LENGTH_SHORT).show()
        }
    }

    private fun copy(context: Context, text: String) {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        manager.setPrimaryClip(clip)
    }

    private fun getFromClipboard(context: Context): String {
        try {
            val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            return manager.text.toString()
        } catch (e: Exception) {
            return ""
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val serviceIntent = Intent(context, BufferRemoteService::class.java)

        val views = RemoteViews(context.packageName, R.layout.layout_main_widget_screen).apply {
            setRemoteAdapter(R.id.listView, serviceIntent)
        }

        val intent = Intent(context, BufferWidgetReceiver::class.java).run {
            PendingIntent.getBroadcast(context, 0, this, FLAG_UPDATE_CURRENT or FLAG_MUTABLE)
        }

        views.setPendingIntentTemplate(R.id.listView, intent)
        appWidgetManager.updateAppWidget(appWidgetIds, views)
    }

}