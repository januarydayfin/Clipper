package com.krayapp.buffercompanion.bargen.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.theme.xsSize
import com.krayapp.buffercompanion.bargen.theme.xxsSize

@Composable
fun SegmentButton(
    modifier: Modifier = Modifier,
    items: List<SegmentButtonType>,
    onClick: (SegmentButtonType) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        items.forEachIndexed { index, type ->
            Button(
                modifier = Modifier
                    .clickable {
                        onClick(type)
                    }, type = type, getShapeFromIndex(index = index, size = items.size)
            )
        }
    }
}

private fun getShapeFromIndex(index: Int, size: Int): RoundedCornerShape {
    return when (index) {
        0 -> RoundedCornerShape(
            topStart = 999.dp,
            bottomStart = 999.dp,
            topEnd = xsSize,
            bottomEnd = xsSize
        )

        size - 1 -> RoundedCornerShape(
            topEnd = 999.dp,
            bottomEnd = 999.dp,
            topStart = xsSize,
            bottomStart = xsSize
        )

        else -> RoundedCornerShape(xsSize)
    }
}

@Composable
private fun Button(
    modifier: Modifier = Modifier,
    type: SegmentButtonType,
    shape: RoundedCornerShape
) {
    Box(
        modifier = modifier
            .background(
                color = colorScheme.primary,
                shape = shape
            )
            .padding(vertical = sSize, horizontal = mSize)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(type.iconRes),
                tint = colorScheme.onPrimary,
                contentDescription = null
            )

            type.textRes?.run {
                Space(xxsSize)
                Text(
                    text = stringResource(this),
                    style = typography.labelLarge,
                    color = colorScheme.onPrimary
                )
            }
        }

    }

}

enum class SegmentButtonType(
    val iconRes: Int,
    val textRes: Int? = null,
) {
    CREATE_RAW(iconRes = R.drawable.ic_plus, textRes = R.string.create),
    SCAN(iconRes = R.drawable.ic_camera),
    FROM_GALLERY(iconRes = R.drawable.add_from_gallery)
}

@Preview(showBackground = true)
@Composable
private fun SegmentButton_Preview() {
    MaterialTheme {
        SegmentButton(
            Modifier,
            listOf(
                SegmentButtonType.CREATE_RAW,
                SegmentButtonType.SCAN,
                SegmentButtonType.FROM_GALLERY
            )
        ) { }
    }
}