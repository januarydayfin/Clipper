package com.krayapp.buffercompanion.bargen.presentation.mvi.settings

import com.krayapp.buffercompanion.bargen.ClipperApp

data class SettingsState(
    val theme: Int,
    val openScannerByButton: Boolean,
    val openCardAfterScan: Boolean,
    val maxBrightOnCode: Boolean,
) {
    companion object {
        fun default() = SettingsState(
            theme = ClipperApp.Companion.getPrefs().theme,
            openScannerByButton = ClipperApp.Companion.getPrefs().scanOnVolume,
            openCardAfterScan = ClipperApp.Companion.getPrefs().openCardAfterScan,
            maxBrightOnCode =  ClipperApp.Companion.getPrefs().maxBrightOnCode
        )
    }
}