package com.krayapp.buffercompanion.bargen.presentation.mvi

import com.krayapp.buffercompanion.bargen.presentation.uiModels.BarcodeUiModel

data class MviState(
    val bottomSheetData: BottomSheetStateData? = null,
    val showTagBottomSheet: ShowTagsBottomsheet = ShowTagsBottomsheet(),
    val showSettingsBottomSheet: ShowSettingsBottomsheet = ShowSettingsBottomsheet(),
    val showSortBottomSheet: ShowSortBottomsheet = ShowSortBottomsheet(),
)

data class BottomSheetStateData(val model: BarcodeUiModel) : Effect
data class ShowTagsBottomsheet(val show: Boolean = false) : Effect
data class ShowSettingsBottomsheet(val show: Boolean = false) : Effect
data class ShowSortBottomsheet(val show: Boolean = false) : Effect

sealed interface Effect
