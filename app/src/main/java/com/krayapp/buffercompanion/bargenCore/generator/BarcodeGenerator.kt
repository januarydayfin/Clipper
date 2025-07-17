package com.krayapp.buffercompanion.bargenCore.generator

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.krayapp.buffercompanion.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargenCore.BitmapCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BarcodeGenerator : BarGenerator {
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

            if (cachedBitmap != null)
                return@withContext cachedBitmap

            val backgroundColor = Color.WHITE

            val bitMatrix = runCatching {
                MultiFormatWriter().encode(
                    content,
                    format,
                    width,
                    height
                )
            }.getOrNull()

            bitMatrix ?: return@withContext null

            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    pixels[y * width + x] =
                        if (bitMatrix[x, y]) Color.BLACK else backgroundColor
                }
            }

            val bpm = createBitmap(width, height).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
            BitmapCache.instance?.put(content, bpm)
            return@withContext bpm
        }
    }
}