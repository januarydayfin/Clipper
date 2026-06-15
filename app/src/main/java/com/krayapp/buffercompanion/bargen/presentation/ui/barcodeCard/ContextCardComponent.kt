package com.krayapp.buffercompanion.bargen.presentation.ui.barcodeCard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mRoundedCornerShape

@Composable
fun ContextCardComponent(
    modifier: Modifier,
    menus: List<MenuOption>,
    reorderModifier: Modifier,
    onClick: (MenuOption) -> Unit,
) {
    Row(
        modifier = modifier
            .height(35.dp)
            .clip(RoundedCornerShape(bottomEnd = lSize, bottomStart = lSize))
            .background(
                color = colorScheme.secondary,
                shape = RoundedCornerShape(bottomEnd = lSize, bottomStart = lSize)
            )
    ) {
        menus.forEachIndexed { index, option ->
            if (index != 0)
                Box(modifier = Modifier
                    .padding(vertical = 2.dp)
                    .background(color = colorScheme.onSecondary.copy(alpha = 0.3f), shape = mRoundedCornerShape)
                    .width(1.dp)
                    .fillMaxHeight()
                )
            Box(
                modifier = Modifier
                    .then(reorderModifier.takeIf { option == MenuOption.DRAG } ?: Modifier)
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        onClick(option)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .padding(5.dp),
                    painter = painterResource(option.icon),
                    tint = colorScheme.onSecondary,
                    contentDescription = null
                )
            }
        }
    }
}


enum class MenuOption(val icon: Int) {
    PIN(R.drawable.ic_pin), UNPIN(R.drawable.ic_unpin), DRAG(R.drawable.ic_drag), DELETE(R.drawable.ic_delete)
}

@Preview
@Composable
fun ContextPreview() {
    MaterialTheme(
    ) {
        ContextCardComponent(
            modifier = Modifier, reorderModifier = Modifier, menus = listOf(
                MenuOption.PIN, MenuOption.DELETE, MenuOption.DRAG
            ), onClick = {}
        )
    }
}