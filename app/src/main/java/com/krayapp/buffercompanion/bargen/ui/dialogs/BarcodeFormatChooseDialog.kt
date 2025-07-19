package com.krayapp.buffercompanion.bargen.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.databinding.ChooseFormatDialogBinding

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

        binding?.run {
            BarcodeFormat.entries.forEach {
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