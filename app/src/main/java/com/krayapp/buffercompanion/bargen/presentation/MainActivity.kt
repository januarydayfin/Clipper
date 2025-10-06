package com.krayapp.buffercompanion.bargen.presentation

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.gun0912.tedpermission.normal.TedPermission
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.R
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
import com.krayapp.buffercompanion.bargen.presentation.screens.mainScreen.MainScreen
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.theme.AppTheme
import com.krayapp.buffercompanion.bargen.utils.addPermissionListener
import com.krayapp.buffercompanion.bargen.utils.launchWithDelay
import com.krayapp.buffercompanion.bargen.utils.savePictureInStorage
import com.krayapp.buffercompanion.bargen.utils.shareBitmap
import kotlinx.coroutines.launch
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private val viewmodel: BargenViewModel by viewModels()
    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewmodel.inSelection)
                lifecycleScope.launch {
                    viewmodel.cardSelector.cleanSelection()
                }
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
                    showScanDialog()
                }

                when {
                    mviState.value.canShowMainBottomSheet -> ShowMainBottomSheet(mviState.value.bottomSheetData!!)
                    mviState.value.canShowTagBottomSheet -> ShowTagBottomsheet(mviState.value.showTagBottomSheet)
                    mviState.value.canShowSettingsBottomsheet -> ShowSettingsBottomsheet(mviState.value.showSettingsBottomSheet)
                }
            }
        }

        if (calledFromShortcut())
            pasteFromClip()
    }

    private fun showScanDialog() {
        TedPermission.create()
            .addPermissionListener(onGranted = {
                ScanDialog {
                    it ?: return@ScanDialog
                    viewmodel.createBarcodeRecord(
                        text = it.text,
                        format = it.barcodeFormat
                    ) { model ->
                        if (ClipperApp.getPrefs().openCardAfterScan)
                            viewmodel.onIntent(MainIntent.ShowBottomsheet(model))
                    }
                }.show(supportFragmentManager, "")
            }, onDenied = {
                Toast.makeText(this, R.string.camera_required, Toast.LENGTH_SHORT).show()
            }).setPermissions(Manifest.permission.CAMERA)
            .check()
    }

    @Composable
    private fun ShowMainBottomSheet(data: BottomSheetStateData) {
        viewmodel.incrementUsageCount(data.model.id)
        MainBottomSheet(
            model = data.model, onDismiss = {
                viewmodel.recycleEffect(data)
                viewmodel.updatePager()
            }, onSharePicture = {
                shareBitmap(bitmap = it, title = Random.nextInt().toString())
            },
            onSaveStoragePicture = {
                savePictureInStorage(bmp = it, filename = "${Random.nextInt()}") { path ->
                    Toast.makeText(
                        this,
                        "${getString(R.string.saved_to)} $path",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
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
                showScanDialog()
                true
            }

            else -> super.dispatchKeyEvent(event)
        }
    }

    private fun calledFromShortcut() = intent.action == "bargen.create.qr.buffer"

    private fun pasteFromClip() {
        lifecycleScope.launchWithDelay(delay = 500) {
            val manager =
                this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val text = manager.primaryClip?.getItemAt(0)?.text.toString()

            if (text.isNotEmpty() && text != "null")
                viewmodel.createBarcodeRecord(text) {
                    viewmodel.onIntent(MainIntent.ShowBottomsheet(it))
                }
        }
    }
}