package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.utils.BargenChip
import kotlin.collections.forEach

@Composable
fun TagRow(list: List<TagUiModel>, onClick: () -> Unit = {}) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        list.forEach {
            BargenChip(model = it, onClick = onClick)
        }
    }

}