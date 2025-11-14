package com.krayapp.buffercompanion.bargen.presentation.models

import com.krayapp.buffercompanion.bargen.ClipperApp

data class SettingsState(
    val theme: Int,
    val openScannerByButton: Boolean,
    val openCardAfterScan: Boolean,
    val maxBrightOnCode: Boolean,
) {
    companion object {
        fun default() = SettingsState(
            theme = ClipperApp.getPrefs().theme,
            openScannerByButton = ClipperApp.getPrefs().scanOnVolume,
            openCardAfterScan = ClipperApp.getPrefs().openCardAfterScan,
            maxBrightOnCode =  ClipperApp.getPrefs().maxBrightOnCode
        )
    }
}