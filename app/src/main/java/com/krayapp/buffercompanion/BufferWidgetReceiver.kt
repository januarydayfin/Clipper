package com.krayapp.buffercompanion

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteCollectionItems
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import com.krayapp.buffercompanion.databinding.LayoutMainWidgetScreenBinding

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
			Log.d("TESTET", String.format("copy %s", text));
			copy(context, text ?: "")
		}
		if (intent.hasExtra(WIDGET_PASTE_ACTION)) {
			Log.d("TESTET", String.format("%s", getFromClipboard(context)));
			Toast.makeText(context, getFromClipboard(context), Toast.LENGTH_SHORT).show()
		}
	}

	private fun copy(context: Context, text: String) {
		val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		val clip = ClipData.newPlainText("label", text)
		manager.setPrimaryClip(clip)
	}

	private fun getFromClipboard(context: Context): String {
		val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
		return manager.text.toString()
	}

	override fun onUpdate(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetIds: IntArray
	) {

		val intent = Intent(context, BufferWidgetReceiver::class.java).apply { putExtra(WIDGET_PASTE_ACTION, "") }
		val views = RemoteViews(
			context.packageName,
			R.layout.layout_main_widget_screen
		).apply {
			setOnClickPendingIntent(
				R.id.add, PendingIntent.getBroadcast(
					context, 0,
					intent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
				)
			)
			setRemoteAdapter(R.id.listView, getRemoteList(context))
		}

		appWidgetManager.updateAppWidget(appWidgetIds, views)
	}

	private fun getRemoteList(context: Context): RemoteCollectionItems {
		val remotes = ArrayList<RemoteViews>()
		val prefs = PreferenceManager(context)
		for (string in prefs.getStrings()) {
			remotes.add(
				RemoteViews(context.packageName, R.layout.item_list).apply {
					setCharSequence(R.id.text, "setText", string)
					setOnClickPendingIntent(
						R.id.copy,
						PendingIntent.getBroadcast(
							context,
							0,
							Intent(context, BufferWidgetReceiver::class.java).apply {
								putExtra(
									WIDGET_COPY_ACTION,
									string
								)
							},
							FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
						)
					)
				}
			)
		}
		val collectionBuilder = RemoteCollectionItems.Builder()

		for (remote in remotes) {
			collectionBuilder.addItem(remote.viewId.toLong(), remote)
		}

		with(collectionBuilder) {
			setHasStableIds(true)
		}

		return collectionBuilder.build()
	}

}