package com.krayapp.buffercompanion.bargen.presentation

import android.Manifest
import android.content.ClipboardManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewManagerFactory
import com.gun0912.tedpermission.normal.TedPermission
import com.krayapp.buffercompanion.bargen.GlobalPrefs
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.CheckDataExistUsecase
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeEntity
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.BottomSheetUiState
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.BottomSheetUiState.Barcode
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.tags.TagIntent
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.MainBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.settingsBottomsheet.SettingsBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.tagsBottomsheet.TagsBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.ScanDialog
import com.krayapp.buffercompanion.bargen.presentation.ui.mainScreen.MainScreen
import com.krayapp.buffercompanion.bargen.presentation.utils.addPermissionListener
import com.krayapp.buffercompanion.bargen.presentation.utils.brightness.peakBright
import com.krayapp.buffercompanion.bargen.presentation.utils.brightness.restoreBright
import com.krayapp.buffercompanion.bargen.presentation.utils.savePictureInStorage
import com.krayapp.buffercompanion.bargen.presentation.utils.shareBitmap
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.TagsViewModel
import com.krayapp.buffercompanion.bargen.theme.AppTheme
import com.krayapp.buffercompanion.bargen.utils.io
import com.krayapp.buffercompanion.bargen.utils.launchWithDelay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val viewmodel: BargenViewModel by viewModel()
    private val tagsViewModel: TagsViewModel by viewModel()
    private val prefs: GlobalPrefs by inject()

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            lifecycleScope.io {
                when {
                    viewmodel.state.first().inSelectionMode -> {
                        viewmodel.onIntent(MainIntent.CleanCardSelection)
                    }

                    viewmodel.currentFilterValue.tagIds.isNotEmpty() -> viewmodel.onIntent(
                        MainIntent.CleanTagsSelection
                    )

                    else -> finishAndRemoveTask()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES

        enableEdgeToEdge(
            statusBarStyle = if (isDarkMode) {
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
            },
            navigationBarStyle = if (isDarkMode) {
                SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            } else {
                SystemBarStyle.light(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT
                )
            }
        )

        onBackPressedDispatcher.addCallback(onBackPressed)
        setContent {
            AppTheme {
                val bottomSheetState = viewmodel.state.collectAsState().value.bottomSheetUiState

                when (bottomSheetState) {
                    is Barcode -> ShowMainBottomSheet(bottomSheetState.model)
                    BottomSheetUiState.Settings -> ShowSettingsBottomsheet()
                    BottomSheetUiState.Tags -> ShowTagBottomsheet()
                    BottomSheetUiState.None -> {}
                }

                MainScreen {
                    showScanDialog()
                }
            }
        }

        if (calledFromShortcut())
            pasteFromClip()

        lifecycleScope.launch {
            if (CheckDataExistUsecase())
                runAppReview()
        }
    }

    private fun runAppReview() {
        val manager = ReviewManagerFactory.create(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(this, reviewInfo)
            }
        }
    }

    private fun showScanDialog() {
        TedPermission.create()
            .addPermissionListener(onGranted = {
                ScanDialog(viewmodel) { result, tags ->
                    result ?: return@ScanDialog
                    viewmodel.onIntent(
                        MainIntent.CreateNewRecord(
                            text = result.text,
                            format = result.barcodeFormat,
                            tagIds = tags,
                            showAfterCreate = true
                        )
                    )
                }.show(supportFragmentManager, "")
            }, onDenied = {
                Toast.makeText(this, R.string.camera_required, Toast.LENGTH_SHORT).show()
            }).setPermissions(Manifest.permission.CAMERA)
            .check()
    }

    @Composable
    private fun ShowMainBottomSheet(data: BottomSheetStateData) {
        viewmodel.incrementUsageCount(data.model.id)
        if (prefs.maxBrightOnCode)
            peakBright()
        MainBottomSheet(
            model = data.model,
            autoOpenLandscape = prefs.openLandscapeOnBsOpen,
            hideBsAfterLandscapeClose = prefs.hideBsAfterLandscapeClose,
            onDismiss = {
                viewmodel.onIntent(MainIntent.HideBottomSheet)

                restoreBright()
            }, onSharePicture = { filename, bmp ->
                shareBitmap(bitmap = bmp, title = filename)
            },
            onSaveStoragePicture = { filename, bmp ->
                savePictureInStorage(bmp = bmp, filename = filename) { path ->
                    Toast.makeText(
                        this,
                        "${getString(R.string.saved_to)} $path",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onApplyBarcode = { model ->
                lifecycleScope.launch(Dispatchers.IO) {
                    viewmodel.onIntent(MainIntent.CreateNewRecord(barcodeEntity = model.toBarcodeEntity()))
                    tagsViewModel.onIntent(TagIntent.SaveTag(model.tags))
                }
            }
        )
    }

    @Composable
    private fun ShowTagBottomsheet() {
        TagsBottomSheet {
            viewmodel.onIntent(MainIntent.HideBottomSheet)
            viewmodel.onIntent(MainIntent.RefreshList)
        }
    }

    @Composable
    private fun ShowSettingsBottomsheet() {
        SettingsBottomSheet {
            viewmodel.onIntent(MainIntent.HideBottomSheet)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val scanOnVolume = prefs.scanOnVolume

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
                this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val text = manager.primaryClip?.getItemAt(0)?.text.toString()

            if (text.isNotEmpty() && text != "null")
                viewmodel.onIntent(MainIntent.CreateNewRecord(text = text, showAfterCreate = true))
        }
    }
}