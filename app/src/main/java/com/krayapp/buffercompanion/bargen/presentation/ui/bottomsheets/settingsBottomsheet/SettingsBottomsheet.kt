package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.settingsBottomsheet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.domain.backup.applyBackupToDatabase
import com.krayapp.buffercompanion.bargen.domain.backup.convertAllDbToJson
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.SheetDragger
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.presentation.utils.backup.bargen_backup_filename
import com.krayapp.buffercompanion.bargen.presentation.utils.backup.readFromFile
import com.krayapp.buffercompanion.bargen.presentation.utils.backup.writeToFile
import com.krayapp.buffercompanion.bargen.presentation.utils.colorizeBottomsheetNavBar
import com.krayapp.buffercompanion.bargen.presentation.utils.toast
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.SettingsViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.utils.io
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    onRestored: () -> Unit,
    onDismiss: () -> Unit,
) {
    val viewmodel: SettingsViewModel = koinViewModel()
    val scope = rememberCoroutineScope()
    val state = viewmodel.collectAsState()
    val context = LocalContext.current
    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri: Uri? ->
            uri?.let {
                scope.io {
                    val string = convertAllDbToJson()
                    context.writeToFile(string, it) {
                        context.toast(R.string.error)
                    }
                }
            }
        }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.io {
                context.readFromFile(uri, onSuccess = {
                    applyBackupToDatabase(it)
                    onRestored()
                }, onError = {
                    context.toast(R.string.backup_error)
                })
            }
        }
    }
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = {
            SheetDragger()
        },
        onDismissRequest = {
            onDismiss()
        }) {
        colorizeBottomsheetNavBar()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = mSize)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppThemeBlock(state, viewmodel)
            Space(height = mSize)
            CheckboxSection(state, viewmodel)
            Space(height = mSize)
            BackupSection(
                onBackupClicked = {
                    exportLauncher.launch(bargen_backup_filename)
                }, onRestoreClicked = {
                    importLauncher.launch(arrayOf("application/json"))
                }
            )
            Space(height = mSize)
        }
    }
}