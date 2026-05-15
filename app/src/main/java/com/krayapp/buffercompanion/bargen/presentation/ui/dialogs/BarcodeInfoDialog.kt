package com.krayapp.buffercompanion.bargen.presentation.ui.dialogs

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.domain.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.utils.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.theme.lRoundedCornerShape
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mRoundedCornerShape
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.theme.xsSize
import org.koin.compose.koinInject

/**
 * Диалог с read-only информацией о штрихкоде в горизонтальном layout.
 *
 * Показывает изображение штрихкода слева и текстовую информацию справа:
 * тип, контент, название, описание и теги. Все поля некликабельны.
 * Изображение генерируется асинхронно; до готовности показывается индикатор загрузки.
 *
 * @param model Данные штрихкода для отображения.
 * @param onDismiss Колбэк закрытия диалога.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = lRoundedCornerShape,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(mSize)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(R.drawable.outline_cancel_24),
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = mSize),
                    verticalAlignment = Alignment.Top
                ) {
                    BarcodeImageSection(bitmap = bitmapState.value)
                    Space(width = mSize)
                    BarcodeInfoSection(model = model, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun BarcodeImageSection(bitmap: Bitmap?) {
    Box(
        modifier = Modifier.width(110.dp),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(mRoundedCornerShape),
                contentScale = ContentScale.FillWidth
            )
        } else {
            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BarcodeInfoSection(model: BarcodeUiModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = model.barcodeType,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Space(height = mSize)
        if (model.content.isNotEmpty()) InfoRow(labelRes = R.string.content, value = model.content)
        if (model.name.isNotEmpty()) InfoRow(labelRes = R.string.name, value = model.name)
        if (model.description.isNotEmpty()) InfoRow(labelRes = R.string.description, value = model.description)
        if (model.tags.isNotEmpty()) {
            Space(height = xsSize)
            FlowRow {
                model.tags.forEach { tag ->
                    BargenChip(model = tag, selectable = false)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(labelRes: Int, value: String) {
    Column(modifier = Modifier.padding(bottom = sSize)) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
