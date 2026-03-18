package com.krayapp.buffercompanion.bargen.presentation.mvi.processing

import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.domain.selector.barcodeSelector.CardSelector
import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelector
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.CreateBarcodeUsecase
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MviProcessorHandler : KoinComponent {
    private val cardSelector: CardSelector by inject()
    private val tagsSelector: TagSelector by inject()
    val cardSelectionFlow = cardSelector.selectedBarcodes
    val tagFilterFlow = tagsSelector.tagsFilterFlow


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