package com.krayapp.buffercompanion.bargen.presentation.mvi.main

import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel

sealed interface MainIntent {
    data class ShowExistCodeBottomsheet(val uiModel: BarcodeUiModel) : MainIntent
    data object ShowEmptyMainBottomSheet : MainIntent
    data object ShowTagsMenu : MainIntent
    data object ShowSettingsBottomsheet : MainIntent

    data object CleanCardSelection: MainIntent
    data object CleanTagsSelection: MainIntent
    data object HideBottomSheet : MainIntent
    data object DeleteAllSelectedCards: MainIntent
    data class CheckBarcodeForSelection(val id: String): MainIntent
    data class CreateNewRecord(
        val text: String = "",
        val format: BarcodeFormat = BarcodeFormat.QR_CODE,
        val tagIds: List<String> = emptyList(),
        val barcodeEntity: BarcodeEntity? = null
    ) : MainIntent

    sealed interface PinIntent: MainIntent {
        data class SwapBarcodes(val from: Int, val to: Int): PinIntent
        data class PinBarcode(val id: String): PinIntent
        data class UnpinBarcode(val id: String): PinIntent

        data object SaveOrder: PinIntent
    }
}