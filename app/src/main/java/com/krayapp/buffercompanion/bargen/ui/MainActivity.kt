package com.krayapp.buffercompanion.bargen.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.lifecycleScope
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.theme.AppTheme
import com.krayapp.buffercompanion.bargen.ui.menu.ContextMenu
import com.krayapp.buffercompanion.bargen.ui.mvi.BargenViewModel
import com.krayapp.buffercompanion.bargen.ui.mvi.PopupShowInfo
import com.krayapp.buffercompanion.bargen.ui.mvi.stateManager.Effect
import com.krayapp.buffercompanion.bargen.ui.screens.MainScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private var onVolumeButtonHandler: () -> Unit = { }
    private val viewmodel: BargenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.navigationBarColor = Color.Transparent.toArgb()


        setContent {
            val showContextMenu = remember { mutableStateOf<PopupShowInfo?>(null) }
            AppTheme {

                LaunchedEffect(Unit) {
                    lifecycleScope.launch {
                        viewmodel.uiState.collectLatest {
                            Log.d("FATA", String.format("%s", it))

                            showContextMenu.value = it.popupShowInfo
                        }
                    }
                }

                MainScreen {

                }
                if (showContextMenu.value != null) {
                    val value = showContextMenu.value ?: return@AppTheme
                    ContextMenu(offset = value.coordinates, uiModel = value.uiModel) {
                        showContextMenu.value = null
                        viewmodel.recycleEffect(Effect.SHOW_POPUP)
                    }
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