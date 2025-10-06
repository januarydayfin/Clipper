package com.krayapp.buffercompanion.bargen.domain.bargenCore

import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.flow.StateFlow

interface BarReader {
    fun readerFlow(): StateFlow<BarcodeResult?>
    fun setView(view: DecoratedBarcodeView)
    fun startScan()
    fun pauseScan()
}