package com.krayapp.buffercompanion.bargen.utils.modifiers

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen


fun Modifier.onCombinedTapScreenOffset(
    onTap: (Offset) -> Unit = {},
    onLong: (Offset) -> Unit = {}
): Modifier {
    var globalOffset = Offset(0f, 0f)
    return this
        .onGloballyPositioned {
            globalOffset = it.positionOnScreen()
        }
        .pointerInput(true) {
            detectTapGestures(onTap = {
                onTap(globalOffset + it)
            }, onLongPress = { touchOffset ->
                onLong(globalOffset + touchOffset)
            })
        }
}