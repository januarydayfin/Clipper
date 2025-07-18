package com.krayapp.buffercompanion.bargen

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.data.SortType

class GlobalPrefs {


    private val prefs =
        ClipperApp.getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var sortType: String
        get() = prefs.getString(SORT_TYPE, SortType.DATE_ASC.toString())
            ?: SortType.DATE_ASC.toString()
        set(value) {
            prefs.edit { putString(SORT_TYPE, value) }
        }

    var lastBarFormat: String
        get() = prefs.getString(BARCODE_FORMAT, BarcodeFormat.QR_CODE.toString())
            ?: BarcodeFormat.QR_CODE.toString()
        set(value) {
            prefs.edit { putString(BARCODE_FORMAT, value) }
        }

    fun isDynamicColors(): Boolean {
        return prefs.getBoolean(KEY_DYNAMIC_COLORS, true)
    }

    fun setDynamicColors(on: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLORS, on).apply()
    }

    fun setThemeMode(mode: Int) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply()
    }

    fun isTutorialShown(): Boolean {
        return prefs.getBoolean(TUTORIAL_SHOWN, false)
    }

    fun onTutorFinished() {
        prefs.edit().putBoolean(TUTORIAL_SHOWN, true).apply()
    }

    fun getTheme(): Int {
        return prefs.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }


    companion object {
        private const val PREFS_NAME = "mainSettings"

        private const val KEY_DYNAMIC_COLORS = "KEY_DYNAMIC_COLORS"
        private const val KEY_THEME_MODE = "KEY_THEME_MODE"
        private const val TUTORIAL_SHOWN = "TUTORIAL_SHOWN"
        private const val SORT_TYPE = "SORT_TYPE"
        private const val BARCODE_FORMAT = "BARCODE_FORMAT"
    }
}