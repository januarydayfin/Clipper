package com.krayapp.buffercompanion.activity

import android.appwidget.AppWidgetManager
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.krayapp.buffercompanion.data.RememberedRepo
import com.krayapp.buffercompanion.databinding.MainActivityBinding
import com.krayapp.buffercompanion.widget.MainWidgetProvider
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
        vb.edit.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val text = vb.edit.text.toString()
                    repo.addText(text)
                    addStringToAdapter(text)
                    vb.edit.text.clear()
                    return true
                }
                return false
            }
        })
    }

    private fun initAdapter() {
        vb.recycler.layoutManager = LinearLayoutManager(this)
        adapter = WordsAdapter { removeString(it) }
        vb.recycler.adapter = adapter

        with(ItemTouchHelper(SwipeControl { removeString(it) })) {
            attachToRecyclerView(vb.recycler)
        }
        repo.loadList { runOnUiThread { adapter.initData(ArrayList(it)) } }
    }

    private fun removeString(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.remove(text)
        }

        runOnUiThread {
            adapter.deleteWord(text)
        }
    }

    private fun addStringToAdapter(text: String) {
        adapter.addWord(text)
    }

    override fun onStop() {
        updateWidget()
        super.onStop()
    }
    private fun updateWidget() {
        val ids: IntArray = AppWidgetManager.getInstance(application)
            .getAppWidgetIds(ComponentName(application, MainWidgetProvider::class.java))
        val intent = Intent(this, MainWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        sendBroadcast(intent)


        Handler(Looper.myLooper()!!).postDelayed({
            sendBroadcast(intent)
        }, 3000)
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