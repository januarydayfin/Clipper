package com.krayapp.buffercompanion.bargen.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.presentation.uiModels.SettingsUiModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    private val _settingsState = MutableStateFlow(SettingsUiModel.Default)
    val settingsState = _settingsState.asStateFlow()

    fun updateTheme(theme: Int) {
        launchInIO {
            ClipperApp.getPrefs().theme = theme
            _settingsState.value = _settingsState.value.copy(theme = theme)
        }
    }

    fun updateOpenAfterScan(open: Boolean) {
        launchInIO {
            ClipperApp.getPrefs().openCardAfterScan = open
            _settingsState.value = _settingsState.value.copy(openCardAfterScan = open)
        }
    }

    fun updateOpenScanByButton(open: Boolean) {
        launchInIO {
            ClipperApp.getPrefs().scanOnVolume = open
            _settingsState.value = _settingsState.value.copy(openScannerByButton = open)
        }
    }
}