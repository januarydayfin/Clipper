package com.krayapp.buffercompanion.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.data.room.StringEntity
import com.krayapp.buffercompanion.databinding.AdapterItemBinding
import com.krayapp.buffercompanion.justVibrateABit
import java.util.Collections


class WordsAdapter(
    private val onClicked: (String) -> Unit,
    private var onRemoveClicked: (String) -> Unit,
    private var onEditClicked: (String) -> Unit
) :
    RecyclerView.Adapter<WordViewHolder>() {
    private val data = ArrayList<StringEntity>()
    private var onStartDrag: ((RecyclerView.ViewHolder) -> Unit)? = null

    fun addWord(text: String) {
        if (!data.contains(StringEntity(text))) {
            data.add(StringEntity(text))
            notifyItemInserted(data.size)
        }
    }

    private fun deleteWord(text: String) {
        var index = -1
        for (i in 0 until data.size) {
            if (text == data[i].text) {
                index = i
                break
            }
        }
        if (index != -1) {
            data.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun setDrag(
        onStartDrag: (RecyclerView.ViewHolder) -> Unit,
    ) {
        this.onStartDrag = onStartDrag
    }


    fun getUpdatedIndexData(): ArrayList<StringEntity> {
        for (i in 0 until data.size)
            data[i].position = i

        return data
    }

    fun initData(data: ArrayList<StringEntity>) {
        this.data.addAll(data)
        notifyItemRangeInserted(0, data.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_item, parent, false)
        )
    }

    fun resetMenus() {
        notifyItemRangeChanged(0, data.size)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun onItemMove(fromPos: Int, toPos: Int) {
        if (fromPos < toPos) {
            for (i in fromPos until toPos) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in fromPos downTo toPos + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        notifyItemMoved(fromPos, toPos)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        if (position < data.size) {
            holder.onBind(
                text = data[position].text,
                onClicked = onClicked,
                onStartDrag = onStartDrag,
                onRemove = {
                    deleteWord(it)
                    onRemoveClicked(it)
                },
                onEdit = { onEditClicked(it) }
            )
        }
    }
}

class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val vb = AdapterItemBinding.bind(view)
    private var originText = ""

    @SuppressLint("ClickableViewAccessibility")
    fun onBind(
        text: String,
        onClicked: (String) -> Unit,
        onStartDrag: ((RecyclerView.ViewHolder) -> Unit)? = null,
        onRemove: (String) -> Unit,
        onEdit: (String) -> Unit
    ) {
        originText = text
        vb.text.text = text
        vb.root.setOnClickListener { onClicked(originText) }
        vb.drag.setOnLongClickListener {
            if (onStartDrag != null)
                onStartDrag(this)

            return@setOnLongClickListener true
        }
        vb.itemMenu.root.isVisible = false

        vb.itemMenu.remove.setOnClickListener { onRemove(originText) }
        vb.itemMenu.edit.setOnClickListener { onEdit(originText) }
    }

    fun showMenu(show: Boolean) {
        if (vb.itemMenu.root.isVisible != show) {
            if (show)
                vb.root.context.justVibrateABit()
            vb.itemMenu.root.isVisible = show
        }
    }
}