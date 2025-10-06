package com.krayapp.buffercompanion.bargen.domain.bargenCore.generator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.domain.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.domain.bargenCore.BitmapCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BarcodeGeneratorImpl : BarGenerator, KoinComponent {
    private val decodedSize = ClipperApp.displayWidth to ClipperApp.displayWidth / 2

    private val cacheBmp: BitmapCache by inject()
    override suspend fun generate(
        content: String,
        type: BarcodeFormat,
    ): Bitmap? {
        return generateBitmap(content = content, format = type)
    }

    private val encoder = BarcodeEncoder()
    private suspend fun generateBitmap(
        content: String,
        format: BarcodeFormat
    ): Bitmap? {
        val (width, height) = decodedSize
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )
        return withContext(Dispatchers.IO) {
            runCatching {
                val cachedBitmap = cacheBmp["$content$format"]
                if (cachedBitmap != null) return@withContext cachedBitmap


                val generated = when (format) {
                    BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX ->
                        encoder.encodeBitmap(content, format, width, height, hints)

                    else -> encoder.encodeBitmap(content, format, width, height)
                }


                cacheBmp.put("$content$format", generated)
                generated
            }.onFailure {

            }.getOrNull()
        }
    }
}