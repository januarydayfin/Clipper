package com.krayapp.buffercompanion.bargen.ui.bottomsheets

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.addTextWatcher
import com.krayapp.buffercompanion.bargen.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.bargenCore.generator.BarcodeGenerator
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity
import com.krayapp.buffercompanion.bargen.databinding.BottomsheetCreateCodeBinding
import com.krayapp.buffercompanion.bargen.expand
import com.krayapp.buffercompanion.bargen.onImeAction
import com.krayapp.buffercompanion.bargen.ui.dialogs.BarcodeFormatChooseDialog
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.colorNavBar
import com.krayapp.buffercompanion.bargen.utils.decodedSize
import com.krayapp.buffercompanion.bargen.utils.displayWidth
import com.krayapp.buffercompanion.bargen.utils.filterChip
import com.krayapp.buffercompanion.bargen.utils.runOnUi
import com.krayapp.buffercompanion.bargen.utils.toEntity
import java.util.UUID

class CreateBarcodeBottomsheet(
    private val existModel: BarcodeUiModel? = null,
    private val saveBarcodeAndTags: (
        barcode: BarcodeEntity, tags: List<TagEntity>
    ) -> Unit
) :
    BottomSheetDialogFragment() {
    private var vb: BottomsheetCreateCodeBinding? = null
    private var bitmapGenerator: BarGenerator? = null
    private var barcodeFormat: BarcodeFormat
        get() {
            return BarcodeFormat.valueOf(ClipperApp.getPrefs().lastBarFormat)
        }
        set(value) {
            vb?.chosenFormat?.text = value.toString()
            ClipperApp.getPrefs().lastBarFormat = value.toString()
        }


    private val newTags = mutableListOf<TagUiModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vb = BottomsheetCreateCodeBinding.inflate(inflater)
        return vb?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        expand()
        colorNavBar()

        vb?.run {
            bitmapGenerator = BarcodeGenerator
            contentEdit.addTextWatcher { generatePreview(it, preview) }
            val (width, height) = decodedSize

            preview.layoutParams =
                LayoutParams(width,height).apply {
                    gravity = Gravity.CENTER
                }

            chosenFormat.text = barcodeFormat.toString()
            chosenFormat.setOnClickListener {
                showChooseFormatDialog()
            }
            save.setOnClickListener {
                collectInfoAndSave()
            }

            cancel.setOnClickListener {
                dismiss()
            }

            tagEdit.addTextWatcher {
                renderTagsEditText(it)
            }.onImeAction {
                collectInfoAndSave()
            }
        }

        if (existModel != null)
            renderExistModel(existModel)
    }

    private fun renderExistModel(uiModel: BarcodeUiModel) {
        vb?.run {
            barcodeFormat = BarcodeFormat.valueOf(uiModel.barcodeType)
            contentEdit.setText(uiModel.content)
            nameEdit.setText(uiModel.name)
            descriptionEdit.setText(uiModel.description)
            tagEdit.setText(uiModel.tags.joinToString { it.name })

            generatePreview(uiModel.content, preview)
        }
    }

    private fun showChooseFormatDialog() {
        context?.run {
            BarcodeFormatChooseDialog(this) {
                barcodeFormat = it
            }.show(childFragmentManager, "")
        }
    }

    private fun renderTagsEditText(text: String) {
        val nameList = text.split(",").map { it.trim() }

        newTags.clear()
        nameList.forEach {
            if (it.isNotEmpty()) {
                val model = TagUiModel(name = it)
                newTags.add(model)
            }
        }


        vb?.run {
            tagsGroup.removeAllViews()
            newTags.forEach {
                tagsGroup.addView(this.root.context.filterChip(it, false))
            }
        }
    }

    private fun collectInfoAndSave() {
        vb?.run {
            val entity = BarcodeEntity(
                id = existModel?.id ?: UUID.randomUUID().toString(),
                content = contentEdit.text.toString(),
                name = nameEdit.text.toString(),
                description = descriptionEdit.text.toString(),
                type = barcodeFormat.toString(),
                tags = newTags.map { it.id }
            )

            saveBarcodeAndTags(entity, newTags.map { it.toEntity() })
            dismiss()
        }
    }

    private fun generatePreview(text: String, view: ImageView) {
        runCatching {
            runOnUi {
                val preview = bitmapGenerator?.generate(
                    content = text,
                    type = barcodeFormat,
                )
                view.setImageBitmap(preview)
            }
        }
    }
}