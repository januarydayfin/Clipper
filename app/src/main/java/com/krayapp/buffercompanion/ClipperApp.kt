package com.krayapp.buffercompanion

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors

class ClipperApp: Application() {

    companion object {
        private var instance : Application? = null
        private lateinit var globalPrefs : GlobalPrefs
        fun getApplication(): Application {
            return instance!!
        }
        fun getPrefs(): GlobalPrefs {
            return globalPrefs
        }
    }
    override fun onCreate() {
        instance = this
        globalPrefs = GlobalPrefs()

        if (globalPrefs.isDynamicColors())
            DynamicColors.applyToActivitiesIfAvailable(this)

        AppCompatDelegate.setDefaultNightMode(globalPrefs.getTheme())
        super.onCreate()
    }
}