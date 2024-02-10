package com.krayapp.buffercompanion

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.krayapp.buffercompanion.ClipperApp

class GlobalPrefs {
	private val PREFS_NAME = "mainSettings"

	private val KEY_DYNAMIC_COLORS = "KEY_DYNAMIC_COLORS"
	private val KEY_THEME_MODE = "KEY_THEME_MODE"
	private val prefs =
		ClipperApp.getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


	fun isDynamicColors(): Boolean {
		return prefs.getBoolean(KEY_DYNAMIC_COLORS, true)
	}

	fun setDynamicColors(on: Boolean) {
		prefs.edit().putBoolean(KEY_DYNAMIC_COLORS, on).apply()
	}

	fun setThemeMode(mode: Int) {
		prefs.edit().putInt(KEY_THEME_MODE, mode).apply()
	}

	fun getTheme(): Int {
		return prefs.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
	}
}