package com.krayapp.buffercompanion.bargen.presentation.mvi.processing

import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.domain.selector.barcodeSelector.CardSelector
import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelector
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.CreateBarcodeUsecase
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.PinnerBarcodeUsecase
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MviProcessorHandler : KoinComponent {
    private val cardSelector: CardSelector by inject()
    private val tagsSelector: TagSelector by inject()
    val cardSelectionFlow = cardSelector.selectedBarcodes
    val tagSelectionFlow = tagsSelector.tagsFilterFlow


    suspend fun onCleanCardSelection() {
        cardSelector.cleanSelection()
    }

    suspend fun onCleanTagsSelection() {
        tagsSelector.cleanSelection()
    }

    suspend fun onDeleteAllSelectedCards() {
        cardSelector.deleteAllSelected()
    }

    suspend fun createBarcodeRecord(
        intent: MainIntent.CreateNewRecord,
    ): BarcodeEntity {
        val entity = intent.barcodeEntity?.run {
            CreateBarcodeUsecase(this)
        } ?: run {
            val text = intent.text
            val format = intent.format.toString()
            val tagIds = intent.tagIds

            CreateBarcodeUsecase(text = text, format = format, tagIds = tagIds)
        }
        return entity
    }

    suspend fun checkBarcodeForSelection(id: String) {
        cardSelector.checkBarcodeForSelection(id)
    }
}