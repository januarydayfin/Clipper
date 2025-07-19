package com.krayapp.buffercompanion.bargen.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.ScanOptions.DATA_MATRIX
import com.journeyapps.barcodescanner.ScanOptions.PDF_417
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.databinding.ItemBarcodeHolderBinding
import com.krayapp.buffercompanion.bargen.justVibrateABit
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
            content.text = uiModel.content
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
            barcodePreview.setImageDrawable(view.context.barcodeExample(uiModel.barcodeType))
            checkbox.isChecked = uiModel.checkedForDeletion
            checkbox.isVisible = uiModel.selectionMode
            contextMenu.isVisible = !uiModel.selectionMode

            root.setOnLongClickListener {
                if (!uiModel.selectionMode) {
                    root.context.justVibrateABit()
                    onContextMenuCalled(uiModel, binding.name)
                }

                false
            }
            contextMenuCall.setOnClickListener {
                if (uiModel.selectionMode)
                    onChecked(uiModel)
                else
                    onContextMenuCalled(uiModel, binding.contextMenuCall)
            }

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun Context.barcodeExample(type: String) =
        getDrawable(
            when (type) {
                PDF_417 -> R.drawable.pdf417_example
                BarcodeFormat.AZTEC.toString() -> R.drawable.aztec_example
                QR_CODE -> R.drawable.qr_example_list
                DATA_MATRIX -> R.drawable.datamatrix_example
                else -> R.drawable.ean_example
            }
        )
}
