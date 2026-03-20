package com.krayapp.buffercompanion.bargen.presentation.ui.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R

@Composable
fun ToTopBadge(modifier: Modifier = Modifier, hide: Boolean = true, onClick: () -> Unit = {}) {
    val hideAnimation = animateDpAsState(
        if (hide) HIDE_POSITION else 0.dp
    )

    Box(
        modifier = modifier
            .offset(x = hideAnimation.value)
            .clip(CircleShape)
            .size(BADGE_SIZE)
            .background(color = colorScheme.secondary)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_up),

            contentDescription = "arrow_up",

            colorFilter = ColorFilter.tint(color = colorScheme.onSecondary)
        )

    }

}

private val HIDE_POSITION = 60.dp
private val BADGE_SIZE = 40.dp

@Preview
@Composable
private fun BadgePreview() {
    MaterialTheme {
        ToTopBadge { }
    }
}