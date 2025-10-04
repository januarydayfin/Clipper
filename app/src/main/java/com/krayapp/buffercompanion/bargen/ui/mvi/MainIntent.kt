package com.krayapp.buffercompanion.bargen.ui.mvi

import androidx.compose.ui.unit.IntOffset
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel

sealed class MainIntent {
    data class ShowPopup(val intOffset: IntOffset, val uiModel: BarcodeUiModel): MainIntent()

}