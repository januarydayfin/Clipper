package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    model: BarcodeUiModel,
    onBitmapGenerated: (Bitmap) -> Unit
) {
    val scope = rememberCoroutineScope()
    var bitmapState by remember { mutableStateOf<Bitmap?>(null) }

    val bmpGenerator: BarGenerator = koinInject()
    val generateBitmap: () -> Unit = {
        scope.launch {
            bitmapState = bmpGenerator.generate(
                model.content,
                BarcodeFormat.valueOf(model.barcodeType)
            )
        }
    }

    LaunchedEffect(bitmapState) {
        bitmapState?.run {
            onBitmapGenerated(this)
        }
    }

    LaunchedEffect(model.content, model.barcodeType) {
        generateBitmap()
    }

    bitmapState?.run {
        Image(
            bitmap = this.asImageBitmap(),
            modifier = modifier.clip(mRoundedCornerShape),
            contentDescription = "image"
        )
    }
}

