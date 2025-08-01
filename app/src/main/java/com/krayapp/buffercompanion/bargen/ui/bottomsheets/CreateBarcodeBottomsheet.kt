package com.krayapp.buffercompanion.bargen.ui.bottomsheets

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.addTextWatcher
import com.krayapp.buffercompanion.bargen.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.bargenCore.generator.BarcodeGenerator
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.databinding.BottomsheetCreateCodeBinding
import com.krayapp.buffercompanion.bargen.expand
import com.krayapp.buffercompanion.bargen.onImeAction
import com.krayapp.buffercompanion.bargen.ui.dialogs.BarcodeFormatChooseDialog
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.colorNavBar
import com.krayapp.buffercompanion.bargen.utils.currentBarcodeFormat
import com.krayapp.buffercompanion.bargen.utils.decodedSize
import com.krayapp.buffercompanion.bargen.utils.filterChip
import com.krayapp.buffercompanion.bargen.utils.runOnUi
import com.krayapp.buffercompanion.bargen.utils.savePictureInStorage
import com.krayapp.buffercompanion.bargen.utils.toReadableTime
import com.krayapp.buffercompanion.bargen.utils.toast
import java.util.Date
import java.util.UUID

class CreateBarcodeBottomsheet(
    private val existModel: BarcodeUiModel? = null,
    private val saveBarcodeAndTags: (
        barcode: BarcodeEntity, tags: List<TagUiModel>
    ) -> Unit,
    private val tagFounder: suspend (name: String) -> List<TagUiModel>
) :
    BottomSheetDialogFragment() {
    private var vb: BottomsheetCreateCodeBinding? = null
    private var bitmapGenerator: BarGenerator? = null
    private var barcodeFormat: BarcodeFormat
        get() = currentBarcodeFormat
        set(value) {
            vb?.chosenFormat?.text = value.toString()
            ClipperApp.getPrefs().lastBarFormat = value.toString()
        }


    private val newTags = mutableListOf<TagUiModel>()
    private var generatedBitmap: Bitmap? = null
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
                LayoutParams(width, height).apply {
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

            saveImage.setOnClickListener {
                it.context.savePictureInStorage(
                    bmp = generatedBitmap,
                    filename = "${nameEdit.text.toString()}_${Date().time.toReadableTime()}"
                ) {
                    toast("${getString(R.string.saved_to)}_${Environment.DIRECTORY_PICTURES}/Bargen")
                }
            }
            shareImage.setOnClickListener {

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
            BarcodeFormatChooseDialog(barcodeFormat) {
                barcodeFormat = it
            }.show(childFragmentManager, "")
        }
    }

    private fun renderTagsEditText(text: String) {
        val nameList = text.split(",").map { it.trim() }
        val previewsTag = mutableListOf<TagUiModel>()

        newTags.clear()
        runOnUi {
            nameList.forEach { stringName ->
                if (stringName.isNotEmpty()) {
                    val foundTags = tagFounder(stringName) //ищем теги по совпадениям в бд
                    val exactTag = runCatching { foundTags.first { stringName == it.name } }.getOrNull() //если тег полностью совпадает по имени, берем из бд
                    newTags.add(exactTag ?: TagUiModel(name = stringName)) //добавляем либо совпавший тег, либо создаем новйы
                    previewsTag.addAll(foundTags.filter { it !in newTags }) // добавляем в превью все теги за минусом добавленных
                }

                vb?.run {
                    tagsGroup.removeAllViews()
                    tagsPreview.removeAllViews()
                    previewsTag
                        .distinct()
                        .forEach { prev ->
                            tagsPreview.addView(this.root.context.filterChip(prev, false).apply {
                                setOnClickListener {
                                    val correctedTags = tagEdit.text.toString().removeSuffix(stringName)
                                        .plus(prev.name)
                                    tagEdit.setText("$correctedTags, ")
                                    tagEdit.setSelection(tagEdit.text?.length ?: 0)
                                    tagsPreview.removeAllViews()
                                }
                            })
                        }

                    newTags
                        .distinct()
                        .forEach { freshTag ->
                        tagsGroup.addView(this.root.context.filterChip(freshTag, false).apply {
                            setOnClickListener {
                                val correctedTags = tagEdit.text.toString().removeSuffix(stringName)
                                    .plus(freshTag.name)
                                tagEdit.setText("$correctedTags, ")
                                tagEdit.setSelection(tagEdit.text?.length ?: 0)
                            }
                        })
                    }
                }
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

            saveBarcodeAndTags(entity, newTags)
            dismiss()
        }
    }

    private fun generatePreview(text: String, view: ImageView) {
        runCatching {
            runOnUi {
                generatedBitmap = bitmapGenerator?.generate(
                    content = text,
                    type = barcodeFormat,
                )

                view.setImageBitmap(generatedBitmap)

                vb?.imageInteraction?.isVisible = generatedBitmap != null
            }
        }
    }
}