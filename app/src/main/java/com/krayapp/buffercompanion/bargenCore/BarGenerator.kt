package com.krayapp.buffercompanion.bargenCore

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat

interface BarGenerator {
    suspend fun generate(content: String, type: BarcodeFormat, width: Int, height: Int): Bitmap?
}