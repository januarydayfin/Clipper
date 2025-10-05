package com.krayapp.buffercompanion.bargen.presentation.mvi

val MviState.canShowMainBottomSheet
    get() = bottomSheetData != null

val MviState.canShowTagBottomSheet
    get() = showTagBottomSheet.show

val MviState.canShowSettingsBottomsheet
    get() = showSettingsBottomSheet.show