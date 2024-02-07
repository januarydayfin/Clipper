package com.krayapp.buffercompanion.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.data.room.StringEntity
import com.krayapp.buffercompanion.databinding.MainScreenAdapterItemBinding

class WordsAdapter(private val onClicked: (String) -> Unit) :
    RecyclerView.Adapter<WordViewHolder>() {
    private val data = ArrayList<StringEntity>()

    fun addWord(text: String) {
        if (!data.contains(StringEntity(text))) {
            data.add(StringEntity(text))
            notifyItemInserted(data.size)
        }
    }

    fun deleteWord(text: String) {
        var index = -1
        for (i in 0 until data.size) {
            if (text == data[i].text) {
                index = i
                break
            }
        }
        if (index != -1) {
            data.removeAt(index)
            notifyDataSetChanged()
        }
    }

    fun initData(data: ArrayList<StringEntity>) {
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.main_screen_adapter_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        if (position < data.size)
            holder.onBind(data[position].text, onClicked)
    }
}

class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val vb = MainScreenAdapterItemBinding.bind(view)
    private var originText = ""
    fun onBind(text: String, onClicked: (String) -> Unit) {
        originText = text
        vb.text.text = text
        vb.root.setOnClickListener { onClicked(originText) }
    }

    fun getText(): String {
        return vb.text.text.toString()
    }
}