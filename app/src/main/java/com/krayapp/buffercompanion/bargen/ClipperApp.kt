package com.krayapp.buffercompanion.bargen

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class ClipperApp : Application() {

    companion object {
        private var instance: Application? = null
        fun getApplication(): Application {
            return instance!!
        }

        fun getPrefs(): GlobalPrefs {
            return GlobalContext.get().get<GlobalPrefs>()
        }

        val displayWidth: Int
            get() = getApplication().resources.displayMetrics.widthPixels
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        DynamicColors.applyToActivitiesIfAvailable(this)

        startKoin {
            androidContext(this@ClipperApp)
            modules(appModule)
        }

        AppCompatDelegate.setDefaultNightMode(getPrefs().theme)
    }
}