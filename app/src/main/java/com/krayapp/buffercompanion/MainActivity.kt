package com.krayapp.buffercompanion

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
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
    }

    private fun initAdapter() {
        vb.recycler.layoutManager = LinearLayoutManager(this)
        adapter = WordsAdapter { removeString(it) }
        vb.recycler.adapter = adapter
        updateAdapter()
    }

    private fun updateAdapter() {
        repo.loadList {
            runOnUiThread { adapter.setData(ArrayList(it)) }
        }
    }

    private fun removeString(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.remove(text)
            updateAdapter()
        }
    }
    private fun pasteFromClip() {
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val textFromClip = manager.primaryClip?.getItemAt(0)?.text.toString()
        if (textFromClip.isNotEmpty() && textFromClip != "null") {

            repo.addText(textFromClip)
            updateAdapter()
//            sendBroadcast(Intent(this, BufferWidgetReceiver::class.java).apply {
//                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//            })
        }
    }
}