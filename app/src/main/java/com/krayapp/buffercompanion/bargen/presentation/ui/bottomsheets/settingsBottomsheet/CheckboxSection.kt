package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.settingsBottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.SettingsUiModel
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.SettingsViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize

@Composable
fun CheckboxSection(state: State<SettingsUiModel>, viewmodel: SettingsViewModel) {
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

            TextCheckbox(
                isChecked = state.value.maxBrightOnCode,
                textRes = R.string.max_bright
            ) {
                viewmodel.updateMaxBrightOnCard(it)
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