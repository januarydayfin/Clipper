package com.krayapp.buffercompanion.bargen.domain.selector.barcodeSelector

import kotlinx.coroutines.flow.StateFlow

interface CardSelector {
    val selectedBarcodes: StateFlow<List<String>>

    suspend fun cleanSelection()
    suspend fun selectAll()
    suspend fun checkBarcodeForSelection(id: String)
    suspend fun deleteAllSelected(onDeleted: () -> Unit)
}