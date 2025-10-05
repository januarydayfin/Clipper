package com.krayapp.buffercompanion.bargen.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.presentation.bottomsheets.MainBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.bottomsheets.SettingsBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.bottomsheets.TagsBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.dialogs.ScanDialog
import com.krayapp.buffercompanion.bargen.presentation.mvi.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.presentation.mvi.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.ShowSettingsBottomsheet
import com.krayapp.buffercompanion.bargen.presentation.mvi.ShowTagsBottomsheet
import com.krayapp.buffercompanion.bargen.presentation.mvi.canShowMainBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.mvi.canShowSettingsBottomsheet
import com.krayapp.buffercompanion.bargen.presentation.mvi.canShowTagBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.screens.MainScreen
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.theme.AppTheme


class MainActivity : AppCompatActivity() {
    private var onVolumeButtonHandler: () -> Unit = { }
    private val viewmodel: BargenViewModel by viewModels()

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewmodel.inSelection)
                viewmodel.cardSelector.cleanSelection()
            else
                finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        onBackPressedDispatcher.addCallback(onBackPressed)
        setContent {
            AppTheme {
                val mviState = viewmodel.uiState.collectAsState()

                MainScreen {
                    ScanDialog {
                        it ?: return@ScanDialog
                        viewmodel.createBarcodeRecord(
                            text = it.text,
                            format = it.barcodeFormat
                        ) { model ->
                            viewmodel.onIntent(MainIntent.ShowBottomsheet(model))
                        }
                    }.show(supportFragmentManager, "")
                }

                when {
                    mviState.value.canShowMainBottomSheet -> ShowMainBottomSheet(mviState.value.bottomSheetData!!)
                    mviState.value.canShowTagBottomSheet -> ShowTagBottomsheet(mviState.value.showTagBottomSheet)
                    mviState.value.canShowSettingsBottomsheet -> ShowSettingsBottomsheet(mviState.value.showSettingsBottomSheet)
                }
            }
        }

    }

    @Composable
    private fun ShowMainBottomSheet(data: BottomSheetStateData) {
        viewmodel.incrementUsageCount(data.model.id)
        MainBottomSheet(model = data.model, onDismiss = {
            viewmodel.recycleEffect(data)
        })
    }

    @Composable
    private fun ShowTagBottomsheet(data: ShowTagsBottomsheet) {
        TagsBottomSheet {
            viewmodel.recycleEffect(data)
            viewmodel.updatePager()
        }
    }

    @Composable
    private fun ShowSettingsBottomsheet(data: ShowSettingsBottomsheet) {
        SettingsBottomSheet {
            viewmodel.recycleEffect(data)
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