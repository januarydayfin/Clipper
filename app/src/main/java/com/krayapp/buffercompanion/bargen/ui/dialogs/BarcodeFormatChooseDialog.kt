package com.krayapp.buffercompanion.bargen.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.ScanOptions.CODE_128
import com.journeyapps.barcodescanner.ScanOptions.CODE_39
import com.journeyapps.barcodescanner.ScanOptions.CODE_93
import com.journeyapps.barcodescanner.ScanOptions.DATA_MATRIX
import com.journeyapps.barcodescanner.ScanOptions.EAN_13
import com.journeyapps.barcodescanner.ScanOptions.EAN_8
import com.journeyapps.barcodescanner.ScanOptions.ITF
import com.journeyapps.barcodescanner.ScanOptions.PDF_417
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE
import com.journeyapps.barcodescanner.ScanOptions.RSS_14
import com.journeyapps.barcodescanner.ScanOptions.RSS_EXPANDED
import com.journeyapps.barcodescanner.ScanOptions.UPC_A
import com.journeyapps.barcodescanner.ScanOptions.UPC_E
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.databinding.ChooseFormatDialogBinding
import com.krayapp.buffercompanion.bargen.utils.setCustomBackground

class BarcodeFormatChooseDialog(
    private val currentFormat: BarcodeFormat,
    private val onFormatSelected: (BarcodeFormat) -> Unit
) : DialogFragment() {
    private var binding: ChooseFormatDialogBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChooseFormatDialogBinding.inflate(inflater)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.setCustomBackground(R.drawable.dialog_background)
        binding?.run {
            val entries = listOf(
                BarcodeFormat.AZTEC,
                UPC_A,
                UPC_E,
                EAN_8,
                EAN_13,
                RSS_14,
                CODE_39,
                CODE_93,
                CODE_128,
                ITF,
                RSS_EXPANDED,
                QR_CODE,
                DATA_MATRIX,
                PDF_417
            )
            entries.forEach {
                radioGroup.addView(radioButtonFrom(it.toString()))
            }

            cancel.setOnClickListener {
                dismiss()
            }
            apply.setOnClickListener {
                val checkedId = radioGroup.checkedRadioButtonId
                val checkedButton = root.findViewById<RadioButton>(checkedId)

                runCatching {
                    onFormatSelected(BarcodeFormat.valueOf(checkedButton.text.toString()))
                }

                dismiss()
            }
        }
        binding?.radioGroup?.check(getPrecheckId() ?: -1)

    }

    private fun getPrecheckId(): Int? {
        var id: Int? = null
        binding?.radioGroup?.children?.forEach {
            it as RadioButton

            if (it.text == currentFormat.toString()) {
                id = it.id
                return@forEach
            }
        }
        return id
    }

    private fun radioButtonFrom(format: String) = RadioButton(context).apply {
        text = format
    }
}