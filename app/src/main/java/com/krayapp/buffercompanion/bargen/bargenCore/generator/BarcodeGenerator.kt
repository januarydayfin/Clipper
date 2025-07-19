package com.krayapp.buffercompanion.bargen.bargenCore.generator

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.krayapp.buffercompanion.bargen.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.bargenCore.BitmapCache
import com.krayapp.buffercompanion.bargen.utils.decodedSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BarcodeGenerator : BarGenerator {
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
                val cachedBitmap = BitmapCache.instance?.get(content)
                if (cachedBitmap != null) return@withContext cachedBitmap


                val generated = when (format) {
                    BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX ->
                        encoder.encodeBitmap(content, format, width, height, hints)

                    else -> encoder.encodeBitmap(content, format, width, height)
                }


                BitmapCache.instance?.put(content, generated)
                generated
            }.onFailure {

            }.getOrNull()
        }
    }
}