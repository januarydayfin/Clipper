package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel

@Composable
fun StringedInfo(
    state: State<BarcodeUiModel>,
    onUpdate: (BarcodeUiModel) -> Unit = { }
) {
    val model = state.value

    OutlinedTextField(
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        value = model.content,
        modifier = Modifier.fillMaxWidth(),
        onValueChange = {
            onUpdate(model.copy(content = it))
        },
        maxLines = 2,
        label = {
            Text(text = stringResource(R.string.content))
        })

    OutlinedTextField(
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
        value = model.name,
        onValueChange = {
            onUpdate(model.copy(name = it))
        },
        label = {
            Text(text = stringResource(R.string.name))
        })

    OutlinedTextField(
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
        value = model.description ?: "",
        onValueChange = {
            onUpdate(model.copy(description = it))
        },
        label = {
            Text(text = stringResource(R.string.description))
        })
}
