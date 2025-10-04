package com.krayapp.buffercompanion.bargen.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.theme.AppTheme
import com.krayapp.buffercompanion.bargen.ui.bottomsheets.MainBottomSheet
import com.krayapp.buffercompanion.bargen.ui.mvi.BargenViewModel
import com.krayapp.buffercompanion.bargen.ui.screens.MainScreen


class MainActivity : AppCompatActivity() {
    private var onVolumeButtonHandler: () -> Unit = { }
    private val viewmodel: BargenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.navigationBarColor = Color.Transparent.toArgb()


        setContent {
            AppTheme {
                val mviState = viewmodel.uiState.collectAsState()

                MainScreen {

                }

                val bottomsheetInfo = mviState.value.bottomSheetData
                if (bottomsheetInfo != null)
                    MainBottomSheet(model = bottomsheetInfo.model, onDismiss = {
                        viewmodel.recycleEffect(bottomsheetInfo)
                    }, onSaveModel = {

                    })
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