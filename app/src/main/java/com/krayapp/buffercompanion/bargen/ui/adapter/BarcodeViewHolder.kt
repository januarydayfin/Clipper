package com.krayapp.buffercompanion.bargen.ui.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.krayapp.buffercompanion.bargen.databinding.ItemBarcodeHolderBinding
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.utils.filterChip

class BarcodeViewHolder(private val view: View) : ViewHolder(view) {
    fun onBind(
        uiModel: BarcodeUiModel,
        onOpenBarcode: (BarcodeUiModel) -> Unit,
        onContextMenuCalled: (uiModel: BarcodeUiModel, v: View) -> Unit,
        onChecked: (BarcodeUiModel) -> Unit,
    ) {
        val binding = ItemBarcodeHolderBinding.bind(view)

        with(binding) {
            name.text = uiModel.name
            barcodeType.text = uiModel.barcodeType
            tags.removeAllViews()
            uiModel.tags.forEach {
                tags.addView(binding.root.context.filterChip(it, false))
            }
            root.setOnClickListener {
                if (uiModel.selectionMode)
                    onChecked(uiModel)
                else
                    onOpenBarcode(uiModel)
            }
            checkbox.isChecked = uiModel.checkedForDeletion
            checkbox.isVisible = uiModel.selectionMode
            contextMenu.isVisible = !uiModel.selectionMode

            contextMenuCall.setOnClickListener {
                if (uiModel.selectionMode)
                    onChecked(uiModel)
                else
                    onContextMenuCalled(uiModel, binding.contextMenuCall)
            }

        }
    }
}