package com.krayapp.buffercompanion.bargen.utils.modifiers

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionOnScreen

fun Modifier.onLongTapScreenOffset(result: (Offset) -> Unit): Modifier {
    var globalOffset = Offset(0f, 0f)
    var viewHeight = 0
    return this
        .onGloballyPositioned {
            globalOffset = it.positionOnScreen()
            viewHeight = it.size.height
        }
        .pointerInput(true) {
            detectTapGestures(onLongPress = { touchOffset ->
                val summ = globalOffset + touchOffset
                result(summ.copy(y = summ.y - viewHeight))
            }) {
            }
        }
}


fun Modifier.onTapScreenOffset(result: (Offset) -> Unit): Modifier {
    var globalOffset = Offset(0f, 0f)
    var viewHeight = 0
    return this
        .onSizeChanged {
            viewHeight = it.height
        }
        .onGloballyPositioned {
            viewHeight = it.size.height


        }
        .pointerInput(true) {
            detectTapGestures(onTap = { touchOffset ->
                val summ = globalOffset + touchOffset
                result(summ.copy(y = summ.y - viewHeight))
            })
        }
}