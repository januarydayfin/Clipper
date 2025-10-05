package com.krayapp.buffercompanion.bargen.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.barcodePreviewSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.ui.BargenChip
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupTagDialog(
    model: TagUiModel,
    onDismiss: () -> Unit,
    onComplete: (TagUiModel) -> Unit
) {
    val modelState = remember { mutableStateOf(model) }
    BasicAlertDialog(onDismissRequest = { onDismiss() }) {
        Surface(shape = RoundedCornerShape(size = mSize)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(all = mSize)
            ) {
                BargenChip(model = modelState.value)
                Space(height = sSize)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = modelState.value.name,
                    placeholder = { Text(text = stringResource(R.string.name)) },
                    onValueChange = {
                        modelState.value = modelState.value.copy(name = it)
                    })
                Space(height = sSize)

                PickColorItem(
                    titleRes = R.string.tag_background,
                    pickedColor = modelState.value.backgroundColor
                ) {
                    modelState.value = modelState.value.copy(backgroundColor = it)
                }
                Space(height = sSize)

                PickColorItem(titleRes = R.string.tag_font_color, pickedColor = modelState.value.fontColor) {
                    modelState.value = modelState.value.copy(fontColor = it)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        onDismiss()
                    }) {
                        Text(text = stringResource(R.string.cancel))
                    }

                    TextButton(onClick = {
                        onComplete(modelState.value)
                        onDismiss()
                    }) {
                        Text(text = stringResource(R.string.apply))
                    }
                }
            }


        }
    }
}

@Composable
private fun PickColorItem(titleRes: Int, pickedColor: Int?, onColorPicked: (Int) -> Unit) {
    val colorPickerShown = remember { mutableStateOf(false) }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(titleRes), style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.weight(1f))

        if (colorPickerShown.value)
            ColorPickedDialog(onDismiss = {
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
                    },
                painter = painterResource(R.drawable.png_background),
                contentDescription = null
            )
        else
            Image(
                modifier = Modifier
                    .size(barcodePreviewSize)
                    .clickable {
                        colorPickerShown.value = true
                    },
                painter = ColorPainter(Color(pickedColor)),
                contentDescription = null
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorPickedDialog(onDismiss: () -> Unit, onColorPicked: (Int) -> Unit) {
    BasicAlertDialog(onDismissRequest = { onDismiss() }) {
        val pickedColor = remember { mutableIntStateOf(0) }
        val controller = rememberColorPickerController()
        Surface(shape = RoundedCornerShape(size = mSize)) {

            Column {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = {
                        pickedColor.intValue = it.color.toArgb()
                    })
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp), controller = controller
                )

                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp), controller = controller
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        onDismiss()
                    }) {
                        Text(text = stringResource(R.string.cancel))
                    }

                    TextButton(onClick = {
                        onColorPicked(pickedColor.intValue)
                        onDismiss()
                    }) {
                        Text(text = stringResource(R.string.apply))
                    }
                }
            }

        }
    }
}
