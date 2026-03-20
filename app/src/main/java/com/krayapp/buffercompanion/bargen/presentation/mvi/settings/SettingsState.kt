package com.krayapp.buffercompanion.bargen.presentation.mvi.settings

import com.krayapp.buffercompanion.bargen.GlobalPrefs

data class SettingsState(
    val theme: Int,
    val openScannerByButton: Boolean,
    val openCardAfterScan: Boolean,
    val maxBrightOnCode: Boolean,
    val swipeToDelete: Boolean,
) {
    companion object {
        fun default(prefs: GlobalPrefs) = SettingsState(
            theme = prefs.theme,
            openScannerByButton = prefs.scanOnVolume,
            openCardAfterScan = prefs.openCardAfterScan,
            maxBrightOnCode = prefs.maxBrightOnCode,
            swipeToDelete = prefs.swipeToDelete
        )
    }
}