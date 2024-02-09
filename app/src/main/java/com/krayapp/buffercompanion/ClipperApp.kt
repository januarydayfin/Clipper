package com.krayapp.buffercompanion

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.krayapp.buffercompanion.ui.fragments.settings.SettingsPrefs

class ClipperApp: Application() {

    companion object {
        private var instance : Application? = null
        private lateinit var globalPrefs : SettingsPrefs
        fun getApplication(): Application {
            return instance!!
        }
        fun getPrefs(): SettingsPrefs {
            return globalPrefs
        }
    }
    override fun onCreate() {
        instance = this
        globalPrefs = SettingsPrefs()

        if (globalPrefs.isDynamicColors())
            DynamicColors.applyToActivitiesIfAvailable(this)

        AppCompatDelegate.setDefaultNightMode(globalPrefs.getTheme())
        super.onCreate()
    }
}