package com.krayapp.buffercompanion.bargen.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krayapp.buffercompanion.bargen.GlobalPrefs
import com.krayapp.buffercompanion.bargen.presentation.mvi.settings.SettingsIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.settings.SettingsSideEffect
import com.krayapp.buffercompanion.bargen.presentation.mvi.settings.SettingsState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class SettingsViewModel(
    private val prefs: GlobalPrefs
) : ContainerHost<SettingsState, SettingsSideEffect>, ViewModel() {

    override val container = container<SettingsState, SettingsSideEffect>(SettingsState.default(prefs))

    init {
        prefs.themeFlow.onEach { theme ->
            intent { reduce { state.copy(theme = theme) } }
        }.launchIn(viewModelScope)

        prefs.openCardAfterScanFlow.onEach { open ->
            intent { reduce { state.copy(openCardAfterScan = open) } }
        }.launchIn(viewModelScope)

        prefs.scanOnVolumeFlow.onEach { open ->
            intent { reduce { state.copy(openScannerByButton = open) } }
        }.launchIn(viewModelScope)

        prefs.maxBrightOnCodeFlow.onEach { bright ->
            intent { reduce { state.copy(maxBrightOnCode = bright) } }
        }.launchIn(viewModelScope)

        prefs.swipeToDeleteFlow.onEach { swipe ->
            intent { reduce { state.copy(swipeToDelete = swipe) } }
        }.launchIn(viewModelScope)
    }

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
        prefs.theme = theme
    }


    private fun updateOpenAfterScan(open: Boolean) = intent {
        prefs.openCardAfterScan = open
    }

    private fun updateOpenScanByButton(open: Boolean) = intent {
        prefs.scanOnVolume = open
    }

    private fun updateMaxBrightOnCard(needToBright: Boolean) = intent {
        prefs.maxBrightOnCode = needToBright
    }

    private fun updateSwipeToDelete(swipe: Boolean) = intent {
        prefs.swipeToDelete = swipe
    }
}
