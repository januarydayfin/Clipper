package com.krayapp.buffercompanion.bargen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel


class BarcodeAdapter(val onSelectionStarted: () -> Unit) :
	RecyclerView.Adapter<BarcodeViewHolder>() {
	private val differ = AsyncListDiffer(this, barcodeDiffer)


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BarcodeViewHolder(
		LayoutInflater.from(parent.context).inflate(R.layout.item_barcode_holder, parent, false)
	)

	override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
		holder.onBind(differ.currentList[position])
	}

	fun updateData(data: List<BarcodeUiModel>) {
		differ.submitList(data)
	}

	private fun selectionModeOn() {
		val listModeOn = differ.currentList.map { it.copy(selectionMode = true) }
		differ.submitList(listModeOn)
		onSelectionStarted()
	}

	fun selectionModeOff() {
		val listModeOff = differ.currentList.map {
			it.copy(
				selectionMode = true,
				checkedForDeletion = false
			)
		}
		differ.submitList(listModeOff)
	}


	fun getListIdsForDelete(): List<String> =
		differ.currentList
			.filter { it.checkedForDeletion }
			.map { it.id }

	private fun checkBarcode(model: BarcodeUiModel) {
		val newList = differ.currentList.map {
			if (it.id == model.id)
				it.copy(checkedForDeletion = !it.checkedForDeletion)
			else
				it
		}

		differ.submitList(newList)
	}

	override fun getItemCount() = differ.currentList.size

	companion object {
		private val barcodeDiffer = object : DiffUtil.ItemCallback<BarcodeUiModel>() {
			override fun areItemsTheSame(
				oldItem: BarcodeUiModel,
				newItem: BarcodeUiModel
			) = oldItem == newItem


			override fun areContentsTheSame(
				oldItem: BarcodeUiModel,
				newItem: BarcodeUiModel
			) = areItemsTheSame(oldItem, newItem)
		}
	}
}
