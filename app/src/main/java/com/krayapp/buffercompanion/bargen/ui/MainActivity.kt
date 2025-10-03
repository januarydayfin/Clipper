package com.krayapp.buffercompanion.bargen.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.theme.AppTheme
import com.krayapp.buffercompanion.bargen.ui.screens.MainScreen


class MainActivity : AppCompatActivity() {
    private var onVolumeButtonHandler: () -> Unit = { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                MainScreen {

                }
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val scanOnVolume = ClipperApp.getPrefs().scanOnVolume

        if (!scanOnVolume || event.action == MotionEvent.ACTION_UP)
            return super.dispatchKeyEvent(event)

        return when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> {
                onVolumeButtonHandler()
                true
            }

            else -> super.dispatchKeyEvent(event)
        }
    }

    fun updateVolumeButtonHandler(block: () -> Unit) {
        onVolumeButtonHandler = block
    }

    fun calledFromShortcut() = intent.action == "bargen.create.qr.buffer"
}