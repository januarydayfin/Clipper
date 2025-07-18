package com.krayapp.buffercompanion.bargen.utils

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

fun BottomSheetDialogFragment.expand() {
    val sheet = dialog as BottomSheetDialog
    sheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
}