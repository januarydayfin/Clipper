package com.krayapp.buffercompanion.bargen.domain.bargenCore.reader

import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanOptions.CODE_128
import com.journeyapps.barcodescanner.ScanOptions.CODE_39
import com.journeyapps.barcodescanner.ScanOptions.CODE_93
import com.journeyapps.barcodescanner.ScanOptions.DATA_MATRIX
import com.journeyapps.barcodescanner.ScanOptions.EAN_13
import com.journeyapps.barcodescanner.ScanOptions.EAN_8
import com.journeyapps.barcodescanner.ScanOptions.ITF
import com.journeyapps.barcodescanner.ScanOptions.PDF_417
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE
import com.journeyapps.barcodescanner.ScanOptions.RSS_14
import com.journeyapps.barcodescanner.ScanOptions.RSS_EXPANDED
import com.journeyapps.barcodescanner.ScanOptions.UPC_A
import com.journeyapps.barcodescanner.ScanOptions.UPC_E
import com.krayapp.buffercompanion.bargen.domain.bargenCore.BarReader
import kotlinx.coroutines.flow.MutableStateFlow

class BargenReaderImpl: BarReader {
    private val readerFlow = MutableStateFlow<BarcodeResult?>(null)
    override fun readerFlow() = readerFlow
    private var scanner: DecoratedBarcodeView? = null

    override fun setView(view: DecoratedBarcodeView) {
        scanner = view
    }
    override fun startScan() {
        startParser()
        scanner?.resume()
    }

    override fun pauseScan() {
        scanner?.pause()
    }

    override fun torchOn() {
        scanner?.setTorchOn()
    }

    override fun torchOff() {
        scanner?.setTorchOff()
    }

    private fun startParser() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(
                UPC_A,
                UPC_E,
                EAN_8,
                EAN_13,
                RSS_14,
                CODE_39,
                CODE_93,
                CODE_128,
                ITF,
                RSS_EXPANDED,
                QR_CODE,
                DATA_MATRIX,
                PDF_417
            )
            addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.NORMAL_SCAN)
            addExtra(Intents.Scan.CHARACTER_SET, "UTF-8")
            setPrompt("")
        }.createScanIntent(scanner?.context)

        scanner?.initializeFromIntent(options)
        scanner?.decodeContinuous { result ->
            readerFlow.tryEmit(result)
        }
    }
}