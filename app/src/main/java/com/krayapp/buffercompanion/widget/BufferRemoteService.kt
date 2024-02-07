package com.krayapp.buffercompanion.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.activity.MainActivity
import com.krayapp.buffercompanion.data.RememberedRepo
import com.krayapp.buffercompanion.data.room.StringEntity
import com.krayapp.buffercompanion.widget.MainWidgetProvider.Companion.OPEN_ACTIVITY_ACTION
import com.krayapp.buffercompanion.widget.MainWidgetProvider.Companion.WIDGET_COPY_ACTION

class BufferRemoteService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ViewsFactory(applicationContext, intent)
    }
}

class ViewsFactory(private val context: Context, private val intent: Intent?) : RemoteViewsFactory {
    private val repo = RememberedRepo(context)

    private val dataList =
        ArrayList<StringEntity>().apply { add(StringEntity(context.packageName)) }

    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        repo.loadList {
            dataList.clear()
            dataList.add(StringEntity(context.packageName))
            dataList.addAll(it)
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        if (dataList[position] == StringEntity(context.packageName)) {
            val appView = RemoteViews(context.packageName, R.layout.widget_header)

            val intent = Intent(context, MainActivity::class.java).apply {
                action = OPEN_ACTIVITY_ACTION
            }

            appView.setOnClickFillInIntent(
                R.id.openApp,
                intent
            )
            return appView
        }
        val remoteView = RemoteViews(context.packageName, R.layout.item_list_widget).apply {
            try {
                setCharSequence(
                    R.id.text,
                    "setText",
                    dataList[position].text
                )
            } catch (e: Exception) {
                setCharSequence(
                    R.id.text,
                    "setText",
                    ""
                )
            }

        }

        val intent = Intent(context, MainWidgetProvider::class.java).apply {
            action = WIDGET_COPY_ACTION
            try {
                putExtra(WIDGET_COPY_ACTION, dataList[position].text)
            } catch (e: Exception) {
                putExtra(WIDGET_COPY_ACTION, "")
            }
        }

        remoteView.setOnClickFillInIntent(R.id.itemRoot, intent)

        return remoteView
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.loading_layout)
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemId(position: Int): Long {
        return 0L
    }

    override fun hasStableIds(): Boolean {
        return true
    }

}