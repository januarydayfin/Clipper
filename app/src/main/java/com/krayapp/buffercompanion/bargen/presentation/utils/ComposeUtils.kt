package com.krayapp.buffercompanion.bargen.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogWindowProvider

@Composable
fun ColumnScope.Space(height: Dp) {
    Spacer(
        modifier = Modifier
            .height(height)
    )
}

@Composable
fun RowScope.Space(width: Dp) {
    Spacer(
        modifier = Modifier
            .width(width)
    )
}

fun Context.toast(textRes: Int) {
    Toast.makeText(this, textRes, Toast.LENGTH_SHORT).show()
}

/**
 * Устанавливает цвет системной панели навигации в соответствии с текущей темой для ModalBottomSheet или Dialog.
 *
 * Функция извлекает провайдер окна из текущей иерархии View и применяет цвет
 * [MaterialTheme.colorScheme.surfaceContainerLow] к навигационной панели.
 * Используется внутри содержимого bottom sheet для обеспечения визуальной целостности между
 * панелью и системным интерфейсом.
 */
@SuppressLint("ComposableNaming")
@Composable
fun colorizeBottomsheetNavBar(color: Color = MaterialTheme.colorScheme.surfaceContainerLow) {
    val windowProvider = LocalView.current.parent as DialogWindowProvider
    val surfaceColor = color.toArgb()
    windowProvider.window.navigationBarColor = surfaceColor
}