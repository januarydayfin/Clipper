package com.krayapp.buffercompanion.bargen.presentation.uiModels

import com.krayapp.buffercompanion.bargen.ClipperApp

data class SettingsUiModel(
    val theme: Int,
    val openScannerByButton: Boolean,
    val openCardAfterScan: Boolean
) {
    companion object {
        val Default = SettingsUiModel(
            theme = ClipperApp.getPrefs().theme,
            openScannerByButton = ClipperApp.getPrefs().scanOnVolume,
            openCardAfterScan = ClipperApp.getPrefs().openCardAfterScan
        )
    }
}