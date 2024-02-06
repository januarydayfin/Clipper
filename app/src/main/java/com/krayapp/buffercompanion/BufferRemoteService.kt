package com.krayapp.buffercompanion

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.krayapp.buffercompanion.BufferWidgetReceiver.Companion.WIDGET_COPY_ACTION
import com.krayapp.buffercompanion.data.RememberedRepo
import com.krayapp.buffercompanion.data.room.StringEntity

class BufferRemoteService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return ViewsFactory(applicationContext, intent)
    }
}


class ViewsFactory(private val context: Context, private val intent: Intent?) : RemoteViewsFactory {
    private val repo = RememberedRepo(context)

    private val dataList = ArrayList<StringEntity>()

    override fun onCreate() {
        dataList.clear()
        repo.loadList { dataList.addAll(it) }
    }

    override fun onDataSetChanged() {
        dataList.clear()
        repo.loadList { dataList.addAll(it) }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.item_list).apply {
            setCharSequence(R.id.text, "setText", dataList[position].text)
        }

        val intent = Intent(context, BufferWidgetReceiver::class.java).apply {
            putExtra(WIDGET_COPY_ACTION, dataList[position].text)
        }

        remoteView.setOnClickFillInIntent(R.id.itemRoot, intent)

        return remoteView
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.loading_layout)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return 0L
    }

    override fun hasStableIds(): Boolean {
        return true
    }

}