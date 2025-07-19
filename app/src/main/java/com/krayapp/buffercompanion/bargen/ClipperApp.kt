package com.krayapp.buffercompanion.bargen

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.krayapp.buffercompanion.bargen.utils.displayWidth

class ClipperApp : Application() {

    companion object {
        private var instance: Application? = null
        private lateinit var globalPrefs: GlobalPrefs
        fun getApplication(): Application {
            return instance!!
        }

        fun getPrefs(): GlobalPrefs {
            return globalPrefs
        }

        val displayWidth: Int
            get() = getApplication().displayWidth()
    }

    override fun onCreate() {
        instance = this
        globalPrefs = GlobalPrefs()

        AppCompatDelegate.setDefaultNightMode(globalPrefs.theme)
        super.onCreate()
    }
}