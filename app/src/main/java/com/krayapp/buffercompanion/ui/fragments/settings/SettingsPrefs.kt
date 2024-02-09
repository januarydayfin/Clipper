package com.krayapp.buffercompanion.ui.fragments.settings

import android.content.Context
import com.krayapp.buffercompanion.ClipperApp

class SettingsPrefs {
    private val PREFS_NAME = "mainSettings"

    private val KEY_DYNAMIC_COLORS = "KEY_DYNAMIC_COLORS"
    private val prefs =
        ClipperApp.getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    fun isDynamicColors(): Boolean {
        return prefs.getBoolean(KEY_DYNAMIC_COLORS, true)
    }

    fun setDynamicColors(on: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLORS, on).apply()
    }
}