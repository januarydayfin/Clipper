package com.krayapp.buffercompanion.bargen.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.krayapp.buffercompanion.bargen.databinding.ItemBarcodeHolderBinding
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel

class BarcodeViewHolder(view: View) : ViewHolder(view) {
    private val binding = ItemBarcodeHolderBinding.bind(view)

    fun onBind(uiModel: BarcodeUiModel) {

    }
}