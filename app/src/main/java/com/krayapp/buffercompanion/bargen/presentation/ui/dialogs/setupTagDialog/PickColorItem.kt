package com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.setupTagDialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.ColorPart
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.ColorPickedDialog
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.theme.barcodePreviewSize

@Composable
fun PickColorItem(
    model: TagUiModel,
    colorPart: ColorPart,
    titleRes: Int,
    pickedColor: Int?,
    onColorPicked: (Int) -> Unit
) {
    val colorPickerShown = remember { mutableStateOf(false) }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(titleRes), style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.weight(1f))

        if (colorPickerShown.value)
            ColorPickedDialog(model = model, colorPart = colorPart, onDismiss = {
                colorPickerShown.value = false
            }) {
                onColorPicked(it)
            }

        if (pickedColor == null)
            Image(
                modifier = Modifier
                    .size(barcodePreviewSize)
                    .clickable {
                        colorPickerShown.value = true
                    }
                    .clip(CircleShape),
                painter = painterResource(R.drawable.png_background),
                contentDescription = null
            )
        else
            Image(
                modifier = Modifier
                    .size(barcodePreviewSize)
                    .clickable {
                        colorPickerShown.value = true
                    }
                    .clip(CircleShape),
                painter = ColorPainter(Color(pickedColor)),
                contentDescription = null
            )
    }
}