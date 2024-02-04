package com.krayapp.buffercompanion

import android.content.Context
import java.util.Collections

class PreferenceManager(private val context: Context) {
	private val PREF_KEY = "PREF_KEY"
	private val SAVED_STRINGS = "SAVED_STRINGS"
	val prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)

	fun putString(text: String) {
		val set = prefs.getStringSet(SAVED_STRINGS, Collections.emptySet())
		set!!.add(text)
		prefs.edit().putStringSet(SAVED_STRINGS, set).apply()
	}

	fun getStrings(): ArrayList<String> {
//		val set = prefs.getStringSet(SAVED_STRINGS, Collections.emptySet())
//		return ArrayList(set!!)

		return ArrayList<String>().apply {
			for (i in 0..5)
				add(i.hashCode().toString())
		}
	}

}