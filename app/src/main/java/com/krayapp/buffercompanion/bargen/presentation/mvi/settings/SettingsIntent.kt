package com.krayapp.buffercompanion.bargen.presentation.mvi.settings

sealed interface SettingsIntent {
    data class UpdateTheme(val theme: Int): SettingsIntent
    data class UpdateOpenAfterScan(val open: Boolean): SettingsIntent
    data class UpdateOpenScanByButton(val open: Boolean): SettingsIntent
    data class UpdateMaxBrightOnCard(val needToBright: Boolean): SettingsIntent
    data class UpdateSwipeToDelete(val swipe: Boolean): SettingsIntent
    data class UpdateOpenLandscapeOnBsOpen(val open: Boolean): SettingsIntent
    data class UpdateHideBsAfterLandscapeClose(val hide: Boolean): SettingsIntent
}