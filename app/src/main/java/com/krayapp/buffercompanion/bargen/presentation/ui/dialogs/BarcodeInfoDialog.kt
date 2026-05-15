package com.krayapp.buffercompanion.bargen.presentation.ui.dialogs

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.domain.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.theme.lRoundedCornerShape
import com.krayapp.buffercompanion.bargen.theme.mRoundedCornerShape
import com.krayapp.buffercompanion.bargen.theme.mSize
import org.koin.compose.koinInject

/**
 * Диалог полноэкранного просмотра штрихкода в горизонтальной ориентации.
 *
 * Занимает 95% ширины и 80% высоты экрана. Отображает только изображение
 * штрихкода, повёрнутое на 90°. Bitmap генерируется асинхронно; до готовности
 * показывается индикатор загрузки.
 *
 * @param model Данные штрихкода для генерации изображения.
 * @param onDismiss Колбэк закрытия диалога.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeInfoDialog(model: BarcodeUiModel, onDismiss: () -> Unit) {
    val generator: BarGenerator = koinInject()
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(model.content, model.barcodeType) {
        bitmapState.value = generator.generate(model.content, BarcodeFormat.valueOf(model.barcodeType))
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = lRoundedCornerShape,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(mSize)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(R.drawable.outline_cancel_24),
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }

                BarcodeImageSection(
                    bitmap = bitmapState.value,
                    modifier = Modifier
                        .rotate(90f)
                )
            }
        }
    }
}

@Composable
private fun BarcodeImageSection(bitmap: Bitmap?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.5f)
                    .clip(mRoundedCornerShape),
                contentScale = ContentScale.Fit
            )
        } else {
            CircularProgressIndicator()
        }
    }
}
