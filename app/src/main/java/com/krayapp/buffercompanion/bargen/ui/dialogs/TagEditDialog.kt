package com.krayapp.buffercompanion.bargen.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.krayapp.buffercompanion.bargen.addTextWatcher
import com.krayapp.buffercompanion.bargen.databinding.DialogTagEditBinding
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.decodedSize
import com.krayapp.buffercompanion.bargen.utils.filterChip

class TagEditDialog(uiModel: TagUiModel, private val saveModel: (TagUiModel) -> Unit) :
    DialogFragment() {
    private var binding: DialogTagEditBinding? = null

    private var editedModel = uiModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogTagEditBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)

        binding?.run {
            tagName.setText(editedModel.name)

            tagName.addTextWatcher {
                editedModel = editedModel.copy(name = it)
                render()
            }

            tagBackgroundColor.setOnClickListener { v ->
                v.context.showColorPickerDialog {
                    editedModel = editedModel.copy(backgroundColor = it)
                    render()
                }
            }

            tagFontColor.setOnClickListener { v ->
                v.context.showColorPickerDialog {
                    editedModel = editedModel.copy(fontColor = it)
                    render()
                }
            }

            apply.setOnClickListener {
                saveModel(editedModel)
                dismiss()
            }
            cancel.setOnClickListener {
                dismiss()
            }
        }
        render()
    }


    private fun render() {
        binding?.run {
            editedModel.backgroundColor?.run {
                tagBackgroundColor.setColorFilter(this)
            }
            editedModel.fontColor?.run {
                tagFontColor.setColorFilter(this)
            }

            chipPreview.removeAllViews()
            chipPreview.addView(chipPreview.context.filterChip(editedModel))
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding = null
    }
}