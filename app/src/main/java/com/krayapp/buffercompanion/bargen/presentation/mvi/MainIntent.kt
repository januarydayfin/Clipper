package com.krayapp.buffercompanion.bargen.presentation.mvi

import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel

sealed class MainIntent {
    data class ShowBottomsheet(val uiModel: BarcodeUiModel) : MainIntent()
    data object CreateNewBarcode : MainIntent()
    data object ShowTagsMenu : MainIntent()
    data object ShowSettingsBottomsheet : MainIntent()
}