package com.krayapp.buffercompanion.bargen.presentation.mvi.main

import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel

data class MviState(
    val mainBottomSheetState: BottomSheetStateData? = null,
    val showTagBottomSheet:  Boolean = false,
    val showSettingsBottomSheet:  Boolean = false,
    val showSortBottomSheet: Boolean = false,
    val swipeToDelete: Boolean = true,
    val pinnedBarcodes: List<BarcodeUiModel> = emptyList(),
    val selectedBarcodesIds: List<String> = emptyList(),
) {
    val inSelectionMode: Boolean
        get() = selectedBarcodesIds.isNotEmpty()

    val hasPinnedBarcodes: Boolean
        get() = pinnedBarcodes.isNotEmpty()
}

data class BottomSheetStateData(val model: BarcodeUiModel)
