package com.krayapp.buffercompanion.bargen.presentation.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.utils.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.presentation.utils.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickedDialog(
    model: TagUiModel,
    colorPart: ColorPart,
    onDismiss: () -> Unit,
    onColorPicked: (Int) -> Unit
) {
    BasicAlertDialog(onDismissRequest = { onDismiss() }) {
        val modelState = remember { mutableStateOf(model) }
        val pickedColor = remember { mutableIntStateOf(0) }
        val controller = rememberColorPickerController()

        val initialColor = when (colorPart) {
            ColorPart.BACKGROUND -> modelState.value.backgroundColor
            ColorPart.CONTENT -> modelState.value.fontColor
        } ?: Color.Unspecified.toArgb()
        val newModel = when (colorPart) {
            ColorPart.BACKGROUND -> modelState.value.copy(backgroundColor = controller.selectedColor.value.toArgb())
            ColorPart.CONTENT -> modelState.value.copy(fontColor = controller.selectedColor.value.toArgb())
        }
        modelState.value = newModel

        Surface(shape = RoundedCornerShape(size = mSize)) {
            Column {
                Space(height = mSize)

                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    BargenChip(model = modelState.value)
                }

                Space(height = mSize)
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = {
                        pickedColor.intValue = it.color.toArgb()
                    },
                    initialColor = Color(initialColor)
                )
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

enum class ColorPart {
    BACKGROUND, CONTENT
}