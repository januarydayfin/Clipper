package com.krayapp.buffercompanion.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.data.MainRepo
import com.krayapp.buffercompanion.data.room.StringEntity
import com.krayapp.buffercompanion.widget.MainWidgetProvider.Companion.WIDGET_COPY_ACTION

class BufferRemoteService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ViewsFactory(applicationContext)
    }
}

class ViewsFactory(private val context: Context) : RemoteViewsFactory {
    private val repo = MainRepo(context)

    private val dataList = ArrayList<StringEntity>()

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        repo.loadList {
            dataList.clear()
            dataList.addAll(it)
        }
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        if (count == 0)
            return RemoteViews(context.packageName, R.layout.item_list_widget)

        if (position > count)
            onDataSetChanged()

        val remoteView = RemoteViews(context.packageName, R.layout.item_list_widget)
        runCatching { remoteView.setCharSequence(R.id.text, "setText", dataList[position].text) }


        val intent = Intent(context, MainWidgetProvider::class.java).apply {
            action = WIDGET_COPY_ACTION
            try {
                putExtra(WIDGET_COPY_ACTION, dataList[position].text)
            } catch (e: Exception) {
                putExtra(WIDGET_COPY_ACTION, "")
            }
        }

        if (dataList[position].text == context.getString(R.string.empty))
            remoteView.setOnClickFillInIntent(R.id.widgetItemRoot, Intent())
        else
            remoteView.setOnClickFillInIntent(R.id.widgetItemRoot, intent)
        return remoteView
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.loading_layout)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return dataList[position].hashCode().toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

}