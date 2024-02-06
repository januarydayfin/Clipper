package com.krayapp.buffercompanion

import android.app.Application
import com.google.android.material.color.DynamicColors

class BufferApp: Application() {

    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)
        super.onCreate()
    }
}