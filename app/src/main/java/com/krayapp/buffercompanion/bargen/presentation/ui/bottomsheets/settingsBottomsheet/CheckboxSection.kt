package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.settingsBottomsheet

import androidx.compose.foundation.clickable
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
import com.krayapp.buffercompanion.bargen.presentation.mvi.settings.SettingsIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.settings.SettingsState
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.SettingsViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize

@Composable
fun CheckboxSection(state: State<SettingsState>, viewmodel: SettingsViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(mSize)) {
            TextCheckbox(
                isChecked = state.value.openCardAfterScan,
                textRes = R.string.show_card_after_scan
            ) {
                viewmodel.onIntent(SettingsIntent.UpdateOpenAfterScan(it))
            }

            TextCheckbox(
                isChecked = state.value.openScannerByButton,
                textRes = R.string.volume_button_open_scanner
            ) {
                viewmodel.onIntent(SettingsIntent.UpdateOpenScanByButton(it))
            }

            TextCheckbox(
                isChecked = state.value.maxBrightOnCode,
                textRes = R.string.max_bright
            ) {
                viewmodel.onIntent(SettingsIntent.UpdateMaxBrightOnCard(it))
            }

            TextCheckbox(
                isChecked = state.value.swipeToDelete,
                textRes = R.string.swipe_for_delete
            ) {
                viewmodel.onIntent(SettingsIntent.UpdateSwipeToDelete(it))
            }

            TextCheckbox(
                isChecked = state.value.openLandscapeOnBsOpen,
                textRes = R.string.open_landscape_on_open
            ) {
                viewmodel.onIntent(SettingsIntent.UpdateOpenLandscapeOnBsOpen(it))
            }

            TextCheckbox(
                isChecked = state.value.hideBsAfterLandscapeClose,
                textRes = R.string.hide_bs_after_landscape_close
            ) {
                viewmodel.onIntent(SettingsIntent.UpdateHideBsAfterLandscapeClose(it))
            }
        }
    }
}

@Composable
private fun TextCheckbox(isChecked: Boolean, textRes: Int, onCheckChanged: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().clickable {
        onCheckChanged(!isChecked)
    }, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isChecked, onCheckedChange = { onCheckChanged(it) })
        Text(text = stringResource(textRes), style = MaterialTheme.typography.labelLarge)
    }
}