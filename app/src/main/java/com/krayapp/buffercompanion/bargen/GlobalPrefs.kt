package com.krayapp.buffercompanion.bargen

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.domain.type.SearchType
import com.krayapp.buffercompanion.bargen.domain.type.SortType

class GlobalPrefs {
    private val prefs =
        ClipperApp.getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var sortType: String
        get() = prefs.getString(SORT_TYPE, SortType.DATE_DESC.toString())
            ?: SortType.DATE_DESC.toString()
        set(value) {
            prefs.edit { putString(SORT_TYPE, value) }
        }

    var lastBarFormat: String
        get() = prefs.getString(BARCODE_FORMAT, BarcodeFormat.QR_CODE.toString())
            ?: BarcodeFormat.QR_CODE.toString()
        set(value) {
            prefs.edit { putString(BARCODE_FORMAT, value) }
        }


    var theme: Int
        get() = prefs.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) = prefs.edit { putInt(KEY_THEME_MODE, value) }


    var searchType: String
        get() = prefs.getString(SEARCH_TYPE, SearchType.NAME.toString())
            ?: SearchType.NAME.toString()
        set(value) {
            prefs.edit { putString(SEARCH_TYPE, value) }
        }

    var openCardAfterScan: Boolean
        get() = prefs.getBoolean(OPEN_AFTER_SCAN, true)
        set(value) = prefs.edit { putBoolean(OPEN_AFTER_SCAN, value) }

    var scanOnVolume: Boolean
        get() = prefs.getBoolean(VOLUME_BUTTON_SCAN, true)
        set(value) = prefs.edit { putBoolean(VOLUME_BUTTON_SCAN, value) }



    companion object {
        private const val PREFS_NAME = "mainSettings"

        private const val KEY_THEME_MODE = "KEY_THEME_MODE"
        private const val SEARCH_TYPE = "SEARCH_TYPE"
        private const val SORT_TYPE = "SORT_TYPE"
        private const val VOLUME_BUTTON_SCAN = "VOLUME_BUTTON_SCAN "
        private const val BARCODE_FORMAT = "BARCODE_FORMAT"
        private const val OPEN_AFTER_SCAN = "OPEN_AFTER_SCAN"
    }
}