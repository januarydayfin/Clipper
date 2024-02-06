package com.krayapp.buffercompanion

import android.appwidget.AppWidgetManager
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.krayapp.buffercompanion.data.RememberedRepo
import com.krayapp.buffercompanion.databinding.MainActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var vb: MainActivityBinding
    private lateinit var repo: RememberedRepo
    private lateinit var adapter: WordsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = MainActivityBinding.inflate(layoutInflater)
        setContentView(vb.root)
        repo = RememberedRepo(this)
        initAdapter()

        vb.paste.setOnClickListener { pasteFromClip() }
        vb.update.setOnClickListener { updateWidget() }
    }

    private fun initAdapter() {
        vb.recycler.layoutManager = LinearLayoutManager(this)
        adapter = WordsAdapter { removeString(it) }
        vb.recycler.adapter = adapter

        repo.loadList { adapter.initData(ArrayList(it)) }
    }

    private fun removeString(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.remove(text)

        }

        runOnUiThread {
            adapter.deleteWord(text)
            updateWidget()
        }
    }

    private fun addStringToAdapter(text: String) {
        adapter.addWord(text)
        updateWidget()
    }

    private fun updateWidget() {
        val ids: IntArray = AppWidgetManager.getInstance(application)
            .getAppWidgetIds(ComponentName(application, MainWidgetProvider::class.java))
        val intent = Intent(this, MainWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }

        Handler(Looper.myLooper()!!).postDelayed({
            sendBroadcast(intent)

        }, 1000)
    }

    private fun pasteFromClip() {
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val textFromClip = manager.primaryClip?.getItemAt(0)?.text.toString()

        if (textFromClip.isNotEmpty() && textFromClip != "null") {
            repo.addText(textFromClip)
            addStringToAdapter(textFromClip)
        }
    }
}