package com.krayapp.buffercompanion.bargen.ui.mvi

import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel

data class MviState(
    val bottomSheetData: BottomSheetStateData? = null,
    val showTagBottomSheet: ShowTagsBottomsheet = ShowTagsBottomsheet()
)

data class BottomSheetStateData(
    val model: BarcodeUiModel
) : Effect

data class ShowTagsBottomsheet(val show: Boolean = false) : Effect

sealed interface Effect
