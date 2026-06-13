package com.krayapp.buffercompanion.bargen.presentation.utils

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.BarcodeResult
import com.krayapp.buffercompanion.bargen.ClipperApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ContentFromUriImage {
    suspend operator fun invoke(uri: Uri): BarcodeResult? = withContext(Dispatchers.IO) {
        runCatching {
            Log.i("FATA", String.format("%s", uri))
            val contentResolver = ClipperApp.getApplication().contentResolver
            val bitmap = contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
                ?: return@runCatching null

            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            val hints = mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(
                    BarcodeFormat.UPC_A, BarcodeFormat.UPC_E,
                    BarcodeFormat.EAN_8, BarcodeFormat.EAN_13,
                    BarcodeFormat.RSS_14, BarcodeFormat.CODE_39,
                    BarcodeFormat.CODE_93, BarcodeFormat.CODE_128,
                    BarcodeFormat.ITF, BarcodeFormat.RSS_EXPANDED,
                    BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX,
                    BarcodeFormat.PDF_417
                ),
                DecodeHintType.CHARACTER_SET to "UTF-8"
            )

            val result = MultiFormatReader().decode(binaryBitmap, hints)
            BarcodeResult(result, null)
        }.getOrNull()
    }
}
