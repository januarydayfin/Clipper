package com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.setupTagDialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.ColorPart
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.ConfirmationDialog
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mRoundedCornerShape
import com.krayapp.buffercompanion.bargen.theme.sSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupTagDialog(
    model: TagUiModel,
    onDeleteTag: (TagUiModel) -> Unit,
    onDismiss: () -> Unit,
    onComplete: (TagUiModel) -> Unit
) {
    val modelState = remember { mutableStateOf(model) }
    val confirmationDialogState = remember { mutableStateOf(false) }

    if (confirmationDialogState.value)
        ConfirmationDialog(onDismiss = {
            confirmationDialogState.value = false
        }) {
            onDeleteTag(model)
        }
    BasicAlertDialog(onDismissRequest = { onDismiss() }) {
        Surface(shape = mRoundedCornerShape) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(all = lSize)
            ) {
                BargenChip(model = modelState.value)
                Space(height = sSize)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = modelState.value.name,
                    placeholder = { Text(text = stringResource(R.string.name)) },
                    label = { Text(text = stringResource(R.string.name)) },
                    onValueChange = {
                        modelState.value = modelState.value.copy(name = it)
                    })
                Space(height = sSize)

                PickColorItem(
                    model = modelState.value,
                    colorPart = ColorPart.BACKGROUND,
                    titleRes = R.string.tag_background,
                    pickedColor = modelState.value.backgroundColor
                ) {
                    modelState.value = modelState.value.copy(backgroundColor = it)
                }
                Space(height = sSize)

                PickColorItem(
                    model = modelState.value,
                    colorPart = ColorPart.CONTENT,
                    titleRes = R.string.tag_font_color,
                    pickedColor = modelState.value.fontColor
                ) {
                    modelState.value = modelState.value.copy(fontColor = it)
                }
                Space(height = sSize)


                TextButton(
                    onClick = {
                        confirmationDialogState.value = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors()
                        .copy(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = null
                    )
                    Text(text = stringResource(R.string.delete))
                }

                Space(height = sSize)

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



