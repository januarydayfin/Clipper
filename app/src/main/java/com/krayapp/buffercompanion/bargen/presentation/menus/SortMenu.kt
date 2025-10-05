package com.krayapp.buffercompanion.bargen.presentation.menus

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.data.SortType
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.toDpOffset

@Composable
fun SortDropdownMenu(
    offset: Offset,
    viewModel: BargenViewModel,
    onDismiss: () -> Unit
) {
    val currentChecked = viewModel.sortType.collectAsState()
    DropdownMenu(
        offset = offset.toDpOffset(),
        expanded = true,
        onDismissRequest = { onDismiss() }) {
        SortType.entries.forEach {
            DropdownMenuItem(text = {
                Text(text = stringResource(it.labelRes))
            }, onClick = {
                viewModel.changeSort(it)
                onDismiss()
            }, trailingIcon = {
                if (it == currentChecked.value)
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.outline_check_24),
                        contentDescription = null
                    )
            })
        }
    }

}