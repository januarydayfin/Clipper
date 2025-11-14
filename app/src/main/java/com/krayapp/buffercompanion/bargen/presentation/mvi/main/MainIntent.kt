package com.krayapp.buffercompanion.bargen.presentation.mvi.main

import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel

sealed interface MainIntent {
    data class ShowExistCodeBottomsheet(val uiModel: BarcodeUiModel) : MainIntent
    data object ShowEmptyMainBottomSheet : MainIntent
    data object ShowTagsMenu : MainIntent
    data object ShowSettingsBottomsheet : MainIntent

    data object HideBottomSheet : MainIntent

    data class CreateNewRecordFromRawData(
        val text: String,
        val format: BarcodeFormat = BarcodeFormat.QR_CODE,
        val tagIds: List<String> = emptyList()
    ) : MainIntent

    data class CreateNewRecordFromEntity(
        val barcodeEntity: BarcodeEntity
    ) : MainIntent
}