package com.krayapp.buffercompanion.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.data.room.StringEntity
import com.krayapp.buffercompanion.databinding.ItemListBinding
import com.krayapp.buffercompanion.databinding.MainScreenAdapterItemBinding

class WordsAdapter(private val onDelete: (String) -> Unit) :
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
            holder.onBind(data[position].text, onDelete)
    }
}

class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val vb = MainScreenAdapterItemBinding.bind(view)
    fun onBind(text: String, onDelete: (String) -> Unit) {
        vb.text.text = text
    }

    fun getText(): String {
        return vb.text.text.toString()
    }
}