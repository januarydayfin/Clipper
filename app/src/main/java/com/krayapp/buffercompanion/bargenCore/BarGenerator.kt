package com.krayapp.buffercompanion.bargenCore

import android.graphics.Bitmap

interface BarGenerator {
    suspend fun generateFromText(text: String): Bitmap
}