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
    var viewHeight = 0
    return this
        .onGloballyPositioned {
            globalOffset = it.positionOnScreen()
            viewHeight = it.size.height
        }
        .pointerInput(true) {
            detectTapGestures(onTap = {
                val summ = globalOffset + it
                onTap(summ.copy(y = summ.y - viewHeight))
            }, onLongPress = { touchOffset ->
                val summ = globalOffset + touchOffset
                onLong(summ.copy(y = summ.y - viewHeight))
            })
        }
}