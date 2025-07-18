package com.krayapp.buffercompanion.bargen.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.krayapp.buffercompanion.bargen.databinding.DialogBarcodeInfoBinding
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel

class BarcodeDialog(private val uiModel: BarcodeUiModel) : DialogFragment() {
    private var binding: DialogBarcodeInfoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogBarcodeInfoBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            name.text = uiModel.name
            description.text = uiModel.description
            barcode.setImageBitmap(uiModel.image)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding = null
    }
}