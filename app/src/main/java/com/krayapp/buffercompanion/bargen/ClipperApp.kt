package com.krayapp.buffercompanion.bargen

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import org.koin.core.context.startKoin

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
            get() = getApplication().resources.displayMetrics.widthPixels
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        globalPrefs = GlobalPrefs()

        DynamicColors.applyToActivitiesIfAvailable(this)
        AppCompatDelegate.setDefaultNightMode(globalPrefs.theme)

        startKoin {
            modules(appModule)
        }
    }
}