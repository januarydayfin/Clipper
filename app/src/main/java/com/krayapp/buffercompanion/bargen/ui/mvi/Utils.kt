package com.krayapp.buffercompanion.bargen.ui.mvi

val MviState.canShowMainBottomSheet
    get() = bottomSheetData != null

val MviState.canShowTagBottomSheet
    get() = showTagBottomSheet.show