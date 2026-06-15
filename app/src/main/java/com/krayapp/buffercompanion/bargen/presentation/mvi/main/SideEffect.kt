package com.krayapp.buffercompanion.bargen.presentation.mvi.main

sealed interface SideEffect {
    data class Snackbar(val text: String): SideEffect
    data class SnackbarRes(val textRes: Int): SideEffect
}