package com.krayapp.buffercompanion.bargen.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.bargenCore.generator.BarcodeGenerator
import com.krayapp.buffercompanion.bargen.databinding.DialogBarcodeInfoBinding
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.utils.runOnUi

class BarcodeDialog(private val uiModel: BarcodeUiModel) : DialogFragment() {
    private var binding: DialogBarcodeInfoBinding? = null
    private val generator = BarcodeGenerator
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

            runOnUi {
                barcode.setImageBitmap(
                    generator.generate(
                        content = uiModel.content,
                        type = BarcodeFormat.valueOf(uiModel.barcodeType),
                        width = ClipperApp.displayWidth,
                        height = ClipperApp.displayWidth / 2
                    )
                )
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding = null
    }
}