package com.krayapp.buffercompanion.bargen.bargenCore

import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.coroutines.flow.StateFlow

interface BarReader {
    fun readerFlow(): StateFlow<BarcodeResult?>

    fun startScan()
    fun pauseScan()
}