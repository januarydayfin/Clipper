package com.krayapp.buffercompanion.bargenCore

import android.graphics.Bitmap
import androidx.collection.LruCache

class BitmapCache private constructor() : LruCache<String, Bitmap>(MAX_CACHE_SIZE) {

    companion object {
        var instance: BitmapCache? = null
            get() {
                if (field == null)
                    field = BitmapCache()

                return field
            }

        private const val MAX_CACHE_SIZE = 50 * 1024 * 1024
    }
}