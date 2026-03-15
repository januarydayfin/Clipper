package com.krayapp.buffercompanion.bargen.domain.selector.barcodeSelector

import com.krayapp.buffercompanion.bargen.domain.repository.BarcodeRepo
import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CardSelectorImpl(private val barcodeRepo: BarcodeRepo) :
    CardSelector {
    private val _selectedBarcodes = MutableStateFlow(emptyList<String>())

    override val selectedBarcodes
        get() = _selectedBarcodes.asStateFlow()

    override suspend fun cleanSelection() {
        withIO {
            _selectedBarcodes.value = emptyList()
        }
    }


    override suspend fun checkBarcodeForSelection(id: String) {
        withIO {
            val newList = mutableListOf<String>().apply {
                addAll(_selectedBarcodes.value)
            }

            val index = newList.indexOf(id)
            if (index != -1)
                newList.removeAt(index)
            else
                newList.add(id)

            _selectedBarcodes.value = newList
        }
    }

    override suspend fun deleteAllSelected() {
        withIO {
            barcodeRepo.removeBarcodesByIds(_selectedBarcodes.value)
            cleanSelection()
        }
    }
}