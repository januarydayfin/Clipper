package com.krayapp.buffercompanion.bargen.presentation.mvi.main

import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel

data class MviState(
    val mainBottomSheetState: BottomSheetStateData? = null,
    val showTagBottomSheet:  Boolean = false,
    val showSettingsBottomSheet:  Boolean = false,
    val showSortBottomSheet: Boolean = false,
)

data class BottomSheetStateData(val model: BarcodeUiModel)
