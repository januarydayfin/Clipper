package com.krayapp.buffercompanion.bargen.ui.mvi

import androidx.compose.ui.unit.IntOffset
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel

data class MviState(
    val popupShowInfo: PopupShowInfo? = null
)

data class PopupShowInfo(
    val coordinates: IntOffset,
    val uiModel: BarcodeUiModel
)