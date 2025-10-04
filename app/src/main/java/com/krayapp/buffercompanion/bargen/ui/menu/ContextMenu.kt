package com.krayapp.buffercompanion.bargen.ui.menu

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Popup
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.toIntOffset
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel

@Composable
fun ContextMenu(
    offset: Offset,
    uiModel: BarcodeUiModel,
    onDeleteClick: (BarcodeUiModel) -> Unit = {},
    onEditClick: (BarcodeUiModel) -> Unit = {},
    onSelectClick: (BarcodeUiModel) -> Unit = {},
    onDismiss: () -> Unit
) {
    Popup(
        offset = offset.toIntOffset(),
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Card(
            shape = RoundedCornerShape(size = mSize),
            elevation = CardDefaults.cardElevation(defaultElevation = sSize),
            colors = CardDefaults.cardColors()
        ) {
            val text: @Composable (Int, clickable: () -> Unit) -> Unit = { res, click ->
                Box(modifier = Modifier
                    .padding(horizontal = lSize, vertical = mSize)
                    .clickable() {
                        click()
                    }) {
                    Text(
                        text = stringResource(res), style = MaterialTheme.typography.labelLarge,
                    )
                }

            }
            Column {
                text(R.string.choose) { onSelectClick(uiModel) }
                text(R.string.edit) { onEditClick(uiModel) }
                text(R.string.delete) { onDeleteClick(uiModel) }
            }
        }
    }
}
