package com.krayapp.buffercompanion.bargen.presentation.ui.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.sSize

@Composable
fun ToTopBadge(modifier: Modifier = Modifier, hide: Boolean = true, onClick: () -> Unit = {}) {
    val heightAnimation = animateDpAsState(
        if (hide) HIDE_POSITION else 0.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = heightAnimation.value),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(horizontal = sSize)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_up),
                null,
                tint = colorScheme.onSecondaryContainer
            )
            Text(
                text = stringResource(R.string.scroll_to_top),
                style = typography.labelSmall,
                color = colorScheme.onSecondaryContainer
            )
        }
    }

}

private val HIDE_POSITION = -(50.dp)

@Preview
@Composable
private fun BadgePreview() {
    MaterialTheme {
        ToTopBadge { }
    }
}