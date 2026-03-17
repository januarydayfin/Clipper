package com.krayapp.buffercompanion.bargen.domain.bargenCore

import android.graphics.Bitmap
import androidx.collection.LruCache

class BitmapCache : LruCache<String, Bitmap>(MAX_CACHE_SIZE) {
    companion object {
        private const val MAX_CACHE_SIZE = 50 * 1024 * 1024
    }
}