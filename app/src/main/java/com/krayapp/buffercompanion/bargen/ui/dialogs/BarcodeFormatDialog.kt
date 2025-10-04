package com.krayapp.buffercompanion.bargen.ui.dialogs

import androidx.compose.runtime.Composable
import com.google.zxing.BarcodeFormat
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

@Composable
fun BarcodeFormatDialog(onPicked: (BarcodeFormat) -> Unit) {
    val entries = listOf(
        BarcodeFormat.AZTEC,
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
}