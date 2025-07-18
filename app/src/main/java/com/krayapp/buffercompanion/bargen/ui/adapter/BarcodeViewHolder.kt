package com.krayapp.buffercompanion.bargen.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.krayapp.buffercompanion.bargen.databinding.ItemBarcodeHolderBinding
import com.krayapp.buffercompanion.bargen.justVibrateABit
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.utils.filterChip
import com.krayapp.buffercompanion.bargen.utils.onTouchCoordinates

class BarcodeViewHolder(view: View) : ViewHolder(view) {
    private val binding = ItemBarcodeHolderBinding.bind(view)

    fun onBind(
        uiModel: BarcodeUiModel,
        onOpenBarcode: (BarcodeUiModel) -> Unit,
        onContextMenuCalled: (x: Int, y: Int, v: View) -> Unit,
        onChecked: (BarcodeUiModel) -> Unit,
    ) {
        var translationX = 0
        var translationY = 0

        with(binding) {
            name.text = uiModel.name
            barcodeType.text = uiModel.barcodeType
            tags.removeAllViews()
            uiModel.tags.forEach {
                tags.addView(binding.root.context.filterChip(it, false))
            }
            root.onTouchCoordinates { x, y ->
                translationX = x.toInt()
                translationY = y.toInt()
            }
            root.setOnClickListener {
                onOpenBarcode(uiModel)
            }

            root.setOnLongClickListener { v ->
                if (!uiModel.selectionMode) {
                    v.context.justVibrateABit()
                    onContextMenuCalled(translationX, translationY, v)
                }
                true
            }
            contextMenuCall.setOnClickListener {
                if (uiModel.selectionMode)
                    onChecked(uiModel)
                else
                    onContextMenuCalled(translationX, translationY, binding.contextMenuCall)
            }
        }
    }
}