package com.krayapp.buffercompanion.bargen.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mRoundedCornerShape

@Composable
fun SegmentFab(
    modifier: Modifier = Modifier,
    items: List<SegmentButtonType>,
    onClick: (SegmentButtonType) -> Unit
) {
    FloatingActionButton(modifier = modifier, onClick = {}) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            items.forEachIndexed { index, type ->
                if (index != 0)
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(
                                color = contentColorFor(FloatingActionButtonDefaults.containerColor).copy(alpha = 0.5f),
                                shape = mRoundedCornerShape
                            )
                    )

                Icon(
                    modifier = Modifier
                        .padding(lSize)
                        .clickable(interactionSource = null, indication = null) {
                            onClick(type)
                        }, painter = painterResource(type.iconRes), contentDescription = null
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
private fun SegmentFab_Preview() {
    MaterialTheme {
        SegmentFab(
            modifier = Modifier,
            items = listOf(
                SegmentButtonType.FROM_GALLERY,
                SegmentButtonType.CREATE_RAW,
                SegmentButtonType.SCAN
            )
        ) { }
    }
}