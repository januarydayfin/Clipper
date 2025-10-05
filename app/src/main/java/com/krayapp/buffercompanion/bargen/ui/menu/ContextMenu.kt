package com.krayapp.buffercompanion.bargen.ui.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.krayapp.buffercompanion.bargen.toDpOffset
import com.krayapp.buffercompanion.bargen.toIntOffset
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel

@Composable
fun TagContextMenu(
    offset: Offset,
    tagModel: TagUiModel,
    onDeleteClick: (TagUiModel) -> Unit = {},
    onEditClick: (TagUiModel) -> Unit = {},
    onDismiss: () -> Unit
) {
    DropdownMenu(offset = offset.toDpOffset(), expanded = true, onDismissRequest = { onDismiss() }) {
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.edit)) },
            onClick = { onEditClick(tagModel) })

        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.delete)) },
            onClick = { onDeleteClick(tagModel) })
    }
//    Popup(
//        offset = offset.toIntOffset(),
//        onDismissRequest = {
//            onDismiss()
//        }
//    ) {
//        Card(
//            shape = RoundedCornerShape(size = mSize),
//            elevation = CardDefaults.cardElevation(defaultElevation = sSize),
//            colors = CardDefaults.cardColors()
//        ) {
//            Column {
//                text(R.string.edit) { onEditClick(tagModel) }
//                text(R.string.delete) { onDeleteClick(tagModel) }
//            }
//        }
//    }

}

val text: @Composable (Int, click: () -> Unit) -> Unit = { res, click ->
    Box(
        modifier = Modifier
            .padding(horizontal = lSize, vertical = mSize)
    ) {
        Text(
            text = stringResource(res), style = MaterialTheme.typography.labelLarge,
        )
    }

}