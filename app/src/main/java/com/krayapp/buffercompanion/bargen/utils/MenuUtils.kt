package com.krayapp.buffercompanion.bargen.utils

import android.view.ContextThemeWrapper
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.data.SortType
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel

fun View.showBarcodeMenu(
    uiModel: BarcodeUiModel,
    chooseMode: () -> Unit,
    startEdit: (BarcodeUiModel) -> Unit,
    callDeleteDialog: (BarcodeUiModel) -> Unit
) {
    val popup = PopupMenu(ContextThemeWrapper(this.context, R.style.ThemeOverlay_App_PopupMenu), this)
    with(popup) {

        menuInflater.inflate(R.menu.barcode_menu, menu)
        setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.choose -> {
                    chooseMode()
                }

                R.id.edit -> {
                    startEdit(uiModel)
                }

                R.id.delete -> {
                    callDeleteDialog(uiModel)
                }

                else -> {}
            }
            dismiss()
            true
        }
        show()
    }
}

fun View.showSortMenu(
    onSortSelected: (SortType) -> Unit
) {
    val popup = PopupMenu(ContextThemeWrapper(this.context, R.style.ThemeOverlay_App_PopupMenu), this)

    with(popup) {
        menuInflater.inflate(R.menu.sort_menu, menu)

        setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.by_date_asc -> {
                    onSortSelected(SortType.DATE_ASC)
                }

                R.id.by_date_desc -> {
                    onSortSelected(SortType.DATE_DESC)

                }

                R.id.by_name -> {
                    onSortSelected(SortType.NAME)

                }

                R.id.by_usage -> {
                    onSortSelected(SortType.USAGE)
                }

                else -> {}
            }
            dismiss()
            true
        }
        show()
    }
}

fun View.showTagEditMenu(
    onEditCalled: () -> Unit,
    onDeleteCalled: () -> Unit,
) {
    val popup = PopupMenu(ContextThemeWrapper(this.context, R.style.ThemeOverlay_App_PopupMenu), this)

    with(popup) {
        menuInflater.inflate(R.menu.tag_menu, menu)

        setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    onEditCalled()
                }

                R.id.delete -> {
                    onDeleteCalled()
                }

                else -> {}
            }
            dismiss()
            true
        }
        show()
    }
}