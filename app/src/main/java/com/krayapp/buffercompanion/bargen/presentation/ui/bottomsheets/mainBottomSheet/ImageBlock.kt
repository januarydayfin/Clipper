package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.domain.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.theme.mRoundedCornerShape
import com.krayapp.buffercompanion.bargen.theme.mSize
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ImageBlock(
    modifier: Modifier = Modifier,
    state: State<BarcodeUiModel>,
    onSharePicture: (Bitmap?) -> Unit = {},
    onSaveStoragePicture: (Bitmap?) -> Unit = {},
    onOpenInfoDialog: () -> Unit = {},
) {
    val model = state.value
    val scope = rememberCoroutineScope()
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    val bmpGenerator: BarGenerator = koinInject()
    val bmp = bitmapState.value
    val generateBitmap: () -> Unit = {
        scope.launch {
            bitmapState.value = bmpGenerator.generate(
                model.content,
                BarcodeFormat.valueOf(model.barcodeType)
            )
        }
    }
    LaunchedEffect(model.content) {
        generateBitmap()
    }

    if (bmp != null) {
        Image(
            bitmap = bmp.asImageBitmap(),
            modifier = modifier.clip(mRoundedCornerShape),
            contentDescription = "image"
        )
        Space(height = mSize)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedIconButton(
                onClick = { onSaveStoragePicture(bmp) },
                textRes = R.string.save,
                iconRes = R.drawable.ic_download
            )

            Spacer(Modifier.width(mSize))

            OutlinedIconButton(
                onClick = { onSharePicture(bmp) },
                textRes = R.string.share,
                iconRes = R.drawable.ic_share
            )

            Spacer(Modifier.width(mSize))

            FilledTonalIconButton(onClick = onOpenInfoDialog) {
                Icon(
                    painter = painterResource(R.drawable.ic_landscape),
                    contentDescription = stringResource(R.string.landscape_view)
                )
            }
        }
    }
}
@Composable
private fun OutlinedIconButton(onClick: () -> Unit, textRes: Int, iconRes: Int) {
    OutlinedButton(onClick = {
        onClick()
    }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.padding(end = 4.dp),
                painter = painterResource(iconRes),
                contentDescription = stringResource(textRes),
            )
            Text(
                text = stringResource(textRes),
            )
        }
    }
}