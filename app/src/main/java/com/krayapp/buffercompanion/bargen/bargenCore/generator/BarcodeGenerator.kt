package com.krayapp.buffercompanion.bargen.bargenCore.generator

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.krayapp.buffercompanion.bargen.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.bargenCore.BitmapCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BarcodeGenerator : BarGenerator {
    private val encoder = BarcodeEncoder()
    override suspend fun generate(
        content: String,
        type: BarcodeFormat,
        width: Int,
        height: Int
    ): Bitmap? {
        return generateBitmap(content = content, width = width, height = height, format = type)
    }


    private suspend fun generateBitmap(
        content: String,
        width: Int,
        height: Int,
        format: BarcodeFormat
    ): Bitmap? {
        return withContext(Dispatchers.IO) {
            val cachedBitmap = BitmapCache.instance?.get(content)

            if (cachedBitmap != null) return@withContext cachedBitmap

            runCatching {
                // Подготовка данных с учетом кодировки
                val encodedContent = when (format) {
                    BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX -> {
                        // Для QR-кода указываем UTF-8 через Map
                        val hints = mapOf(
                            EncodeHintType.CHARACTER_SET to "UTF-8"
                        )
                        MultiFormatWriter().encode(
                            content,
                            format,
                            width,
                            height,
                            hints
                        )
                    }


                    else -> {
                        MultiFormatWriter().encode(
                            content,
                            format,
                            width,
                            height
                        )
                    }
                }

                val pixels = IntArray(width * height)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        pixels[y * width + x] =
                            if (encodedContent[x, y]) Color.BLACK else Color.WHITE
                    }
                }

                createBitmap(width, height).apply {
                    setPixels(pixels, 0, width, 0, 0, width, height)
                }.also { bitmap ->
                    BitmapCache.instance?.put(content, bitmap)
                }
            }.getOrNull()
        }
    }

}