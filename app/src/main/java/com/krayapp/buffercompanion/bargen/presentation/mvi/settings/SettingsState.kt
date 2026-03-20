package com.krayapp.buffercompanion.bargen.presentation.mvi.settings

import com.krayapp.buffercompanion.bargen.ClipperApp

data class SettingsState(
    val theme: Int,
    val openScannerByButton: Boolean,
    val openCardAfterScan: Boolean,
    val maxBrightOnCode: Boolean,
    val swipeToDelete: Boolean,
) {
    companion object {
        fun default() = SettingsState(
            theme = ClipperApp.getPrefs().theme,
            openScannerByButton = ClipperApp.getPrefs().scanOnVolume,
            openCardAfterScan = ClipperApp.getPrefs().openCardAfterScan,
            maxBrightOnCode = ClipperApp.getPrefs().maxBrightOnCode,
            swipeToDelete = ClipperApp.getPrefs().swipeToDelete
        )
    }
}