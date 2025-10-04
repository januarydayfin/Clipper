package com.krayapp.buffercompanion.bargen.ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.labelLargeStyle
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel

@Composable
fun ContextMenu(
    offset: IntOffset,
    uiModel: BarcodeUiModel,
    onDeleteClick: (BarcodeUiModel) -> Unit = {},
    onEditClick: (BarcodeUiModel) -> Unit = {},
    onSelectClick: (BarcodeUiModel) -> Unit = {},
    onDismiss: () -> Unit
) {
    Popup(
        offset = offset,
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Card(
            shape = RoundedCornerShape(size = mSize),
            elevation = CardDefaults.cardElevation(defaultElevation = sSize),
            colors = CardDefaults.cardColors()
//                .copy(
//                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
//                    contentColor = MaterialTheme.colorScheme.onSurface
//                )
        ) {
            val text: @Composable (Int, clickable: () -> Unit) -> Unit = { res, click ->
                Box(modifier = Modifier
                    .padding(horizontal = mSize, vertical = sSize)
                    .clickable() {
                        click()
                    }) {
                    Text(
                        text = stringResource(res), style = labelLargeStyle(),
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
