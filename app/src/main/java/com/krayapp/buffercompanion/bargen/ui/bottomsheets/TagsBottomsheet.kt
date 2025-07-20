package com.krayapp.buffercompanion.bargen.ui.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.krayapp.buffercompanion.bargen.BargenViewModel
import com.krayapp.buffercompanion.bargen.databinding.BottomsheetTagsBinding
import com.krayapp.buffercompanion.bargen.expand
import com.krayapp.buffercompanion.bargen.justVibrateABit
import com.krayapp.buffercompanion.bargen.ui.dialogs.TagEditDialog
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.colorNavBar
import com.krayapp.buffercompanion.bargen.utils.filterChip
import com.krayapp.buffercompanion.bargen.utils.runOnUi
import com.krayapp.buffercompanion.bargen.utils.showDeleteConfirmationDialog
import com.krayapp.buffercompanion.bargen.utils.showTagEditMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class TagsBottomsheet(private val viewModel: BargenViewModel) : BottomSheetDialogFragment() {
    private var vb: BottomsheetTagsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vb = BottomsheetTagsBinding.inflate(inflater)
        return vb?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expand()
        colorNavBar()
        loadAllTags()
        vb?.run {
            close.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun loadAllTags() {
        runOnUi {
            delay(100)
            vb?.run {
                chipGroup.removeAllViews()
                viewModel.loadAllTags {
                    val checkedTags = viewModel.tagFilterFlow.value.map { it.id }
                    withContext(Dispatchers.Main) {
                        it.forEach { raw ->
                            val tagUi = raw.copy(checked = raw.id in checkedTags)
                            chipGroup.addView(
                                chipGroup.context.filterChip(tagUi).apply {
                                    setOnLongClickListener { v ->
                                        v.context.justVibrateABit()
                                        v.showTagEditMenu(
                                            onEditCalled = {
                                                showTagEditDialog(tagUi)
                                            },
                                            onDeleteCalled = {
                                                showDeletionDialog(tagUi)
                                            },
                                        )
                                        false
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

    }


    private fun showTagEditDialog(uiModel: TagUiModel) {
        TagEditDialog(uiModel) {
            viewModel.recordTag(it)
            loadAllTags()
        }.show(childFragmentManager, "")
    }

    private fun showDeletionDialog(uiModel: TagUiModel) {
        context?.run {
            showDeleteConfirmationDialog {
                viewModel.removeTagById(uiModel.id)
                loadAllTags()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        applyFilter()
        super.onDismiss(dialog)
        vb = null
    }

    private fun applyFilter() {
        vb?.run {
            val checkedChips = chipGroup.checkedChipIds
            val checkedTagIds = mutableListOf<String>()
            checkedChips.forEach { viewId ->
                checkedTagIds.add((chipGroup.findViewById<Chip>(viewId).tag as TagUiModel).id)
            }

            viewModel.updateTagFilter(checkedTagIds)
        }
    }
}