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
import com.krayapp.buffercompanion.switchState
import com.krayapp.buffercompanion.ui.fragments.interfaces.ListEditWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections


class WordsAdapter(private val watcher: ListEditWatcher) : RecyclerView.Adapter<WordViewHolder>() {
	private val data = ArrayList<StringEntity>()
	private val removeList = ArrayList<StringEntity>()

	private var onStartDrag: ((RecyclerView.ViewHolder) -> Unit)? = null

	private var inEditionCardsCount = 0

	var inEditionMode = false


	//PRIVATE----->
	private fun deleteWord(item: StringEntity) {
		var index = -1
		for (i in 0 until data.size) {
			if (item == data[i]) {
				index = i
				break
			}
		}
		if (index != -1) {
			data.removeAt(index)
			notifyItemRemoved(index)
		}
	}

	private fun addToRemoveList(item: StringEntity) {
		if (removeList.contains(item)) {
			removeList.remove(item)
			inEditionCardsCount--
			if (removeList.size == 0)
				resetEdition()
		} else {
			removeList.add(item)
			inEditionCardsCount++
		}
	}


	//ADAPTER IMPL----->
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
		return WordViewHolder(
			LayoutInflater.from(parent.context).inflate(R.layout.adapter_item, parent, false)
		)
	}

	override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
		if (position < data.size) {
			holder.onBind(item = data[position],
				onCopy = { watcher.onCopyClicked(it) },
				editionFlag = inEditionMode,
				onStartDrag = onStartDrag,
				onEditionModeOn = {
					if (!inEditionMode) {
						watcher.onEditionStart()
						inEditionCardsCount = 0
						inEditionMode = true
						watcher.onCheckRemoveStart()
						notifyItemRangeChanged(0, data.size)
					}
					addToRemoveList(it)
				},
				itemInRemoveList = removeList.contains(data[position]),
				onRemove = {
					deleteWord(it)
					watcher.onRemoveClicked(it)
				},
				onEdit = { watcher.onEditClicked(it) },
				onMenuOpened = {
					inEditionCardsCount++
					watcher.onEditionStart()
				},
				onMenuClosed = {
					inEditionCardsCount--
					if (inEditionCardsCount <= 0) watcher.onEditionReset()
				})
		}
	}

	override fun getItemCount(): Int {
		return data.size
	}


	//PUBLIC----->

	fun addWord(text: String) {
		if (!data.contains(StringEntity(text))) {
			data.add(StringEntity(text))
			notifyItemInserted(data.size)
		}
	}

	fun updateWord(oldKey: String, new: StringEntity) {
		var index = 0
		for (i in 0 until data.size) {
			val item = data[i]
			if (item.text == oldKey) {
				item.text = new.text
				index = i
				break
			}
		}
		watcher.onEditionReset()
		notifyItemChanged(index)
	}

	fun checkAllRemove() {
		if (removeList.size == data.size)
			removeList.clear()
		else {
			removeList.clear()
			removeList.addAll(data)
		}
		notifyItemRangeChanged(0, data.size)
	}

	fun removeChecked() {
		data.removeAll(removeList.toSet())

		CoroutineScope(Dispatchers.IO).launch {
			delay(3000)
			removeList.clear()
		}
		watcher.onEditionReset()
		inEditionMode = false
		notifyDataSetChanged()
	}

	fun getCheckedList() : List<StringEntity> {
		return removeList
	}

	fun setDrag(
		onStartDrag: (RecyclerView.ViewHolder) -> Unit,
	) {
		this.onStartDrag = onStartDrag
	}

	fun getUpdatedIndexData(): ArrayList<StringEntity> {
		for (i in 0 until data.size) data[i].position = i

		return data
	}

	fun initData(data: ArrayList<StringEntity>) {
		this.data.addAll(data)
		notifyItemRangeInserted(0, data.size)
	}


	fun resetEdition() {
		inEditionCardsCount = 0
		inEditionMode = false
		removeList.clear()
		notifyItemRangeChanged(0, data.size)
		watcher.onEditionReset()
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
}

class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
	private val vb = AdapterItemBinding.bind(view)
	private lateinit var currentItem: StringEntity
	private var editionFlag = false
	private var itemInRemoveList = false

	private lateinit var onMenuOpened: (Any) -> Unit
	private lateinit var onEditionModeOn: (StringEntity) -> Unit
	private lateinit var onMenuClosed: (Any) -> Unit
	private lateinit var onCopy: (StringEntity) -> Unit
	private var onStartDrag: ((RecyclerView.ViewHolder) -> Unit)? = null
	private lateinit var onRemove: (StringEntity) -> Unit
	private lateinit var onEdit: (StringEntity) -> Unit

	@SuppressLint("ClickableViewAccessibility")
	fun onBind(
		item: StringEntity,
		editionFlag: Boolean,
		itemInRemoveList: Boolean,
		onCopy: (StringEntity) -> Unit,
		onStartDrag: ((RecyclerView.ViewHolder) -> Unit)? = null,
		onEditionModeOn: (StringEntity) -> Unit,
		onRemove: (StringEntity) -> Unit,
		onEdit: (StringEntity) -> Unit,
		onMenuOpened: (Any) -> Unit,
		onMenuClosed: (Any) -> Unit
	) {
		currentItem = item
		this.editionFlag = editionFlag
		this.itemInRemoveList = itemInRemoveList
		this.onEditionModeOn = onEditionModeOn
		this.onMenuOpened = onMenuOpened
		this.onMenuClosed = onMenuClosed
		this.onCopy = onCopy
		this.onStartDrag = onStartDrag
		this.onRemove = onRemove
		this.onEdit = onEdit

		renderViews()
		setClick()
	}

	private fun renderViews() {
		vb.drag.isVisible = !editionFlag
		vb.checkBox.isVisible = editionFlag
		vb.checkBox.isChecked = itemInRemoveList
		vb.text.text = currentItem.text
		vb.itemMenu.root.isVisible = false
	}

	private fun setClick() {
		vb.root.setOnClickListener {
			if (editionFlag) {
				vb.checkBox.switchState()
				onEditionModeOn(currentItem)
			} else
				onCopy(currentItem)
		}
		vb.root.setOnLongClickListener {
			onEditionModeOn(currentItem)
			return@setOnLongClickListener true
		}
		vb.drag.setOnLongClickListener {
			if (onStartDrag != null) onStartDrag!!(this)
			return@setOnLongClickListener true
		}
		vb.itemMenu.remove.setOnClickListener { onRemove(currentItem) }
		vb.itemMenu.edit.setOnClickListener { onEdit(currentItem) }
	}

	fun showMenu(show: Boolean) {
		if (vb.itemMenu.root.isVisible != show) {
			if (show) {
				vb.root.context.justVibrateABit()
				onMenuOpened("")
			} else onMenuClosed("")

			vb.itemMenu.root.isVisible = show
		}
	}
}