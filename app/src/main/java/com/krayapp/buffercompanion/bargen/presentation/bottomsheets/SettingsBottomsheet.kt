package com.krayapp.buffercompanion.bargen.presentation.bottomsheets

import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.uiModels.SettingsUiModel
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.SettingsViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.utils.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    onRestoreClicked: () -> Unit,
    onBackupClicked: () -> Unit,
    onDismiss: () -> Unit,
) {
    val viewmodel: SettingsViewModel = viewModel()

    val state = viewmodel.settingsState.collectAsState()

    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = {
            onDismiss()
        }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = mSize),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AppThemeBlock(state, viewmodel)
            Space(height = mSize)
            CheckboxSection(state, viewmodel)
            Space(height = mSize)
            BackupSection(onRestoreClicked, onBackupClicked)
        }
    }
}

@Composable
private fun AppThemeBlock(state: State<SettingsUiModel>, viewModel: SettingsViewModel) {
    val buttonModel = state.value.theme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = mSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_theme),
            style = MaterialTheme.typography.labelLarge
        )
        Space(height = sSize)
        SingleChoiceSegmentedButtonRow {
            AppTheme.entries.forEachIndexed { index, item ->
                SegmentedButton(
                    selected =
                        item.value == buttonModel,
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = AppTheme.entries.size
                    ),
                    onClick = {
                        viewModel.updateTheme(item.value)
                        setDefaultNightMode(item.value)
                    },
                    label = {
                        Text(text = stringResource(item.displayName))
                    }
                )
            }
        }
    }
}

@Composable
private fun CheckboxSection(state: State<SettingsUiModel>, viewmodel: SettingsViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(mSize)) {
            TextCheckbox(
                isChecked = state.value.openCardAfterScan,
                textRes = R.string.show_card_after_scan
            ) {
                viewmodel.updateOpenAfterScan(it)
            }

            TextCheckbox(
                isChecked = state.value.openScannerByButton,
                textRes = R.string.volume_button_open_scanner
            ) {
                viewmodel.updateOpenScanByButton(it)
            }
        }
    }
}

@Composable
private fun TextCheckbox(isChecked: Boolean, textRes: Int, onCheckChanged: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isChecked, onCheckedChange = {
            onCheckChanged(it)
        })
        Text(text = stringResource(textRes), style = MaterialTheme.typography.labelLarge)
    }
}

private enum class AppTheme(val value: Int, val displayName: Int) {
    DARK(MODE_NIGHT_YES, R.string.dark_theme),
    SYSTEM(
        MODE_NIGHT_FOLLOW_SYSTEM,
        R.string.system_theme
    ),
    LIGHT(MODE_NIGHT_NO, R.string.light_theme)
}

@Composable
private fun BackupSection(
    onRestoreClicked: () -> Unit,
    onBackupClicked: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .padding(all = mSize)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.backup_restore),
                style = MaterialTheme.typography.labelLarge
            )
            Space(height = mSize)
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        onRestoreClicked()
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.padding(end = 4.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_download),
                            contentDescription = null,
                        )
                        Text(
                            text = stringResource(R.string.restore),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }

                Space(width = mSize)
                Button(
                    onClick = {
                        onBackupClicked()
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.padding(end = 4.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_share),
                            contentDescription = null,
                        )
                        Text(
                            text = stringResource(R.string.backup),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }

}