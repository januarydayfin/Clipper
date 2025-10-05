package com.krayapp.buffercompanion.bargen.presentation.selector

import com.krayapp.buffercompanion.bargen.data.BarcodeRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardSelector(private val scope: CoroutineScope, private val barcodeRepo: BarcodeRepo) {
    private val _selectedBarcodes = MutableStateFlow(emptyList<String>())
    val selectedBarcodes = _selectedBarcodes.asStateFlow()

    fun cleanSelection() {
        launchInIO {
            _selectedBarcodes.value = emptyList()
        }
    }

    fun selectAll() {
        launchInIO {
            val list = barcodeRepo.getAllBarcodes().map { it.id }
            _selectedBarcodes.value = list
        }
    }

    fun checkBarcodeForSelection(id: String) {
        launchInIO {
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

    fun deleteAllSelected(onDeleted: () -> Unit) {
        launchInIO {
            barcodeRepo.removeBarcodesByIds(_selectedBarcodes.value)
            onDeleted()
            cleanSelection()
        }
    }

    private fun launchInIO(block: suspend () -> Unit) {
        scope.launch(Dispatchers.IO) { block() }
    }
}