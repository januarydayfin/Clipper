package com.krayapp.buffercompanion.bargen.presentation.mvi.main

val MviState.canShowMainBottomSheet
    get() = mainBottomSheetState != null

val MviState.canShowTagBottomSheet
    get() = showTagBottomSheet

val MviState.canShowSettingsBottomsheet
    get() = showSettingsBottomSheet