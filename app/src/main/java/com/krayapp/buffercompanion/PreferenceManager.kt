package com.krayapp.buffercompanion

import android.content.Context
import android.util.ArraySet

class PreferenceManager(private val context: Context) {
	private val PREF_KEY = "PREF_KEY"
	private val SAVED_STRINGS = "SAVED_STRINGS"
	val prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)

	fun putString(text: String) {
		val set = prefs.getStringSet(SAVED_STRINGS, ArraySet<String>())
		set!!.add(text)
		prefs.edit().putStringSet(SAVED_STRINGS, set).apply()
	}

	fun getStrings(): ArrayList<String> {
		val set = prefs.getStringSet(SAVED_STRINGS, ArraySet<String>())
		return ArrayList(set!!)
	}

}