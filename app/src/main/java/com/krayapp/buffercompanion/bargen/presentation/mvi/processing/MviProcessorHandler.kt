package com.krayapp.buffercompanion.bargen.presentation.mvi.processing

import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.domain.selector.barcodeSelector.CardSelector
import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelector
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.CreateBarcodeUsecase
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MviProcessorHandler(private val viewModel: BargenViewModel) : KoinComponent {
    private val cardSelector: CardSelector by inject()
    private val tagsSelector: TagSelector by inject()
    val cardSelectionFlow = cardSelector.selectedBarcodes
    val tagSelectionFlow = tagsSelector.tagsFilterFlow


    fun onCleanCardSelection() {
        viewModel.launchInIO {
            cardSelector.cleanSelection()
        }
    }

    fun onCleanTagsSelection() {
        viewModel.launchInIO {
            tagsSelector.cleanSelection()
        }
    }

    fun onDeleteAllSelectedCards() {
        viewModel.launchInIO {
            cardSelector.deleteAllSelected {
                viewModel.updatePager()
            }
        }
    }

    fun createBarcodeRecord(intent: MainIntent.CreateNewRecord) {
        viewModel.launchInIO {
            val entity = intent.barcodeEntity?.run {
                CreateBarcodeUsecase(this)
            } ?: run {
                val text = intent.text
                val format = intent.format.toString()
                val tagIds = intent.tagIds

                CreateBarcodeUsecase(text = text, format = format, tagIds = tagIds)
            }
            viewModel.updatePager()

            if (ClipperApp.getPrefs().openCardAfterScan)
                viewModel.onIntent(MainIntent.ShowExistCodeBottomsheet(entity.toBarcodeUiModel()))
        }
    }
    fun checkBarcodeForSelection(id: String) {
        viewModel.launchInIO {
            cardSelector.checkBarcodeForSelection(id)
        }
    }
}