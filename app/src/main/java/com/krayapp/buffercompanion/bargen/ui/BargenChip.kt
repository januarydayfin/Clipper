package com.krayapp.buffercompanion.bargen.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.theme.xsPadding
import com.krayapp.buffercompanion.bargen.theme.xxsPadding
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel

@Composable
fun BargenChip(tagUiModel: TagUiModel, onClick: () -> Unit = {}) {

    val containerColor =
        if (tagUiModel.backgroundColor != null) Color(tagUiModel.backgroundColor) else Color.Unspecified
    val labelColor =
        if (tagUiModel.fontColor != null) Color(tagUiModel.fontColor) else Color.Unspecified

    FilterChip(
        modifier = Modifier
            .padding(horizontal = xxsPadding)
            .minimumInteractiveComponentSize(),
        colors = FilterChipDefaults.filterChipColors().copy(
            containerColor = containerColor,
            selectedContainerColor = containerColor,
            labelColor = labelColor,
            selectedLabelColor = labelColor
        ),
        selected = tagUiModel.checked,
        onClick = onClick,
        label = { Text(text = tagUiModel.name) },
    )
}