package com.krayapp.buffercompanion.bargen.domain.bargenCore

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat

interface BarGenerator {
    suspend fun generate(content: String, type: BarcodeFormat): Bitmap?
}