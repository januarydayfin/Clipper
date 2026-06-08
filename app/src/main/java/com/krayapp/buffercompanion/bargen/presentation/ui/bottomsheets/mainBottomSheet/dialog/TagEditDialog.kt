package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.TagBlock
import com.krayapp.buffercompanion.bargen.theme.mRoundedCornerShape
import com.krayapp.buffercompanion.bargen.theme.mSize

@Composable
fun TagEditDialog(
    uiModel: BarcodeUiModel,
    onApply: (BarcodeUiModel) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        var modelState by remember { mutableStateOf(uiModel) }

        Column(
            modifier = Modifier.background(
                color = colorScheme.surface,
                shape = mRoundedCornerShape
            ).padding(mSize)
        ) {
            TagBlock(
                initialValue = modelState.tags
            ) {
                modelState = modelState.copy(tags = it)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text(text = stringResource(R.string.cancel))
                }

                TextButton(onClick = {
                    onApply(modelState)
                    onDismiss()
                }) {
                    Text(text = stringResource(R.string.apply))
                }
            }
        }
    }
}