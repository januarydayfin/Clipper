package com.krayapp.buffercompanion.bargen.ui.mvi

import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel

sealed class MainIntent {
    data class ShowBottomsheet(val uiModel: BarcodeUiModel) : MainIntent()
    data object CreateNewBarcode : MainIntent()
}