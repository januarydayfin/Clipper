package com.krayapp.buffercompanion

import android.app.Application
import com.google.android.material.color.DynamicColors

class ClipperApp: Application() {

    companion object {
        private var instance : Application? = null

        fun getApplication(): Application {
            return instance!!
        }
    }
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        instance = this
    }
}