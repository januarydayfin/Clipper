package com.krayapp.buffercompanion.bargen.presentation.mvi.main

import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel

data class MviState(
    val bottomSheetUiState: BottomSheetUiState = BottomSheetUiState.None,
    val swipeToDelete: Boolean = true,
    val searchMode: Boolean = false,
    val pinnedBarcodes: List<BarcodeUiModel> = emptyList(),
    val selectedBarcodesIds: List<String> = emptyList(),
) {
    val inSelectionMode: Boolean
        get() = selectedBarcodesIds.isNotEmpty()

    val hasPinnedBarcodes: Boolean
        get() = pinnedBarcodes.isNotEmpty()
}

data class BottomSheetStateData(val model: BarcodeUiModel)

sealed interface BottomSheetUiState {
    data object Tags : BottomSheetUiState
    data object Settings : BottomSheetUiState
    data object None: BottomSheetUiState
    data class Barcode(val model: BottomSheetStateData) : BottomSheetUiState
}
