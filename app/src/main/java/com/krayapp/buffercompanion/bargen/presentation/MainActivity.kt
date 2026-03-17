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
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewManagerFactory
import com.gun0912.tedpermission.normal.TedPermission
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelector
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.CheckDataExistUsecase
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeEntity
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.canShowMainBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.canShowSettingsBottomsheet
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.canShowTagBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.MainBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.settingsBottomsheet.SettingsBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.tagsBottomsheet.TagsBottomSheet
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.ScanDialog
import com.krayapp.buffercompanion.bargen.presentation.ui.mainScreen.MainScreen
import com.krayapp.buffercompanion.bargen.presentation.utils.TagsRouter
import com.krayapp.buffercompanion.bargen.presentation.utils.addPermissionListener
import com.krayapp.buffercompanion.bargen.presentation.utils.brightness.peakBright
import com.krayapp.buffercompanion.bargen.presentation.utils.brightness.restoreBright
import com.krayapp.buffercompanion.bargen.presentation.utils.savePictureInStorage
import com.krayapp.buffercompanion.bargen.presentation.utils.shareBitmap
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.theme.AppTheme
import com.krayapp.buffercompanion.bargen.utils.io
import com.krayapp.buffercompanion.bargen.utils.launchWithDelay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.orbitmvi.orbit.compose.collectAsState
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private val viewmodel: BargenViewModel by viewModels()
    private val tagsRouter = TagsRouter
    private val tagSelector: TagSelector by inject()

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            lifecycleScope.io {
                when {
                    viewmodel.inSelection -> {
                        viewmodel.cardSelector.cleanSelection()
                    }

                    tagSelector.tagsFilterFlow.value.isNotEmpty() -> tagSelector.cleanSelection()
                    else -> finishAndRemoveTask()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        onBackPressedDispatcher.addCallback(onBackPressed)
        setContent {
            AppTheme {
                val state = viewmodel.collectAsState().value

                when {
                    state.canShowMainBottomSheet -> ShowMainBottomSheet(state.mainBottomSheetState!!)
                    state.canShowTagBottomSheet -> ShowTagBottomsheet()
                    state.canShowSettingsBottomsheet -> ShowSettingsBottomsheet()
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
                ScanDialog(viewModel = viewmodel) { result, tags ->
                    result ?: return@ScanDialog
                    viewmodel.onIntent(
                        MainIntent.CreateNewRecordFromRawData(
                            text = result.text,
                            format = result.barcodeFormat,
                            tagIds = tags
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
        if (ClipperApp.getPrefs().maxBrightOnCode)
            peakBright()
        MainBottomSheet(
            model = data.model, onDismiss = {
                viewmodel.onIntent(MainIntent.HideBottomSheet)

                if (ClipperApp.getPrefs().maxBrightOnCode)
                    restoreBright()
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
            },
            onApplyBarcode = { model ->
                lifecycleScope.launch(Dispatchers.IO) {
                    viewmodel.onIntent(MainIntent.CreateNewRecordFromEntity(model.toBarcodeEntity()))
                    tagsRouter.saveTags(model.tags)
                }
            }
        )
    }

    @Composable
    private fun ShowTagBottomsheet() {
        TagsBottomSheet {
            viewmodel.onIntent(MainIntent.HideBottomSheet)
            viewmodel.updatePager()
        }
    }

    @Composable
    private fun ShowSettingsBottomsheet() {
        SettingsBottomSheet {
            viewmodel.onIntent(MainIntent.HideBottomSheet)
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
                viewmodel.onIntent(MainIntent.CreateNewRecordFromRawData(text = text))
        }
    }
}