package com.krayapp.buffercompanion.bargen.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.presentation.mvi.settings.SettingsIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.settings.SettingsSideEffect
import com.krayapp.buffercompanion.bargen.presentation.mvi.settings.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class SettingsViewModel : ContainerHost<SettingsState, SettingsSideEffect>, ViewModel() {

    override val container = container<SettingsState, SettingsSideEffect>(SettingsState.default())

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.UpdateMaxBrightOnCard -> updateMaxBrightOnCard(intent.needToBright)
            is SettingsIntent.UpdateOpenAfterScan -> updateOpenAfterScan(intent.open)
            is SettingsIntent.UpdateOpenScanByButton -> updateOpenScanByButton(intent.open)
            is SettingsIntent.UpdateTheme -> updateTheme(intent.theme)
            is SettingsIntent.UpdateSwipeToDelete -> updateSwipeToDelete(intent.swipe)
        }
    }

    private fun updateTheme(theme: Int) = intent {
        ClipperApp.getPrefs().theme = theme
        reduce {
            state.copy(theme = theme)
        }
    }


    private fun updateOpenAfterScan(open: Boolean) = intent {
        ClipperApp.getPrefs().openCardAfterScan = open
        reduce {
            state.copy(openCardAfterScan = open)
        }
    }

    private fun updateOpenScanByButton(open: Boolean) = intent {
        ClipperApp.getPrefs().scanOnVolume = open
        reduce {
            state.copy(openScannerByButton = open)
        }

    }

    private fun updateMaxBrightOnCard(needToBright: Boolean) = intent {
        ClipperApp.getPrefs().maxBrightOnCode = needToBright
        reduce {
            state.copy(maxBrightOnCode = needToBright)
        }
    }

    private fun updateSwipeToDelete(swipe: Boolean) = intent {
        ClipperApp.getPrefs().swipeToDelete = swipe
        reduce {
            state.copy(swipeToDelete = swipe)
        }
    }


}