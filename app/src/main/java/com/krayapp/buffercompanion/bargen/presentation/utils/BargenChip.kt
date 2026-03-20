package com.krayapp.buffercompanion.bargen.presentation.utils

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.labelIconSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.theme.xsSize
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel

@Composable
fun BargenChip(
    model: TagUiModel,
    modifier: Modifier = Modifier,
    selectable: Boolean = true,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val containerColor =
        if (model.backgroundColor != null) Color(model.backgroundColor) else MaterialTheme.colorScheme.tertiaryContainer
    val labelColor =
        if (model.fontColor != null) Color(model.fontColor) else MaterialTheme.colorScheme.onTertiaryContainer

    val haptic = LocalHapticFeedback.current

    Box(modifier = Modifier.padding(horizontal = xsSize, vertical = sSize)) {
        Surface(
            color = containerColor,
            contentColor = labelColor,
            shape = RoundedCornerShape(size = 8.dp),
            modifier = modifier
                .clip(RoundedCornerShape(size = 8.dp))
                .combinedClickable(enabled = selectable, onClick = {
                    if (selectable)
                        onClick()
                }, onLongClick = {
                    if (selectable) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick()
                    }
                })
        ) {
            Row(
                modifier = Modifier.padding(vertical = xsSize, horizontal = sSize),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (model.checked && selectable) {
                    Icon(
                        modifier = Modifier.size(size = labelIconSize),
                        painter = painterResource(R.drawable.outline_check_24),
                        contentDescription = null,
                    )
                    Space(width = xsSize)
                }
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}