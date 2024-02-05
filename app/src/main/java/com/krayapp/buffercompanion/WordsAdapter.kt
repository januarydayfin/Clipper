package com.krayapp.buffercompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krayapp.buffercompanion.databinding.ItemListBinding

class WordsAdapter() : RecyclerView.Adapter<WordViewHolder>() {
	private val data = ArrayList<String>()

	fun setData(data: ArrayList<String>) {
		this.data.clear()
		this.data.addAll(data)
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
		return WordViewHolder(
			LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
		)
	}

	override fun getItemCount(): Int {
		return data.size
	}

	override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
		if (position < data.size)
			holder.onBind(data[position])
	}
}

class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
	private val vb = ItemListBinding.bind(view)
	fun onBind(text: String) {
		vb.text.text = text
	}
}