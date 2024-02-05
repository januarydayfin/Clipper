package com.krayapp.buffercompanion

import android.appwidget.AppWidgetManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.krayapp.buffercompanion.BufferWidgetReceiver.Companion.WIDGET_PASTE_ACTION
import com.krayapp.buffercompanion.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
	private lateinit var vb: MainActivityBinding
	private lateinit var prefs: PreferenceManager
	private lateinit var adapter: WordsAdapter
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		prefs = PreferenceManager(this)
		vb = MainActivityBinding.inflate(layoutInflater)
		setContentView(vb.root)

		initAdapter()

		vb.paste.setOnClickListener { pasteFromClip() }
	}

	private fun initAdapter() {
		vb.recycler.layoutManager = LinearLayoutManager(this)
		adapter = WordsAdapter()
		vb.recycler.adapter = adapter
		adapter.setData(prefs.getStrings())
	}

	private fun pasteFromClip() {
		val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

		val textFromClip = manager.primaryClip?.getItemAt(0)?.text.toString()
		if (textFromClip != "null") {
			prefs.putString(textFromClip)
			adapter.setData(prefs.getStrings())

			sendBroadcast(Intent(this, BufferWidgetReceiver::class.java).apply {
				action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
			})
		}
	}
}