package com.krayapp.buffercompanion.bargen.ui.mvi

import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.mvi.stateManager.Effect

data class MviState(
    val bottomSheetData: BottomSheetStateData? = null,
)

data class BottomSheetStateData(
    val model: BarcodeUiModel
) : Effect